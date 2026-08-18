package app.plainkit.audit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Accent = Color(0xFF00D4E8)
private val Ink = Color(0xFF07090F)

private val PlainkitDark = darkColorScheme(
    primary = Accent,
    onPrimary = Ink,
    primaryContainer = Color(0xFF0C2A33),
    onPrimaryContainer = Accent,
    secondary = Color(0xFF6FD3E0),
    onSecondary = Ink,
    background = Color(0xFF0A0F14),
    onBackground = Color(0xFFDBE7EA),
    surface = Color(0xFF0E1720),
    onSurface = Color(0xFFDBE7EA),
    surfaceVariant = Color(0xFF13202A),
    onSurfaceVariant = Color(0xFF8BA6AD),
    outline = Color(0xFF1E3B47),
    outlineVariant = Color(0xFF162C36),
    error = Color(0xFFFF6B6B),
    onError = Ink,
    errorContainer = Color(0xFF3A1418),
    onErrorContainer = Color(0xFFFF9B9B)
)

/** Hranaté tvary — technický, prístrojový dojem namiesto zaoblených bublín. */
private val SharpShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(3.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(6.dp)
)

@Composable
fun AuditTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PlainkitDark,
        shapes = SharpShapes,
        content = content
    )
}