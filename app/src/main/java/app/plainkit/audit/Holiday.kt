package app.plainkit.audit

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * v2.3: sviatočný pozdrav pri spustení (Zdenko, 7. 11. 2026).
 * 24.–26. 12. stromček z „obvodov" so svetielkami, 31. 12. a 1. 1. ohňostroj.
 * ~3 s, ťuknutím sa preskočí, raz za deň, pri vypnutých animáciách v systéme sa neukáže.
 * Bez povolení a bez siete — len dátum v telefóne.
 */
enum class Holiday { TREE, FIREWORKS }

/** V debug builde sa dá pozdrav vyvolať z Nastavení (test mimo sviatkov). */
object HolidayPreview { var request by mutableStateOf<Holiday?>(null) }

private const val HOLIDAY_PREFS = "audit_prefs"
private const val KEY_HOLIDAY_DAY = "holiday_shown_day"

private val H_BG = Color(0xF20A0F14)
private val H_ACCENT = Color(0xFF00D4E8)
private val H_TRACE = Color(0xFF1E5F6B)
private val LIGHTS = listOf(Color(0xFF00D4E8), Color(0xFFFFFFFF), Color(0xFFFF4FD8), Color(0xFFFFC940))

internal fun holidayToday(cal: Calendar = Calendar.getInstance()): Holiday? {
    val m = cal.get(Calendar.MONTH)
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return when {
        m == Calendar.DECEMBER && d in 24..26 -> Holiday.TREE
        (m == Calendar.DECEMBER && d == 31) || (m == Calendar.JANUARY && d == 1) -> Holiday.FIREWORKS
        else -> null
    }
}

internal fun animationsOff(ctx: Context): Boolean =
    runCatching { Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
        .getOrDefault(false)

/** Rozhodne raz pri spustení, či sa dnes pozdrav ukáže, a zapamätá si deň. */
private fun holidayToShow(ctx: Context): Holiday? {
    val h = holidayToday() ?: return null
    if (animationsOff(ctx)) return null
    val cal = Calendar.getInstance()
    val day = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    val p = ctx.getSharedPreferences(HOLIDAY_PREFS, Context.MODE_PRIVATE)
    if (p.getString(KEY_HOLIDAY_DAY, null) == day) return null
    p.edit().putString(KEY_HOLIDAY_DAY, day).apply()
    return h
}

@Composable
fun HolidayOverlay(s: S) {
    val ctx = LocalContext.current
    var shown by remember { mutableStateOf(holidayToShow(ctx)) }
    val preview = HolidayPreview.request
    val which = preview ?: shown ?: return
    val t = remember(which) { Animatable(0f) }
    LaunchedEffect(which) {
        t.snapTo(0f)
        t.animateTo(1f, animationSpec = tween(3000, easing = LinearEasing))
        shown = null
        HolidayPreview.request = null
    }
    val fade = when {
        t.value < 0.08f -> t.value / 0.08f
        t.value > 0.85f -> (1f - t.value) / 0.15f
        else -> 1f
    }.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(fade)
            .background(H_BG)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                shown = null
                HolidayPreview.request = null
            },
        contentAlignment = Alignment.Center
    ) {
        val sparks = remember { buildSparks() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (which == Holiday.TREE) drawCircuitTree(t.value) else drawBursts(sparks, t.value)
        }
        Text(
            text = if (which == Holiday.TREE) s.holidayXmas else s.holidayNewYear,
            color = H_ACCENT,
            fontFamily = FontFamily.Monospace,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 120.dp)
        )
    }
}

/** Stromček z pravouhlých spojov (štýl dosky z úvodu) so svetielkami, ktoré sa postupne rozsvietia. */
private fun DrawScope.drawCircuitTree(t: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val top = h * 0.16f
    val bottom = h * 0.62f
    val levels = 5
    val maxHalf = w * 0.36f
    val lights = mutableListOf<Offset>()
    // schodovitý obrys: každá úroveň = vodorovná čiara + zvislý schod
    var prevLeft = Offset(cx, top)
    var prevRight = Offset(cx, top)
    for (i in 1..levels) {
        val y = top + (bottom - top) * i / levels
        val half = maxHalf * i / levels
        val left = Offset(cx - half, y)
        val right = Offset(cx + half, y)
        drawLine(H_TRACE, prevLeft, Offset(prevLeft.x, y), strokeWidth = 4f)
        drawLine(H_TRACE, Offset(prevLeft.x, y), left, strokeWidth = 4f)
        drawLine(H_TRACE, prevRight, Offset(prevRight.x, y), strokeWidth = 4f)
        drawLine(H_TRACE, Offset(prevRight.x, y), right, strokeWidth = 4f)
        // vnútorné spoje ako na doske
        drawLine(H_TRACE, Offset(cx - half * 0.6f, y), Offset(cx + half * 0.6f, y - (bottom - top) / levels * 0.5f), strokeWidth = 2f)
        lights += left; lights += right
        lights += Offset(cx - half * 0.5f, y); lights += Offset(cx + half * 0.5f, y)
        prevLeft = left; prevRight = right
    }
    // kmeň
    drawLine(H_TRACE, Offset(cx, bottom), Offset(cx, bottom + h * 0.06f), strokeWidth = 10f)
    drawLine(H_TRACE, Offset(cx - w * 0.08f, bottom + h * 0.06f), Offset(cx + w * 0.08f, bottom + h * 0.06f), strokeWidth = 4f)
    // svetielka sa rozsvecujú postupne a blikajú
    lights.forEachIndexed { i, p ->
        val on = t * lights.size * 1.6f > i
        if (!on) { drawCircle(H_TRACE, radius = 7f, center = p); return@forEachIndexed }
        val c = LIGHTS[i % LIGHTS.size]
        val blink = 0.55f + 0.45f * sin((t * 18f) + i)
        drawCircle(c.copy(alpha = 0.18f * blink), radius = 22f, center = p)
        drawCircle(c.copy(alpha = blink.coerceIn(0.3f, 1f)), radius = 8f, center = p)
    }
    // hviezda na špičke
    val star = Offset(cx, top - 6f)
    drawCircle(Color(0xFFFFC940).copy(alpha = 0.25f), radius = 34f, center = star)
    drawCircle(Color(0xFFFFC940), radius = 13f, center = star)
}

private class HolidayBurst(val center: Offset, val start: Float, val sparks: List<Pair<Float, Float>>, val color: Color)

private fun buildSparks(): List<HolidayBurst> {
    val rnd = Random(2027)
    val centers = listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.25f), Offset(0.5f, 0.45f), Offset(0.25f, 0.55f), Offset(0.75f, 0.5f))
    return centers.mapIndexed { i, c ->
        HolidayBurst(c, i * 0.14f, List(70) { rnd.nextFloat() * 6.2832f to (0.3f + rnd.nextFloat() * 0.7f) }, LIGHTS[i % LIGHTS.size])
    }
}

/** Niekoľko výbuchov za sebou — rovnaký princíp ako ohňostroj na úvodnej obrazovke. */
private fun DrawScope.drawBursts(bursts: List<HolidayBurst>, t: Float) {
    val reach = hypot(size.width, size.height) * 0.22f
    for (b in bursts) {
        val lt = ((t - b.start) / 0.5f)
        if (lt <= 0f || lt >= 1f) continue
        val c = Offset(b.center.x * size.width, b.center.y * size.height)
        for ((angle, speed) in b.sparks) {
            val r = speed * lt * reach
            val p = Offset(c.x + cos(angle) * r, c.y + sin(angle) * r + lt * lt * size.height * 0.08f)
            drawCircle(b.color.copy(alpha = (1f - lt).coerceIn(0f, 1f)), radius = 5f * (1f - lt * 0.6f), center = p)
        }
    }
}
