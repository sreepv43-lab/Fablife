package io.github.sreepv43.fablife.audio.android

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpeechPlayerTest {
    @Test
    fun speaksThroughTheEngineAndReportsSuccess() = runTest {
        val tts = FakeTts()
        val player = SpeechPlayer(tts)
        assertTrue(player.speak("Breathe in."))
        assertEquals(listOf("Breathe in."), tts.spoken)
        assertTrue(player.available.value)
    }

    @Test
    fun anEngineWithoutADataPackReportsUnavailableAndFailsEveryLine() = runTest {
        val tts = FakeTts(ready = false)
        val player = SpeechPlayer(tts)
        assertFalse(player.available.value)
        assertFalse(player.speak("hello"))
        assertTrue("nothing was sent to the engine", tts.spoken.isEmpty())
    }

    @Test
    fun anInterruptedLineReportsFalse() = runTest {
        val tts = FakeTts().also { it.speakResult = false }
        val player = SpeechPlayer(tts)
        assertFalse(player.speak("hello"))
        assertTrue("one failed line doesn't mark the engine as broken", player.available.value)
    }

    @Test
    fun waitsForASlowEngineToInitialise() = runTest {
        val tts = FakeTts(autoReady = false)
        val player = SpeechPlayer(tts, initTimeoutMs = 5_000)
        var result: Boolean? = null
        launch { result = player.speak("late") }
        advanceTimeBy(2_000)
        assertEquals(null, result)
        tts.becomeReady()
        runCurrent()
        assertEquals(true, result)
        assertEquals(listOf("late"), tts.spoken)
    }

    @Test
    fun givesUpOnAnEngineThatNeverInitialises() = runTest {
        val tts = FakeTts(autoReady = false)
        val player = SpeechPlayer(tts, initTimeoutMs = 5_000)
        var result: Boolean? = null
        launch { result = player.speak("never") }
        advanceTimeBy(5_001)
        runCurrent()
        assertEquals(false, result)
        assertFalse(player.available.value)
    }

    @Test
    fun cancellingASpokenLineStopsTheEngine() = runTest {
        val tts = FakeTts().also { it.speakResult = null }
        val player = SpeechPlayer(tts)
        val job = launch { player.speak("a long line") }
        runCurrent()
        assertEquals(0, tts.stops)
        job.cancel()
        runCurrent()
        assertEquals(1, tts.stops)
    }

    @Test
    fun stopAndShutdownReachTheEngine() = runTest {
        val tts = FakeTts()
        val player = SpeechPlayer(tts)
        player.stop()
        player.shutdown()
        assertEquals(1, tts.stops)
        assertEquals(1, tts.shutdowns)
    }
}
