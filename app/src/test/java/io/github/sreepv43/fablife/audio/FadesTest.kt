package io.github.sreepv43.fablife.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FadesTest {
    @Test
    fun fadesInSmoothlyFromSilence() {
        assertEquals(0f, Fades.gain(0, 2_000, null, 0), 0f)
        assertEquals(0.5f, Fades.gain(1_000, 2_000, null, 0), 1e-6f)
        assertEquals(1f, Fades.gain(2_000, 2_000, null, 0), 0f)
        assertEquals(1f, Fades.gain(60_000, 2_000, null, 0), 0f)
        var last = -1f
        for (t in 0..2_000 step 100) {
            val g = Fades.gain(t.toLong(), 2_000, null, 0)
            assertTrue("monotonic at $t", g >= last)
            last = g
        }
    }

    @Test
    fun fadesOutOverTheLastStretchThenStopsAtTheEnd() {
        assertEquals(1f, Fades.gain(0, 0, 10_000, 3_000), 0f)
        assertEquals(1f, Fades.gain(7_000, 0, 10_000, 3_000), 0f)
        assertEquals(0.5f, Fades.gain(8_500, 0, 10_000, 3_000), 1e-6f)
        assertEquals(0f, Fades.gain(10_000, 0, 10_000, 3_000), 0f)
        assertEquals(0f, Fades.gain(12_000, 0, 10_000, 3_000), 0f)
        assertEquals(0f, Fades.gain(-5, 0, 10_000, 3_000), 0f)
    }

    @Test
    fun overlappingFadesMultiplyAndStayInRange() {
        for (t in 0..4_000 step 50) {
            val g = Fades.gain(t.toLong(), 3_000, 4_000, 3_000)
            assertTrue("in range at $t: $g", g in 0f..1f)
        }
    }

    @Test
    fun sleepTimerCountsDownFadesAndFinishes() {
        val plan = SleepTimerPlan(minutes = 15)
        assertEquals(900_000L, plan.totalMs)
        assertEquals(900_000L, plan.remainingMs(0))
        assertEquals(100_000L, plan.remainingMs(800_000))
        assertEquals(0L, plan.remainingMs(2_000_000))
        assertEquals(1f, plan.gain(0), 0f)
        assertEquals(1f, plan.gain(860_000), 0f)
        assertTrue(plan.gain(885_000) in 0.01f..0.99f)
        assertFalse(plan.isFinished(899_999))
        assertTrue(plan.isFinished(900_000))
        assertEquals(0f, plan.gain(900_000), 0f)
    }

    @Test
    fun aTimerShorterThanItsFadeStillFadesCleanly() {
        val plan = SleepTimerPlan(minutes = 1, fadeOutSeconds = 120)
        assertEquals(1f, plan.gain(0), 0f)
        assertTrue(plan.gain(30_000) < 1f)
        assertEquals(0f, plan.gain(60_000), 0f)
        assertTrue(runCatching { SleepTimerPlan(0) }.isFailure)
    }
}
