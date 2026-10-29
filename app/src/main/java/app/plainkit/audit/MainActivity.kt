package app.plainkit.audit

import android.app.AppOpsManager
import androidx.core.app.NotificationManagerCompat
import android.view.inputmethod.InputMethodManager
import android.view.accessibility.AccessibilityManager
import android.content.ComponentName
import android.app.admin.DevicePolicyManager
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.PowerManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.telecom.TelecomManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import app.plainkit.audit.ui.theme.AuditTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipFile

private const val MAX_DEPTH = 6

private val OkGreen = Color(0xFF3FBF7F)
private val WarnAmber = Color(0xFFFFB020)

/** Čo sa zobrazuje a skenuje: len nainštalované, len systémové, alebo všetko. */
enum class ListScope { APPS, SYSTEM, ALL }

internal fun AppEntry.inScope(scope: ListScope): Boolean = when (scope) {
    ListScope.APPS -> !isSystem
    ListScope.SYSTEM -> isSystem
    ListScope.ALL -> true
}

private fun installerOf(pm: PackageManager, pkg: String): String? = runCatching {
    if (Build.VERSION.SDK_INT >= 30) pm.getInstallSourceInfo(pkg).installingPackageName
    else @Suppress("DEPRECATION") pm.getInstallerPackageName(pkg)
}.getOrNull()

/**
 * Odkiaľ appka prišla. Null = nevieme to povedať spoľahlivo, a vtedy nič netvrdíme.
 *
 * FLAG_SYSTEM = je v systémovom obraze, teda prišla s telefónom (aj keď ju
 * neskôr aktualizoval Obchod Play). Pri ostatných rozhoduje inštalátor:
 * Play, systémový inštalátor balíkov (= ručne zo súboru APK), alebo iný obchod.
 * Prázdny inštalátor NIE JE „ručne": tak vyzerajú aj appky, ktoré výrobca
 * predinštaloval mimo systému, alebo inštalácia cez ADB.
 */
private fun originText(pm: PackageManager, app: AppEntry, s: S): String? {
    if (app.isWebApk) return s.originWebapk
    if (app.isSystem) return s.originSystem
    val inst = installerOf(pm, app.packageName) ?: return null
    return when {
        inst == "com.android.vending" -> s.originPlay
        inst.contains("packageinstaller") -> s.originManual
        else -> s.originOther(
            runCatching { pm.getApplicationLabel(pm.getApplicationInfo(inst, 0)).toString() }
                .getOrDefault(inst)
        )
    }
}

/**
 * Súčasti systému, pri ktorých appka návod na odstránenie neponúkne —
 * ich odstránenie môže telefón zablokovať. Spolu s nimi aj aktuálny launcher,
 * telefón a SMS (zisťujú sa za behu, líšia sa podľa výrobcu).
 */
private val CRITICAL_PACKAGES = setOf(
    "android", "com.android.systemui", "com.android.phone", "com.android.settings",
    "com.android.vending", "com.google.android.gms", "com.google.android.gsf",
    "com.android.shell", "com.android.packageinstaller", "com.google.android.packageinstaller",
    "com.android.permissioncontroller", "com.google.android.permissioncontroller",
    "com.android.inputmethod.latin", "com.google.android.inputmethod.latin",
    "com.android.bluetooth", "com.android.nfc", "com.android.server.telecom",
    "com.android.networkstack", "com.google.android.networkstack",
    "com.android.webview", "com.google.android.webview", "com.android.se",
    "com.android.location.fused", "com.android.externalstorage", "com.android.documentsui",
    "com.android.keychain", "com.android.certinstaller", "com.android.emergency",
)

private fun criticalRuntimePackages(ctx: Context): Set<String> = buildSet {
    runCatching {
        ctx.packageManager.resolveActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName
    }.getOrNull()?.let { add(it) }
    runCatching { Telephony.Sms.getDefaultSmsPackage(ctx) }.getOrNull()?.let { add(it) }
    runCatching { ctx.getSystemService(TelecomManager::class.java)?.defaultDialerPackage }
        .getOrNull()?.let { add(it) }
    add(ctx.packageName)
}

private fun isCritical(pkg: String, runtime: Set<String>): Boolean =
    pkg in CRITICAL_PACKAGES || pkg in runtime || pkg.startsWith("com.android.providers.")

object AppCache {
    var apps: List<AppEntry>? = null
    /** v1.7: počty z rozhraní mimoriadnych schopností (ladenie). */
    var capsDebug: String = ""
    val scanned = mutableMapOf<String, List<Tracker>>()
}

data class AppEntry(
    val label: String,
    val packageName: String,
    val isSystem: Boolean,
    val permissions: List<String>,
    val granted: Set<String>,
    val apkPaths: List<String>,
    val versionCode: Long,
    // ── v1.6 ──
    val targetSdk: Int = 0,
    /** Špeciálne povolenia, ktorých skutočný stav sa podarilo zistiť (AppOps / PowerManager). */
    val specialKnown: Set<String> = emptySet(),
    /** Surová odpoveď AppOps (len na ladenie, zobrazuje sa v debug builde). */
    val specialModes: Map<String, Int> = emptyMap(),
    /** Ostatné appky s tou istou identitou (sharedUserId) — zdieľajú povolenia aj dáta. */
    val sharedWith: List<String> = emptyList(),
    /** Meno appky z tej istej identity, ktorá má INTERNET (null = žiadna). */
    val sharedNet: String? = null,
    // ── v1.7 ──
    /** Zapnuté mimoriadne schopnosti (služba dostupnosti, notifikácie, klávesnica, správca). */
    val caps: Set<Cap> = emptySet(),
    /** Webová stránka uložená na plochu cez Chrome (WebAPK) — beží v prehliadači. */
    val isWebApk: Boolean = false
)

/** v1.7: mimoriadne schopnosti — nie sú to bežné povolenia, zapína ich používateľ (alebo výrobca) v nastaveniach. */
enum class Cap { A11Y, NOTIF, IME, ADMIN }

/**
 * v1.7: ktoré appky majú zapnuté mimoriadne schopnosti. Všetko cez verejné
 * rozhrania Androidu, bez nového povolenia. Zisťuje sa raz pri načítaní zoznamu.
 * `debug` = počty z jednotlivých zdrojov (len na ladenie, ukazuje sa v debug builde).
 */
class CapsInfo(val byPkg: Map<String, Set<Cap>>, val debug: String)

private fun loadCaps(ctx: Context): CapsInfo {
    fun <T> safe(block: () -> T?): T? = try { block() } catch (e: Exception) { null }
    val a11yApi = safe {
        ctx.getSystemService(AccessibilityManager::class.java)
            ?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            ?.mapNotNull { it.resolveInfo?.serviceInfo?.packageName }?.toSet() ?: emptySet()
    }
    // Záloha: verejný kľúč nastavení so zoznamom zapnutých služieb dostupnosti.
    // (null výsledok = nič zapnuté → prázdna množina; „x" v ladení = rozhranie hodilo výnimku)
    val a11ySettings = safe {
        Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?.split(':')?.mapNotNull { ComponentName.unflattenFromString(it)?.packageName }?.toSet() ?: emptySet()
    }
    val notif = safe { NotificationManagerCompat.getEnabledListenerPackages(ctx) }
    val ime = safe {
        ctx.getSystemService(InputMethodManager::class.java)?.enabledInputMethodList?.map { it.packageName }?.toSet() ?: emptySet()
    }
    val admin = safe {
        ctx.getSystemService(DevicePolicyManager::class.java)?.activeAdmins?.map { it.packageName }?.toSet() ?: emptySet()
    }
    val map = HashMap<String, MutableSet<Cap>>()
    fun add(pkgs: Set<String>?, cap: Cap) = pkgs?.forEach { map.getOrPut(it) { mutableSetOf() }.add(cap) }
    add(a11yApi, Cap.A11Y); add(a11ySettings, Cap.A11Y)
    add(notif, Cap.NOTIF); add(ime, Cap.IME); add(admin, Cap.ADMIN)
    val dbg = "a11y=${a11yApi?.size ?: "x"}/${a11ySettings?.size ?: "x"} notif=${notif?.size ?: "x"} " +
            "ime=${ime?.size ?: "x"} admin=${admin?.size ?: "x"}"
    return CapsInfo(map, dbg)
}

private fun codeOf(pkg: PackageInfo): Long =
    if (Build.VERSION.SDK_INT >= 28) pkg.longVersionCode
    else @Suppress("DEPRECATION") pkg.versionCode.toLong()

private fun loadApps(ctx: Context): List<AppEntry> {
    val pm = ctx.packageManager
    val uidCache = HashMap<Int, Pair<List<String>, String?>>()
    val caps = loadCaps(ctx)
    AppCache.capsDebug = caps.debug
    return pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        .map { toEntry(it, ctx, uidCache, caps) }
        .sortedBy { it.label.lowercase() }
}

/**
 * Čerstvé údaje o jednej appke priamo zo systému. Null = medzitým ju odinštalovali.
 * Prečo: AppCache drží zoznam, kým Android nezabije proces — aj niekoľko dní.
 * Keď sa medzitým iná appka aktualizuje, jej stará cesta k APK už neexistuje
 * a aj zoznam povolení je starý.
 */
private fun loadApp(ctx: Context, packageName: String): AppEntry? = runCatching {
    toEntry(ctx.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS), ctx, HashMap(), loadCaps(ctx))
}.getOrNull()

/**
 * Appky s tou istou identitou (sharedUserId) a meno prvej z nich, ktorá má INTERNET.
 * Zdieľajú všetky povolenia aj súkromné dáta — appka bez INTERNET sa tak
 * môže dostať na internet cez „sestru" z tej istej skupiny.
 */
private fun sharedInfo(pm: PackageManager, uid: Int): Pair<List<String>, String?> {
    val pkgs = runCatching { pm.getPackagesForUid(uid)?.toList() }.getOrNull() ?: emptyList()
    if (pkgs.size < 2) return Pair(pkgs, null)
    val net = pkgs.firstOrNull { other ->
        runCatching {
            pm.getPackageInfo(other, PackageManager.GET_PERMISSIONS)
                .requestedPermissions?.contains("android.permission.INTERNET") == true
        }.getOrDefault(false)
    }
    val netLabel = net?.let { n ->
        runCatching { pm.getApplicationLabel(pm.getApplicationInfo(n, 0)).toString() }.getOrDefault(n)
    }
    return Pair(pkgs, netLabel)
}

/** Surový stav špeciálnych povolení z AppOps; -1 = Android odpoveď odmietol. */
private fun specialModes(ctx: Context, uid: Int, pkg: String, perms: List<String>): Map<String, Int> {
    val ops = ctx.getSystemService(AppOpsManager::class.java) ?: return emptyMap()
    return buildMap {
        for (p in perms) {
            val op = SPECIAL_OPS[p] ?: continue
            val mode = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ops.unsafeCheckOpNoThrow(op, uid, pkg)
                else @Suppress("DEPRECATION") ops.checkOpNoThrow(op, uid, pkg)
            } catch (e: Exception) {
                -1
            }
            put(p, mode)
        }
    }
}

private fun toEntry(
    pkg: PackageInfo,
    ctx: Context,
    uidCache: MutableMap<Int, Pair<List<String>, String?>>,
    caps: CapsInfo
): AppEntry {
    val pm = ctx.packageManager
    val info = pkg.applicationInfo
    val paths = buildList {
        info?.sourceDir?.let { add(it) }
        info?.splitSourceDirs?.forEach { add(it) }
    }
    val perms = pkg.requestedPermissions?.toList() ?: emptyList()
    val flags = pkg.requestedPermissionsFlags
    val granted = buildSet {
        if (flags != null) {
            for (idx in perms.indices) {
                if (idx < flags.size &&
                    (flags[idx] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                ) {
                    add(perms[idx])
                }
            }
        }
    }

    // ── v1.6: skutočný stav špeciálnych povolení ──
    // Pravidlo Androidu: ALLOWED = áno, IGNORED/ERRORED = nie, DEFAULT = rozhodne
    // samotné povolenie (príznak GRANTED). Keď Android odpoveď odmietne (-1),
    // stav nevieme a ostane „povoľuje sa zvlášť".
    val uid = info?.uid ?: -1
    val modes = if (uid >= 0) specialModes(ctx, uid, pkg.packageName, perms) else emptyMap()
    val known = mutableSetOf<String>()
    val finalGranted = granted.toMutableSet()
    for (p in perms) {
        if (PERM_NAMES[p]?.kind != PermKind.SPECIAL) continue
        val flagGranted = p in granted
        val state: Boolean? = when {
            p == BATTERY_PERM -> runCatching {
                ctx.getSystemService(PowerManager::class.java)?.isIgnoringBatteryOptimizations(pkg.packageName)
            }.getOrNull()
            p in modes -> when (modes[p]) {
                AppOpsManager.MODE_ALLOWED -> true
                AppOpsManager.MODE_IGNORED, AppOpsManager.MODE_ERRORED -> false
                AppOpsManager.MODE_DEFAULT -> flagGranted
                else -> null
            }
            else -> null
        }
        finalGranted.remove(p)
        if (state != null) {
            known.add(p)
            if (state) finalGranted.add(p)
        }
    }

    val (group, netLabel) = if (uid >= 0) uidCache.getOrPut(uid) { sharedInfo(pm, uid) }
                            else Pair(emptyList(), null)

    return AppEntry(
        label = info?.loadLabel(pm)?.toString() ?: pkg.packageName,
        packageName = pkg.packageName,
        isSystem = ((info?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM) != 0,
        permissions = perms,
        granted = finalGranted,
        apkPaths = paths,
        versionCode = codeOf(pkg),
        targetSdk = info?.targetSdkVersion ?: 0,
        specialKnown = known,
        specialModes = modes,
        sharedWith = group.filter { it != pkg.packageName },
        sharedNet = netLabel,
        caps = caps.byPkg[pkg.packageName] ?: emptySet(),
        isWebApk = pkg.packageName.startsWith("org.chromium.webapk.")
    )
}

/**
 * Triedy, ktoré ležia pod prefixom niektorej signatúry, ale nie sú tou knižnicou.
 *
 * `gms/ads/identifier/` je samostatná malá knižnica Google, ktorá len prečíta
 * reklamný identifikátor telefónu; ťahá ju Firebase a ďalšie SDK. Nie je to AdMob.
 * Overené na Lenovo ID: 5 tried výlučne z identifier/, žiadny AdMob — bez tejto
 * výnimky appka hlásila „obsahuje reklamné SDK (Google AdMob)". Skutočný AdMob
 * má vždy aj ďalšie triedy (AdView, AdRequest…), takže ho to neskryje.
 */
private val IGNORED_CLASS_PREFIXES: List<ByteArray> = listOf(
    "Lcom/google/android/gms/ads/identifier/",
    // To isté u Huawei (čítanie OAID). Overené na Samsung Galaxy Store: 4 triedy
    // výlučne z hms/ads/identifier/ → falošné „Huawei Ads".
    "Lcom/huawei/hms/ads/identifier/",
    // Rozhrania, cez ktoré iné Firebase moduly (Crashlytics, Remote Config) hovoria
    // s analytikou, ak je prítomná. Samy nič nezbierajú. Overené na Samsung Galaxy
    // Store: 5 tried výlučne z connector/, žiadna trieda FirebaseAnalytics →
    // falošné „Firebase Analytics". Skutočná analytika má aj triedu FirebaseAnalytics.
    "Lcom/google/firebase/analytics/connector/",
).map { it.toByteArray(Charsets.ISO_8859_1) }

private fun startsWithAt(data: ByteArray, at: Int, prefix: ByteArray): Boolean {
    if (at + prefix.size > data.size) return false
    for (i in prefix.indices) {
        if (data[at + i] != prefix[i]) return false
    }
    return true
}

/**
 * Prefixy balíkov tried, ktoré DEX súbor naozaj DEFINUJE (tabuľka class_defs).
 *
 * Do v1.3 sa DEX čítal ako text a hľadali sa v ňom všetky reťazce tvaru
 * `Lcom/foo/Bar;`. Tie sú však v súbore aj pri triedach, ktoré appka len
 * SPOMÍNA (tabuľka type_ids) — typicky reklamná mediácia (napr. AppLovin)
 * má adaptéry skompilované proti desiatkam sietí, ktoré v APK fyzicky nie sú.
 * Overené na realme Počasí: 7 nahlásených knižníc, 4 naozaj obsiahnuté.
 * Appka by tak tvrdila viac, než vie dokázať.
 *
 * Formát DEX: hlavička little-endian; string_ids_off @0x3C, type_ids_off @0x44,
 * class_defs_size @0x60, class_defs_off @0x64. Každý class_def má 32 bajtov
 * a začína indexom typu; typ ukazuje na reťazec (ULEB128 dĺžka + MUTF-8).
 * Poškodený súbor skončí výnimkou, ktorú scanApk zachytí ako „nečitateľné".
 */
private fun collectDefinedPrefixes(dex: ByteArray, out: MutableSet<String>) {
    if (dex.size < 0x70 ||
        dex[0] != 'd'.code.toByte() || dex[1] != 'e'.code.toByte() || dex[2] != 'x'.code.toByte()
    ) {
        throw IllegalArgumentException("not a dex file")
    }
    val buf = ByteBuffer.wrap(dex).order(ByteOrder.LITTLE_ENDIAN)
    val stringIdsOff = buf.getInt(0x3C)
    val typeIdsOff = buf.getInt(0x44)
    val classDefsSize = buf.getInt(0x60)
    val classDefsOff = buf.getInt(0x64)
    val semicolon = ';'.code.toByte()
    val slash = '/'.code.toByte()

    for (c in 0 until classDefsSize) {
        val typeIdx = buf.getInt(classDefsOff + 32 * c)
        val stringIdx = buf.getInt(typeIdsOff + 4 * typeIdx)
        var p = buf.getInt(stringIdsOff + 4 * stringIdx)
        // Preskočiť ULEB128 dĺžku reťazca.
        while ((dex[p].toInt() and 0x80) != 0) p++
        p++
        if (dex[p] != 'L'.code.toByte()) continue
        if (IGNORED_CLASS_PREFIXES.any { startsWithAt(dex, p, it) }) continue

        var k = p + 1
        var depth = 0
        while (k < dex.size && depth < MAX_DEPTH) {
            val b = dex[k]
            if (b == semicolon || b == 0.toByte()) break
            if (b == slash) {
                out.add(String(dex, p, k + 1 - p, Charsets.ISO_8859_1))
                depth++
            }
            k++
        }
    }
}

/**
 * Vráti null, ak sa niektorý APK súbor nepodarilo prečítať.
 * Predtým sa chyba potichu zhltla a výsledok bol prázdny zoznam — a ten sa
 * uložil ako „appka nemá žiadne trackery" a do logu zapísal „ubudli trackery: …".
 */
/**
 * v1.9: appka s kódom v JavaScripte (React Native, Cordova, Capacitor).
 * Spoznáva sa len podľa známych súborov v APK — skener JavaScript nečíta,
 * takže sledovanie napísané v ňom by nevidel. Flutter sem zámerne nepatrí:
 * jeho pluginy (Firebase…) majú Java časť v dex, a tú skener vidí.
 */
private fun isJsEntry(name: String): Boolean =
    (name.startsWith("assets/") && name.endsWith(".android.bundle")) ||   // React Native
    name == "assets/www/cordova.js" ||                                       // Cordova
    name == "assets/capacitor.config.json"                                   // Capacitor

/** Zapamätá výsledok skenu kódu (mimo Room, viď Prefs). */
private fun rememberCode(ctx: Context, pkg: String, dex: Boolean, js: Boolean) {
    Prefs.setNoCode(ctx, pkg, !dex)
    Prefs.setJsCode(ctx, pkg, js)
}

private fun scanApk(paths: List<String>, codeSeen: (dex: Boolean, js: Boolean) -> Unit = { _, _ -> }): List<Tracker>? {
    if (paths.isEmpty()) return null
    val prefixes = HashSet<String>(8192)
    var dexCount = 0
    var js = false
    for (path in paths) {
        try {
            ZipFile(path).use { zip ->
                val all = zip.entries().toList()
                if (!js) js = all.any { isJsEntry(it.name) }
                val dexEntries = all.filter {
                    it.name.startsWith("classes") && it.name.endsWith(".dex")
                }
                dexCount += dexEntries.size
                for (entry in dexEntries) {
                    val bytes = zip.getInputStream(entry).use { it.readBytes() }
                    collectDefinedPrefixes(bytes, prefixes)
                }
            }
        } catch (e: Exception) {
            return null
        } catch (e: OutOfMemoryError) {
            // Veľké APK sa načítava celé do pamäte a v troch vláknach naraz
            // to môže prejsť cez strop haldy. OutOfMemoryError je Error, nie
            // Exception, takže by catch vyššie neplatil a appka by spadla
            // uprostred skenu. Takto z toho je bežná hláška „nepodarilo sa
            // prečítať".
            return null
        }
    }
    // v1.6: žiadny classes*.dex = kód je mimo APK (predkompilovaný výrobcom)
    // alebo zašifrovaný. „0 trackerov" by tu znamenalo „nevieme", nie „čistá".
    codeSeen(dexCount > 0, js)
    return TRACKERS.filter { it.prefix in prefixes }
}

private suspend fun persist(ctx: Context, db: AuditDb, app: AppEntry, trackers: List<Tracker>) {
    val dao = db.dao()
    val old = dao.scan(app.packageName)
    val names = trackers.map { it.name }

    // Po zmene zoznamu signatúr sa v prvom skene nezapisujú ani prírastky,
    // ani úbytky trackerov. Prírastok môže byť len signatúra, ktorá knižnicu
    // konečne vidí; úbytok zasa signatúra, ktorú sme zo zoznamu odobrali
    // alebo premenovali — mená trackerov sa ukladajú ako text, takže
    // premenovanie vyzerá ako odobratie plus pridanie. Ani jedno nie je
    // zmena v aplikácii. Zmeny povolení sú spoľahlivé a potláčajú sa.
    val refreshing = Prefs.needsBaselineRefresh(ctx, app.packageName)
    val diff = computeDiff(old, names, app.permissions)
        ?.let {
            if (refreshing) it.copy(trackersAdded = emptyList(), trackersRemoved = emptyList())
            else it
        }
        ?.takeIf { !it.isEmpty() }
    val text = diff?.encode()
    val now = System.currentTimeMillis()

    dao.save(
        ScanRecord(
            packageName = app.packageName,
            label = app.label,
            versionCode = app.versionCode,
            trackers = names.joinToString(","),
            permissions = app.permissions.joinToString(","),
            scannedAt = now
        )
    )
    if (text != null) {
        dao.addChange(
            ChangeRecord(packageName = app.packageName, label = app.label, at = now, text = text)
        )
    }
    Prefs.markRescanned(ctx, app.packageName)
}

private suspend fun scanAllApps(
    ctx: Context,
    apps: List<AppEntry>,
    db: AuditDb,
    onResult: (String, List<Tracker>) -> Unit,
    onProgress: (Int) -> Unit
) = coroutineScope {
    val counter = AtomicInteger(0)
    val workers = 3
    val chunkSize = ((apps.size + workers - 1) / workers).coerceAtLeast(1)
    apps.chunked(chunkSize).map { chunk ->
        async(Dispatchers.IO) {
            for (app in chunk) {
                val result = scanApk(app.apkPaths) { dex, js -> rememberCode(ctx, app.packageName, dex, js) }
                if (result != null) persist(ctx, db, app, result)
                withContext(Dispatchers.Main) {
                    if (result != null) onResult(app.packageName, result)
                    onProgress(counter.incrementAndGet())
                }
            }
        }
    }.awaitAll()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            AuditTheme { BurstHost {
                val ctx = LocalContext.current
                var lang by remember { mutableStateOf(Prefs.lang(ctx)) }
                val current = lang
                val s = remember(current) { current?.let { S(ctx, it) } }

                when {
                    current == null || s == null -> OnboardingScreen { chosen ->
                        Prefs.setLang(ctx, chosen)
                        lang = chosen
                    }

                    else -> Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppListScreen(
                            s = s,
                            onLangChange = { chosen ->
                                Prefs.setLang(ctx, chosen)
                                lang = chosen
                            },
                            onShowIntro = {
                                Prefs.clearLang(ctx)
                                AppCache.apps = null
                                AppCache.scanned.clear()
                                lang = null
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            } }
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val accent = MaterialTheme.colorScheme.primary
    val border = if (active) accent else MaterialTheme.colorScheme.outline
    Column(
        modifier = modifier
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .background(
                if (active) accent.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surface
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 22.sp,
            color = if (active) accent else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Trojstavový prepínač Aplikácie / Systémové / Spolu. Jeden rám, zvýraznenie
 * sa pri prepnutí posunie (nie skočí), čísla sa dopočítajú. Aktívny segment
 * má aj značku ▸, aby nebol rozoznateľný len farbou.
 */
@Composable
private fun ScopeSwitch(
    selected: ListScope,
    counts: Map<ListScope, Int>?,
    labels: Map<ListScope, String>,
    onSelect: (ListScope) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    val entries = ListScope.entries
    val pos by animateFloatAsState(
        targetValue = entries.indexOf(selected).toFloat(),
        animationSpec = tween(200),
        label = "scopePos"
    )
    val shape = RoundedCornerShape(3.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .border(1.dp, accent.copy(alpha = 0.4f), shape)
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                val w = size.width / entries.size
                val left = w * pos
                drawRect(accent.copy(alpha = 0.12f), Offset(left, 0f), Size(w, size.height))
                val stroke = 1.dp.toPx()
                drawRect(
                    accent.copy(alpha = 0.9f),
                    Offset(left + stroke / 2, stroke / 2),
                    Size(w - stroke, size.height - stroke),
                    style = Stroke(stroke)
                )
            }
    ) {
        entries.forEachIndexed { i, sc ->
            if (i > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(outline.copy(alpha = 0.5f))
                )
            }
            val active = sc == selected
            val target = counts?.get(sc) ?: 0
            val shown by animateIntAsState(target, tween(300), label = "count$i")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .selectable(selected = active, role = Role.Tab) { onSelect(sc) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (counts == null) "…" else shown.toString(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    maxLines = 1,
                    color = if (active) accent else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = (if (active) "▸ " else "") + labels.getValue(sc).uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    color = if (active) accent else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Jeden riadok s ADB príkazom: dá sa označiť aj skopírovať. */
@Composable
internal fun CommandLine(label: String, cmd: String, s: S, copiedKey: String?, onCopied: (String) -> Unit) {
    val context = LocalContext.current
    if (label.isNotEmpty()) Text(
        label,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        SelectionContainer(modifier = Modifier.weight(1f)) {
            Text(cmd, fontFamily = FontFamily.Monospace, fontSize = 11.sp, lineHeight = 15.sp)
        }
        FxTextButton(onClick = {
            runCatching {
                context.getSystemService(ClipboardManager::class.java)
                    ?.setPrimaryClip(ClipData.newPlainText("adb", cmd))
            }
            onCopied(cmd)
        }) {
            Text(if (copiedKey == cmd) s.copied else s.copy, fontSize = 12.sp)
        }
    }
}

/**
 * Rozbalený zoznam technických povolení (v1.5).
 *  1. Povolenia Androidu — s ľudským popisom, ak ho máme ([TECH_NAMES]),
 *     a so značkou „len pre systém" podľa údaja z telefónu.
 *  2. Povolenia výrobcov (com.vivo, com.google…) — od 3 kusov zbalené do
 *     jedného riadku s počtom; ich význam výrobcovia nezverejňujú, preto ich
 *     neprekladáme, len ukážeme, koľko z nich je chránených podpisom.
 */
@Composable
private fun TechList(tech: List<String>, pm: PackageManager, s: S) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val strong = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    // Úroveň ochrany sa pýta telefónu raz, až keď sa zoznam rozbalí.
    val protectedMap = remember(tech) { tech.associateWith { isSignatureProtected(pm, it) } }
    val (androidPerms, vendorPerms) = remember(tech) {
        tech.partition { it.startsWith("android.permission.") }
    }
    val groups = remember(vendorPerms) {
        vendorPerms.groupBy { vendorPrefix(it) }.toList().sortedByDescending { it.second.size }
    }
    val expanded = remember(tech) { mutableStateSetOf<String>() }
    var anyProtected = false

    // a) Android: najprv tie s ľudským popisom, potom zvyšok podľa abecedy
    val sortedAndroid = androidPerms.sortedWith(
        compareBy<String> { techHuman(it, s) == null }.thenBy { techHuman(it, s) ?: it }
    )
    sortedAndroid.forEach { p ->
        val human = techHuman(p, s)
        val sys = protectedMap[p] == true
        if (sys) anyProtected = true
        Text(
            text = buildAnnotatedString {
                append("•\u00A0")
                if (human != null) {
                    withStyle(SpanStyle(color = strong, fontSize = 12.sp)) { append(human) }
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = muted)) {
                        append("  " + breakable(techLabel(p)))
                    }
                } else {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = muted)) {
                        append(breakable(techLabel(p)))
                    }
                }
                if (sys) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = accent)) {
                        append(" · " + s.sysOnly)
                    }
                }
            },
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }

    // b) výrobcovia
    groups.forEach { (prefix, perms) ->
        val sig = perms.count { protectedMap[it] == true }
        if (sig > 0) anyProtected = true
        if (perms.size < 3) {
            perms.sorted().forEach { p ->
                Text(
                    text = "•\u00A0" + breakable(p),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = muted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        } else {
            val open = prefix in expanded
            Text(
                text = (if (open) "▾ " else "▸ ") + s.vendorGroup(prefix, perms.size, sig),
                fontSize = 12.sp,
                color = strong,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { if (open) expanded.remove(prefix) else expanded.add(prefix) }
            )
            if (open) {
                perms.sorted().forEach { p ->
                    Text(
                        text = "•\u00A0" + breakable(p),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = muted,
                        modifier = Modifier.padding(start = 12.dp, top = 1.dp)
                    )
                }
            }
        }
    }

    if (anyProtected || groups.any { it.second.size >= 3 }) {
        Text(
            s.sigNote,
            style = MaterialTheme.typography.bodySmall,
            color = muted,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
internal fun levelColor(level: Level?): Color = when (level) {
    Level.ALERT -> MaterialTheme.colorScheme.error
    Level.WARN -> WarnAmber
    Level.INFO -> MaterialTheme.colorScheme.primary
    Level.OK -> OkGreen
    null -> MaterialTheme.colorScheme.outline
}

@Composable
fun AppListScreen(
    s: S,
    onLangChange: (Lang) -> Unit,
    onShowIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pm = remember { context.packageManager }
    val scope = rememberCoroutineScope()
    val db = remember { AuditDb.get(context) }
    val dateFmt = remember { SimpleDateFormat("d.M. HH:mm", Locale.getDefault()) }

    val appsLabel = s.tileApps
    val sysLabel = s.tileSystem

    var apps by remember { mutableStateOf(AppCache.apps ?: emptyList()) }
    var listScope by rememberSaveable { mutableStateOf(ListScope.APPS) }
    var showRemove by remember { mutableStateOf<String?>(null) }
    var copiedKey by remember { mutableStateOf<String?>(null) }
    val expandedChanges = remember { mutableStateSetOf<Long>() }
    // v1.7
    var sortMode by remember { mutableStateOf(Prefs.sort(context)) }
    var allChanges by remember { mutableStateOf(false) }
    val criticalRuntime = remember { criticalRuntimePackages(context) }
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }
    var scanning by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<Int?>(null) }
    // Menovateľ ukazovateľa priebehu, zafixovaný pri štarte skenu.
    var scanTotal by remember { mutableIntStateOf(0) }
    var scanRevision by remember { mutableIntStateOf(0) }
    // v1.6: appky bez čitateľného kódu (zapisuje sken do Prefs)
    val noCode = remember(scanRevision) { Prefs.noCode(context) }
    // v1.9: appky s kódom v JavaScripte (skener ho nečíta)
    val jsCode = remember(scanRevision) { Prefs.jsCode(context) }
    val debuggable = remember {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    var changes by remember { mutableStateOf<List<ChangeRecord>>(emptyList()) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    // v1.8
    var showCheck by rememberSaveable { mutableStateOf(false) }
    var showByPerm by rememberSaveable { mutableStateOf(false) }
    var pendingUninstall by rememberSaveable { mutableStateOf<String?>(null) }
    var justCleared by remember { mutableStateOf(false) }
    var failedPkg by remember { mutableStateOf<String?>(null) }
    val scanned = remember {
        mutableStateMapOf<String, List<Tracker>>().apply { putAll(AppCache.scanned) }
    }

    fun store(pkg: String, result: List<Tracker>) {
        scanned[pkg] = result
        AppCache.scanned[pkg] = result
        // Kľúč pre prepočet závažností. Nestačí scanned.size — pri opätovnom
        // skene sa prepíše hodnota existujúceho záznamu a veľkosť mapy
        // zostane rovnaká, takže by sa závažnosť neprepočítala.
        scanRevision++
    }

    // v1.8: odinštalovanie — systémové okno, potvrdzuje používateľ. Po návrate
    // overíme, či appka naozaj zmizla (mohol zrušiť).
    val uninstallLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val pkg = pendingUninstall ?: return@rememberLauncherForActivityResult
        pendingUninstall = null
        val gone = runCatching { pm.getPackageInfo(pkg, 0); false }.getOrDefault(true)
        if (gone) {
            apps = apps.filter { it.packageName != pkg }
            AppCache.apps = apps
            scanned.remove(pkg)
            AppCache.scanned.remove(pkg)
            if (expanded == pkg) expanded = null
        }
    }

    LaunchedEffect(Unit) {
        if (AppCache.apps == null) {
            val loaded = withContext(Dispatchers.IO) { loadApps(context) }
            AppCache.apps = loaded
            apps = loaded
        }
        if (AppCache.scanned.isEmpty()) {
            db.dao().allScans().forEach { rec ->
                val names = rec.trackers.split(",").filter { it.isNotBlank() }.toSet()
                store(rec.packageName, TRACKERS.filter { it.name in names })
            }
        }
        changes = db.dao().recentChanges()
    }

    if (showSettings) {
        SettingsScreen(
            s = s,
            justCleared = justCleared,
            onLangChange = { onLangChange(it) },
            onClearChanges = {
                scope.launch {
                    db.dao().clearChanges()
                    changes = emptyList()
                    justCleared = true
                }
            },
            onClearScans = {
                scope.launch {
                    db.dao().clearScans()
                    db.dao().clearChanges()
                    Prefs.clearNoCode(context)
                    Prefs.clearJsCode(context)
                    scanned.clear()
                    AppCache.scanned.clear()
                    scanRevision++
                    changes = emptyList()
                    justCleared = true
                }
            },
            onShowIntro = onShowIntro,
            onBack = {
                showSettings = false
                justCleared = false
            },
            modifier = modifier
        )
        return
    }

    if (showByPerm) {
        ByPermScreen(
            s = s,
            apps = apps.filter { it.inScope(listScope) },
            scopeName = when (listScope) {
                ListScope.APPS -> appsLabel
                ListScope.SYSTEM -> sysLabel
                ListScope.ALL -> s.tileAll
            },
            onBack = { showByPerm = false },
            onOpenApp = { app ->
                showByPerm = false
                showCheck = false
                query = app.label
                expanded = app.packageName
            },
            modifier = modifier
        )
        return
    }
    if (showCheck) {
        CheckScreen(
            s = s,
            apps = apps,
            onBack = { showCheck = false },
            onByPerm = { showByPerm = true },
            modifier = modifier
        )
        return
    }

    val counts = if (apps.isEmpty()) null else mapOf(
        ListScope.APPS to apps.count { !it.isSystem },
        ListScope.SYSTEM to apps.count { it.isSystem },
        ListScope.ALL to apps.size
    )
    val scanTarget = apps.filter { it.inScope(listScope) }

    // Závažnosť sa počíta raz na aplikáciu, nie v komparátore. Predtým to tam
    // bolo a buildFindings() — ktoré načítava a formátuje texty — sa pri každom
    // zoradení volalo ~2·N·log N krát. Počas skenu sa zoznam prekresľuje po
    // každej hotovej appke, takže z toho boli státisíce volaní na hlavnom vlákne.
    val levels = remember(apps, scanRevision, s, jsCode) {
        apps.associate { app ->
            app.packageName to (scanned[app.packageName]
                ?.let {
                    worstLevel(buildFindings(app.permissions, app.granted, it, s,
                        app.packageName !in noCode, app.sharedNet, app.caps, app.isWebApk,
                        app.packageName in jsCode)).ordinal
                }
                ?: -1)
        }
    }

    val visible = remember(scanTarget, query, levels, sortMode, scanRevision) {
        scanTarget
            .filter {
                query.isBlank() ||
                        it.label.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
            .sortedWith(
                when (sortMode) {
                    SortMode.SEVERITY -> compareByDescending<AppEntry> { levels[it.packageName] ?: -1 }
                        .thenBy { it.label.lowercase() }
                    SortMode.NAME -> compareBy { it.label.lowercase() }
                    // Neoskenované (-1) idú na koniec, aby sa nemiešali s tými, čo majú 0.
                    SortMode.FINDINGS -> compareByDescending<AppEntry> { scanned[it.packageName]?.size ?: -1 }
                        .thenBy { it.label.lowercase() }
                }
            )
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp)) {

        // ── hlavička ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 10.dp)
        ) {
            // Logo nerastie so systémovým písmom (fixedSp) a nikdy sa nezalamuje.
            // V1.0 tu pri veľkom písme na Samsungu „audit" padalo po písmenách pod seba.
            Text(
                text = "plainkit.",
                fontFamily = FontFamily.Monospace,
                fontSize = fixedSp(19f),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = "audit",
                fontFamily = FontFamily.Monospace,
                fontSize = fixedSp(15f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .weight(1f)
            )
            // pri veľkom písme len ikonka; popis ostáva pre čítačku obrazovky
            FxTextButton(onClick = { showSettings = true }) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = if (isLargeFont()) s.settings else null,
                    modifier = Modifier.size(16.dp)
                )
                if (!isLargeFont()) {
                    Spacer(Modifier.width(6.dp))
                    Text(s.settings, fontSize = 13.sp, maxLines = 1)
                }
            }
        }

        // ── rozsah: aplikácie / systémové / spolu ──
        ScopeSwitch(
            selected = listScope,
            counts = counts,
            labels = mapOf(
                ListScope.APPS to appsLabel,
                ListScope.SYSTEM to sysLabel,
                ListScope.ALL to s.tileAll
            ),
            onSelect = { listScope = it }
        )

        // ── hľadanie ──
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    s.searchLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
            shape = RoundedCornerShape(3.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = s.clear,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        // ── skenovanie ──
        FxButton(
            onClick = {
                progress = 0
                scope.launch {
                    // čerstvý zoznam: aktuálne cesty k APK, aktuálne povolenia,
                    // a aj appky nainštalované od spustenia Auditu
                    val fresh = withContext(Dispatchers.IO) { loadApps(context) }
                    AppCache.apps = fresh
                    apps = fresh
                    val target = fresh.filter { it.inScope(listScope) }
                    // Celkový počet sa zapamätá pri štarte. Prepínač rozsahu
                    // zostáva klikateľný a scanTarget sa z neho prepočítava, takže
                    // prepnutím počas skenu by ukazovateľ ukazoval „300 / 150".
                    scanTotal = target.size
                    scanAllApps(
                        ctx = context,
                        apps = target,
                        db = db,
                        onResult = { pkg, result -> store(pkg, result) },
                        onProgress = { done -> progress = done }
                    )
                    changes = db.dao().recentChanges()
                    progress = null
                }
            },
            enabled = progress == null && apps.isNotEmpty(),
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(
                text = progress?.let { s.scanProgress(it, scanTotal) } ?: s.scanAll,
                fontFamily = FontFamily.Monospace
            )
        }

        val progressNow = progress
        if (progressNow != null) {
            val total = scanTotal.coerceAtLeast(1)
            LinearProgressIndicator(
                progress = { (progressNow.toFloat() / total).coerceIn(0f, 1f) },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(top = 4.dp)
            )
        }
        // v1.8: kontrola telefónu a prehľad podľa povolení
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            FxOutlinedButton(
                onClick = { showCheck = true },
                modifier = Modifier.weight(1f)
            ) { Text(s.navCheck, fontSize = 13.sp, textAlign = TextAlign.Center) }
            FxOutlinedButton(
                onClick = { showByPerm = true },
                modifier = Modifier.weight(1f)
            ) { Text(s.navByPerm, fontSize = 13.sp, textAlign = TextAlign.Center) }
        }
        // v1.7: zoradenie — jeden nenápadný riadok, ťuknutím sa prepína
        Text(
            text = s.sortLabel + " " + s.sortName(sortMode) + " ▾",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    val next = SortMode.entries[(sortMode.ordinal + 1) % SortMode.entries.size]
                    sortMode = next
                    Prefs.setSort(context, next)
                }
                .padding(top = 8.dp, bottom = 4.dp)
        )

        // Zmeny, z ktorých po odfiltrovaní vlastných povolení aplikácie nič
        // nezostalo, sa nezobrazujú — inak by na obrazovke visel prázdny rámček.
        val shownChanges = remember(changes, s) {
            changes.mapNotNull { ch ->
                val full = renderChange(ch.text, ch.packageName, s)
                if (full.isEmpty()) null
                else Triple(ch, full, renderChange(ch.text, ch.packageName, s, compact = true))
            }
        }

        // ── zoznam ──
        LazyColumn {
            if (shownChanges.isNotEmpty() && query.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(3.dp)
                            )
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Text(
                            s.changesTitle,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // v1.7: posledných 5, zvyšok po ťuknutí
                        val listed = if (allChanges) shownChanges else shownChanges.take(5)
                        listed.forEach { (ch, full, compact) ->
                            // Dlhé zoznamy technických povolení sa zbalia na počet;
                            // ťuknutím sa záznam rozbalí celý.
                            val open = ch.id in expandedChanges
                            val canToggle = full != compact
                            Text(
                                text = "${ch.label} — " + if (open || !canToggle) full else compact,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .then(
                                        if (canToggle) Modifier.clickable {
                                            if (open) expandedChanges.remove(ch.id)
                                            else expandedChanges.add(ch.id)
                                        } else Modifier
                                    )
                            )
                            Text(
                                text = dateFmt.format(Date(ch.at)),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (shownChanges.size > 5) {
                            Text(
                                text = if (allChanges) s.changesLess else s.changesMore(shownChanges.size - 5),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .clickable { allChanges = !allChanges }
                            )
                        }
                    }
                }
            }

            items(visible, key = { it.packageName }) { app ->
                val trackers = scanned[app.packageName]
                val findings = trackers?.let {
                    buildFindings(app.permissions, app.granted, it, s,
                        app.packageName !in noCode, app.sharedNet, app.caps, app.isWebApk,
                        app.packageName in jsCode)
                }
                val level = findings?.let { worstLevel(it) }
                val stripe = levelColor(level)
                val icon = remember(app.packageName) {
                    runCatching {
                        pm.getApplicationIcon(app.packageName).toBitmap(96, 96).asImageBitmap()
                    }.getOrNull()
                }
                var showTech by remember(app.packageName) { mutableStateOf(false) }

                val named = app.permissions.filter { PERM_NAMES.containsKey(it) }
                // Desiatky povolení na push a na odznak na ikonke zhrnie jeden riadok.
                val hasNotifyBadge = app.permissions.any { isNotifyOrBadge(it) }
                // Do „technických" ide zvyšok: ani pomenované, ani zhrnuté,
                // ani vlastné povolenia toho istého výrobcu.
                val tech = app.permissions.filterNot {
                    PERM_NAMES.containsKey(it) ||
                        isNotifyOrBadge(it) ||
                        isOwnPermission(it, app.packageName)
                }
                val namedSorted = named.sortedWith(
                    compareByDescending<String> { isSensitive(it) && it in app.granted }
                        .thenByDescending { isSensitive(it) }
                        .thenBy { permLabel(it, s) ?: it }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .height(IntrinsicSize.Min)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            expanded = if (expanded == app.packageName) null else app.packageName
                            // Počas „Skenovať všetky" sa tu nový sken nespúšťa:
                            // ten istý balík by inak skenovali dve vlákna naraz,
                            // obe by prečítali tú istú základňu a tú istú zmenu
                            // by zapísali dvakrát. Rozbaliť riadok sa dá, výsledok
                            // doň doplní bežiaci sken, keď naň príde rad.
                            if (progress == null &&
                                expanded == app.packageName &&
                                !scanned.containsKey(app.packageName)
                            ) {
                                scanning = app.packageName
                                failedPkg = null
                                scope.launch {
                                    val fresh = withContext(Dispatchers.IO) {
                                        loadApp(context, app.packageName)
                                    }
                                    val result = fresh?.let {
                                        withContext(Dispatchers.IO) {
                                            scanApk(it.apkPaths) { dex, js -> rememberCode(context, it.packageName, dex, js) }
                                        }
                                    }
                                    if (fresh != null && result != null) {
                                        persist(context, db, fresh, result)
                                        store(app.packageName, result)
                                        apps = apps.map { if (it.packageName == fresh.packageName) fresh else it }
                                        AppCache.apps = apps
                                        changes = db.dao().recentChanges()
                                    } else {
                                        failedPkg = app.packageName
                                    }
                                    scanning = null
                                }
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(stripe)
                    )

                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (icon != null) {
                                Image(
                                    bitmap = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                val showTag = listScope == ListScope.ALL && app.isSystem
                                if (level != null || showTag) {
                                    Row {
                                        if (level != null) {
                                            Text(
                                                text = s.level(level),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = stripe
                                            )
                                        }
                                        if (showTag) {
                                            Text(
                                                text = (if (level != null) " · " else "") + s.tagPreinstalled,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Text(
                                    app.label,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    app.packageName,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = s.summary(app.permissions.size, trackers?.size, app.packageName in noCode),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (expanded == app.packageName) {
                            when {
                                scanning == app.packageName -> Text(
                                    s.scanningOne,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                findings == null -> Text(
                                    if (failedPkg == app.packageName) s.scanFailed else "—",
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                else -> findings.forEach { f ->
                                    Text(
                                        text = f.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (f.level == Level.ALERT)
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            Text(
                                text = s.permissionsTitle,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                            )

                            if (namedSorted.isEmpty() && !hasNotifyBadge) {
                                Text(s.noNamedPerms, style = MaterialTheme.typography.bodySmall)
                            } else if (namedSorted.isNotEmpty()) {
                                namedSorted.forEach { p ->
                                    val label = permLabel(p, s) ?: p
                                    val isGranted = p in app.granted
                                    val known = p in app.specialKnown
                                    val unused = isUnusedHere(p, app.targetSdk)
                                    // Ladenie (len debug build): surová odpoveď AppOps
                                    val dbg = if (debuggable) app.specialModes[p]?.let { "  [op=$it]" } ?: "" else ""
                                    // v1.7: stav za pomlčkou menším a tlmeným písmom, aby sa
                                    // nelámal uprostred a oko videlo najprv názov. Červené
                                    // UDELENÉ ostáva celé červené.
                                    val active = s.permIsActive(p, isGranted, known, unused)
                                    val main = if (active) MaterialTheme.colorScheme.error
                                               else MaterialTheme.colorScheme.onSurface
                                    val sub = if (active) MaterialTheme.colorScheme.error
                                              else MaterialTheme.colorScheme.onSurfaceVariant
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(SpanStyle(color = main)) { append("• $label") }
                                            withStyle(SpanStyle(color = sub, fontSize = 13.sp)) {
                                                append(s.permMark(p, isGranted, known, unused) + dbg)
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Na koniec zoznamu: je to najmenej podstatná položka
                            // zo sekcie a nemá stav udelené/neudelené.
                            if (hasNotifyBadge) {
                                Text(
                                    text = "• " + s.notifyBadge,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // ── v1.7: mimoriadne schopnosti ──
                            if (app.caps.isNotEmpty()) {
                                Text(
                                    text = s.capsTitle,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                                )
                                listOf(
                                    Cap.A11Y to s.capA11y, Cap.NOTIF to s.capNotif,
                                    Cap.IME to s.capIme, Cap.ADMIN to s.capAdmin
                                ).filter { it.first in app.caps }.forEach { (cap, text) ->
                                    Text(
                                        text = "• $text",
                                        style = MaterialTheme.typography.bodyMedium,
                                        // Dostupnosť a notifikácie červeno — najčastejšia cesta krádeže kódov.
                                        color = if (cap == Cap.A11Y || cap == Cap.NOTIF)
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (tech.isNotEmpty()) {
                                FxTextButton(
                                    onClick = { showTech = !showTech },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        if (showTech) s.hideTech else s.techPerms(tech.size),
                                        fontSize = 13.sp
                                    )
                                }
                                if (showTech) {
                                    TechList(tech, pm, s)
                                }
                            }

                            // ── zdieľaná identita (v1.6) ──
                            if (app.sharedWith.isNotEmpty()) {
                                val names = remember(app.packageName, app.sharedWith) {
                                    app.sharedWith.map { other ->
                                        runCatching {
                                            pm.getApplicationLabel(pm.getApplicationInfo(other, 0)).toString()
                                        }.getOrDefault(other)
                                    }.sorted()
                                }
                                Text(
                                    text = s.sharedTitle,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                                )
                                // Pri systémovej identite býva skupina veľká (desiatky appiek) —
                                // ukáže sa prvých 5 a zvyšok po ťuknutí na „a ďalších N".
                                var sharedOpen by remember(app.packageName) { mutableStateOf(false) }
                                if (names.size <= 5 || sharedOpen) {
                                    Text(
                                        text = names.joinToString(", "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    Text(
                                        text = buildAnnotatedString {
                                            append(names.take(5).joinToString(", ") + " ")
                                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                                append(s.sharedMore(names.size - 5) + " ▸")
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable { sharedOpen = true }
                                    )
                                }
                            }

                            // ── pôvod ──
                            val origin = remember(app.packageName, s) { originText(pm, app, s) }
                            if (origin != null) {
                                Text(
                                    text = s.originTitle,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                                )
                                Text(origin, style = MaterialTheme.typography.bodyMedium)
                            }

                            // ── čo s tým: nastavenia appky, pri systémových návod na odstránenie ──
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                FxTextButton(onClick = {
                                    runCatching {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", app.packageName, null)
                                            )
                                        )
                                    }
                                }) { Text(s.openSettings, fontSize = 13.sp) }
                                // v1.8: bežné appky sa dajú odinštalovať priamo (potvrdzuje Android)
                                if (!app.isSystem && app.packageName != context.packageName) {
                                    FxTextButton(onClick = {
                                        pendingUninstall = app.packageName
                                        runCatching {
                                            uninstallLauncher.launch(
                                                Intent(Intent.ACTION_DELETE, Uri.fromParts("package", app.packageName, null))
                                                    .putExtra(Intent.EXTRA_RETURN_RESULT, true)
                                            )
                                        }.onFailure { pendingUninstall = null }
                                    }) { Text(s.uninstall, fontSize = 13.sp) }
                                }
                                if (app.isSystem && !isCritical(app.packageName, criticalRuntime)) {
                                    FxTextButton(onClick = {
                                        showRemove = if (showRemove == app.packageName) null else app.packageName
                                    }) { Text(s.howToRemove, fontSize = 13.sp) }
                                }
                            }
                            if (showRemove == app.packageName) {
                                Text(
                                    s.removeIntro,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                CommandLine(
                                    label = s.removeCmd,
                                    cmd = "adb shell pm uninstall --user 0 ${app.packageName}",
                                    s = s,
                                    copiedKey = copiedKey,
                                    onCopied = { copiedKey = it }
                                )
                                CommandLine(
                                    label = s.restoreCmd,
                                    cmd = "adb shell cmd package install-existing ${app.packageName}",
                                    s = s,
                                    copiedKey = copiedKey,
                                    onCopied = { copiedKey = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}