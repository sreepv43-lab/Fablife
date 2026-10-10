package io.github.sreepv43.fablife.engine

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/** The "logical day" a moment belongs to: a check-in at 01:30 still counts for the previous day. */
fun logicalDay(at: LocalDateTime, dayStartOffsetHours: Int): LocalDate =
    at.minusHours(dayStartOffsetHours.toLong()).toLocalDate()

object JourneyRules {

    /**
     * A challenge completes when [targetDays] distinct done days fall inside any rolling window of
     * [windowDays] days, counting only days on or after [activatedDay].
     */
    fun isChallengeComplete(doneDays: Set<LocalDate>, activatedDay: LocalDate, targetDays: Int, windowDays: Int): Boolean =
        challengeProgress(doneDays, activatedDay, targetDays, windowDays) >= targetDays

    /** Best number of done days found in any rolling window (capped at [targetDays]), for "2 of 3 days" UI. */
    fun challengeProgress(doneDays: Set<LocalDate>, activatedDay: LocalDate, targetDays: Int, windowDays: Int): Int {
        val days = doneDays.filter { !it.isBefore(activatedDay) }.sorted()
        var best = 0
        for (i in days.indices) {
            val windowEnd = days[i].plusDays(windowDays - 1L)
            var count = 0
            for (j in i until days.size) {
                if (days[j].isAfter(windowEnd)) break
                count++
            }
            best = maxOf(best, count)
        }
        return minOf(best, targetDays)
    }

    /** Days on which every habit in [habitIds] was done; used for ritual challenges and streaks. */
    fun fullRitualDays(habitIds: Set<String>, doneByDay: Map<LocalDate, Set<String>>): Set<LocalDate> {
        if (habitIds.isEmpty()) return emptySet()
        return doneByDay.filterValues { it.containsAll(habitIds) }.keys
    }

    /** True when the challenge has been active for 3+ days and none of the last 3 days had a check-in. */
    fun needsNudge(doneDays: Set<LocalDate>, activatedDay: LocalDate, today: LocalDate): Boolean {
        if (ChronoUnit.DAYS.between(activatedDay, today) < 3) return false
        return (1..3).none { doneDays.contains(today.minusDays(it.toLong())) }
    }

    /** True when the user has been away for 7+ days since their last check-in. */
    fun isWelcomeBack(lastCheckInDay: LocalDate?, today: LocalDate): Boolean =
        lastCheckInDay != null && ChronoUnit.DAYS.between(lastCheckInDay, today) >= 7

    /** Consecutive complete days ending today, or ending yesterday if today is not complete yet. */
    fun currentStreak(completeDays: Set<LocalDate>, today: LocalDate): Int {
        var day = if (completeDays.contains(today)) today else today.minusDays(1)
        var streak = 0
        while (completeDays.contains(day)) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    fun longestStreak(completeDays: Set<LocalDate>): Int {
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in completeDays.sorted()) {
            run = if (previous != null && previous.plusDays(1) == day) run + 1 else 1
            best = maxOf(best, run)
            previous = day
        }
        return best
    }
}
