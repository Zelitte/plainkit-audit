package app.plainkit.audit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

private val BG = Color(0xFF0A0F14)
private val ACCENT = Color(0xFF00D4E8)
private val TRACE = Color(0xFF12313A)
private val MUTED = Color(0xFF7FA6AE)

/**
 * Veľkosť písma, ktorá NErastie so systémovým nastavením „Veľkosť písma".
 * Len pre logo — to je obrázok z písmen, nie text na čítanie. Bežný text
 * nechávame rásť, to je dôležité pre ľudí, ktorí zle vidia.
 */
@Composable
fun fixedSp(value: Float): TextUnit {
    val scale = LocalDensity.current.fontScale
    return (value / scale.coerceAtLeast(1f)).sp
}

/** Pri veľkom písme v systéme (Samsung ho má často zapnuté) prepneme rozloženie. */
@Composable
fun isLargeFont(): Boolean = LocalDensity.current.fontScale > 1.3f

private class Trace(val pts: List<Offset>, val phase: Float, val speed: Float)

/** Vygeneruje pravouhlé spoje v normalizovaných súradniciach 0..1. */
private fun buildTraces(seed: Int, count: Int): List<Trace> {
    val rnd = Random(seed)
    val out = mutableListOf<Trace>()
    repeat(count) {
        var x = rnd.nextInt(0, 13) / 12f
        var y = rnd.nextInt(0, 21) / 20f
        val pts = mutableListOf(Offset(x, y))
        var horizontal = rnd.nextBoolean()
        val steps = rnd.nextInt(3, 7)
        repeat(steps) {
            val len = (rnd.nextInt(1, 4)) / 12f
            val dir = if (rnd.nextBoolean()) 1f else -1f
            if (horizontal) x = (x + len * dir).coerceIn(0.02f, 0.98f)
            else y = (y + len * dir).coerceIn(0.02f, 0.98f)
            pts.add(Offset(x, y))
            horizontal = !horizontal
        }
        out.add(Trace(pts, rnd.nextFloat(), 0.5f + rnd.nextFloat()))
    }
    return out
}

private fun pointAt(pts: List<Offset>, w: Float, h: Float, t: Float): Offset {
    val scaled = pts.map { Offset(it.x * w, it.y * h) }
    var total = 0f
    for (i in 0 until scaled.size - 1) total += (scaled[i + 1] - scaled[i]).getDistance()
    if (total <= 0f) return scaled.first()
    var target = t * total
    for (i in 0 until scaled.size - 1) {
        val seg = (scaled[i + 1] - scaled[i]).getDistance()
        if (target <= seg) {
            val f = if (seg == 0f) 0f else target / seg
            return Offset(
                scaled[i].x + (scaled[i + 1].x - scaled[i].x) * f,
                scaled[i].y + (scaled[i + 1].y - scaled[i].y) * f
            )
        }
        target -= seg
    }
    return scaled.last()
}

private fun DrawScope.drawBoard(traces: List<Trace>, t: Float) {
    val w = size.width
    val h = size.height
    for (tr in traces) {
        val scaled = tr.pts.map { Offset(it.x * w, it.y * h) }
        for (i in 0 until scaled.size - 1) {
            drawLine(TRACE, scaled[i], scaled[i + 1], strokeWidth = 3f)
        }
        drawCircle(TRACE, radius = 7f, center = scaled.first())
        drawCircle(TRACE, radius = 7f, center = scaled.last())

        val p = ((t * tr.speed + tr.phase) % 1f)
        val dot = pointAt(tr.pts, w, h, p)
        drawCircle(ACCENT.copy(alpha = 0.18f), radius = 14f, center = dot)
        drawCircle(ACCENT, radius = 4f, center = dot)
    }
}

private class Spark(val angle: Float, val speed: Float, val color: Color)

private fun DrawScope.drawFireworks(sparks: List<Spark>, t: Float) {
    if (t <= 0f) return
    val c = Offset(size.width / 2f, size.height / 2.4f)
    val reach = hypot(size.width, size.height) * 0.45f
    for (s in sparks) {
        val r = s.speed * t * reach
        val x = c.x + cos(s.angle) * r
        val y = c.y + sin(s.angle) * r + (t * t) * size.height * 0.25f
        val alpha = (1f - t).coerceIn(0f, 1f)
        drawCircle(s.color.copy(alpha = alpha), radius = 5f * (1f - t * 0.6f), center = Offset(x, y))
    }
}

@Composable
private fun BoardBackground(burst: Float = 0f) {
    val traces = remember { buildTraces(seed = 42, count = 22) }
    val sparks = remember {
        val rnd = Random(7)
        List(140) {
            Spark(
                angle = rnd.nextFloat() * 6.2832f,
                speed = 0.25f + rnd.nextFloat() * 0.75f,
                color = if (rnd.nextBoolean()) ACCENT else Color(0xFFFFFFFF)
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "board")
    val flow by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBoard(traces, flow)
        drawFireworks(sparks, burst)
    }
}

@Composable
private fun Logo() {
    Text(
        text = "plainkit.app",
        color = ACCENT,
        fontSize = fixedSp(34f),
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        softWrap = false
    )
    Text(
        text = "audit",
        color = Color(0xFFBFD9DF),
        fontSize = fixedSp(20f),
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        modifier = Modifier.padding(bottom = 36.dp)
    )
}

/** navigationBarsPadding = odsadenie od systémovej lišty (tri tlačidlá / gestá dole). */
@Composable
private fun Footer(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFF4E6B72),
        fontSize = 11.sp,
        modifier = modifier
            .navigationBarsPadding()
            .padding(16.dp)
    )
}

/**
 * Mriežka tlačidiel jazykov: 2 v rade, pri veľkom písme 1 v rade.
 * Používa ju úvodná obrazovka aj Nastavenia.
 */
@Composable
fun LangGrid(
    selected: Lang?,
    modifier: Modifier = Modifier,
    button: @Composable (lang: Lang, selected: Boolean, modifier: Modifier) -> Unit
) {
    val perRow = if (isLargeFont()) 1 else 2
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Lang.entries.chunked(perRow).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { lang ->
                    button(lang, lang == selected, Modifier.weight(1f))
                }
                // posledný riadok s jedným tlačidlom nech má rovnakú šírku ako ostatné
                repeat(perRow - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** Prvé spustenie: výber jazyka + ohňostroj. */
@Composable
fun OnboardingScreen(onChosen: (Lang) -> Unit) {
    val ctx = LocalContext.current
    var chosen by remember { mutableStateOf<Lang?>(null) }
    val burst = remember { Animatable(0f) }
    val current = chosen
    val s = remember(current) { current?.let { S(ctx, it) } }

    LaunchedEffect(chosen) {
        val c = chosen ?: return@LaunchedEffect
        burst.animateTo(1f, animationSpec = tween(2000, easing = LinearEasing))
        delay(1500)
        onChosen(c)
    }

    Box(modifier = Modifier.fillMaxSize().background(BG)) {
        BoardBackground(burst = burst.value)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Logo()

            if (s == null) {
                LangGrid(selected = null, modifier = Modifier.fillMaxWidth()) { lang, _, mod ->
                    LangButton(lang.nativeName, mod) { chosen = lang }
                }
                // pred výberom jazyka nevieme, komu píšeme — angličtina je najbezpečnejšia
                Text(
                    text = "no ads · no signup · nothing is uploaded",
                    color = MUTED,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                )
            } else {
                Text(
                    text = s.claims,
                    color = ACCENT,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = s.disclosure,
                    color = MUTED,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        }

        Footer(s?.partOf ?: "plainkit.app", Modifier.align(Alignment.BottomEnd))
    }
}

/** Každé ďalšie spustenie: to isté bez ohňostroja, preklepnuteľné. */
@Composable
fun SplashScreen(s: S, onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDone() }
    ) {
        BoardBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Logo()
            Text(
                text = s.claims,
                color = ACCENT,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = s.disclosure,
                color = MUTED,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            Text(
                text = s.tapToContinue,
                color = Color(0xFF4E6B72),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp)
            )
        }

        Footer(s.partOf, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun LangButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ACCENT,
            contentColor = Color(0xFF07090F)
        ),
        modifier = modifier
    ) {
        Text(label, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
