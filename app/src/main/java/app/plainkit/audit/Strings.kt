package app.plainkit.audit

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.content.res.Configuration
import java.util.Locale

/**
 * Jazyky appky. `name` (SK, EN…) sa ukladá do SharedPreferences — preto sa
 * existujúce hodnoty z v1.0 ("SK", "EN") načítajú bez zmeny.
 * `nativeName` sa zobrazuje na tlačidlách vždy v danom jazyku, nie v aktuálnom.
 */
enum class Lang(val tag: String, val nativeName: String) {
    SK("sk", "Slovensky"),
    EN("en", "English"),
    DE("de", "Deutsch"),
    FR("fr", "Français"),
    ES("es", "Español"),
    IT("it", "Italiano")
}

object Prefs {
    private const val FILE = "audit_prefs"
    private const val KEY_LANG = "lang"

    fun lang(ctx: Context): Lang? {
        val v = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_LANG, null) ?: return null
        return runCatching { Lang.valueOf(v) }.getOrNull()
    }

    fun setLang(ctx: Context, lang: Lang) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANG, lang.name).apply()
    }

    fun clearLang(ctx: Context) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().remove(KEY_LANG).apply()
    }

    private const val KEY_TRACKER_DB = "tracker_db_version"
    private const val KEY_RESCANNED = "rescanned_after_db_change"

    /**
     * Vráti true, ak sa tejto aplikácii po zmene zoznamu signatúr ešte
     * neobnovila základňa. Pri takom skene sa prírastky trackerov do logu
     * zmien nezapíšu — nevieme totiž rozlíšiť, či knižnica v appke naozaj
     * pribudla, alebo ju len konečne vidí nová signatúra.
     *
     * Prvé volanie po zmene TRACKER_DB_VERSION zoznam preskenovaných
     * vyprázdni, takže poistka platí pre všetky aplikácie, nie len pre tie
     * zoskenované ako prvé.
     */
    @Synchronized
    fun needsBaselineRefresh(ctx: Context, pkg: String): Boolean {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        if (p.getInt(KEY_TRACKER_DB, 0) != TRACKER_DB_VERSION) {
            p.edit()
                .putInt(KEY_TRACKER_DB, TRACKER_DB_VERSION)
                .putStringSet(KEY_RESCANNED, emptySet())
                .apply()
            return true
        }
        return pkg !in (p.getStringSet(KEY_RESCANNED, emptySet()) ?: emptySet())
    }

    /**
     * Zapíše, že táto aplikácia už bola preskenovaná aktuálnym zoznamom.
     *
     * @Synchronized tu nie je ozdoba: „Skenovať všetky" beží v troch vláknach
     * naraz a bez zámku by si navzájom prepísali zoznam preskenovaných
     * (načítaj–uprav–zapíš). Časť aplikácií by v ňom chýbala a poistka by sa
     * im uplatnila druhýkrát — teda by sa stratil skutočný prírastok trackera.
     */
    @Synchronized
    fun markRescanned(ctx: Context, pkg: String) {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val done = p.getStringSet(KEY_RESCANNED, emptySet()) ?: emptySet()
        if (pkg in done) return
        p.edit().putStringSet(KEY_RESCANNED, done + pkg).apply()
    }

    // v1.6: appky, v ktorých APK nebol žiadny čitateľný kód (classes*.dex).
    // Mimo Room zámerne — zmena schémy by bez Migration zmazala celú základňu.
    private const val KEY_NO_CODE = "no_code"

    fun noCode(ctx: Context): Set<String> =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getStringSet(KEY_NO_CODE, emptySet())?.toSet() ?: emptySet()

    @Synchronized
    fun setNoCode(ctx: Context, pkg: String, noCode: Boolean) {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val cur = p.getStringSet(KEY_NO_CODE, emptySet())?.toSet() ?: emptySet()
        val next = if (noCode) cur + pkg else cur - pkg
        if (next != cur) p.edit().putStringSet(KEY_NO_CODE, next).apply()
    }

    // v1.7: zoradenie zoznamu
    private const val KEY_SORT = "sort"

    fun sort(ctx: Context): SortMode = runCatching {
        SortMode.valueOf(
            ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .getString(KEY_SORT, SortMode.SEVERITY.name) ?: SortMode.SEVERITY.name
        )
    }.getOrDefault(SortMode.SEVERITY)

    fun setSort(ctx: Context, mode: SortMode) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_SORT, mode.name).apply()
    }

    // v1.9: appky s kódom v JavaScripte (React Native, Cordova, Capacitor)
    private const val KEY_JS_CODE = "js_code"

    fun jsCode(ctx: Context): Set<String> =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getStringSet(KEY_JS_CODE, emptySet())?.toSet() ?: emptySet()

    @Synchronized
    fun setJsCode(ctx: Context, pkg: String, js: Boolean) {
        val p = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val cur = p.getStringSet(KEY_JS_CODE, emptySet())?.toSet() ?: emptySet()
        val next = if (js) cur + pkg else cur - pkg
        if (next != cur) p.edit().putStringSet(KEY_JS_CODE, next).apply()
    }

    fun clearJsCode(ctx: Context) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().remove(KEY_JS_CODE).apply()
    }

    fun clearNoCode(ctx: Context) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().remove(KEY_NO_CODE).apply()
    }
}

/** v1.7: poradie zoznamu appiek. */
enum class SortMode { SEVERITY, NAME, FINDINGS }

/**
 * RUNTIME  = kliknúť „Povoliť"
 * INSTALL  = appka to dostala automaticky pri inštalácii
 * SPECIAL  = povoľuje sa zvlášť v systémových nastaveniach, stav sa nedá spoľahlivo zistiť
 */
enum class PermKind { RUNTIME, INSTALL, SPECIAL }

/** `res` = id textu v res/values-xx/strings.xml, preklady žijú tam. */
data class PermName(val res: Int, val kind: PermKind)

private const val A = "android.permission."

val PERM_NAMES: Map<String, PermName> = mapOf(
    "${A}ACCESS_FINE_LOCATION" to PermName(R.string.perm_ACCESS_FINE_LOCATION, PermKind.RUNTIME),
    "${A}ACCESS_COARSE_LOCATION" to PermName(R.string.perm_ACCESS_COARSE_LOCATION, PermKind.RUNTIME),
    "${A}ACCESS_BACKGROUND_LOCATION" to PermName(R.string.perm_ACCESS_BACKGROUND_LOCATION, PermKind.RUNTIME),
    "${A}RECORD_AUDIO" to PermName(R.string.perm_RECORD_AUDIO, PermKind.RUNTIME),
    "${A}CAMERA" to PermName(R.string.perm_CAMERA, PermKind.RUNTIME),
    "${A}READ_CONTACTS" to PermName(R.string.perm_READ_CONTACTS, PermKind.RUNTIME),
    "${A}WRITE_CONTACTS" to PermName(R.string.perm_WRITE_CONTACTS, PermKind.RUNTIME),
    "${A}GET_ACCOUNTS" to PermName(R.string.perm_GET_ACCOUNTS, PermKind.RUNTIME),
    "${A}READ_SMS" to PermName(R.string.perm_READ_SMS, PermKind.RUNTIME),
    "${A}RECEIVE_SMS" to PermName(R.string.perm_RECEIVE_SMS, PermKind.RUNTIME),
    "${A}SEND_SMS" to PermName(R.string.perm_SEND_SMS, PermKind.RUNTIME),
    "${A}READ_CALL_LOG" to PermName(R.string.perm_READ_CALL_LOG, PermKind.RUNTIME),
    "${A}WRITE_CALL_LOG" to PermName(R.string.perm_WRITE_CALL_LOG, PermKind.RUNTIME),
    "${A}CALL_PHONE" to PermName(R.string.perm_CALL_PHONE, PermKind.RUNTIME),
    "${A}ANSWER_PHONE_CALLS" to PermName(R.string.perm_ANSWER_PHONE_CALLS, PermKind.RUNTIME),
    "${A}READ_PHONE_STATE" to PermName(R.string.perm_READ_PHONE_STATE, PermKind.RUNTIME),
    "${A}READ_PHONE_NUMBERS" to PermName(R.string.perm_READ_PHONE_NUMBERS, PermKind.RUNTIME),
    "${A}READ_CALENDAR" to PermName(R.string.perm_READ_CALENDAR, PermKind.RUNTIME),
    "${A}WRITE_CALENDAR" to PermName(R.string.perm_WRITE_CALENDAR, PermKind.RUNTIME),
    "${A}READ_EXTERNAL_STORAGE" to PermName(R.string.perm_READ_EXTERNAL_STORAGE, PermKind.RUNTIME),
    "${A}WRITE_EXTERNAL_STORAGE" to PermName(R.string.perm_WRITE_EXTERNAL_STORAGE, PermKind.RUNTIME),
    "${A}READ_MEDIA_IMAGES" to PermName(R.string.perm_READ_MEDIA_IMAGES, PermKind.RUNTIME),
    "${A}READ_MEDIA_VIDEO" to PermName(R.string.perm_READ_MEDIA_VIDEO, PermKind.RUNTIME),
    "${A}READ_MEDIA_AUDIO" to PermName(R.string.perm_READ_MEDIA_AUDIO, PermKind.RUNTIME),
    "${A}READ_MEDIA_VISUAL_USER_SELECTED" to PermName(R.string.perm_READ_MEDIA_VISUAL_USER_SELECTED, PermKind.RUNTIME),
    "${A}ACCESS_MEDIA_LOCATION" to PermName(R.string.perm_ACCESS_MEDIA_LOCATION, PermKind.RUNTIME),
    "${A}ACTIVITY_RECOGNITION" to PermName(R.string.perm_ACTIVITY_RECOGNITION, PermKind.RUNTIME),
    "${A}BODY_SENSORS" to PermName(R.string.perm_BODY_SENSORS, PermKind.RUNTIME),
    "${A}BLUETOOTH_CONNECT" to PermName(R.string.perm_BLUETOOTH_CONNECT, PermKind.RUNTIME),
    "${A}BLUETOOTH_SCAN" to PermName(R.string.perm_BLUETOOTH_SCAN, PermKind.RUNTIME),
    "${A}NEARBY_WIFI_DEVICES" to PermName(R.string.perm_NEARBY_WIFI_DEVICES, PermKind.RUNTIME),
    "${A}ACCESS_LOCAL_NETWORK" to PermName(R.string.perm_ACCESS_LOCAL_NETWORK, PermKind.RUNTIME),
    "${A}POST_NOTIFICATIONS" to PermName(R.string.perm_POST_NOTIFICATIONS, PermKind.RUNTIME),

    "${A}MANAGE_EXTERNAL_STORAGE" to PermName(R.string.perm_MANAGE_EXTERNAL_STORAGE, PermKind.SPECIAL),
    "${A}SYSTEM_ALERT_WINDOW" to PermName(R.string.perm_SYSTEM_ALERT_WINDOW, PermKind.SPECIAL),
    "${A}WRITE_SETTINGS" to PermName(R.string.perm_WRITE_SETTINGS, PermKind.SPECIAL),
    "${A}REQUEST_INSTALL_PACKAGES" to PermName(R.string.perm_REQUEST_INSTALL_PACKAGES, PermKind.SPECIAL),
    "${A}PACKAGE_USAGE_STATS" to PermName(R.string.perm_PACKAGE_USAGE_STATS, PermKind.SPECIAL),
    "${A}SCHEDULE_EXACT_ALARM" to PermName(R.string.perm_SCHEDULE_EXACT_ALARM, PermKind.SPECIAL),
    "${A}REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" to PermName(R.string.perm_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, PermKind.SPECIAL),

    "${A}QUERY_ALL_PACKAGES" to PermName(R.string.perm_QUERY_ALL_PACKAGES, PermKind.INSTALL),
    "${A}RECEIVE_BOOT_COMPLETED" to PermName(R.string.perm_RECEIVE_BOOT_COMPLETED, PermKind.INSTALL),
    "${A}DETECT_SCREEN_CAPTURE" to PermName(R.string.perm_DETECT_SCREEN_CAPTURE, PermKind.INSTALL),
    "${A}DOWNLOAD_WITHOUT_NOTIFICATION" to PermName(R.string.perm_DOWNLOAD_WITHOUT_NOTIFICATION, PermKind.INSTALL),
    "com.google.android.gms.permission.AD_ID" to PermName(R.string.perm_AD_ID, PermKind.INSTALL),

    // Služby na pozadí. Samotné povolenie neudeľuje prístup k mikrofónu, kamere
    // ani polohe — tie si appka musí vypýtať zvlášť. Znamená, že takú službu
    // smie držať spustenú, aj keď ju používateľ nemá otvorenú.
    "${A}FOREGROUND_SERVICE_MICROPHONE" to PermName(R.string.perm_FOREGROUND_SERVICE_MICROPHONE, PermKind.INSTALL),
    "${A}FOREGROUND_SERVICE_CAMERA" to PermName(R.string.perm_FOREGROUND_SERVICE_CAMERA, PermKind.INSTALL),
    "${A}FOREGROUND_SERVICE_LOCATION" to PermName(R.string.perm_FOREGROUND_SERVICE_LOCATION, PermKind.INSTALL),
    "${A}FOREGROUND_SERVICE_MEDIA_PROJECTION" to PermName(R.string.perm_FOREGROUND_SERVICE_MEDIA_PROJECTION, PermKind.INSTALL),

    "${A}DETECT_SCREEN_RECORDING" to PermName(R.string.perm_DETECT_SCREEN_RECORDING, PermKind.INSTALL),
    "${A}MANAGE_OWN_CALLS" to PermName(R.string.perm_MANAGE_OWN_CALLS, PermKind.INSTALL),

    // Google Play. Install Referrer hovorí appke, z akej reklamy alebo odkazu
    // si ju nainštaloval — to je atribúcia, nie technická drobnosť.
    "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE" to
        PermName(R.string.perm_BIND_GET_INSTALL_REFERRER_SERVICE, PermKind.INSTALL),
    "com.android.vending.BILLING" to PermName(R.string.perm_BILLING, PermKind.INSTALL),

    // Privacy Sandbox — reklamné rozhrania priamo v Androide.
    "${A}ACCESS_ADSERVICES_TOPICS" to PermName(R.string.perm_ACCESS_ADSERVICES_TOPICS, PermKind.INSTALL),
    "${A}ACCESS_ADSERVICES_AD_ID" to PermName(R.string.perm_ACCESS_ADSERVICES_AD_ID, PermKind.INSTALL),
    "${A}ACCESS_ADSERVICES_ATTRIBUTION" to PermName(R.string.perm_ACCESS_ADSERVICES_ATTRIBUTION, PermKind.INSTALL)
)

/**
 * v1.6: špeciálne povolenia sa nezapínajú oknom „Povoliť", ale prepínačom
 * v nastaveniach. Ich stav Android neukladá medzi bežné povolenia, ale do
 * AppOps. Kľúč = povolenie, hodnota = názov operácie v AppOps.
 */
val SPECIAL_OPS: Map<String, String> = mapOf(
    "${A}MANAGE_EXTERNAL_STORAGE" to "android:manage_external_storage",
    "${A}SYSTEM_ALERT_WINDOW" to "android:system_alert_window",
    "${A}WRITE_SETTINGS" to "android:write_settings",
    "${A}PACKAGE_USAGE_STATS" to "android:get_usage_stats",
    "${A}REQUEST_INSTALL_PACKAGES" to "android:request_install_packages",
    "${A}SCHEDULE_EXACT_ALARM" to "android:schedule_exact_alarm"
)
const val BATTERY_PERM = "${A}REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"

/**
 * v1.6: povolenie, ktoré na TOMTO telefóne nič neznamená — buď už nahradené
 * (staré úložné povolenia pri appke cielenej na nový Android), alebo ešte
 * neexistuje (napr. notifikácie na Androide 12 a starších). Bez toho by sa
 * ukázalo „neudelené", hoci appka môže mať prístup inou cestou.
 */
fun isUnusedHere(permission: String, targetSdk: Int): Boolean {
    val sdk = Build.VERSION.SDK_INT
    return when (permission) {
        "${A}READ_EXTERNAL_STORAGE" -> sdk >= 33 && targetSdk >= 33
        "${A}WRITE_EXTERNAL_STORAGE" -> sdk >= 30 && targetSdk >= 30
        "${A}POST_NOTIFICATIONS", "${A}NEARBY_WIFI_DEVICES",
        "${A}READ_MEDIA_IMAGES", "${A}READ_MEDIA_VIDEO", "${A}READ_MEDIA_AUDIO" -> sdk < 33
        "${A}READ_MEDIA_VISUAL_USER_SELECTED" -> sdk < 34
        "${A}BLUETOOTH_CONNECT", "${A}BLUETOOTH_SCAN" -> sdk < 31
        "${A}ACCESS_BACKGROUND_LOCATION", "${A}ACTIVITY_RECOGNITION",
        "${A}ACCESS_MEDIA_LOCATION" -> sdk < 29
        else -> false
    }
}

fun permLabel(permission: String, s: S): String? =
    PERM_NAMES[permission]?.let { s.str(it.res) }

/**
 * Povolenie, ktoré si aplikácia vyrobila sama — jeho názov začína jej vlastným
 * balíkom (napr. com.whatsapp si pýta com.whatsapp.permission.NIECO). Takým
 * povolením si appka chráni vlastné súčiastky pred inými aplikáciami;
 * používateľa sa to nijako netýka, preto ho nezobrazujeme.
 *
 * Pokrýva to aj DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION, ktoré do každej
 * modernej aplikácie pridáva knižnica AndroidX a tiež nič neznamená.
 *
 * Filtruje sa len ZOBRAZENIE. Do databázy sa ukladá všetko, čo appka deklaruje,
 * aby sa porovnanie so starším skenom nerozbilo.
 */
/**
 * com.android a com.google nie sú priestory jedného výrobcu — patrí do nich
 * systém aj appky mnohých strán. Pre ne sa dodávateľská predpona nepoužíva,
 * inak by sa Chromu (com.android.chrome) skrylo napríklad
 * com.android.browser.permission.READ_HISTORY_BOOKMARKS, teda prístup
 * k histórii prehliadania — a to je presne nález, ktorý má byť vidieť.
 */
private val SHARED_VENDOR_PREFIXES = setOf("com.android", "com.google")

fun isOwnPermission(permission: String, packageName: String): Boolean {
    if (permission.startsWith("$packageName.")) return true
    // Appky jedného výrobcu si navzájom chránia komponenty: Messenger
    // (com.facebook.orca) si pýta com.facebook.katana.provider.ACCESS. Preto sa
    // porovnáva aj dodávateľská predpona — prvé dve časti balíka.
    // Pozor: porovnáva sa s balíkom TEJ ISTEJ appky. Keď si com.facebook.*
    // vypýta cudzia hra, zostane to viditeľné, lebo to zaujímavé je.
    val parts = packageName.split('.')
    if (parts.size < 3) return false
    val vendor = parts[0] + "." + parts[1]
    if (vendor in SHARED_VENDOR_PREFIXES) return false
    return permission.startsWith("$vendor.")
}

/**
 * Povolenia na príjem push správ (Google, Amazon, Xiaomi, Nokia, Oppo…) a na
 * odznak s počtom neprečítaných na ikonke (Samsung, HTC, Huawei, Sony,
 * Motorola…). Je ich desiatky a všetky znamenajú to isté, preto sa v zozname
 * ukážu ako jeden riadok.
 */
fun isNotifyOrBadge(permission: String): Boolean {
    val u = permission.uppercase()
    if ("BADGE" in u) return true
    if ("SHORTCUT" in u && ("LAUNCHER" in u || "HOME" in u)) return true
    if ("LAUNCHER" in u && u.endsWith(".READ_SETTINGS")) return true
    if ("MIPUSH" in u || u.endsWith("C2D_MESSAGE")) return true
    if (u.endsWith(".RECEIVE") &&
        ("C2DM" in u || "PUSH" in u || "MESSAGING" in u || "MCS" in u)
    ) return true
    return false
}

/**
 * Názov technického povolenia na zobrazenie. Zhodí len androidovú predponu,
 * zvyšok nechá celý — `com.google.android.c2dm.permission.RECEIVE` skrátené
 * na „RECEIVE" už nikomu nič nepovie.
 */
fun techLabel(permission: String): String =
    permission.removePrefix("android.permission.")

/**
 * Dlhý technický názov s neviditeľnými miestami na zalomenie za bodkou
 * a podčiarkovníkom. Bez nich Compose zalomí riadok hneď za „·" a názov
 * spadne celý na ďalší riadok (chyba z v1.4).
 */
fun breakable(name: String): String =
    name.replace(".", ".\u200B").replace("_", "_\u200B")

/**
 * Ľudský popis technických povolení Androidu (v1.5). Len povolenia, ktoré
 * Android verejne opisuje — nič nehádame. Úroveň (bežné / len pre systém)
 * sa tu NEUVÁDZA: číta sa za behu z telefónu, viď [isSignatureProtected].
 * Zoznam overený na vivo V50 Lite a Redmi (pm list permissions -f), 21. 10. 2026.
 */
val TECH_NAMES: Map<String, Int> = mapOf(
    "${A}INTERNET" to R.string.tperm_INTERNET,
    "${A}ACCESS_NETWORK_STATE" to R.string.tperm_ACCESS_NETWORK_STATE,
    "${A}ACCESS_WIFI_STATE" to R.string.tperm_ACCESS_WIFI_STATE,
    "${A}CHANGE_NETWORK_STATE" to R.string.tperm_CHANGE_NETWORK_STATE,
    "${A}CHANGE_WIFI_STATE" to R.string.tperm_CHANGE_WIFI_STATE,
    "${A}WAKE_LOCK" to R.string.tperm_WAKE_LOCK,
    "${A}VIBRATE" to R.string.tperm_VIBRATE,
    "${A}FOREGROUND_SERVICE" to R.string.tperm_FOREGROUND_SERVICE,
    "${A}KILL_BACKGROUND_PROCESSES" to R.string.tperm_KILL_BACKGROUND_PROCESSES,
    "${A}RESTART_PACKAGES" to R.string.tperm_RESTART_PACKAGES,
    "${A}GET_TASKS" to R.string.tperm_GET_TASKS,
    "${A}SET_WALLPAPER" to R.string.tperm_SET_WALLPAPER,
    "${A}EXPAND_STATUS_BAR" to R.string.tperm_EXPAND_STATUS_BAR,
    "${A}DISABLE_KEYGUARD" to R.string.tperm_DISABLE_KEYGUARD,
    "${A}USE_BIOMETRIC" to R.string.tperm_USE_BIOMETRIC,
    "${A}USE_FINGERPRINT" to R.string.tperm_USE_FINGERPRINT,
    "${A}ACCESS_NOTIFICATION_POLICY" to R.string.tperm_ACCESS_NOTIFICATION_POLICY,
    "${A}MANAGE_ACCOUNTS" to R.string.tperm_MANAGE_ACCOUNTS,
    "${A}AUTHENTICATE_ACCOUNTS" to R.string.tperm_AUTHENTICATE_ACCOUNTS,
    "${A}NFC" to R.string.tperm_NFC,
    "${A}BLUETOOTH" to R.string.tperm_BLUETOOTH,
    "${A}BLUETOOTH_ADMIN" to R.string.tperm_BLUETOOTH_ADMIN,
    "${A}TRANSMIT_IR" to R.string.tperm_TRANSMIT_IR,
    "${A}USE_FULL_SCREEN_INTENT" to R.string.tperm_USE_FULL_SCREEN_INTENT,
    "${A}INSTALL_PACKAGES" to R.string.tperm_INSTALL_PACKAGES,
    "${A}DELETE_PACKAGES" to R.string.tperm_DELETE_PACKAGES,
    "${A}INSTALL_SELF_UPDATES" to R.string.tperm_INSTALL_SELF_UPDATES,
    "${A}FORCE_STOP_PACKAGES" to R.string.tperm_FORCE_STOP_PACKAGES,
    "${A}CLEAR_APP_USER_DATA" to R.string.tperm_CLEAR_APP_USER_DATA,
    "${A}WRITE_SECURE_SETTINGS" to R.string.tperm_WRITE_SECURE_SETTINGS,
    "${A}MANAGE_USERS" to R.string.tperm_MANAGE_USERS,
    "${A}INTERACT_ACROSS_USERS" to R.string.tperm_INTERACT_ACROSS_USERS,
    "${A}INTERACT_ACROSS_USERS_FULL" to R.string.tperm_INTERACT_ACROSS_USERS_FULL,
    "${A}READ_LOGS" to R.string.tperm_READ_LOGS,
    "${A}DUMP" to R.string.tperm_DUMP,
    "${A}REAL_GET_TASKS" to R.string.tperm_REAL_GET_TASKS,
    "${A}READ_PRIVILEGED_PHONE_STATE" to R.string.tperm_READ_PRIVILEGED_PHONE_STATE,
    "${A}MODIFY_PHONE_STATE" to R.string.tperm_MODIFY_PHONE_STATE,
    "${A}CALL_PRIVILEGED" to R.string.tperm_CALL_PRIVILEGED,
    "${A}STATUS_BAR" to R.string.tperm_STATUS_BAR,
    "${A}INTERNAL_SYSTEM_WINDOW" to R.string.tperm_INTERNAL_SYSTEM_WINDOW,
    "${A}ACCESS_SURFACE_FLINGER" to R.string.tperm_ACCESS_SURFACE_FLINGER,
    "${A}READ_FRAME_BUFFER" to R.string.tperm_READ_FRAME_BUFFER,
    "${A}CAPTURE_AUDIO_OUTPUT" to R.string.tperm_CAPTURE_AUDIO_OUTPUT,
    "${A}MODIFY_AUDIO_ROUTING" to R.string.tperm_MODIFY_AUDIO_ROUTING,
    "${A}MOUNT_UNMOUNT_FILESYSTEMS" to R.string.tperm_MOUNT_UNMOUNT_FILESYSTEMS,
    "${A}CHANGE_CONFIGURATION" to R.string.tperm_CHANGE_CONFIGURATION,
    "${A}START_ACTIVITIES_FROM_BACKGROUND" to R.string.tperm_START_ACTIVITIES_FROM_BACKGROUND,
    "${A}MANAGE_ACTIVITY_STACKS" to R.string.tperm_MANAGE_ACTIVITY_STACKS,
    "${A}MANAGE_APP_TOKENS" to R.string.tperm_MANAGE_APP_TOKENS,
    "${A}REMOVE_TASKS" to R.string.tperm_REMOVE_TASKS,
    "${A}BROADCAST_CLOSE_SYSTEM_DIALOGS" to R.string.tperm_BROADCAST_CLOSE_SYSTEM_DIALOGS,
    "${A}ACCESS_KEYGUARD_SECURE_STORAGE" to R.string.tperm_ACCESS_KEYGUARD_SECURE_STORAGE,
    "${A}CONNECTIVITY_INTERNAL" to R.string.tperm_CONNECTIVITY_INTERNAL,
    "${A}SET_PREFERRED_APPLICATIONS" to R.string.tperm_SET_PREFERRED_APPLICATIONS,
    "${A}CHANGE_OVERLAY_PACKAGES" to R.string.tperm_CHANGE_OVERLAY_PACKAGES,
    "${A}MODIFY_DAY_NIGHT_MODE" to R.string.tperm_MODIFY_DAY_NIGHT_MODE,
    "${A}MANAGE_LOW_POWER_STANDBY" to R.string.tperm_MANAGE_LOW_POWER_STANDBY,
    "${A}SET_WALLPAPER_COMPONENT" to R.string.tperm_SET_WALLPAPER_COMPONENT,
    "${A}BIND_WALLPAPER" to R.string.tperm_BIND_WALLPAPER,
    "${A}ACCESS_CACHE_FILESYSTEM" to R.string.tperm_ACCESS_CACHE_FILESYSTEM,
    "${A}GRANT_RUNTIME_PERMISSIONS" to R.string.tperm_GRANT_RUNTIME_PERMISSIONS,
    "${A}REVOKE_RUNTIME_PERMISSIONS" to R.string.tperm_REVOKE_RUNTIME_PERMISSIONS,
    "${A}MANAGE_APP_OPS_MODES" to R.string.tperm_MANAGE_APP_OPS_MODES,
    "${A}GET_ACCOUNTS_PRIVILEGED" to R.string.tperm_GET_ACCOUNTS_PRIVILEGED,
    "${A}LOCATION_HARDWARE" to R.string.tperm_LOCATION_HARDWARE,
    // v1.7
    "${A}REORDER_TASKS" to R.string.tperm_REORDER_TASKS,
    "${A}GET_PACKAGE_SIZE" to R.string.tperm_GET_PACKAGE_SIZE,
    "${A}FOREGROUND_SERVICE_SPECIAL_USE" to R.string.tperm_FOREGROUND_SERVICE_SPECIAL_USE,
    "${A}FOREGROUND_SERVICE_DATA_SYNC" to R.string.tperm_FOREGROUND_SERVICE_DATA_SYNC,
    "${A}FOREGROUND_SERVICE_MEDIA_PLAYBACK" to R.string.tperm_FOREGROUND_SERVICE_MEDIA_PLAYBACK,
    "${A}FOREGROUND_SERVICE_CONNECTED_DEVICE" to R.string.tperm_FOREGROUND_SERVICE_CONNECTED_DEVICE,
    "${A}FOREGROUND_SERVICE_PHONE_CALL" to R.string.tperm_FOREGROUND_SERVICE_PHONE_CALL,
    "${A}FOREGROUND_SERVICE_HEALTH" to R.string.tperm_FOREGROUND_SERVICE_HEALTH,
    "${A}FOREGROUND_SERVICE_REMOTE_MESSAGING" to R.string.tperm_FOREGROUND_SERVICE_REMOTE_MESSAGING,
    "${A}FOREGROUND_SERVICE_SYSTEM_EXEMPTED" to R.string.tperm_FOREGROUND_SERVICE_SYSTEM_EXEMPTED,
    "${A}FOREGROUND_SERVICE_MEDIA_PROCESSING" to R.string.tperm_FOREGROUND_SERVICE_MEDIA_PROCESSING,
    "${A}MODIFY_AUDIO_SETTINGS" to R.string.tperm_MODIFY_AUDIO_SETTINGS,
    "${A}READ_SYNC_SETTINGS" to R.string.tperm_READ_SYNC_SETTINGS,
    "${A}WRITE_SYNC_SETTINGS" to R.string.tperm_WRITE_SYNC_SETTINGS,
    "${A}REQUEST_DELETE_PACKAGES" to R.string.tperm_REQUEST_DELETE_PACKAGES,
    "${A}UPDATE_PACKAGES_WITHOUT_USER_ACTION" to R.string.tperm_UPDATE_PACKAGES_WITHOUT_USER_ACTION,
    "${A}CHANGE_WIFI_MULTICAST_STATE" to R.string.tperm_CHANGE_WIFI_MULTICAST_STATE,
    "${A}READ_BASIC_PHONE_STATE" to R.string.tperm_READ_BASIC_PHONE_STATE,
)

fun techHuman(permission: String, s: S): String? =
    TECH_NAMES[permission]?.let { s.str(it) }

/**
 * true  = povolenie dostane len systém alebo appka od toho istého autora
 *         (úroveň signature / signatureOrSystem / internal),
 * false = môže ho dostať aj cudzia appka,
 * null  = telefón také povolenie nepozná (appka ho žiada, ale nikto ho nedefinuje).
 * Údaj pochádza priamo z telefónu, preto sedí na každom zariadení.
 */
fun isSignatureProtected(pm: PackageManager, permission: String): Boolean? = try {
    val info = pm.getPermissionInfo(permission, 0)
    val base = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.protection
               else info.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE
    // 2 = signature, 3 = signatureOrSystem (zastarané), 4 = internal (Android 12+)
    base == 2 || base == 3 || base == 4
} catch (e: PackageManager.NameNotFoundException) {
    null
} catch (e: RuntimeException) {
    null
}

/** Predpona výrobcu = prvé dve časti názvu (com.vivo, bbk.theme, com.google). */
fun vendorPrefix(permission: String): String =
    permission.split('.').take(2).joinToString(".")

/**
 * Všetky texty appky v zvolenom jazyku.
 *
 * Texty samotné sú v res/values-xx/strings.xml. Táto trieda si otvorí
 * „Resources" nastavené na zvolený jazyk — nie na jazyk telefónu. Preto
 * prepínač v Nastaveniach funguje okamžite, bez reštartu obrazovky
 * a bez knižnice AppCompat.
 *
 * Vytvor ju cez remember(lang) { S(context, lang) }, nie pri každom prekreslení.
 */
class S(context: Context, val lang: Lang) {
    private val res = context.createConfigurationContext(
        Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(lang.tag))
        }
    ).resources

    fun str(id: Int): String = res.getString(id)
    private fun f(id: Int, vararg args: Any): String = res.getString(id, *args)
    /** Plurál: správny tvar podľa čísla (1 tracker / 2 trackery / 5 trackerov). */
    private fun q(id: Int, n: Int): String = res.getQuantityString(id, n, n)

    // ── obrazovka so zoznamom ──
    val searchLabel = str(R.string.search_label)
    val clear = str(R.string.clear)
    val scanAll = str(R.string.scan_all)
    val scanningOne = str(R.string.scanning_one)
    val scanFailed = str(R.string.scan_failed)
    val tileApps = str(R.string.tile_apps)
    val tileSystem = str(R.string.tile_system)
    val tileAll = str(R.string.tile_all)
    val tagPreinstalled = str(R.string.tag_preinstalled)
    val originTitle = str(R.string.origin_title)
    val originSystem = str(R.string.origin_system)
    val originPlay = str(R.string.origin_play)
    val originManual = str(R.string.origin_manual)
    fun originOther(store: String) = f(R.string.origin_other, store)
    val openSettings = str(R.string.open_settings)
    val howToRemove = str(R.string.how_to_remove)
    val removeIntro = str(R.string.remove_intro)
    val removeCmd = str(R.string.remove_cmd)
    val restoreCmd = str(R.string.restore_cmd)
    val copy = str(R.string.copy)
    val copied = str(R.string.copied)
    val changesTitle = str(R.string.changes_title)
    val permissionsTitle = str(R.string.permissions_title)
    val noNamedPerms = str(R.string.no_named_perms)

    // Android pri načítaní z XML oreže medzery na začiatku textu,
    // preto oddeľovač „  — " pridávam až tu v kóde.
    private val markGranted = "  — " + str(R.string.mark_granted)
    private val markNotGranted = "  — " + str(R.string.mark_not_granted)
    private val markInstall = "  — " + str(R.string.mark_install)
    private val markSpecial = "  — " + str(R.string.mark_special)

    private val markUnused = "  — " + str(R.string.mark_unused)

    /**
     * @param specialKnown pri špeciálnom povolení: podarilo sa zistiť skutočný
     *        stav (AppOps)? Ak nie, ostáva „povoľuje sa zvlášť".
     * @param unused povolenie na tomto Androide nič neznamená ([isUnusedHere]).
     */
    fun permMark(permission: String, granted: Boolean, specialKnown: Boolean = false, unused: Boolean = false): String {
        if (unused) return markUnused
        return when (PERM_NAMES[permission]?.kind) {
            PermKind.RUNTIME -> if (granted) markGranted else markNotGranted
            PermKind.INSTALL -> markInstall
            PermKind.SPECIAL -> if (specialKnown) (if (granted) markGranted else markNotGranted) else markSpecial
            null -> ""
        }
    }

    fun permIsActive(permission: String, granted: Boolean, specialKnown: Boolean = false, unused: Boolean = false): Boolean {
        if (unused || !granted) return false
        return when (PERM_NAMES[permission]?.kind) {
            PermKind.RUNTIME -> true
            PermKind.SPECIAL -> specialKnown
            else -> false
        }
    }

    fun techPerms(n: Int) = q(R.plurals.tech_perms, n)
    val hideTech = str(R.string.hide_tech)
    val sysOnly = str(R.string.sys_only)
    fun vendorGroup(prefix: String, n: Int, sig: Int) =
        if (sig > 0) f(R.string.vendor_group, prefix, n, sig) else f(R.string.vendor_group_plain, prefix, n)
    val sigNote = str(R.string.sig_note)

    fun scanProgress(done: Int, total: Int) = f(R.string.scan_progress, done, total)
    fun summary(perms: Int, trackers: Int?, noCode: Boolean = false) =
        q(R.plurals.summary_perms, perms) + when {
            trackers == null -> ""
            noCode -> " · " + str(R.string.summary_no_code)
            else -> " · " + q(R.plurals.summary_trackers, trackers)
        }

    // ── úrovne ──
    fun level(l: Level) = when (l) {
        Level.ALERT -> str(R.string.level_alert)
        Level.WARN -> str(R.string.level_warn)
        Level.INFO -> str(R.string.level_info)
        Level.OK -> str(R.string.level_ok)
    }

    // ── nálezy ──
    val noInternet = str(R.string.no_internet)
    val noCode = str(R.string.no_code)
    val jsCode = str(R.string.js_code)
    fun sharedNet(label: String) = f(R.string.shared_net, label)
    val sharedTitle = str(R.string.shared_title)
    fun sharedMore(n: Int) = q(R.plurals.shared_more, n)

    // ── v1.7 ──
    val capsTitle = str(R.string.caps_title)
    val capA11y = str(R.string.cap_a11y)
    val capNotif = str(R.string.cap_notif)
    val capIme = str(R.string.cap_ime)
    val capAdmin = str(R.string.cap_admin)
    val capA11yShort = str(R.string.cap_a11y_short)
    val capNotifShort = str(R.string.cap_notif_short)
    fun capsWarn(list: String) = f(R.string.caps_warn, list)
    val webapkNote = str(R.string.webapk_note)
    val originWebapk = str(R.string.origin_webapk)
    fun changesMore(n: Int) = f(R.string.changes_more, n)
    val changesLess = str(R.string.changes_less)
    // ── v1.8 ──
    val navCheck = str(R.string.nav_check)
    val navByPerm = str(R.string.nav_byperm)
    val checkTitle = str(R.string.check_title)
    val checkNote = str(R.string.check_note)
    fun checkPatchOk(d: String) = f(R.string.check_patch_ok, d)
    fun checkPatchOld(d: String) = f(R.string.check_patch_old, d)
    val checkPatchUnknown = str(R.string.check_patch_unknown)
    val checkLockOk = str(R.string.check_lock_ok)
    val checkLockNo = str(R.string.check_lock_no)
    val checkAdbOn = str(R.string.check_adb_on)
    val checkDevOn = str(R.string.check_dev_on)
    val checkDevOff = str(R.string.check_dev_off)
    val checkInstallNone = str(R.string.check_install_none)
    fun checkInstallSome(names: String) = f(R.string.check_install_some, names)
    val checkCapsNone = str(R.string.check_caps_none)
    fun checkCapsSome(names: String) = f(R.string.check_caps_some, names)
    val checkRootNone = str(R.string.check_root_none)
    fun checkRootFound(what: String) = f(R.string.check_root_found, what)
    val checkOpen = str(R.string.check_open)
    val byPermTitle = str(R.string.byperm_title)
    fun byPermScope(scope: String) = f(R.string.byperm_scope, scope)
    val byPermNone = str(R.string.byperm_none)
    val capImeShort = str(R.string.cap_ime_short)
    val capAdminShort = str(R.string.cap_admin_short)
    val uninstall = str(R.string.uninstall)
    val sortLabel = str(R.string.sort_label)
    fun sortName(m: SortMode) = when (m) {
        SortMode.SEVERITY -> str(R.string.sort_severity)
        SortMode.NAME -> str(R.string.sort_name)
        SortMode.FINDINGS -> str(R.string.sort_findings)
    }
    // v1.9: menu Zoradiť a filtrovať
    val menuSort = str(R.string.menu_sort)
    val menuShow = str(R.string.menu_show)
    val filterLabel = str(R.string.filter_label)
    val filterNone = str(R.string.filter_none)
    fun filterUnscanned(n: Int) = q(R.plurals.filter_unscanned, n)
    fun filterName(f: ListFilter) = when (f) {
        ListFilter.ALL -> str(R.string.filter_all)
        ListFilter.WARNING -> str(R.string.filter_warning)
        ListFilter.ADS -> str(R.string.filter_ads)
        ListFilter.TRACKERS -> str(R.string.filter_trackers)
        ListFilter.CAPS -> str(R.string.filter_caps)
        ListFilter.MIC -> str(R.string.filter_mic)
        ListFilter.LOCATION -> str(R.string.filter_location)
        ListFilter.CAMERA -> str(R.string.filter_camera)
        ListFilter.FILES -> str(R.string.filter_files)
        ListFilter.PARTIAL_CODE -> str(R.string.filter_partial)
        ListFilter.NON_PLAY -> str(R.string.filter_nonplay)
    }
    fun marketingWithGranted(granted: String, names: String) = f(R.string.marketing_with_granted, granted, names)
    fun marketingOnly(names: String) = f(R.string.marketing_only, names)
    fun analyticsWithGranted(names: String, granted: String) = f(R.string.analytics_with_granted, names, granted)
    fun analyticsOnly(names: String) = f(R.string.analytics_only, names)
    fun fraud(names: String) = f(R.string.fraud, names)
    fun crashOnly(names: String) = f(R.string.crash_only, names)
    fun tooMany(n: Int) = f(R.string.too_many, n)
    val noTrackers = str(R.string.no_trackers)
    fun noTrackersButGranted(granted: String) = f(R.string.no_trackers_but_granted, granted)
    fun pending(text: String) = f(R.string.pending, text)
    val rtbNote = str(R.string.rtb_note)

    // ── log zmien ──
    fun addedTrackers(list: String) = f(R.string.change_trackers_added, list)
    fun removedTrackers(list: String) = f(R.string.change_trackers_removed, list)
    fun addedPerms(list: String) = f(R.string.change_perms_added, list)
    fun removedPerms(list: String) = f(R.string.change_perms_removed, list)
    fun techList(list: String) = f(R.string.change_tech, list)

    // ── nastavenia ──
    val settings = str(R.string.settings)
    val back = str(R.string.back)
    val languageTitle = str(R.string.language_title)
    val dataTitle = str(R.string.data_title)
    val clearChanges = str(R.string.clear_changes)
    val clearScans = str(R.string.clear_scans)
    val clearScansNote = str(R.string.clear_scans_note)
    val showIntro = str(R.string.show_intro)
    val cleared = str(R.string.cleared)
    val aboutTitle = str(R.string.about_title)
    val aboutText = str(R.string.about_text)
    val privacyPolicy = str(R.string.privacy_policy)
    val reportLink = str(R.string.report_link)
    val reportNote = str(R.string.report_note)
    val dnsTitle = str(R.string.dns_title)
    fun dnsOn(host: String) = f(R.string.dns_on, host)
    val dnsAuto = str(R.string.dns_auto)
    val dnsOff = str(R.string.dns_off)
    val dnsUnknown = str(R.string.dns_unknown)
    val dnsGuide = str(R.string.dns_guide)
    val dnsTradeoff = str(R.string.dns_tradeoff)
    val dnsOpen = str(R.string.dns_open)
    val dnsRecheck = str(R.string.dns_recheck)

    // ── úvodná obrazovka ──
    val claims = str(R.string.claims)
    val disclosure = str(R.string.disclosure)
    val tapToContinue = str(R.string.tap_to_continue)
    val notifyBadge = str(R.string.perm_notify_badge)
    val partOf = str(R.string.part_of)
}
