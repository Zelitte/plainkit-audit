package app.plainkit.audit

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
            TextButton(onClick = onBack) { Text("← " + s.back) }
            Text(s.settings, style = MaterialTheme.typography.titleLarge)
        }

        Text(
            s.languageTitle,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LangChip(s.slovak, s.lang == Lang.SK) { onLangChange(Lang.SK) }
            LangChip(s.english, s.lang == Lang.EN) { onLangChange(Lang.EN) }
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

        Text(
            text = s.partOf,
            style = MaterialTheme.typography.bodyMedium,
            color = LINK.copy(alpha = alpha),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 28.dp)
                .clickable { open("https://plainkit.app") }
        )
    }
}

@Composable
private fun LangChip(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}