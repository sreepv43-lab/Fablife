package io.github.sreepv43.fablife.health

import java.time.Instant
import java.time.LocalDate

enum class HealthAvailability {
    /** Health Connect is ready to use. */
    AVAILABLE,

    /** Health Connect exists for this device but its app isn't installed or needs an update. */
    NOT_INSTALLED,

    /** This device doesn't support Health Connect. */
    UNAVAILABLE,
}

enum class SleepStageType { AWAKE, SLEEPING, LIGHT, DEEP, REM, UNKNOWN }

data class RawStage(val start: Instant, val end: Instant, val type: SleepStageType)

/** A sleep session as read from Health Connect. If [stages] is empty, the whole session counts as sleep. */
data class RawSleepSession(val start: Instant, val end: Instant, val stages: List<RawStage> = emptyList())

/** Sleep attributed to one calendar day: the night that ended that day, and any naps taken that day. */
data class SleepDay(val day: LocalDate, val nightMinutes: Int, val napMinutes: Int)

/** What was recorded for one day. A null means nothing was recorded, which is different from zero. */
data class DayHealth(val day: LocalDate, val steps: Long?, val nightSleepMinutes: Int?)

data class HealthSummary(val days: List<DayHealth>) {
    fun forDay(day: LocalDate): DayHealth? = days.firstOrNull { it.day == day }
}
