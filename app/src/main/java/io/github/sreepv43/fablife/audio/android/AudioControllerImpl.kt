package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.SessionRunner
import io.github.sreepv43.fablife.audio.SessionTimeline
import io.github.sreepv43.fablife.audio.SleepTimerPlan
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.Speaker
import io.github.sreepv43.fablife.content.GuidedSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A voice that can say whether it is usable; implemented by [SpeechPlayer]. */
interface Voice : Speaker {
    val available: StateFlow<Boolean>

    /** Releases the speech engine; the voice isn't used again. */
    fun shutdown() = Unit
}

class SpeechVoice(private val player: SpeechPlayer) : Voice {
    override val available: StateFlow<Boolean> get() = player.available
    override suspend fun speak(text: String): Boolean = player.speak(text)
    override fun stop() = player.stop()
    override fun shutdown() = player.shutdown()
}

/**
 * The single place that decides what plays. Must be called from one thread (the main thread in the app); the audio
 * thread's callbacks hop onto [scope] before touching anything here.
 */
class AudioControllerImpl(
    private val scope: CoroutineScope,
    private val player: SoundPlayer,
    private val voiceFactory: () -> Voice?,
    private val focus: FocusGate,
    private val wakeLock: WakeLockGate,
    private val noisy: NoisyGate,
    private val service: ServiceGate,
    private val soundTitle: (SoundId) -> String = { it.key },
) : AudioController, SoundPlayer.Listener, FocusGate.Listener {
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val _ended = MutableSharedFlow<PlaybackEnded>(extraBufferCapacity = 8)
    override val ended: SharedFlow<PlaybackEnded> = _ended.asSharedFlow()

    private var runner: SessionRunner? = null
    private var observers = emptyList<Job>()
    private var voice: Voice? = null
    private var pausedByFocus = false
    @Volatile private var playedMs = 0L

    init {
        player.listener = this
    }

    override fun playSound(id: SoundId, volume: Float?, timerMinutes: Int?) {
        val level = (volume ?: _state.value.volume).coerceIn(0f, 1f)
        beginPlayback(PlaybackState(PlaybackMode.SOUND, soundId = id, title = soundTitle(id), volume = level, sleepTimerRemainingMs = timerMinutes?.let { it * 60_000L })) ?: return
        player.play(id, level, FADE_IN_MS, timerMinutes?.let(::SleepTimerPlan))
    }

    override fun playGuided(session: GuidedSession, withAmbient: Boolean) {
        val timeline = SessionTimeline(session)
        val level = _state.value.volume.coerceIn(0f, 1f).coerceAtMost(AMBIENT_MAX_VOLUME)
        beginPlayback(
            PlaybackState(
                PlaybackMode.GUIDED, sessionId = session.id, title = session.title, volume = level, totalMs = timeline.totalMs,
                ttsAvailable = true,
            ),
        ) ?: return
        val ambient = if (withAmbient) SoundId.fromKey(session.ambient) else null
        if (ambient != null) player.play(ambient, level, AMBIENT_FADE_IN_MS, null)
        val v = voiceFactory()
        voice = v
        val r = SessionRunner(session, v)
        runner = r
        observers = listOfNotNull(
            scope.launch {
                r.state.collect { s ->
                    if (s.index >= 0) playedMs = maxOf(playedMs, timeline.startOfStepMs(s.index))
                    _state.update { it.copy(captionText = s.caption, captionIndex = s.index, elapsedMs = timeline.startOfStepMs(maxOf(0, s.index))) }
                    if (s.finished) finishGuided(timeline.totalMs)
                }
            },
            v?.let { vv -> scope.launch { vv.available.collect { ok -> _state.update { it.copy(ttsAvailable = ok) } } } },
        )
        r.start(scope)
    }

    /** Stops what is playing, takes audio focus and starts the platform helpers. Returns null if focus was refused. */
    private fun beginPlayback(next: PlaybackState): Unit? {
        endCurrent(completed = false)
        if (!focus.request(this)) {
            _state.value = PlaybackState(volume = next.volume, error = "Another app is using audio right now. Try again in a moment.")
            return null
        }
        pausedByFocus = false
        playedMs = 0
        _state.value = next.copy(isPlaying = true)
        service.start()
        wakeLock.acquire(WAKE_LOCK_MS)
        noisy.register { pause() }
        return Unit
    }

    override fun pause() {
        val s = _state.value
        if (!s.active) return
        pausedByFocus = false // a pause the user asked for must not be undone when focus comes back
        if (s.isPlaying) applyPause()
    }

    private fun applyPause() {
        runner?.pause()
        player.pause()
        wakeLock.release()
        _state.update { it.copy(isPlaying = false) }
    }

    override fun resume() {
        val s = _state.value
        if (!s.active || s.isPlaying) return
        if (!focus.request(this)) {
            _state.update { it.copy(error = "Another app is using audio right now. Try again in a moment.") }
            return
        }
        pausedByFocus = false
        wakeLock.acquire(WAKE_LOCK_MS)
        player.resume()
        runner?.resume()
        _state.update { it.copy(isPlaying = true, error = null) }
    }

    override fun stop() {
        endCurrent(completed = false)
        _state.value = PlaybackState(volume = _state.value.volume)
    }

    override fun setVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        player.setVolume(v)
        _state.update { it.copy(volume = v) }
    }

    override fun setSleepTimer(minutes: Int?) {
        val s = _state.value
        if (s.mode != PlaybackMode.SOUND || !s.active) return
        player.setTimer(minutes?.let(::SleepTimerPlan))
        _state.update { it.copy(sleepTimerRemainingMs = minutes?.let { m -> m * 60_000L }) }
    }

    // SoundPlayer.Listener: called on the audio thread.

    override fun onProgress(elapsedMs: Long, timerRemainingMs: Long?) {
        if (_state.value.mode == PlaybackMode.SOUND) {
            playedMs = elapsedMs
            _state.update { if (it.mode == PlaybackMode.SOUND) it.copy(elapsedMs = elapsedMs, sleepTimerRemainingMs = timerRemainingMs) else it }
        }
    }

    override fun onTimerFinished() {
        scope.launch {
            if (_state.value.mode != PlaybackMode.SOUND) return@launch
            val ref = _state.value.soundId?.key.orEmpty()
            val minutes = minutesOf(playedMs)
            teardown()
            _state.update { it.copy(isPlaying = false, finished = true, sleepTimerRemainingMs = 0) }
            _ended.tryEmit(PlaybackEnded(PlaybackMode.SOUND, ref, minutes, completed = true))
        }
    }

    override fun onError(message: String) {
        scope.launch {
            endCurrent(completed = false)
            _state.value = PlaybackState(volume = _state.value.volume, error = message)
        }
    }

    // FocusGate.Listener

    override fun onFocusLost() {
        scope.launch { if (_state.value.isPlaying) { pausedByFocus = false; applyPause() } }
    }

    override fun onFocusLostTransient() {
        scope.launch { if (_state.value.isPlaying) { applyPause(); pausedByFocus = true } }
    }

    override fun onFocusDuck() {
        scope.launch { player.setDucked(true) }
    }

    override fun onFocusGained() {
        scope.launch {
            player.setDucked(false)
            if (pausedByFocus && _state.value.active && !_state.value.isPlaying) {
                pausedByFocus = false
                wakeLock.acquire(WAKE_LOCK_MS)
                player.resume()
                runner?.resume()
                _state.update { it.copy(isPlaying = true) }
            }
        }
    }

    private fun finishGuided(totalMs: Long) {
        val s = _state.value
        if (s.mode != PlaybackMode.GUIDED || s.finished) return
        val ref = s.sessionId.orEmpty()
        val minutes = minutesOf(totalMs)
        teardown()
        _state.update { it.copy(isPlaying = false, finished = true, elapsedMs = totalMs) }
        _ended.tryEmit(PlaybackEnded(PlaybackMode.GUIDED, ref, minutes, completed = true))
    }

    /** Ends playback the user (or a new playback) interrupted, reporting the time actually spent. */
    private fun endCurrent(completed: Boolean) {
        val s = _state.value
        if (s.mode == PlaybackMode.IDLE) return
        if (!s.finished) {
            val ref = if (s.mode == PlaybackMode.SOUND) s.soundId?.key else s.sessionId
            val minutes = minutesOf(playedMs)
            if (ref != null && minutes > 0) _ended.tryEmit(PlaybackEnded(s.mode, ref, minutes, completed))
        }
        teardown()
    }

    /** Releases the player, voice, focus, wake lock, receiver and service. Safe to call more than once. */
    private fun teardown() {
        observers.forEach { it.cancel() }
        observers = emptyList()
        runner?.stop()
        runner = null
        voice?.shutdown()
        voice = null
        player.stop()
        focus.abandon()
        wakeLock.release()
        noisy.unregister()
        service.stop()
        pausedByFocus = false
    }

    private fun minutesOf(ms: Long): Int = ((ms + 30_000) / 60_000).toInt()

    private companion object {
        const val FADE_IN_MS = 1_500L
        const val AMBIENT_FADE_IN_MS = 3_000L
        const val AMBIENT_MAX_VOLUME = 0.3f
        const val WAKE_LOCK_MS = 3 * 60 * 60 * 1000L
    }
}
