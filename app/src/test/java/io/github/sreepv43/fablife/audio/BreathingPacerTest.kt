package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.BreathingPattern
import io.github.sreepv43.fablife.content.ContentParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BreathingPacerTest {
    private val patterns = ContentParser.audio(File("src/main/assets/audio.json").readText()).breathing.associateBy { it.id }
    private fun p(id: String) = patterns.getValue(id)

    @Test
    fun theCatalogPatternsAreAllThere() {
        assertEquals(setOf("box", "relax_478", "coherent", "calm_exhale"), patterns.keys)
    }

    @Test
    fun relax478HasItsPhaseBoundariesAtFourElevenAndNineteenSeconds() {
        val phases = BreathingPacer.phases(p("relax_478"))
        assertEquals(listOf(BreathPhase.INHALE, BreathPhase.HOLD_IN, BreathPhase.EXHALE), phases.map { it.phase })
        assertEquals(listOf(0L, 4_000L, 11_000L), phases.map { it.startMs })
        assertEquals(19_000L, BreathingPacer.cycleMs(p("relax_478")))
    }

    @Test
    fun boxBreathingHasFourEqualPhases() {
        val phases = BreathingPacer.phases(p("box"))
        assertEquals(4, phases.size)
        assertTrue(phases.all { it.durationMs == 4_000L })
        assertEquals(16_000L, BreathingPacer.cycleMs(p("box")))
    }

    @Test
    fun roundsFitTheRequestedMinutesAndNeverDropBelowOne() {
        assertEquals(18, BreathingPacer.roundsFor(p("calm_exhale"), 3)) // 10 s breath, 180 s
        assertEquals(11, BreathingPacer.roundsFor(p("box"), 3))
        assertEquals(1, BreathingPacer.roundsFor(p("relax_478"), 0))
        assertEquals(190_000L, BreathingPacer.totalMs(p("relax_478"), 10))
    }

    @Test
    fun stateTracksPhaseProgressCountdownAndCycle() {
        val pattern = p("relax_478")
        val start = BreathingPacer.stateAt(pattern, 0, 3)
        assertEquals(BreathPhase.INHALE, start.phase)
        assertEquals(4, start.secondsLeft)
        assertEquals(0f, start.scale, 0f)

        val midInhale = BreathingPacer.stateAt(pattern, 2_000, 3)
        assertEquals(0.5f, midInhale.progress, 1e-6f)
        assertEquals(0.5f, midInhale.scale, 1e-6f)
        assertEquals(2, midInhale.secondsLeft)

        val hold = BreathingPacer.stateAt(pattern, 4_000, 3)
        assertEquals(BreathPhase.HOLD_IN, hold.phase)
        assertEquals(1f, hold.scale, 0f)
        assertEquals(7, hold.secondsLeft)

        val exhale = BreathingPacer.stateAt(pattern, 11_000, 3)
        assertEquals(BreathPhase.EXHALE, exhale.phase)
        assertEquals(1f, exhale.scale, 1e-6f)
        assertEquals(0.5f, BreathingPacer.stateAt(pattern, 15_000, 3).scale, 1e-6f)

        val secondBreath = BreathingPacer.stateAt(pattern, 19_000 + 500, 3)
        assertEquals(1, secondBreath.cycle)
        assertEquals(BreathPhase.INHALE, secondBreath.phase)
    }

    @Test
    fun theSessionFinishesAtTheEndAndAfter() {
        val pattern = p("coherent")
        val total = BreathingPacer.totalMs(pattern, 3)
        assertFalse(BreathingPacer.stateAt(pattern, total - 1, 3).finished)
        val end = BreathingPacer.stateAt(pattern, total, 3)
        assertTrue(end.finished)
        assertEquals(0, end.secondsLeft)
        assertEquals(0f, end.scale, 0f)
        assertTrue(BreathingPacer.stateAt(pattern, total + 99_999, 3).finished)
        assertFalse(BreathingPacer.stateAt(pattern, -5, 3).finished)
    }

    @Test
    fun theCircleNeverJumpsBetweenNeighbouringMoments() {
        for (pattern in patterns.values) {
            val rounds = 2
            var last = BreathingPacer.stateAt(pattern, 0, rounds).scale
            var t = 0L
            while (t < BreathingPacer.totalMs(pattern, rounds)) {
                val s = BreathingPacer.stateAt(pattern, t, rounds).scale
                assertTrue("${pattern.id} jumps at $t: $last -> $s", kotlin.math.abs(s - last) < 0.05f)
                assertTrue(s in 0f..1f)
                last = s
                t += 50
            }
        }
    }

    @Test
    fun fractionalSecondsAreRounded() {
        val pattern = BreathingPattern("t", "t", "t", 5.5f, 0f, 5.5f, 0f, 3)
        assertEquals(11_000L, BreathingPacer.cycleMs(pattern))
        assertEquals(2, BreathingPacer.phases(pattern).size)
    }
}
