package app.plainkit.audit

import android.content.Context
import android.os.Build
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * v1.8.1: zapamätané názvy appiek.
 *
 * Meranie (29. 10. 2026, debug): loadLabel bol najpomalší krok štartu — vivo ~1,6 s,
 * Redmi ~1,0 s pre ~400 appiek (načíta zdroje každej appky zvlášť).
 * Názov appky sa mení len s jej novou verziou, so zmenou jazyka systému alebo
 * s aktualizáciou systému — presne to je kľúč, inak sa názov načíta nanovo.
 *
 * Ide LEN o názov. Povolenia, AppOps, schopnosti ani nálezy sa sem neukladajú —
 * tie sa pri každom štarte čítajú čerstvo, aby appka nikdy neukázala starý stav.
 * Súbor je v súkromnom priečinku appky, nikam sa neposiela.
 */
object LabelCache {
    private const val FILE = "labels.v1"
    /** balík -> "pečiatka\tnázov" */
    private val map = ConcurrentHashMap<String, String>()
    @Volatile private var dirty = false
    val hits = AtomicInteger()
    val misses = AtomicInteger()

    /** Hlavička súboru: pri aktualizácii systému alebo inom jazyku sa celý zoznam zahodí. */
    private fun header(ctx: Context): String =
        "v1|" + Build.FINGERPRINT + "|" + ctx.resources.configuration.locales.toLanguageTags()

    /** Pečiatka verzie appky: čas poslednej aktualizácie + versionCode. */
    fun stamp(lastUpdateTime: Long, versionCode: Long) = "$lastUpdateTime:$versionCode"

    fun clean(label: String) = label.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ')

    fun load(ctx: Context) {
        map.clear(); dirty = false; hits.set(0); misses.set(0)
        runCatching {
            val f = File(ctx.filesDir, FILE)
            if (!f.exists()) return
            val lines = f.readLines()
            if (lines.firstOrNull() != header(ctx)) { dirty = true; return }
            for (line in lines.drop(1)) {
                val p = line.split('\t', limit = 3)
                if (p.size == 3) map[p[0]] = p[1] + "\t" + p[2]
            }
        }
    }

    fun get(pkg: String, stamp: String): String? {
        val v = map[pkg] ?: return null.also { misses.incrementAndGet() }
        val i = v.indexOf('\t')
        return if (i > 0 && v.substring(0, i) == stamp) { hits.incrementAndGet(); v.substring(i + 1) }
               else { misses.incrementAndGet(); null }
    }

    fun put(pkg: String, stamp: String, label: String) {
        map[pkg] = stamp + "\t" + label
        dirty = true
    }

    /** Uloží, ak sa niečo zmenilo; vyhodí odinštalované appky. Zápis cez dočasný súbor. */
    fun save(ctx: Context, installed: Set<String>) {
        if (map.keys.retainAll(installed)) dirty = true
        if (!dirty) return
        runCatching {
            val tmp = File(ctx.filesDir, "$FILE.tmp")
            tmp.bufferedWriter().use { w ->
                w.write(header(ctx)); w.write("\n")
                for ((pkg, v) in map) { w.write(pkg); w.write("\t"); w.write(v); w.write("\n") }
            }
            if (tmp.renameTo(File(ctx.filesDir, FILE))) dirty = false
        }
    }
}
