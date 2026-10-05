package app.plainkit.audit

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "scans")
data class ScanRecord(
    @PrimaryKey val packageName: String,
    val label: String,
    val versionCode: Long,
    val trackers: String,
    val permissions: String,
    val scannedAt: Long
)

/**
 * `text` od v1.1 neobsahuje hotovú vetu, ale surové dáta ako JSON
 * (pozri ChangeDiff). Vetu poskladá až renderChange() pri zobrazení —
 * v aktuálnom jazyku a s ľudskými názvami povolení.
 * Záznamy z v1.0 sú hotové vety; tie sa zobrazia tak, ako sú.
 */
@Entity(tableName = "changes")
data class ChangeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val label: String,
    val at: Long,
    val text: String
)

@Dao
interface AuditDao {
    @Query("SELECT * FROM scans")
    suspend fun allScans(): List<ScanRecord>

    @Query("SELECT * FROM scans WHERE packageName = :pkg")
    suspend fun scan(pkg: String): ScanRecord?

    @Upsert
    suspend fun save(record: ScanRecord)

    @Insert
    suspend fun addChange(change: ChangeRecord)

    @Query("SELECT * FROM changes ORDER BY at DESC LIMIT 50")
    suspend fun recentChanges(): List<ChangeRecord>

    @Query("DELETE FROM changes")
    suspend fun clearChanges()

    @Query("DELETE FROM scans")
    suspend fun clearScans()
}

/*
 * POZOR pri budúcich zmenách: fallbackToDestructiveMigration(true) znamená,
 * že ak zvýšiš `version` bez napísanej Migration, Room pri aktualizácii
 * potichu ZMAŽE celú databázu — aj základňu skenov, na ktorej stojí
 * „Čo sa zmenilo". Preto v1.1 schému nemení (version ostáva 2).
 */
@Database(entities = [ScanRecord::class, ChangeRecord::class], version = 2)
abstract class AuditDb : RoomDatabase() {
    abstract fun dao(): AuditDao

    companion object {
        @Volatile
        private var instance: AuditDb? = null

        fun get(context: Context): AuditDb = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AuditDb::class.java,
                "audit.db"
            ).fallbackToDestructiveMigration(true)
                .build().also { instance = it }
        }
    }
}

/** Čo sa zmenilo medzi dvoma skenmi — len dáta, bez jazyka. */
data class ChangeDiff(
    val trackersAdded: List<String>,
    val trackersRemoved: List<String>,
    val permsAdded: List<String>,
    val permsRemoved: List<String>
) {
    fun encode(): String = JSONObject()
        .put("v", 2)
        .put("ta", JSONArray(trackersAdded))
        .put("tr", JSONArray(trackersRemoved))
        .put("pa", JSONArray(permsAdded))
        .put("pr", JSONArray(permsRemoved))
        .toString()

    /** Nie je čo zobraziť — všetky štyri zoznamy sú prázdne. */
    fun isEmpty(): Boolean = trackersAdded.isEmpty() && trackersRemoved.isEmpty() &&
            permsAdded.isEmpty() && permsRemoved.isEmpty()

    companion object {
        /** Vráti null, ak text nie je v novom formáte (teda je to záznam z v1.0). */
        fun decode(text: String): ChangeDiff? {
            if (!text.startsWith("{")) return null
            return runCatching {
                val o = JSONObject(text)
                fun list(key: String): List<String> {
                    val a = o.optJSONArray(key) ?: return emptyList()
                    return List(a.length()) { a.getString(it) }
                }
                ChangeDiff(list("ta"), list("tr"), list("pa"), list("pr"))
            }.getOrNull()
        }
    }
}

/** Porovná uložený sken s novým. Null = nie je s čím porovnať, alebo sa nič nezmenilo. */
fun computeDiff(old: ScanRecord?, newTrackers: List<String>, newPerms: List<String>): ChangeDiff? {
    if (old == null) return null
    val oldT = old.trackers.split(",").filter { it.isNotBlank() }.toSet()
    val oldP = old.permissions.split(",").filter { it.isNotBlank() }.toSet()
    val newT = newTrackers.toSet()
    val newP = newPerms.toSet()

    val diff = ChangeDiff(
        trackersAdded = (newT - oldT).sorted(),
        trackersRemoved = (oldT - newT).sorted(),
        permsAdded = (newP - oldP).sorted(),
        permsRemoved = (oldP - newP).sorted()
    )
    return if (diff.isEmpty()) null else diff
}

/**
 * Povolenia do jednej vety: najprv tie, ktoré vieme pomenovať po ľudsky,
 * potom technické (s krátkym názvom — posledná časť za bodkou).
 * Napr. „poloha aj na pozadí; technické: BIND_SERVICE".
 */
private fun permsText(perms: List<String>, packageName: String, s: S): String {
    val named = perms.mapNotNull { permLabel(it, s) }.toMutableList()
    // Push a odznak na ikonke: jeden riadok namiesto desiatok názvov, rovnako
    // ako v detaile aplikácie.
    if (perms.any { isNotifyOrBadge(it) }) named.add(s.notifyBadge)
    val tech = perms
        .filter { permLabel(it, s) == null }
        .filterNot { isNotifyOrBadge(it) || isOwnPermission(it, packageName) }
        .map { techLabel(it) }
    return buildList {
        if (named.isNotEmpty()) add(named.joinToString(", "))
        if (tech.isNotEmpty()) add(s.techList(tech.joinToString(", ")))
    }.joinToString("; ")
}

/**
 * Poskladá vetu na zobrazenie. Staré záznamy z v1.0 vráti nezmenené.
 *
 * Vráti prázdny reťazec, ak po odfiltrovaní vlastných povolení aplikácie
 * nezostalo nič, čo by malo zmysel ukázať — volajúci taký záznam preskočí.
 */
fun renderChange(stored: String, packageName: String, s: S): String {
    val d = ChangeDiff.decode(stored) ?: return stored
    val parts = mutableListOf<String>()
    if (d.trackersAdded.isNotEmpty()) parts.add(s.addedTrackers(d.trackersAdded.joinToString(", ")))
    if (d.trackersRemoved.isNotEmpty()) parts.add(s.removedTrackers(d.trackersRemoved.joinToString(", ")))
    permsText(d.permsAdded, packageName, s).takeIf { it.isNotEmpty() }?.let { parts.add(s.addedPerms(it)) }
    permsText(d.permsRemoved, packageName, s).takeIf { it.isNotEmpty() }?.let { parts.add(s.removedPerms(it)) }
    return parts.joinToString(" · ")
}
