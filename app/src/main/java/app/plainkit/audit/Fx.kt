package app.plainkit.audit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random

/*
 * v1.8: po ťuknutí na tlačidlo z jeho okrajov krátko vybehnú „obvody"
 * (ako na úvodnej obrazovke) a do ~1 s zmiznú. Kreslí sa v jednej vrstve
 * nad celou appkou, takže efekt dobehne, aj keď tlačidlo prepne obrazovku.
 * Vrstva nezachytáva dotyky.
 */

private val FX_ACCENT = Color(0xFF00D4E8)

/** Funkcia, ktorou tlačidlo spustí efekt (obdĺžnik tlačidla v súradniciach okna). */
val LocalBurst = staticCompositionLocalOf<(Rect) -> Unit> { {} }

private class Burst(val traces: List<List<Offset>>)

private fun makeTraces(r: Rect, unit: Float, seed: Int): List<List<Offset>> {
    val rnd = Random(seed)
    return List(10) {
        val side = rnd.nextInt(4)
        val f = 0.1f + rnd.nextFloat() * 0.8f
        // začiatok na okraji tlačidla, smer von
        val (start, dir) = when (side) {
            0 -> Offset(r.left + r.width * f, r.top) to Offset(0f, -1f)
            1 -> Offset(r.right, r.top + r.height * f) to Offset(1f, 0f)
            2 -> Offset(r.left + r.width * f, r.bottom) to Offset(0f, 1f)
            else -> Offset(r.left, r.top + r.height * f) to Offset(-1f, 0f)
        }
        val pts = mutableListOf(start)
        var p = start
        var d = dir
        repeat(rnd.nextInt(2, 4)) { i ->
            val len = unit * (6 + rnd.nextInt(14))
            p = p + d * len
            pts.add(p)
            // pravý uhol: striedavo bokom a znova von
            d = if (i % 2 == 0) Offset(d.y, d.x) * (if (rnd.nextBoolean()) 1f else -1f) else dir
        }
        pts
    }
}

/** Bod na lomenej čiare po prejdení `frac` jej dĺžky + časť čiary dovtedy. */
private fun partial(pts: List<Offset>, frac: Float): List<Offset> {
    var total = 0f
    for (i in 0 until pts.size - 1) total += (pts[i + 1] - pts[i]).getDistance()
    var left = total * frac
    val out = mutableListOf(pts.first())
    for (i in 0 until pts.size - 1) {
        val seg = (pts[i + 1] - pts[i]).getDistance()
        if (left >= seg) { out.add(pts[i + 1]); left -= seg }
        else { if (seg > 0f) out.add(pts[i] + (pts[i + 1] - pts[i]) * (left / seg)); break }
    }
    return out
}

@Composable
fun BurstHost(content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val unit = with(LocalDensity.current) { 3f * density }   // 3 dp
    val t = remember { Animatable(1f) }
    var burst by remember { mutableStateOf<Burst?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    var seed by remember { mutableStateOf(0) }

    val fire: (Rect) -> Unit = remember(unit) {
        { r ->
            if (r != Rect.Zero) {
                seed++
                burst = Burst(makeTraces(r, unit, seed))
                job?.cancel()
                job = scope.launch {
                    t.snapTo(0f)
                    t.animateTo(1f, tween(1000, easing = LinearEasing))
                    burst = null
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalBurst provides fire) { content() }
        Canvas(Modifier.fillMaxSize()) {
            val b = burst ?: return@Canvas
            val v = t.value
            // rast: rýchlo vybehne (prvých 60 %), potom len mizne
            val grow = (v / 0.6f).coerceAtMost(1f).let { 1f - (1f - it) * (1f - it) }
            val alpha = (1f - v).coerceIn(0f, 1f)
            for (tr in b.traces) {
                val part = partial(tr, grow)
                for (i in 0 until part.size - 1) {
                    drawLine(FX_ACCENT.copy(alpha = 0.55f * alpha), part[i], part[i + 1], strokeWidth = 2.5f)
                }
                drawCircle(FX_ACCENT.copy(alpha = 0.5f * alpha), radius = 5f, center = part.first())
                val head = part.last()
                drawCircle(FX_ACCENT.copy(alpha = 0.2f * alpha), radius = 12f, center = head)
                drawCircle(FX_ACCENT.copy(alpha = alpha), radius = 4f, center = head)
            }
        }
    }
}

/** Zapamätá si polohu tlačidla a pri ťuknutí spustí efekt. */
@Composable
private fun fxClick(onClick: () -> Unit): Pair<Modifier, () -> Unit> {
    val fire = LocalBurst.current
    val bounds = remember { arrayOf(Rect.Zero) }
    val mod = Modifier.onGloballyPositioned { bounds[0] = it.boundsInRoot() }
    return mod to { fire(bounds[0]); onClick() }
}

@Composable
fun FxTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val (m, click) = fxClick(onClick)
    TextButton(onClick = click, modifier = modifier.then(m), enabled = enabled, content = content)
}

@Composable
fun FxOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val (m, click) = fxClick(onClick)
    OutlinedButton(onClick = click, modifier = modifier.then(m), enabled = enabled, content = content)
}

@Composable
fun FxButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    content: @Composable RowScope.() -> Unit
) {
    val (m, click) = fxClick(onClick)
    Button(onClick = click, modifier = modifier.then(m), enabled = enabled, shape = shape, content = content)
}
