package io.github.sreepv43.fablife.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import io.github.sreepv43.fablife.content.Ritual
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

enum class StepState { LOCKED, UNLOCKED, ACTIVE, COMPLETED }

enum class CheckInStatus { DONE, SKIPPED }

enum class JournalKind { GRATITUDE, REFLECTION, MOOD }

enum class SessionKind { FOCUS, GUIDED, BREATHING, SOUND }

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val icon: String,
    val ritual: Ritual,
    val order: Int,
    val durationSeconds: Int?,
    val smallerVersion: String?,
    val useSmaller: Boolean = false,
    val sourceStepId: String?,
    val createdAtMillis: Long,
    val toolType: String? = null,
    val toolRef: String? = null,
)

@Entity(tableName = "check_ins", primaryKeys = ["habitId", "day"])
data class CheckInEntity(
    val habitId: String,
    val day: LocalDate,
    val status: CheckInStatus,
    val timestampMillis: Long,
)

@Entity(tableName = "step_progress")
data class StepProgressEntity(
    @PrimaryKey val stepId: String,
    val journeyId: String,
    val state: StepState,
    val letterRead: Boolean = false,
    val activatedDay: LocalDate? = null,
    val completedDay: LocalDate? = null,
)

@Entity(tableName = "ritual_settings")
data class RitualSettingsEntity(
    @PrimaryKey val ritual: Ritual,
    val reminderTime: LocalTime,
    val reminderEnabled: Boolean,
)

@Entity(tableName = "routine_logs")
data class RoutineLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: String,
    val day: LocalDate,
    val minutes: Int,
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: LocalDate,
    val kind: JournalKind,
    val mood: Int? = null,
    val text: String,
    val createdAtMillis: Long,
)

@Entity(tableName = "top_tasks", primaryKeys = ["day", "slot"])
data class TopTaskEntity(
    val day: LocalDate,
    val slot: Int,
    val text: String,
    val done: Boolean = false,
)

@Entity(tableName = "session_logs")
data class SessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kind: SessionKind,
    val ref: String,
    val day: LocalDate,
    val minutes: Int,
)

@Entity(tableName = "coach_notes")
data class CoachNoteEntity(
    @PrimaryKey val key: String,
    val text: String,
    val source: String,
    val createdAtMillis: Long,
)

class Converters {
    @TypeConverter fun dayToLong(day: LocalDate?): Long? = day?.toEpochDay()
    @TypeConverter fun longToDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
    @TypeConverter fun timeToInt(time: LocalTime?): Int? = time?.toSecondOfDay()
    @TypeConverter fun intToTime(value: Int?): LocalTime? = value?.let { LocalTime.ofSecondOfDay(it.toLong()) }
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY ritual, `order`")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY ritual, `order`")
    suspend fun all(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(habit: HabitEntity)

    @Upsert
    suspend fun upsert(habits: List<HabitEntity>)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM habits")
    suspend fun clear()
}

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins")
    fun observeAll(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins")
    suspend fun all(): List<CheckInEntity>

    @Upsert
    suspend fun upsert(checkIn: CheckInEntity)

    @Query("DELETE FROM check_ins WHERE habitId = :habitId AND day = :day")
    suspend fun delete(habitId: String, day: LocalDate)

    @Query("DELETE FROM check_ins WHERE habitId = :habitId")
    suspend fun deleteForHabit(habitId: String)

    @Query("DELETE FROM check_ins")
    suspend fun clear()
}

@Dao
interface StepDao {
    @Query("SELECT * FROM step_progress")
    fun observeAll(): Flow<List<StepProgressEntity>>

    @Query("SELECT * FROM step_progress")
    suspend fun all(): List<StepProgressEntity>

    @Upsert
    suspend fun upsert(progress: List<StepProgressEntity>)

    @Query("DELETE FROM step_progress")
    suspend fun clear()
}

@Dao
interface RitualSettingsDao {
    @Query("SELECT * FROM ritual_settings")
    fun observeAll(): Flow<List<RitualSettingsEntity>>

    @Query("SELECT * FROM ritual_settings")
    suspend fun all(): List<RitualSettingsEntity>

    @Upsert
    suspend fun upsert(settings: List<RitualSettingsEntity>)
}

@Dao
interface RoutineLogDao {
    @Query("SELECT * FROM routine_logs")
    fun observeAll(): Flow<List<RoutineLogEntity>>

    @Query("SELECT * FROM routine_logs")
    suspend fun all(): List<RoutineLogEntity>

    @Insert
    suspend fun insert(log: RoutineLogEntity)

    @Query("DELETE FROM routine_logs")
    suspend fun clear()
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY day DESC, createdAtMillis DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY day DESC, createdAtMillis DESC")
    suspend fun all(): List<JournalEntryEntity>

    @Insert
    suspend fun insert(entry: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun clear()
}

@Dao
interface TopTaskDao {
    @Query("SELECT * FROM top_tasks ORDER BY day, slot")
    fun observeAll(): Flow<List<TopTaskEntity>>

    @Query("SELECT * FROM top_tasks ORDER BY day, slot")
    suspend fun all(): List<TopTaskEntity>

    @Upsert
    suspend fun upsert(task: TopTaskEntity)

    @Query("DELETE FROM top_tasks WHERE day = :day AND slot = :slot")
    suspend fun delete(day: LocalDate, slot: Int)

    @Query("DELETE FROM top_tasks")
    suspend fun clear()
}

@Dao
interface SessionLogDao {
    @Query("SELECT * FROM session_logs")
    fun observeAll(): Flow<List<SessionLogEntity>>

    @Query("SELECT * FROM session_logs")
    suspend fun all(): List<SessionLogEntity>

    @Insert
    suspend fun insert(log: SessionLogEntity)

    @Query("DELETE FROM session_logs")
    suspend fun clear()
}

@Dao
interface CoachNoteDao {
    @Query("SELECT * FROM coach_notes WHERE `key` = :key")
    suspend fun get(key: String): CoachNoteEntity?

    @Query("SELECT * FROM coach_notes WHERE `key` = :key")
    fun observe(key: String): Flow<CoachNoteEntity?>

    @Query("SELECT COUNT(*) FROM coach_notes WHERE source = :source AND createdAtMillis >= :sinceMillis")
    suspend fun countSince(source: String, sinceMillis: Long): Int

    @Upsert
    suspend fun upsert(note: CoachNoteEntity)

    @Query("DELETE FROM coach_notes")
    suspend fun clear()
}

@Database(
    entities = [
        HabitEntity::class, CheckInEntity::class, StepProgressEntity::class, RitualSettingsEntity::class,
        RoutineLogEntity::class, JournalEntryEntity::class, TopTaskEntity::class, SessionLogEntity::class,
        CoachNoteEntity::class,
    ],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@TypeConverters(Converters::class)
abstract class FabDatabase : RoomDatabase() {
    abstract fun habits(): HabitDao
    abstract fun checkIns(): CheckInDao
    abstract fun steps(): StepDao
    abstract fun ritualSettings(): RitualSettingsDao
    abstract fun routineLogs(): RoutineLogDao
    abstract fun journal(): JournalDao
    abstract fun topTasks(): TopTaskDao
    abstract fun sessionLogs(): SessionLogDao
    abstract fun coachNotes(): CoachNoteDao

    companion object {
        fun create(context: Context): FabDatabase =
            Room.databaseBuilder(context, FabDatabase::class.java, "fablife.db").build()
    }
}
