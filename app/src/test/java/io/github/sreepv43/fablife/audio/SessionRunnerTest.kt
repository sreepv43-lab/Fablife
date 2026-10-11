package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.GuidedSession
import io.github.sreepv43.fablife.content.GuidedStep
import io.github.sreepv43.fablife.content.SessionCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRunnerTest {
    private fun session(vararg steps: GuidedStep) =
        GuidedSession("t", "Test", 1, SessionCategory.CALM, "d", "b", null, steps.toList())

    /** Speaks each line in [speakMs] of virtual time; records what was spoken and how often it was cut off. */
    private class FakeSpeaker(private val speakMs: Long, private val result: Boolean = true) : Speaker {
        val spoken = mutableListOf<String>()
        val completed = mutableListOf<String>()
        var stops = 0

        override suspend fun speak(text: String): Boolean {
            spoken += text
            delay(speakMs)
            if (result) completed += text
            return result
        }

        override fun stop() { stops++ }
    }

    private val three = session(GuidedStep("one", 2f), GuidedStep("two", 3f), GuidedStep("three", 1f))

    @Test
    fun speaksEveryLineInOrderAndFinishesAtTheExpectedTime() = runTest {
        val speaker = FakeSpeaker(speakMs = 1_000)
        val runner = SessionRunner(three, speaker)
        runner.start(this)
        runCurrent()
        assertEquals(0, runner.state.value.index)
        assertEquals("one", runner.state.value.caption)
        assertTrue(runner.state.value.speaking)

        advanceTimeBy(1_050) // line one spoken, in its silence
        assertFalse(runner.state.value.speaking)
        advanceTimeBy(1_000 + 2_000) // silence over, line two spoken
        runCurrent()
        assertEquals("two", runner.state.value.caption)

        testScheduler.advanceUntilIdle()
        assertEquals(listOf("one", "two", "three"), speaker.completed)
        assertTrue(runner.state.value.finished)
        assertEquals((1_000 + 2_000) + (1_000 + 3_000) + (1_000 + 1_000), currentTime)
    }

    @Test
    fun pausingCutsTheLineOffAndResumeRepeatsItFromTheStart() = runTest {
        val speaker = FakeSpeaker(speakMs = 4_000)
        val runner = SessionRunner(session(GuidedStep("long line", 1f), GuidedStep("next", 1f)), speaker)
        runner.start(this)
        advanceTimeBy(1_500)
        runner.pause()
        runCurrent()
        assertTrue(runner.state.value.paused)
        assertTrue("the voice was cut off", speaker.stops >= 1)
        assertTrue(speaker.completed.isEmpty())

        advanceTimeBy(60_000) // nothing happens while paused
        assertEquals(listOf("long line"), speaker.spoken)

        runner.resume()
        testScheduler.advanceUntilIdle()
        assertEquals(listOf("long line", "long line", "next"), speaker.spoken)
        assertEquals(listOf("long line", "next"), speaker.completed)
        assertTrue(runner.state.value.finished)
    }

    @Test
    fun pausingInASilenceKeepsTheRemainingSilenceAndDoesNotRepeatTheLine() = runTest {
        val speaker = FakeSpeaker(speakMs = 500)
        val runner = SessionRunner(session(GuidedStep("breathe in", 10f), GuidedStep("breathe out", 0f)), speaker)
        runner.start(this)
        advanceTimeBy(500 + 4_000) // 4 s into the 10 s silence
        runner.pause()
        runCurrent()
        val pausedAt = currentTime
        advanceTimeBy(30_000)

        runner.resume()
        testScheduler.advanceUntilIdle()
        assertEquals(listOf("breathe in", "breathe out"), speaker.spoken)
        // About 6 s of silence remained (within one 100 ms tick), then the last line.
        val resumedFor = currentTime - (pausedAt + 30_000)
        assertTrue("resumed for $resumedFor", resumedFor in 6_400L..6_700L)
        assertTrue(runner.state.value.finished)
    }

    @Test
    fun stoppingEndsTheSessionWithoutFinishingIt() = runTest {
        val speaker = FakeSpeaker(speakMs = 1_000)
        val runner = SessionRunner(three, speaker)
        runner.start(this)
        advanceTimeBy(500)
        runner.stop()
        testScheduler.advanceUntilIdle()
        assertTrue(runner.state.value.stopped)
        assertFalse(runner.state.value.finished)
        assertEquals(listOf("one"), speaker.spoken)
        runner.start(this) // a stopped session stays stopped
        testScheduler.advanceUntilIdle()
        assertEquals(listOf("one"), speaker.spoken)
    }

    @Test
    fun aSpeakerThatFailsFallsBackToTimedCaptions() = runTest {
        val speaker = FakeSpeaker(speakMs = 0, result = false)
        val text = "word ".repeat(11).trim() // 11 words = 5 s at 2.2 words a second
        val runner = SessionRunner(session(GuidedStep(text, 2f)), speaker)
        runner.start(this)
        testScheduler.advanceUntilIdle()
        assertEquals(5_000L + 2_000L, currentTime)
        assertTrue(runner.state.value.finished)
        assertEquals(text, runner.state.value.caption)
    }

    @Test
    fun withoutASpeakerTheSessionRunsAsCaptionsAndShortLinesStillGetReadingTime() = runTest {
        val runner = SessionRunner(session(GuidedStep("Hello", 1f), GuidedStep(null, 3f)), null)
        runner.start(this)
        testScheduler.advanceUntilIdle()
        assertEquals(1_500L + 1_000L + 3_000L, currentTime)
        assertTrue(runner.state.value.finished)
        assertEquals(1, runner.state.value.index)
    }

    @Test
    fun aSpeakerThatThrowsDoesNotCrashTheSession() = runTest {
        val throwing = object : Speaker {
            override suspend fun speak(text: String): Boolean = throw IllegalStateException("engine died")
            override fun stop() {}
        }
        val runner = SessionRunner(session(GuidedStep("hi there", 1f)), throwing)
        runner.start(this)
        testScheduler.advanceUntilIdle()
        assertTrue(runner.state.value.finished)
    }

    @Test
    fun pauseAndResumeAreHarmlessWhenNothingIsRunning() = runTest {
        val runner = SessionRunner(three, FakeSpeaker(100))
        runner.pause()
        runner.resume()
        assertEquals(-1, runner.state.value.index)
        assertNull(runner.state.value.caption)
        assertFalse(runner.state.value.paused)
    }

}
