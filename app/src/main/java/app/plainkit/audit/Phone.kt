package app.plainkit.audit

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.time.LocalDate

/*
 * v1.8: „Kontrola telefónu" a „Kto čo smie".
 * Všetko cez verejné rozhrania Androidu, bez nového povolenia a bez internetu.
 * Tvrdíme len to, čo sa dá overiť — kontrola nie je antivírus (veta check_note).
 */

private const val P = "android.permission."
private const val INSTALL_PERM = "${P}REQUEST_INSTALL_PACKAGES"

/** Jeden bod kontroly. `action` = čo otvoriť v nastaveniach (null = bez tlačidla). */
class CheckItem(
    val level: Level,
    val text: String,
    val action: (() -> Unit)? = null,
    val byPerm: Boolean = false
)

/** Mená appiek: prvých 5 a „a ďalších N". */
private fun names(list: List<AppEntry>, s: S): String {
    val sorted = list.map { it.label }.sortedBy { it.lowercase() }
    val head = sorted.take(5).joinToString(", ")
    return if (sorted.size > 5) head + " " + s.sharedMore(sorted.size - 5) else head
}

private fun Context.openFirst(vararg actions: String) {
    for (a in actions) {
        val ok = runCatching {
            startActivity(Intent(a).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true
        }.getOrDefault(false)
        if (ok) return
    }
}

/** Známe znaky rootu. Nič nesľubujeme — skrytý root sa takto nájsť nedá. */
private fun rootSigns(apps: List<AppEntry>): List<String> {
    val out = mutableListOf<String>()
    val su = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su", "/su/bin/su",
        "/system/sd/xbin/su", "/data/local/su", "/data/local/bin/su", "/data/local/xbin/su"
    ).firstOrNull { runCatching { File(it).exists() }.getOrDefault(false) }
    if (su != null) out.add(su)
    if (Build.TAGS?.contains("test-keys") == true) out.add("test-keys")
    val rootApps = setOf(
        "com.topjohnwu.magisk", "eu.chainfire.supersu", "me.weishu.kernelsu",
        "com.koushikdutta.superuser", "com.noshufou.android.su"
    )
    apps.filter { it.packageName in rootApps }.forEach { out.add(it.label) }
    return out
}

fun phoneChecks(ctx: Context, apps: List<AppEntry>, s: S, onByPerm: () -> Unit): List<CheckItem> {
    val out = mutableListOf<CheckItem>()
    val cr = ctx.contentResolver

    // 1. bezpečnostná záplata
    val patch = Build.VERSION.SECURITY_PATCH
    val date = runCatching { LocalDate.parse(patch) }.getOrNull()
    val openUpdate = { ctx.openFirst("android.settings.SYSTEM_UPDATE_SETTINGS", Settings.ACTION_DEVICE_INFO_SETTINGS) }
    out += when {
        date == null -> CheckItem(Level.INFO, s.checkPatchUnknown, openUpdate)
        date.isBefore(LocalDate.now().minusMonths(3)) -> CheckItem(Level.WARN, s.checkPatchOld(patch), openUpdate)
        else -> CheckItem(Level.OK, s.checkPatchOk(patch))
    }

    // 2. zámok obrazovky
    val secure = runCatching { ctx.getSystemService(KeyguardManager::class.java)?.isDeviceSecure }.getOrNull()
    if (secure != null) {
        out += if (secure) CheckItem(Level.OK, s.checkLockOk)
               else CheckItem(Level.ALERT, s.checkLockNo, action = { ctx.openFirst(Settings.ACTION_SECURITY_SETTINGS) })
    }

    // 3. ladenie USB / možnosti pre vývojárov
    val adb = runCatching { Settings.Global.getInt(cr, Settings.Global.ADB_ENABLED, 0) == 1 }.getOrDefault(false)
    val dev = runCatching { Settings.Global.getInt(cr, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1 }.getOrDefault(false)
    val openDev = { ctx.openFirst(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }
    out += when {
        adb -> CheckItem(Level.WARN, s.checkAdbOn, openDev)
        dev -> CheckItem(Level.INFO, s.checkDevOn, openDev)
        else -> CheckItem(Level.OK, s.checkDevOff)
    }

    // 4. kto smie inštalovať ďalšie appky (skutočný stav z AppOps, viď toEntry)
    val installers = apps.filter { INSTALL_PERM in it.granted && INSTALL_PERM in it.specialKnown }
    out += if (installers.isEmpty()) CheckItem(Level.OK, s.checkInstallNone)
    else CheckItem(
        if (installers.any { !it.isSystem }) Level.WARN else Level.INFO,
        s.checkInstallSome(names(installers, s)),
        action = { ctx.openFirst(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) }
    )

    // 5. služba dostupnosti / prístup k notifikáciám
    val strong = apps.filter { Cap.A11Y in it.caps || Cap.NOTIF in it.caps }
    out += if (strong.isEmpty()) CheckItem(Level.OK, s.checkCapsNone)
    else CheckItem(
        if (strong.any { !it.isSystem }) Level.WARN else Level.INFO,
        s.checkCapsSome(names(strong, s)),
        byPerm = true,
        action = onByPerm
    )

    // 6. root — len známe znaky
    val root = rootSigns(apps)
    out += if (root.isEmpty()) CheckItem(Level.OK, s.checkRootNone)
           else CheckItem(Level.WARN, s.checkRootFound(root.joinToString(", ")))
    return out
}

@Composable
private fun ScreenHeader(title: String, back: String, onBack: () -> Unit) {
    // systémové „späť" vráti na predchádzajúcu obrazovku, nie von z appky
    BackHandler { onBack() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        FxTextButton(onClick = onBack) { Text("← $back", maxLines = 1) }
        Text(
            title,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CheckScreen(
    s: S,
    apps: List<AppEntry>,
    onBack: () -> Unit,
    onByPerm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    val items = remember(tick, apps, s) { phoneChecks(context, apps, s, onByPerm) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        ScreenHeader(s.checkTitle, s.back, onBack)
        Text(
            s.checkNote,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        for (item in items) {
            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                Text("●  ", color = levelColor(item.level), fontSize = 14.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.text, style = MaterialTheme.typography.bodyMedium)
                    val act = item.action
                    if (act != null) {
                        Text(
                            (if (item.byPerm) s.navByPerm else s.checkOpen) + " ▸",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { act() }.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
        FxTextButton(
            onClick = { tick++ },
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        ) { Text(s.dnsRecheck) }
    }
}

/** Jedna skupina v „Kto čo smie". */
private class PermBucket(val title: String, val apps: List<AppEntry>)

private fun buckets(apps: List<AppEntry>, s: S): List<PermBucket> {
    fun runtime(perms: Set<String>) = apps.filter { a ->
        perms.any { it in a.granted && !isUnusedHere(it, a.targetSdk) }
    }
    fun special(p: String) = apps.filter { p in it.granted && p in it.specialKnown }
    fun cap(c: Cap) = apps.filter { c in it.caps }
    fun group(res: Int) = SENSITIVE.first { it.res == res }.permissions
    fun lbl(p: String) = permLabel(p, s) ?: p

    return listOf(
        PermBucket(s.capA11yShort, cap(Cap.A11Y)),
        PermBucket(s.capNotifShort, cap(Cap.NOTIF)),
        PermBucket(s.capImeShort, cap(Cap.IME)),
        PermBucket(s.capAdminShort, cap(Cap.ADMIN)),
        PermBucket(s.str(R.string.byperm_microphone), runtime(group(R.string.group_microphone))),
        PermBucket(s.str(R.string.byperm_camera), runtime(group(R.string.group_camera))),
        PermBucket(s.str(R.string.byperm_location), runtime(group(R.string.group_location))),
        PermBucket(s.str(R.string.byperm_files), special("${P}MANAGE_EXTERNAL_STORAGE")),
        PermBucket(s.str(R.string.byperm_sms), runtime(group(R.string.group_sms))),
        PermBucket(s.str(R.string.byperm_calls), runtime(group(R.string.group_call_log))),
        PermBucket(s.str(R.string.byperm_contacts), runtime(group(R.string.group_contacts))),
        PermBucket(s.str(R.string.byperm_calendar), runtime(group(R.string.group_calendar))),
        PermBucket(s.str(R.string.byperm_media), runtime(group(R.string.group_media))),
        PermBucket(s.str(R.string.byperm_phone), runtime(group(R.string.group_phone))),
        PermBucket(s.str(R.string.byperm_activity), runtime(group(R.string.group_activity))),
        PermBucket(lbl("${P}SYSTEM_ALERT_WINDOW"), special("${P}SYSTEM_ALERT_WINDOW")),
        PermBucket(lbl("${P}PACKAGE_USAGE_STATS"), special("${P}PACKAGE_USAGE_STATS")),
        PermBucket(lbl(INSTALL_PERM), special(INSTALL_PERM)),
        PermBucket(lbl("${P}WRITE_SETTINGS"), special("${P}WRITE_SETTINGS")),
        PermBucket(lbl(BATTERY_PERM), special(BATTERY_PERM))
    ).map { b -> PermBucket(b.title, b.apps.sortedBy { it.label.lowercase() }) }
}

@Composable
fun ByPermScreen(
    s: S,
    apps: List<AppEntry>,
    scopeName: String,
    onBack: () -> Unit,
    onOpenApp: (AppEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val list = remember(apps, s) { buckets(apps, s) }
    val open = remember { mutableStateSetOf<String>() }
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        ScreenHeader(s.byPermTitle, s.back, onBack)
        Text(
            s.byPermScope(scopeName),
            style = MaterialTheme.typography.bodySmall,
            color = muted,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        for (b in list) {
            Text(
                b.title.uppercase() + "  (" + b.apps.size + ")",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = if (b.apps.isEmpty()) muted else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
            )
            if (b.apps.isEmpty()) {
                Text(s.byPermNone, style = MaterialTheme.typography.bodySmall, color = muted)
                continue
            }
            val all = b.title in open
            val shown = if (all) b.apps else b.apps.take(5)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                for (a in shown) {
                    Text(
                        a.label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenApp(a) }
                            .padding(vertical = 4.dp)
                    )
                }
            }
            if (b.apps.size > 5) {
                Text(
                    if (all) s.changesLess else s.sharedMore(b.apps.size - 5) + " ▸",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { if (all) open.remove(b.title) else open.add(b.title) }
                        .padding(vertical = 4.dp)
                )
            }
        }
        Text("", modifier = Modifier.padding(bottom = 24.dp))
    }
}
