package io.github.sreepv43.fablife.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class JourneyRulesTest {
    private val mon = LocalDate.of(2026, 10, 5)
    private fun d(offset: Int) = mon.plusDays(offset.toLong())

    @Test
    fun logicalDayRollsOverAtOffset() {
        assertEquals(mon, logicalDay(LocalDateTime.of(2026, 10, 6, 1, 30), 3))
        assertEquals(d(1), logicalDay(LocalDateTime.of(2026, 10, 6, 3, 0), 3))
    }

    @Test
    fun challengeCompletesWithinWindow() {
        // Mon, Tue, Thu with target 3 / window 5 completes (spec example).
        assertTrue(JourneyRules.isChallengeComplete(setOf(d(0), d(1), d(3)), mon, 3, 5))
    }

    @Test
    fun challengeNotCompleteWhenSpreadBeyondWindow() {
        assertFalse(JourneyRules.isChallengeComplete(setOf(d(0), d(3), d(5)), mon, 3, 5))
        assertEquals(2, JourneyRules.challengeProgress(setOf(d(0), d(3), d(5)), mon, 3, 5))
    }

    @Test
    fun daysBeforeActivationDoNotCount() {
        assertFalse(JourneyRules.isChallengeComplete(setOf(d(-2), d(-1), d(0)), mon, 3, 5))
        assertEquals(1, JourneyRules.challengeProgress(setOf(d(-2), d(-1), d(0)), mon, 3, 5))
    }

    @Test
    fun progressIsCappedAtTarget() {
        assertEquals(3, JourneyRules.challengeProgress((0..6).map(::d).toSet(), mon, 3, 5))
    }

    @Test
    fun fullRitualDaysRequireEveryHabit() {
        val done = mapOf(d(0) to setOf("water", "breakfast"), d(1) to setOf("water"))
        assertEquals(setOf(d(0)), JourneyRules.fullRitualDays(setOf("water", "breakfast"), done))
        assertTrue(JourneyRules.fullRitualDays(emptySet(), done).isEmpty())
    }

    @Test
    fun nudgeAfterThreeMissedDays() {
        assertTrue(JourneyRules.needsNudge(setOf(d(0)), mon, d(4)))
        assertFalse(JourneyRules.needsNudge(setOf(d(2)), mon, d(4)))
        assertFalse(JourneyRules.needsNudge(emptySet(), mon, d(2)))
    }

    @Test
    fun welcomeBackAfterSevenDays() {
        assertTrue(JourneyRules.isWelcomeBack(d(0), d(7)))
        assertFalse(JourneyRules.isWelcomeBack(d(0), d(6)))
        assertFalse(JourneyRules.isWelcomeBack(null, d(30)))
    }

    @Test
    fun currentStreakCountsBackFromTodayOrYesterday() {
        val days = setOf(d(1), d(2), d(3))
        assertEquals(3, JourneyRules.currentStreak(days, d(3)))
        assertEquals(3, JourneyRules.currentStreak(days, d(4)))
        assertEquals(0, JourneyRules.currentStreak(days, d(5)))
    }

    @Test
    fun longestStreakFindsLongestRun() {
        assertEquals(3, JourneyRules.longestStreak(setOf(d(0), d(1), d(3), d(4), d(5), d(7))))
        assertEquals(0, JourneyRules.longestStreak(emptySet()))
    }
}
