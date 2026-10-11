package io.github.sreepv43.fablife.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusPlanTest {
    private val s = FocusSettings()

    @Test
    fun theCycleAlternatesFocusAndBreaksWithALongBreakEveryFourthRound() {
        val types = (0..8).map { FocusPlan.segment(s, it) }
        assertEquals(
            listOf(
                SegmentType.FOCUS, SegmentType.SHORT_BREAK, SegmentType.FOCUS, SegmentType.SHORT_BREAK,
                SegmentType.FOCUS, SegmentType.SHORT_BREAK, SegmentType.FOCUS, SegmentType.LONG_BREAK, SegmentType.FOCUS,
            ),
            types.map { it.type },
        )
        assertEquals(listOf(25, 5, 25, 5, 25, 5, 25, 15, 25), types.map { it.minutes })
        assertEquals(listOf(1, 1, 2, 2, 3, 3, 4, 4, 5), types.map { it.round })
        assertEquals(SegmentType.LONG_BREAK, FocusPlan.segment(s, 15).type)
    }

    @Test
    fun customLengthsAndRoundsAreUsed() {
        val custom = FocusSettings(focusMinutes = 50, breakMinutes = 10, longBreakMinutes = 20, roundsBeforeLongBreak = 2)
        assertEquals(50, FocusPlan.segment(custom, 0).minutes)
        assertEquals(10, FocusPlan.segment(custom, 1).minutes)
        assertEquals(SegmentType.LONG_BREAK, FocusPlan.segment(custom, 3).type)
        assertEquals(20L * 60_000, FocusPlan.segment(custom, 3).durationMs)
    }

    @Test
    fun settingsAreCoercedIntoTheirRanges() {
        val c = FocusSettings(focusMinutes = 3, breakMinutes = 99, longBreakMinutes = 1).coerced()
        assertEquals(10, c.focusMinutes)
        assertEquals(15, c.breakMinutes)
        assertEquals(10, c.longBreakMinutes)
    }

    @Test(expected = IllegalArgumentException::class)
    fun nonPositiveLengthsAreRejected() {
        FocusSettings(focusMinutes = 0)
    }

    @Test
    fun aCompletedFocusBlockLogsItsFullLength() {
        assertEquals(25, FocusPlan.minutesToLog(FocusPlan.segment(s, 0), 25 * 60_000L, completed = true))
    }

    @Test
    fun aFocusBlockStoppedEarlyLogsWholeMinutesOnlyOnceEnoughWasDone() {
        val focus = FocusPlan.segment(s, 0)
        assertEquals(0, FocusPlan.minutesToLog(focus, 4 * 60_000L + 59_000, completed = false))
        assertEquals(5, FocusPlan.minutesToLog(focus, 5 * 60_000L, completed = false))
        assertEquals(12, FocusPlan.minutesToLog(focus, 12 * 60_000L + 59_000, completed = false))
        assertEquals(25, FocusPlan.minutesToLog(focus, 99 * 60_000L, completed = false))
    }

    @Test
    fun breaksNeverLog() {
        assertEquals(0, FocusPlan.minutesToLog(FocusPlan.segment(s, 1), 5 * 60_000L, completed = true))
    }

    @Test
    fun theTimerCountsDownOnlyWhileRunning() {
        val t = SegmentTimer(durationMs = 10_000)
        assertFalse(t.running)
        assertEquals(10_000, t.remaining(now = 5_000))
        val running = t.start(now = 1_000)
        assertTrue(running.running)
        assertEquals(7_000, running.remaining(now = 4_000))
        assertEquals(3_000, running.elapsed(now = 4_000))
        val paused = running.pause(now = 4_000)
        assertFalse(paused.running)
        assertEquals(7_000, paused.remaining(now = 100_000))
        val resumed = paused.start(now = 100_000)
        assertEquals(2_000, resumed.remaining(now = 105_000))
    }

    @Test
    fun theTimerFinishesAndStaysAtZero() {
        val t = SegmentTimer(durationMs = 10_000).start(now = 0)
        assertFalse(t.finished(now = 9_999))
        assertTrue(t.finished(now = 10_000))
        assertEquals(0, t.remaining(now = 50_000))
        assertEquals(10_000, t.elapsed(now = 50_000))
    }

    @Test
    fun startingTwiceOrPausingTwiceChangesNothing() {
        val t = SegmentTimer(durationMs = 10_000).start(now = 0)
        assertEquals(t, t.start(now = 5_000))
        val p = t.pause(now = 2_000)
        assertEquals(p, p.pause(now = 9_000))
    }

    @Test
    fun aClockThatJumpsBackwardsNeverGivesMoreThanTheFullDuration() {
        val t = SegmentTimer(durationMs = 10_000).start(now = 5_000)
        assertEquals(10_000, t.remaining(now = 0))
    }
}
