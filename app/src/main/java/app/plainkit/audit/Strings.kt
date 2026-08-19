package app.plainkit.audit

import android.content.Context

enum class Lang { SK, EN }

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

data class PermName(val sk: String, val en: String, val kind: PermKind)

val PERM_NAMES: Map<String, PermName> = mapOf(
    "android.permission.ACCESS_FINE_LOCATION" to PermName("presná poloha", "precise location", PermKind.RUNTIME),
    "android.permission.ACCESS_COARSE_LOCATION" to PermName("približná poloha", "approximate location", PermKind.RUNTIME),
    "android.permission.ACCESS_BACKGROUND_LOCATION" to PermName("poloha aj na pozadí", "location in the background", PermKind.RUNTIME),
    "android.permission.RECORD_AUDIO" to PermName("mikrofón", "microphone", PermKind.RUNTIME),
    "android.permission.CAMERA" to PermName("kamera", "camera", PermKind.RUNTIME),
    "android.permission.READ_CONTACTS" to PermName("čítanie kontaktov", "read contacts", PermKind.RUNTIME),
    "android.permission.WRITE_CONTACTS" to PermName("úprava kontaktov", "modify contacts", PermKind.RUNTIME),
    "android.permission.GET_ACCOUNTS" to PermName("zoznam účtov v telefóne", "accounts on the device", PermKind.RUNTIME),
    "android.permission.READ_SMS" to PermName("čítanie SMS", "read SMS", PermKind.RUNTIME),
    "android.permission.RECEIVE_SMS" to PermName("príjem SMS", "receive SMS", PermKind.RUNTIME),
    "android.permission.SEND_SMS" to PermName("odosielanie SMS", "send SMS", PermKind.RUNTIME),
    "android.permission.READ_CALL_LOG" to PermName("zoznam hovorov", "call log", PermKind.RUNTIME),
    "android.permission.WRITE_CALL_LOG" to PermName("úprava zoznamu hovorov", "modify call log", PermKind.RUNTIME),
    "android.permission.CALL_PHONE" to PermName("volanie bez opýtania", "place calls directly", PermKind.RUNTIME),
    "android.permission.ANSWER_PHONE_CALLS" to PermName("dvíhanie hovorov", "answer phone calls", PermKind.RUNTIME),
    "android.permission.READ_PHONE_STATE" to PermName("stav telefónu", "phone status", PermKind.RUNTIME),
    "android.permission.READ_PHONE_NUMBERS" to PermName("tvoje telefónne číslo", "your phone number", PermKind.RUNTIME),
    "android.permission.READ_CALENDAR" to PermName("čítanie kalendára", "read calendar", PermKind.RUNTIME),
    "android.permission.WRITE_CALENDAR" to PermName("úprava kalendára", "modify calendar", PermKind.RUNTIME),
    "android.permission.READ_EXTERNAL_STORAGE" to PermName("čítanie súborov", "read files", PermKind.RUNTIME),
    "android.permission.WRITE_EXTERNAL_STORAGE" to PermName("zápis do súborov", "write files", PermKind.RUNTIME),
    "android.permission.READ_MEDIA_IMAGES" to PermName("fotky", "photos", PermKind.RUNTIME),
    "android.permission.READ_MEDIA_VIDEO" to PermName("videá", "videos", PermKind.RUNTIME),
    "android.permission.READ_MEDIA_AUDIO" to PermName("hudba a zvuky", "music and audio", PermKind.RUNTIME),
    "android.permission.ACTIVITY_RECOGNITION" to PermName("pohybová aktivita", "physical activity", PermKind.RUNTIME),
    "android.permission.BODY_SENSORS" to PermName("telesné senzory", "body sensors", PermKind.RUNTIME),
    "android.permission.BLUETOOTH_CONNECT" to PermName("pripájanie Bluetooth zariadení", "connect Bluetooth devices", PermKind.RUNTIME),
    "android.permission.BLUETOOTH_SCAN" to PermName("vyhľadávanie Bluetooth zariadení", "scan for Bluetooth devices", PermKind.RUNTIME),
    "android.permission.NEARBY_WIFI_DEVICES" to PermName("zariadenia v okolí cez Wi-Fi", "nearby Wi-Fi devices", PermKind.RUNTIME),
    "android.permission.POST_NOTIFICATIONS" to PermName("notifikácie", "notifications", PermKind.RUNTIME),

    "android.permission.MANAGE_EXTERNAL_STORAGE" to PermName("prístup ku všetkým súborom", "access all files", PermKind.SPECIAL),
    "android.permission.SYSTEM_ALERT_WINDOW" to PermName("kreslenie cez iné aplikácie", "draw over other apps", PermKind.SPECIAL),
    "android.permission.REQUEST_INSTALL_PACKAGES" to PermName("inštalovanie aplikácií", "install apps", PermKind.SPECIAL),
    "android.permission.PACKAGE_USAGE_STATS" to PermName("štatistiky používania aplikácií", "app usage statistics", PermKind.SPECIAL),
    "android.permission.SCHEDULE_EXACT_ALARM" to PermName("presne načasované budíky", "exact alarms", PermKind.SPECIAL),
    "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" to PermName("beh na pozadí bez obmedzení", "unrestricted background activity", PermKind.SPECIAL),

    "android.permission.QUERY_ALL_PACKAGES" to PermName("zoznam všetkých aplikácií", "list of all installed apps", PermKind.INSTALL),
    "android.permission.RECEIVE_BOOT_COMPLETED" to PermName("spustenie po zapnutí telefónu", "start on device boot", PermKind.INSTALL),
    "android.permission.DETECT_SCREEN_CAPTURE" to PermName("zisťovanie snímok obrazovky", "detect screenshots", PermKind.INSTALL),
    "com.google.android.gms.permission.AD_ID" to PermName("reklamný identifikátor", "advertising ID", PermKind.INSTALL)
)

fun permLabel(permission: String, s: S): String? =
    PERM_NAMES[permission]?.let { if (s.lang == Lang.SK) it.sk else it.en }

class S(val lang: Lang) {
    private val sk = lang == Lang.SK
    private fun t(s: String, e: String) = if (sk) s else e

    // ── obrazovka so zoznamom ──
    val loading = t("Načítavam…", "Loading…")
    val searchLabel = t("Hľadať aplikáciu", "Search apps")
    val clear = t("Zrušiť", "Clear")
    val showSystem = t("Zobraziť systémové", "Show system apps")
    val scanAll = t("Skenovať všetky", "Scan all")
    val scanningOne = t("Skenujem…", "Scanning…")
    val changesTitle = t("ČO SA ZMENILO", "WHAT CHANGED")
    val permissionsTitle = t("ČO APPKA MÔŽE", "WHAT THE APP CAN ACCESS")
    val noNamedPerms = t(
        "Nežiada nič, čo by sa dalo zrozumiteľne pomenovať.",
        "It requests nothing that can be named in plain language."
    )

    private val markGranted = t("  — UDELENÉ", "  — GRANTED")
    private val markNotGranted = t("  — neudelené", "  — not granted")
    private val markInstall = t("  — automaticky pri inštalácii", "  — automatic at install")
    private val markSpecial = t("  — povoľuje sa zvlášť", "  — enabled separately")

    fun permMark(permission: String, granted: Boolean): String =
        when (PERM_NAMES[permission]?.kind) {
            PermKind.RUNTIME -> if (granted) markGranted else markNotGranted
            PermKind.INSTALL -> markInstall
            PermKind.SPECIAL -> markSpecial
            null -> ""
        }

    fun permIsActive(permission: String, granted: Boolean): Boolean =
        PERM_NAMES[permission]?.kind == PermKind.RUNTIME && granted

    fun techPerms(n: Int) = t("+ $n technických povolení", "+ $n technical permissions")
    val hideTech = t("skryť technické povolenia", "hide technical permissions")

    fun appsCount(n: Int) = t("$n aplikácií", "$n apps")
    fun someOf(n: Int, total: Int) = t("$n z $total", "$n of $total")
    fun scanProgress(done: Int, total: Int) =
        t("Skenujem $done/$total…", "Scanning $done/$total…")
    fun summary(perms: Int, trackers: Int?) =
        t("$perms povolení", "$perms permissions") +
                if (trackers != null) t(" · $trackers trackerov", " · $trackers trackers") else ""

    // ── úrovne ──
    fun level(l: Level) = when (l) {
        Level.ALERT -> t("POZOR", "WARNING")
        Level.WARN -> t("VŠIMNI SI", "NOTICE")
        Level.INFO -> t("INFO", "INFO")
        Level.OK -> t("OK", "OK")
    }

    // ── nálezy ──
    val noInternet = t(
        "Nežiada prístup na internet — nemá kam nič odosielať.",
        "Does not request internet access — it has nowhere to send anything."
    )

    fun marketingWithGranted(granted: String, names: String) = t(
        "Má udelený prístup k $granted a zároveň obsahuje reklamné/marketingové SDK " +
                "($names). Práve tu môžu citlivé údaje skončiť v reklamnej sieti.",
        "Has been granted access to $granted and also contains advertising/marketing SDKs " +
                "($names). This is where sensitive data can end up in an ad network."
    )

    fun marketingOnly(names: String) = t(
        "Obsahuje reklamné/marketingové SDK ($names). Tvoje správanie v appke sa spája " +
                "s reklamným profilom.",
        "Contains advertising/marketing SDKs ($names). Your behaviour in the app is tied " +
                "to an advertising profile."
    )

    fun analyticsWithGranted(names: String, granted: String) = t(
        "Obsahuje analytické SDK ($names) a má udelený prístup k $granted. Analytika tieto " +
                "údaje sama neodosiela, ale appka ich má k dispozícii — kombinácia stojí za pozornosť.",
        "Contains analytics SDKs ($names) and has been granted access to $granted. Analytics " +
                "does not send this data by itself, but the app has it available — the combination " +
                "is worth noticing."
    )

    fun analyticsOnly(names: String) = t(
        "Obsahuje analytické SDK ($names). Odosiela, čo v appke robíš — nie nutne obsah, " +
                "ale správanie.",
        "Contains analytics SDKs ($names). It reports what you do in the app — not necessarily " +
                "the content, but the behaviour."
    )

    fun fraud(names: String) = t(
        "Obsahuje nástroje na detekciu podvodov ($names). Zbierajú signály o zariadení " +
                "a správaní, ale účelom je ochrana účtu, nie reklama.",
        "Contains fraud-detection tools ($names). They collect device and behaviour signals, " +
                "but the purpose is account protection, not advertising."
    )

    fun crashOnly(names: String) = t(
        "Obsahuje len hlásenie pádov ($names). Bežná vývojárska prax, nie sledovanie na reklamu.",
        "Contains only crash reporting ($names). Common development practice, not ad tracking."
    )

    fun tooMany(n: Int) = t(
        "Obsahuje $n reklamných a analytických knižníc od rôznych firiem — nadpriemerne veľa.",
        "Contains $n advertising and analytics libraries from different companies — " +
                "above average."
    )

    val noTrackers = t("Žiadne známe trackery.", "No known trackers.")

    fun noTrackersButGranted(granted: String) = t(
        "Má udelený prístup k $granted, ale neobsahuje žiadny známy tracker.",
        "Has been granted access to $granted, but contains no known tracker."
    )

    fun pending(text: String) = t(
        "Žiada aj prístup k $text, ten však zatiaľ udelený nemá. Ak mu ho povolíš, " +
                "posúdenie sa zmení.",
        "It also requests access to $text, but has not been granted it. If you allow it, " +
                "this assessment will change."
    )

    // ── log zmien ──
    fun addedTrackers(list: String) = t("pribudli trackery: $list", "trackers added: $list")
    fun removedTrackers(list: String) = t("ubudli trackery: $list", "trackers removed: $list")
    fun addedPerms(list: String) = t("pribudli povolenia: $list", "permissions added: $list")
    fun removedPerms(list: String) = t("ubudli povolenia: $list", "permissions removed: $list")

    // ── nastavenia ──
    val settings = t("Nastavenia", "Settings")
    val back = t("Späť", "Back")
    val languageTitle = t("JAZYK", "LANGUAGE")
    val slovak = "Slovensky"
    val english = "English"
    val dataTitle = t("ÚDAJE", "DATA")
    val clearChanges = t("Vymazať log zmien", "Clear change log")
    val clearScans = t("Vymazať výsledky skenov", "Clear scan results")
    val clearScansNote = t(
        "Zmaže aj základňu na porovnávanie — po ňom prvý sken nemá s čím porovnávať.",
        "This also deletes the comparison baseline — after it, the first scan has nothing " +
                "to compare against."
    )
    val showIntro = t("Znova zobraziť úvodnú obrazovku", "Show the intro screen again")
    val cleared = t("Vymazané.", "Cleared.")
    val aboutTitle = t("O APLIKÁCII", "ABOUT")
    val aboutText = t(
        "Celá analýza prebieha v telefóne. Appka číta nainštalované aplikácie, hľadá v ich " +
                "kóde známe sledovacie knižnice a spája to s povoleniami. Nič neodosiela.\n\n" +
                "Appka hovorí, čo je v kóde prítomné — nie čo sa práve deje. Zoznam signatúr " +
                "nie je úplný, takže skôr podhodnocuje, než preháňa.",
        "The whole analysis runs on your phone. The app reads installed applications, looks " +
                "for known tracking libraries in their code and combines that with permissions. " +
                "Nothing is uploaded.\n\n" +
                "The app reports what is present in the code — not what is happening right now. " +
                "The signature list is not complete, so it under-reports rather than exaggerates."
    )
    val privacyPolicy = t("Zásady ochrany súkromia", "Privacy policy")

    // ── úvodná obrazovka ──
    val claims = t(
        "po slovensky · bez reklám · bez účtu · nič neodosiela",
        "no ads · no signup · nothing is uploaded"
    )
    val disclosure = t(
        "Aby appka mohla skontrolovať ostatné aplikácie, potrebuje vidieť ich zoznam. " +
                "Celá analýza prebieha v telefóne. Nič sa neodosiela.",
        "To check your other apps, this app needs to see the list of installed packages. " +
                "The whole analysis runs on your phone. Nothing is uploaded."
    )
    val tapToContinue = t("ťukni pre pokračovanie", "tap to continue")
    val partOf = "súčasť projektu plainkit.app"
}