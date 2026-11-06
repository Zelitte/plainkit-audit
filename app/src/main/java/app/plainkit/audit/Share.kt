package app.plainkit.audit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * v2.1: text nálezu JEDNEJ appky na zdieľanie (Zdenko, 5. 11. 2026).
 * Áno: názov, balík, verzia, úroveň, nálezy, nájdené knižnice, udelené povolenia.
 * Nie: model telefónu, verzia Androidu, iné appky — ani meno appky so zdieľanou identitou.
 */
internal fun shareText(
    pm: PackageManager,
    app: AppEntry,
    s: S,
    level: Level?,
    findings: List<Finding>,
    trackers: List<Tracker>,
    named: List<String>
): String = buildString {
    val version = runCatching { pm.getPackageInfo(app.packageName, 0).versionName }.getOrNull()
    appendLine(s.shareHead)
    appendLine()
    append(app.label).append(" (").append(app.packageName).append(')')
    if (!version.isNullOrBlank()) append(", ").append(s.shareVersion(version))
    appendLine()
    if (level != null) appendLine(s.shareLevel(s.level(level)))

    appendLine()
    appendLine(s.shareFindings)
    findings.forEach { appendLine("- " + it.text) }

    if (trackers.isNotEmpty()) {
        appendLine()
        appendLine(s.libsTitle)
        trackers.forEach { t ->
            val info = TRACKER_INFO[t.name]?.let { s.str(it) }
            appendLine("- " + t.name + (if (info != null) ": $info" else ""))
        }
    }

    // Len skutočne udelené a na tomto Androide platné — rovnaké pravidlo ako červené UDELENÉ v detaile.
    val granted = named.filter {
        s.permIsActive(it, it in app.granted, it in app.specialKnown, isUnusedHere(it, app.targetSdk))
    }
    appendLine()
    appendLine(s.sharePerms)
    if (granted.isEmpty()) appendLine("- " + s.sharePermsNone)
    else granted.forEach { appendLine("- " + (permLabel(it, s) ?: it)) }

    if (app.sharedWith.isNotEmpty()) {
        appendLine()
        appendLine(s.shareShared)
    }

    appendLine()
    // Veta o knižniciach len vtedy, keď nejaké sú — pri čistej appke by mätla.
    if (trackers.isNotEmpty()) appendLine(s.shareNote)
    append(s.shareSource)
}

internal fun shareFinding(context: Context, subject: String, text: String, chooserTitle: String) {
    val send = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, subject)
        .putExtra(Intent.EXTRA_TEXT, text)
    runCatching { context.startActivity(Intent.createChooser(send, chooserTitle)) }
}
