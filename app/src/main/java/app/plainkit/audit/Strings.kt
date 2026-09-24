package app.plainkit.audit

import android.content.Context
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
}

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
    "${A}REQUEST_INSTALL_PACKAGES" to PermName(R.string.perm_REQUEST_INSTALL_PACKAGES, PermKind.SPECIAL),
    "${A}PACKAGE_USAGE_STATS" to PermName(R.string.perm_PACKAGE_USAGE_STATS, PermKind.SPECIAL),
    "${A}SCHEDULE_EXACT_ALARM" to PermName(R.string.perm_SCHEDULE_EXACT_ALARM, PermKind.SPECIAL),
    "${A}REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" to PermName(R.string.perm_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, PermKind.SPECIAL),

    "${A}QUERY_ALL_PACKAGES" to PermName(R.string.perm_QUERY_ALL_PACKAGES, PermKind.INSTALL),
    "${A}RECEIVE_BOOT_COMPLETED" to PermName(R.string.perm_RECEIVE_BOOT_COMPLETED, PermKind.INSTALL),
    "${A}DETECT_SCREEN_CAPTURE" to PermName(R.string.perm_DETECT_SCREEN_CAPTURE, PermKind.INSTALL),
    "${A}DOWNLOAD_WITHOUT_NOTIFICATION" to PermName(R.string.perm_DOWNLOAD_WITHOUT_NOTIFICATION, PermKind.INSTALL),
    "com.google.android.gms.permission.AD_ID" to PermName(R.string.perm_AD_ID, PermKind.INSTALL)
)

fun permLabel(permission: String, s: S): String? =
    PERM_NAMES[permission]?.let { s.str(it.res) }

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

    // ── obrazovka so zoznamom ──
    val searchLabel = str(R.string.search_label)
    val clear = str(R.string.clear)
    val scanAll = str(R.string.scan_all)
    val scanningOne = str(R.string.scanning_one)
    val scanFailed = str(R.string.scan_failed)
    val tileApps = str(R.string.tile_apps)
    val tileSystem = str(R.string.tile_system)
    val changesTitle = str(R.string.changes_title)
    val permissionsTitle = str(R.string.permissions_title)
    val noNamedPerms = str(R.string.no_named_perms)

    // Android pri načítaní z XML oreže medzery na začiatku textu,
    // preto oddeľovač „  — " pridávam až tu v kóde.
    private val markGranted = "  — " + str(R.string.mark_granted)
    private val markNotGranted = "  — " + str(R.string.mark_not_granted)
    private val markInstall = "  — " + str(R.string.mark_install)
    private val markSpecial = "  — " + str(R.string.mark_special)

    fun permMark(permission: String, granted: Boolean): String =
        when (PERM_NAMES[permission]?.kind) {
            PermKind.RUNTIME -> if (granted) markGranted else markNotGranted
            PermKind.INSTALL -> markInstall
            PermKind.SPECIAL -> markSpecial
            null -> ""
        }

    fun permIsActive(permission: String, granted: Boolean): Boolean =
        PERM_NAMES[permission]?.kind == PermKind.RUNTIME && granted

    fun techPerms(n: Int) = f(R.string.tech_perms, n)
    val hideTech = str(R.string.hide_tech)

    fun scanProgress(done: Int, total: Int) = f(R.string.scan_progress, done, total)
    fun summary(perms: Int, trackers: Int?) =
        f(R.string.summary_perms, perms) +
                if (trackers != null) " · " + f(R.string.summary_trackers, trackers) else ""

    // ── úrovne ──
    fun level(l: Level) = when (l) {
        Level.ALERT -> str(R.string.level_alert)
        Level.WARN -> str(R.string.level_warn)
        Level.INFO -> str(R.string.level_info)
        Level.OK -> str(R.string.level_ok)
    }

    // ── nálezy ──
    val noInternet = str(R.string.no_internet)
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

    // ── úvodná obrazovka ──
    val claims = str(R.string.claims)
    val disclosure = str(R.string.disclosure)
    val tapToContinue = str(R.string.tap_to_continue)
    val partOf = str(R.string.part_of)
}
