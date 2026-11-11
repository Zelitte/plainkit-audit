package app.plainkit.audit

import android.content.Context
import android.content.pm.PackageManager

/**
 * v2.3: „zapojené / len v kóde" — len pri reklamných knižniciach, ktoré majú v manifeste
 * spoľahlivú stopu (prieskum v2.3 na Redmi a vivo, 11. 11. 2026: AdMob 18/20 a 10/11,
 * Meta Audience Network 9/11 a 4/4, Pangle, AppLovin, Yandex Ads, VK myTarget, Vungle,
 * Bigo, Mintegral, InMobi). Analytika a hlásenie pádov sa v manifeste nehlásia (Firebase
 * 0/57) — pri nich sa to nerozlišuje. Chýbajúca stopa NEDOKAZUJE, že reklama nebeží
 * (napr. banner nepotrebuje aktivitu) — preto text „je možné, že sa nepoužíva".
 */
internal val VERIFIED_WIRED_ADS = setOf(
    "Google AdMob", "Meta Audience Network", "ByteDance Pangle", "AppLovin", "Yandex Ads",
    "VK myTarget", "Vungle", "Bigo Ads", "Mintegral", "InMobi"
)

/** Mená overených reklamných knižníc, ktoré sú v kóde, ale v manifeste nemajú žiadnu stopu. */
internal fun unwiredAds(ctx: Context, pkg: String, trackers: List<Tracker>): Set<String> {
    val candidates = trackers.map { it.name }.filter { it in VERIFIED_WIRED_ADS }.toSet()
    if (candidates.isEmpty()) return emptySet()
    val pm = ctx.packageManager
    val names = HashSet<String>()
    // po jednom príznaku — veľké appky inak padnú na limite Binder transakcie
    for (f in listOf(PackageManager.GET_ACTIVITIES, PackageManager.GET_SERVICES,
                     PackageManager.GET_RECEIVERS, PackageManager.GET_PROVIDERS)) {
        val ok = runCatching {
            val pi = pm.getPackageInfo(pkg, f)
            pi.activities?.forEach { names += it.name }
            pi.services?.forEach { names += it.name }
            pi.receivers?.forEach { names += it.name }
            pi.providers?.forEach { names += it.name }
        }.isSuccess
        // keď manifest nejde prečítať, radšej nič netvrdiť
        if (!ok) return emptySet()
    }
    runCatching {
        pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA).metaData?.keySet()?.forEach { names += it }
    }
    return candidates.filter { name ->
        val dots = TRACKERS.filter { it.name == name }.map { it.prefix.removePrefix("L").replace('/', '.') }
        names.none { n -> dots.any { n.startsWith(it) } }
    }.toSet()
}
