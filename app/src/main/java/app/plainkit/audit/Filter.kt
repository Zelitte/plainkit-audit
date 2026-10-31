package app.plainkit.audit

import android.content.pm.PackageManager
import android.os.Build

/**
 * v1.9: „Zobraziť len" v menu Zoradiť a filtrovať (rozhodnuté 31. 10. 2026).
 * Jedna voľba naraz. Filter sa NEpamätá — po spustení vždy „všetky", aby skrytý
 * filter neklamal o počtoch. Povolenia len UDELENÉ (vrátane skutočného stavu
 * z AppOps), nie žiadané.
 */
enum class ListFilter(val needsScan: Boolean) {
    ALL(false), WARNING(true), ADS(true), TRACKERS(true), CAPS(false),
    MIC(false), LOCATION(false), CAMERA(false), FILES(false), PARTIAL_CODE(true), NON_PLAY(false)
}

private const val FP = "android.permission."

private fun grantedAny(app: AppEntry, groupRes: Int): Boolean {
    val perms = SENSITIVE.first { it.res == groupRes }.permissions
    return perms.any { it in app.granted && !isUnusedHere(it, app.targetSdk) }
}

/**
 * Inštalátor nesystémovej appky; null = nevieme (prázdny inštalátor = ADB alebo
 * predinštalované výrobcom — vtedy „mimo Google Play" netvrdíme).
 */
internal fun installerOfPkg(pm: PackageManager, pkg: String): String? = runCatching {
    if (Build.VERSION.SDK_INT >= 30) pm.getInstallSourceInfo(pkg).installingPackageName
    else @Suppress("DEPRECATION") pm.getInstallerPackageName(pkg)
}.getOrNull()

/**
 * Zodpovedá appka filtru? Pri filtroch závislých od skenu neoskenovaná appka
 * NEzodpovedá (o jej nálezoch nevieme) — preto UI výrazne ukáže počet neoskenovaných.
 */
internal fun matchesFilter(
    app: AppEntry,
    filter: ListFilter,
    level: Int,                    // Level.ordinal, -1 = neoskenované
    trackers: List<Tracker>?,      // null = neoskenované
    noCode: Set<String>,
    jsCode: Set<String>,
    nonPlay: Set<String>
): Boolean = when (filter) {
    ListFilter.ALL -> true
    ListFilter.WARNING -> level >= Level.WARN.ordinal
    ListFilter.ADS -> trackers?.any { it.kind == TrackerKind.ADS } == true
    ListFilter.TRACKERS -> !trackers.isNullOrEmpty()
    ListFilter.CAPS -> app.caps.isNotEmpty()
    ListFilter.MIC -> grantedAny(app, R.string.group_microphone)
    ListFilter.LOCATION -> grantedAny(app, R.string.group_location)
    ListFilter.CAMERA -> grantedAny(app, R.string.group_camera)
    ListFilter.FILES -> "${FP}MANAGE_EXTERNAL_STORAGE".let { it in app.granted && it in app.specialKnown }
    ListFilter.PARTIAL_CODE -> trackers != null && (app.packageName in noCode || app.packageName in jsCode)
    ListFilter.NON_PLAY -> app.packageName in nonPlay
}
