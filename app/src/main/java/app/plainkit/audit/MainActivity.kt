package app.plainkit.audit

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipFile

private const val MAX_DEPTH = 6

private val OkGreen = Color(0xFF3FBF7F)
private val WarnAmber = Color(0xFFFFB020)

object AppCache {
    var apps: List<AppEntry>? = null
    val scanned = mutableMapOf<String, List<Tracker>>()
}

data class AppEntry(
    val label: String,
    val packageName: String,
    val isSystem: Boolean,
    val permissions: List<String>,
    val granted: Set<String>,
    val apkPaths: List<String>,
    val versionCode: Long
)

private fun codeOf(pkg: PackageInfo): Long =
    if (Build.VERSION.SDK_INT >= 28) pkg.longVersionCode
    else @Suppress("DEPRECATION") pkg.versionCode.toLong()

private fun loadApps(pm: PackageManager): List<AppEntry> {
    return pm.getInstalledPackages(PackageManager.GET_PERMISSIONS).map { pkg ->
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
        AppEntry(
            label = info?.loadLabel(pm)?.toString() ?: pkg.packageName,
            packageName = pkg.packageName,
            isSystem = ((info?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM) != 0,
            permissions = perms,
            granted = granted,
            apkPaths = paths,
            versionCode = codeOf(pkg)
        )
    }.sortedBy { it.label.lowercase() }
}

private fun collectPrefixes(text: String, out: MutableSet<String>) {
    var i = 0
    val n = text.length
    while (i < n) {
        if (text[i] != 'L') {
            i++
            continue
        }

        var j = i + 1
        var slashes = 0
        while (j < n) {
            val c = text[j]
            when {
                c == ';' -> break
                c == '/' -> {
                    slashes++
                    j++
                }

                c.isLetterOrDigit() || c == '_' || c == '$' -> j++
                else -> break
            }
        }

        if (j < n && text[j] == ';' && slashes > 0) {
            var k = i + 1
            var depth = 0
            while (k < j && depth < MAX_DEPTH) {
                if (text[k] == '/') {
                    out.add(text.substring(i, k + 1))
                    depth++
                }
                k++
            }
        }
        i = j + 1
    }
}

private fun scanApk(paths: List<String>): List<Tracker> {
    val prefixes = HashSet<String>(8192)
    for (path in paths) {
        try {
            ZipFile(path).use { zip ->
                val dexEntries = zip.entries().toList().filter {
                    it.name.startsWith("classes") && it.name.endsWith(".dex")
                }
                for (entry in dexEntries) {
                    val text = zip.getInputStream(entry).use { input ->
                        String(input.readBytes(), Charsets.ISO_8859_1)
                    }
                    collectPrefixes(text, prefixes)
                }
            }
        } catch (e: Exception) {
            // APK sa nedá prečítať — preskočím
        }
    }
    return TRACKERS.filter { it.prefix in prefixes }
}

private suspend fun persist(db: AuditDb, app: AppEntry, trackers: List<Tracker>, s: S) {
    val dao = db.dao()
    val old = dao.scan(app.packageName)
    val names = trackers.map { it.name }
    val text = diffText(old, names, app.permissions, s)
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
}

private suspend fun scanAllApps(
    apps: List<AppEntry>,
    db: AuditDb,
    s: S,
    onResult: (String, List<Tracker>) -> Unit,
    onProgress: (Int) -> Unit
) = coroutineScope {
    val counter = AtomicInteger(0)
    val workers = 3
    val chunkSize = ((apps.size + workers - 1) / workers).coerceAtLeast(1)
    apps.chunked(chunkSize).map { chunk ->
        async(Dispatchers.IO) {
            for (app in chunk) {
                val result = scanApk(app.apkPaths)
                persist(db, app, result, s)
                withContext(Dispatchers.Main) {
                    onResult(app.packageName, result)
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
            AuditTheme {
                val ctx = LocalContext.current
                var lang by remember { mutableStateOf(Prefs.lang(ctx)) }
                var splashDone by rememberSaveable { mutableStateOf(false) }
                val current = lang

                when {
                    current == null -> OnboardingScreen { chosen ->
                        Prefs.setLang(ctx, chosen)
                        lang = chosen
                        splashDone = true
                    }

                    !splashDone -> SplashScreen(S(current)) { splashDone = true }

                    else -> Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppListScreen(
                            s = S(current),
                            onLangChange = { chosen ->
                                Prefs.setLang(ctx, chosen)
                                lang = chosen
                            },
                            onShowIntro = {
                                Prefs.clearLang(ctx)
                                AppCache.apps = null
                                AppCache.scanned.clear()
                                lang = null
                                splashDone = false
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
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

@Composable
private fun levelColor(level: Level?): Color = when (level) {
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

    // krátke popisky dlaždíc — zámerne tu, aby sa kvôli dvom slovám neprepisovali Strings.kt
    val appsLabel = if (s.lang == Lang.SK) "aplikácií" else "apps"
    val sysLabel = if (s.lang == Lang.SK) "systémových" else "system"

    var apps by remember { mutableStateOf(AppCache.apps ?: emptyList()) }
    var showSystem by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }
    var scanning by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<Int?>(null) }
    var changes by remember { mutableStateOf<List<ChangeRecord>>(emptyList()) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var justCleared by remember { mutableStateOf(false) }
    val scanned = remember {
        mutableStateMapOf<String, List<Tracker>>().apply { putAll(AppCache.scanned) }
    }

    fun store(pkg: String, result: List<Tracker>) {
        scanned[pkg] = result
        AppCache.scanned[pkg] = result
    }

    LaunchedEffect(Unit) {
        if (AppCache.apps == null) {
            val loaded = withContext(Dispatchers.IO) { loadApps(context.packageManager) }
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
                    scanned.clear()
                    AppCache.scanned.clear()
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

    val systemCount = apps.count { it.isSystem }
    val scanTarget = apps.filter { showSystem || !it.isSystem }

    val visible = scanTarget
        .filter {
            query.isBlank() ||
                    it.label.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
        }
        .sortedWith(
            compareByDescending<AppEntry> { app ->
                scanned[app.packageName]
                    ?.let { worstLevel(buildFindings(app.permissions, app.granted, it, s)).ordinal }
                    ?: -1
            }.thenBy { it.label.lowercase() }
        )

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
            Text(
                text = "plainkit.",
                fontFamily = FontFamily.Monospace,
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "audit",
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .weight(1f)
            )
            TextButton(onClick = { showSettings = true }) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(s.settings, fontSize = 13.sp)
            }
        }

        // ── dlaždice ──
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(
                value = if (apps.isEmpty()) "…" else visible.size.toString(),
                label = appsLabel,
                active = false,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = systemCount.toString(),
                label = sysLabel,
                active = showSystem,
                modifier = Modifier.weight(1f),
                onClick = { showSystem = !showSystem }
            )
        }

        // ── hľadanie ──
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    s.searchLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
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
        Button(
            onClick = {
                val target = scanTarget
                progress = 0
                scope.launch {
                    scanAllApps(
                        apps = target,
                        db = db,
                        s = s,
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
                text = progress?.let { s.scanProgress(it, scanTarget.size) } ?: s.scanAll,
                fontFamily = FontFamily.Monospace
            )
        }

        val progressNow = progress
        if (progressNow != null) {
            val total = scanTarget.size.coerceAtLeast(1)
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
        Spacer(Modifier.height(10.dp))

        // ── zoznam ──
        LazyColumn {
            if (changes.isNotEmpty() && query.isBlank()) {
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
                        changes.take(8).forEach { ch ->
                            Text(
                                text = "${ch.label} — ${ch.text}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Text(
                                text = dateFmt.format(Date(ch.at)),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(visible, key = { it.packageName }) { app ->
                val trackers = scanned[app.packageName]
                val findings = trackers?.let {
                    buildFindings(app.permissions, app.granted, it, s)
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
                val tech = app.permissions.filterNot { PERM_NAMES.containsKey(it) }
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
                            if (expanded == app.packageName && !scanned.containsKey(app.packageName)) {
                                scanning = app.packageName
                                scope.launch {
                                    val result =
                                        withContext(Dispatchers.IO) { scanApk(app.apkPaths) }
                                    persist(db, app, result, s)
                                    store(app.packageName, result)
                                    changes = db.dao().recentChanges()
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
                                if (level != null) {
                                    Text(
                                        text = s.level(level),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = stripe
                                    )
                                }
                                Text(
                                    app.label,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    app.packageName,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = s.summary(app.permissions.size, trackers?.size),
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
                                    "—",
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

                            if (namedSorted.isEmpty()) {
                                Text(s.noNamedPerms, style = MaterialTheme.typography.bodySmall)
                            } else {
                                namedSorted.forEach { p ->
                                    val label = permLabel(p, s) ?: p
                                    val isGranted = p in app.granted
                                    Text(
                                        text = "• $label" + s.permMark(p, isGranted),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (s.permIsActive(p, isGranted))
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (tech.isNotEmpty()) {
                                TextButton(
                                    onClick = { showTech = !showTech },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        if (showTech) s.hideTech else s.techPerms(tech.size),
                                        fontSize = 13.sp
                                    )
                                }
                                if (showTech) {
                                    tech.forEach { p ->
                                        Text(
                                            text = "· " + p.removePrefix("android.permission."),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}