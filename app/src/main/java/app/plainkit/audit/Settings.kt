package app.plainkit.audit

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings as AndroidSettings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

private val LINK = Color(0xFF00D4E8)

@Composable
fun SettingsScreen(
    s: S,
    justCleared: Boolean,
    onLangChange: (Lang) -> Unit,
    onClearChanges: () -> Unit,
    onClearScans: () -> Unit,
    onShowIntro: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun open(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    val pulse = rememberInfiniteTransition(label = "link")
    val alpha by pulse.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            TextButton(onClick = onBack) { Text("← " + s.back, maxLines = 1) }
            Text(
                s.settings,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            s.languageTitle,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
        )
        // tá istá mriežka ako na úvodnej obrazovke (Onboarding.kt)
        LangGrid(selected = s.lang, modifier = Modifier.fillMaxWidth()) { lang, selected, mod ->
            LangChip(lang.nativeName, selected, mod) { onLangChange(lang) }
        }

        Text(
            s.dataTitle,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 6.dp)
        )
        OutlinedButton(
            onClick = onClearChanges,
            modifier = Modifier.fillMaxWidth()
        ) { Text(s.clearChanges) }

        OutlinedButton(
            onClick = onClearScans,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text(s.clearScans) }
        Text(
            s.clearScansNote,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )

        OutlinedButton(
            onClick = onShowIntro,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) { Text(s.showIntro) }

        if (justCleared) {
            Text(
                s.cleared,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // ── Súkromné DNS (v1.5) ──
        // Stav sa číta cez verejné API Androidu (LinkProperties). Potrebuje len
        // bežné povolenie ACCESS_NETWORK_STATE — appka sa k sieti NEpripája.
        // Na Androide 8 Súkromné DNS neexistuje, sekcia sa vtedy neukáže.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            var recheck by remember { mutableIntStateOf(0) }
            var copiedKey by remember { mutableStateOf<String?>(null) }
            val dnsStatus = remember(recheck, s) { privateDnsText(context, s) }
            Text(
                s.dnsTitle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 6.dp)
            )
            Text(dnsStatus, style = MaterialTheme.typography.bodyMedium)
            Text(
                s.dnsGuide,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            CommandLine("", DNS_HOST, s, copiedKey) { copiedKey = it }
            Text(
                s.dnsTradeoff,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row {
                TextButton(onClick = {
                    runCatching {
                        context.startActivity(Intent(AndroidSettings.ACTION_WIRELESS_SETTINGS))
                    }
                }) { Text(s.dnsOpen) }
                TextButton(onClick = { recheck++ }) { Text(s.dnsRecheck) }
            }
        }

        // Číslo verzie sa číta z nainštalovaného balíka, nie z konštanty v kóde —
        // tým vždy sedí s tým, čo je naozaj v telefóne (aj s príponou -debug).
        // Zámerne bez prekladu: „v1.2" je zrozumiteľné vo všetkých jazykoch.
        val version = remember {
            runCatching {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }.getOrNull()
        }

        Text(
            s.aboutTitle,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 28.dp, bottom = 6.dp)
        )
        Text(s.aboutText, style = MaterialTheme.typography.bodySmall)

        Text(
            text = s.privacyPolicy,
            style = MaterialTheme.typography.bodyMedium,
            color = LINK,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 14.dp)
                .clickable { open("https://plainkit.app/audit") }
        )

        // Nahlásenie nesprávneho nálezu. Do adresy ide LEN verzia appky —
        // nie model telefónu ani zoznam aplikácií: prehliadač adresu odošle
        // GitHubu hneď pri otvorení, ešte skôr, než sa človek rozhodne niečo
        // odoslať. Ostatné dopíše sám, ak chce.
        Text(
            text = s.reportLink,
            style = MaterialTheme.typography.bodyMedium,
            color = LINK,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                    // Titulok jednotne po anglicky — hlásenia prídu zo 6 jazykov, takto sa dajú triediť.
                    val title = android.net.Uri.encode("Incorrect finding (v${version ?: "?"})")
                    open("https://github.com/Zelitte/plainkit-audit/issues/new?title=$title")
                }
        )
        Text(
            s.reportNote,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = s.partOf,
            style = MaterialTheme.typography.bodyMedium,
            color = LINK.copy(alpha = alpha),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable { open("https://plainkit.app") }
        )

        // v1.7, len debug build: počty z rozhraní mimoriadnych schopností (x = rozhranie odmietlo)
        // a jednorazový prieskum, koľko appiek je zabalených „packerom" (zašifrovaný kód).
        if ((context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            var packed by remember { mutableStateOf("") }
            Text(
                text = "caps: " + AppCache.capsDebug,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
            TextButton(onClick = {
                packed = "…"
                Thread { packed = debugPackedReport(AppCache.apps ?: emptyList()) }.start()
            }) { Text("debug: hľadať zašifrované appky", fontSize = 12.sp) }
            if (packed.isNotEmpty()) {
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(packed, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (version != null) {
            Text(
                text = "v$version",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp, bottom = 28.dp)
            )
        } else {
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun LangChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) {
            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) {
            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private const val DNS_HOST = "dns.adguard-dns.com"

/** Stav Súkromného DNS na aktívnej sieti (Android 9+). */
private fun privateDnsText(context: Context, s: S): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return s.dnsUnknown
    return runCatching {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return s.dnsUnknown
        val net = cm.activeNetwork ?: return s.dnsUnknown
        val lp = cm.getLinkProperties(net) ?: return s.dnsUnknown
        val host = lp.privateDnsServerName
        when {
            lp.isPrivateDnsActive && !host.isNullOrBlank() -> s.dnsOn(host)
            lp.isPrivateDnsActive -> s.dnsAuto
            else -> s.dnsOff
        }
    }.getOrElse { s.dnsUnknown }
}

/**
 * Len na ladenie (v1.7): ktoré appky nesú knižnicu známeho „packera" — kód je
 * zašifrovaný a skener v ňom nič nenájde. Mená knižníc sú len kandidáti na
 * overenie, do appky ako tvrdenie zatiaľ NEJDÚ.
 */
private val PACKER_LIBS = listOf(
    "libjiagu", "libprotectClass", "libsecexe", "libsecmain", "libDexHelper", "libexec.so",
    "libshell", "libtup", "liblegu", "libnesec", "libbaiduprotect", "libSecShell", "libitsec", "libmobisec"
)

internal fun debugPackedReport(apps: List<AppEntry>): String {
    val hits = mutableListOf<String>()
    var noDex = 0
    for (app in apps) {
        var dex = 0
        val libs = mutableSetOf<String>()
        for (path in app.apkPaths) {
            try {
                java.util.zip.ZipFile(path).use { zip ->
                    for (e in zip.entries()) {
                        val n = e.name
                        if (n.startsWith("classes") && n.endsWith(".dex")) dex++
                        if (n.startsWith("lib/") || n.startsWith("assets/")) {
                            val base = n.substringAfterLast('/')
                            PACKER_LIBS.firstOrNull { base.startsWith(it) }?.let { libs.add(base) }
                        }
                    }
                }
            } catch (e: Exception) { }
        }
        if (dex == 0) noDex++
        if (libs.isNotEmpty()) hits.add("${app.label} (${app.packageName}): ${libs.joinToString()}")
    }
    return "appky: ${apps.size}, bez dex: $noDex, packer: ${hits.size}\n" + hits.joinToString("\n")
}
