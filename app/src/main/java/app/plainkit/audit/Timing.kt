package app.plainkit.audit

import android.os.Process
import android.os.SystemClock
import java.util.concurrent.atomic.AtomicLong

/**
 * Len na ladenie (debug a r8test build): kde ide čas pri načítaní zoznamu appiek.
 * Výsledok sa ukazuje v Nastaveniach. Merať treba studený štart (appku najprv
 * zavrieť zo zoznamu posledných appiek), inak sa zoznam berie z pamäte.
 *
 * Od v1.8.1 sa záznamy appiek načítavajú na viacerých vláknach — „názvy / AppOps /
 * zdieľaná id." sú preto SÚČET času všetkých vlákien a môžu byť väčšie než
 * „záznamy", čo je skutočný (nástenný) čas.
 */
object LoadTiming {
    /** Zbiera sa len počas loadApps — detail jednej appky (loadApp) čísla nekazí. */
    @Volatile var on = false
    @Volatile var count = 0
    @Volatile var caps = 0L; @Volatile var packages = 0L; @Volatile var entries = 0L
    @Volatile var sort = 0L; @Volatile var loadApps = 0L; @Volatile var threads = 0
    val label = AtomicLong(); val appOps = AtomicLong(); val shared = AtomicLong()
    @Volatile var labelHits = 0; @Volatile var labelMisses = 0
    @Volatile var room = 0L; @Volatile var roomRows = 0; @Volatile var roomWait = 0L
    @Volatile var store = 0L; @Volatile var changes = 0L
    @Volatile var levels = 0L; @Volatile var levelsRuns = 0
    /** ms od štartu procesu; -1 = nemerané (zoznam z pamäte). reset() mažu len listAt/scansAt. */
    @Volatile var listAt = -1L; @Volatile var scansAt = -1L
    @Volatile var createAt = -1L; @Volatile var effectAt = -1L

    fun reset() {
        count = 0; caps = 0; packages = 0; entries = 0; sort = 0; loadApps = 0; threads = 0
        label.set(0); appOps.set(0); shared.set(0); labelHits = 0; labelMisses = 0
        levels = 0; levelsRuns = 0; listAt = -1; scansAt = -1
    }

    fun sinceStart(): Long = SystemClock.elapsedRealtime() - Process.getStartElapsedRealtime()

    private fun ms(ns: Long) = ns / 1_000_000

    fun report(): String {
        if (listAt < 0 && loadApps == 0L) return "load: zoznam z pamäte — zavri appku a spusti znova"
        return "load (ms): loadApps ${ms(loadApps)} [$count appiek, $threads vlákna]\n" +
            "  caps ${ms(caps)} · balíky ${ms(packages)} · záznamy ${ms(entries)} · triedenie ${ms(sort)}\n" +
            "  súčet vlákien: názvy ${ms(label.get())} (z pamäte $labelHits/${labelHits + labelMisses})" +
            " · AppOps ${ms(appOps.get())} · zdieľaná id. ${ms(shared.get())}\n" +
            "  Room skeny ${ms(room)} [$roomRows] súbežne, čakanie ${ms(roomWait)} · uloženie ${ms(store)}" +
            " · zmeny ${ms(changes)} · závažnosti ${ms(levels)} [${levelsRuns}×]\n" +
            "  od štartu procesu: onCreate $createAt · štart načítania $effectAt · zoznam $listAt · skeny $scansAt"
    }
}
