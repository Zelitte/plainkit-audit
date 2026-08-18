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

@Entity(tableName = "scans")
data class ScanRecord(
    @PrimaryKey val packageName: String,
    val label: String,
    val versionCode: Long,
    val trackers: String,
    val permissions: String,
    val scannedAt: Long
)

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

fun diffText(
    old: ScanRecord?,
    newTrackers: List<String>,
    newPerms: List<String>,
    s: S
): String? {
    if (old == null) return null
    val oldT = old.trackers.split(",").filter { it.isNotBlank() }.toSet()
    val oldP = old.permissions.split(",").filter { it.isNotBlank() }.toSet()
    val newT = newTrackers.toSet()
    val newP = newPerms.toSet()

    fun short(p: String) = p.removePrefix("android.permission.")

    val parts = mutableListOf<String>()
    (newT - oldT).let { if (it.isNotEmpty()) parts.add(s.addedTrackers(it.joinToString(", "))) }
    (oldT - newT).let { if (it.isNotEmpty()) parts.add(s.removedTrackers(it.joinToString(", "))) }
    (newP - oldP).let {
        if (it.isNotEmpty()) parts.add(s.addedPerms(it.joinToString(", ") { p -> short(p) }))
    }
    (oldP - newP).let {
        if (it.isNotEmpty()) parts.add(s.removedPerms(it.joinToString(", ") { p -> short(p) }))
    }

    return if (parts.isEmpty()) null else parts.joinToString(" · ")
}