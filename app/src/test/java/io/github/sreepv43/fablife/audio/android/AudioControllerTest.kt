package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.content.GuidedSession
import io.github.sreepv43.fablife.content.GuidedStep
import io.github.sreepv43.fablife.content.SessionCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AudioControllerTest {
    private class Rig(scope: TestScope, voice: FakeVoice? = FakeVoice()) {
        val player = FakePlayer()
        val focus = FakeFocus()
        val wake = FakeWakeLock()
        val noisy = FakeNoisy()
        val service = FakeServiceGate()
        var voice: FakeVoice? = voice
        // A plain scope on the test scheduler: coroutines in backgroundScope are ignored by advanceUntilIdle().
        val appScope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(scope.testScheduler))
        val controller = AudioControllerImpl(appScope, player, { this.voice }, focus, wake, noisy, service) { "Sound ${it.key}" }
        val ended = mutableListOf<PlaybackEnded>()

        init {
            scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) { controller.ended.collect { ended += it } }
        }

        val state get() = controller.state.value
        fun idle() = assertEquals(PlaybackMode.IDLE, state.mode)
    }

    private fun session(vararg lines: String, ambient: String? = "focus_pad") =
        GuidedSession("calm", "Calm minute", 1, SessionCategory.CALM, "d", "b", ambient, lines.map { GuidedStep(it, 1f) })

    @Test
    fun playingASoundTakesFocusStartsTheServiceAndHoldsTheDevice() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.RAIN, volume = 0.4f, timerMinutes = 30)
        val s = rig.state
        assertEquals(PlaybackMode.SOUND, s.mode)
        assertEquals(SoundId.RAIN, s.soundId)
        assertEquals("Sound rain", s.title)
        assertTrue(s.isPlaying && s.active)
        assertEquals(0.4f, s.volume, 0f)
        assertEquals(30 * 60_000L, s.sleepTimerRemainingMs)
        assertEquals(listOf("play:rain"), rig.player.calls)
        assertEquals(0.4f, rig.player.lastVolume, 0f)
        assertEquals(30, rig.player.lastTimer?.minutes)
        assertEquals(1, rig.focus.requests)
        assertTrue(rig.service.running)
        assertTrue(rig.wake.held)
        assertNotNull(rig.noisy.callback)
    }

    @Test
    fun aRefusedAudioFocusPlaysNothingAndSaysSo() = runTest {
        val rig = Rig(this)
        rig.focus.grant = false
        rig.controller.playSound(SoundId.RAIN)
        assertEquals(PlaybackMode.IDLE, rig.state.mode)
        assertNotNull(rig.state.error)
        assertTrue(rig.player.calls.none { it.startsWith("play") })
        assertFalse(rig.service.running)
        assertFalse(rig.wake.held)
    }

    @Test
    fun pauseAndResumeToggleThePlayerAndTheWakeLock() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.OCEAN)
        rig.controller.pause()
        assertFalse(rig.state.isPlaying)
        assertTrue(rig.state.active)
        assertFalse(rig.wake.held)
        assertTrue(rig.service.running)
        rig.controller.resume()
        assertTrue(rig.state.isPlaying)
        assertTrue(rig.wake.held)
        assertEquals(listOf("play:ocean", "pause", "resume"), rig.player.calls)
        assertEquals(2, rig.focus.requests)
    }

    @Test
    fun resumeWithAnotherAppHoldingAudioStaysPausedWithAMessage() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.OCEAN)
        rig.controller.pause()
        rig.focus.grant = false
        rig.controller.resume()
        assertFalse(rig.state.isPlaying)
        assertNotNull(rig.state.error)
        assertEquals(listOf("play:ocean", "pause"), rig.player.calls)
    }

    @Test
    fun unpluggingHeadphonesPauses() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.PINK)
        rig.noisy.callback!!.invoke()
        assertFalse(rig.state.isPlaying)
        assertTrue(rig.state.active)
    }

    @Test
    fun aCallPausesAndResumesByItselfButAPermanentLossDoesNot() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.PINK)

        rig.focus.listener!!.onFocusLostTransient()
        runCurrent()
        assertFalse(rig.state.isPlaying)
        rig.focus.listener!!.onFocusGained()
        runCurrent()
        assertTrue(rig.state.isPlaying)

        rig.focus.listener!!.onFocusLost()
        runCurrent()
        assertFalse(rig.state.isPlaying)
        rig.focus.listener!!.onFocusGained()
        runCurrent()
        assertFalse("another app took focus for good", rig.state.isPlaying)
    }

    @Test
    fun aUserPauseIsNotUndoneWhenFocusReturns() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.PINK)
        rig.focus.listener!!.onFocusLostTransient()
        runCurrent()
        rig.controller.pause()
        rig.focus.listener!!.onFocusGained()
        runCurrent()
        assertFalse(rig.state.isPlaying)
    }

    @Test
    fun aNotificationDucksTheSound() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.PINK)
        rig.focus.listener!!.onFocusDuck()
        runCurrent()
        assertTrue(rig.player.duckLevel)
        rig.focus.listener!!.onFocusGained()
        runCurrent()
        assertFalse(rig.player.duckLevel)
        assertTrue(rig.state.isPlaying)
    }

    @Test
    fun stoppingReleasesEverythingAndReportsTheTimeSpent() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.RAIN)
        rig.controller.onProgress(7 * 60_000L + 10_000, null)
        rig.controller.stop()
        rig.idle()
        assertFalse(rig.service.running)
        assertFalse(rig.wake.held)
        assertNull(rig.noisy.callback)
        assertEquals(1, rig.focus.abandoned)
        assertEquals(listOf(PlaybackEnded(PlaybackMode.SOUND, "rain", 7, completed = false)), rig.ended)
    }

    @Test
    fun aSoundStoppedAlmostStraightAwayIsNotLogged() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.RAIN)
        rig.controller.onProgress(8_000, null)
        rig.controller.stop()
        assertTrue(rig.ended.isEmpty())
    }

    @Test
    fun startingAnotherSoundEndsTheFirstAndLogsIt() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.RAIN)
        rig.controller.onProgress(3 * 60_000L, null)
        rig.controller.playSound(SoundId.OCEAN)
        assertEquals(SoundId.OCEAN, rig.state.soundId)
        assertEquals(listOf(PlaybackEnded(PlaybackMode.SOUND, "rain", 3, completed = false)), rig.ended)
        assertEquals(listOf("play:rain", "stop", "play:ocean"), rig.player.calls)
    }

    @Test
    fun theSleepTimerEndingFinishesThePlaybackAndLogsItAsCompleted() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.BROWN, timerMinutes = 15)
        rig.controller.onProgress(15 * 60_000L, 0)
        rig.controller.onTimerFinished()
        runCurrent()
        val s = rig.state
        assertTrue(s.finished)
        assertFalse(s.active)
        assertFalse(s.isPlaying)
        assertFalse(rig.service.running)
        assertFalse(rig.wake.held)
        assertEquals(listOf(PlaybackEnded(PlaybackMode.SOUND, "brown", 15, completed = true)), rig.ended)
        rig.controller.stop()
        rig.idle()
        assertEquals("already logged, not again", 1, rig.ended.size)
    }

    @Test
    fun progressUpdatesTheTimerAndElapsedTime() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.BROWN, timerMinutes = 30)
        rig.controller.onProgress(60_000, 29 * 60_000L)
        assertEquals(60_000L, rig.state.elapsedMs)
        assertEquals(29 * 60_000L, rig.state.sleepTimerRemainingMs)
    }

    @Test
    fun theSleepTimerCanBeSetAndCancelledWhileASoundPlays() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.BROWN)
        rig.controller.setSleepTimer(60)
        assertEquals(60 * 60_000L, rig.state.sleepTimerRemainingMs)
        assertEquals(60, rig.player.lastTimer?.minutes)
        rig.controller.setSleepTimer(null)
        assertNull(rig.state.sleepTimerRemainingMs)
        assertNull(rig.player.lastTimer)
    }

    @Test
    fun theVolumeIsClampedAndRemembered() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.BROWN)
        rig.controller.setVolume(1.7f)
        assertEquals(1f, rig.state.volume, 0f)
        assertEquals(1f, rig.player.lastVolume, 0f)
        rig.controller.stop()
        assertEquals("the next sound starts at the same level", 1f, rig.controller.state.value.volume, 0f)
    }

    @Test
    fun anOutputErrorStopsEverythingAndShowsTheMessage() = runTest {
        val rig = Rig(this)
        rig.controller.playSound(SoundId.RAIN)
        rig.controller.onError("no audio device")
        runCurrent()
        rig.idle()
        assertEquals("no audio device", rig.state.error)
        assertFalse(rig.service.running)
        assertFalse(rig.wake.held)
    }

    @Test
    fun aGuidedSessionShowsCaptionsInOrderAndFinishesAsCompleted() = runTest {
        val rig = Rig(this)
        val captions = mutableListOf<String?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { rig.controller.state.collect { captions += it.captionText } }
        rig.controller.playGuided(session("One.", "Two.", "Three."), withAmbient = true)
        assertEquals(PlaybackMode.GUIDED, rig.state.mode)
        assertEquals("calm", rig.state.sessionId)
        assertTrue(rig.state.totalMs > 0)
        advanceUntilIdle()
        assertEquals(listOf("One.", "Two.", "Three."), captions.filterNotNull().distinct().take(3))
        assertTrue(rig.state.finished)
        assertFalse(rig.service.running)
        assertEquals(1, rig.ended.size)
        assertEquals(PlaybackEnded(PlaybackMode.GUIDED, "calm", 1, completed = true).copy(minutes = rig.ended[0].minutes), rig.ended[0])
        assertEquals(listOf("One.", "Two.", "Three."), rig.voice!!.spoken)
        assertEquals("the voice is released", 1, rig.voice!!.shutdowns)
    }

    @Test
    fun theAmbientBedIsQuietAndOptional() = runTest {
        val loud = Rig(this)
        loud.controller.setVolume(0.9f)
        loud.controller.playGuided(session("One."), withAmbient = true)
        assertEquals(listOf("play:focus_pad"), loud.player.calls.filter { it.startsWith("play") })
        assertEquals(0.3f, loud.player.lastVolume, 0.0001f)

        val none = Rig(this)
        none.controller.playGuided(session("One."), withAmbient = false)
        assertTrue(none.player.calls.none { it.startsWith("play") })
    }

    @Test
    fun aMissingVoiceFallsBackToTimedCaptionsAndSaysSo() = runTest {
        val rig = Rig(this, voice = FakeVoice(available = false))
        rig.controller.playGuided(session("One.", "Two."), withAmbient = false)
        runCurrent()
        assertFalse(rig.state.ttsAvailable)
        advanceUntilIdle()
        assertTrue(rig.state.finished)
        assertEquals(PlaybackMode.GUIDED, rig.ended.single().mode)
    }

    @Test
    fun noVoiceAtAllStillRunsTheSession() = runTest {
        val rig = Rig(this, voice = null)
        rig.controller.playGuided(session("One.", "Two."), withAmbient = false)
        advanceUntilIdle()
        assertTrue(rig.state.finished)
    }

    @Test
    fun stoppingAGuidedSessionPartWayLogsOnlyTheTimeReached() = runTest {
        val rig = Rig(this, voice = FakeVoice(speakMs = 60_000))
        rig.controller.playGuided(session("One.", "Two.", "Three."), withAmbient = false)
        advanceTimeBy(70_000)
        rig.controller.stop()
        rig.idle()
        assertTrue(rig.ended.size <= 1)
        rig.ended.firstOrNull()?.let { assertFalse(it.completed) }
        assertFalse(rig.service.running)
        assertEquals(1, rig.voice!!.shutdowns)
    }

    @Test
    fun pausingAGuidedSessionPausesTheVoiceAndTheBed() = runTest {
        val rig = Rig(this)
        rig.controller.playGuided(session("One.", "Two."), withAmbient = true)
        runCurrent()
        rig.controller.pause()
        assertFalse(rig.state.isPlaying)
        assertTrue(rig.player.calls.contains("pause"))
        val spokenWhilePaused = rig.voice!!.spoken.size
        advanceTimeBy(120_000)
        assertEquals("nothing is spoken while paused", spokenWhilePaused, rig.voice!!.spoken.size)
        rig.controller.resume()
        advanceUntilIdle()
        assertTrue(rig.state.finished)
    }

    @Test
    fun theSleepTimerIsIgnoredForGuidedSessions() = runTest {
        val rig = Rig(this)
        rig.controller.playGuided(session("One."), withAmbient = false)
        rig.controller.setSleepTimer(30)
        assertNull(rig.state.sleepTimerRemainingMs)
    }
}
