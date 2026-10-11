package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.content.GuidedSession
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

enum class PlaybackMode { IDLE, SOUND, GUIDED }

/** Everything the UI shows about audio that is playing, paused or just finished. */
data class PlaybackState(
    val mode: PlaybackMode = PlaybackMode.IDLE,
    val soundId: SoundId? = null,
    val sessionId: String? = null,
    val title: String? = null,
    val isPlaying: Boolean = false,
    /** The user's volume 0..1 for the sound (for a guided session, its ambient bed). */
    val volume: Float = 0.6f,
    val sleepTimerRemainingMs: Long? = null,
    val captionText: String? = null,
    val captionIndex: Int = -1,
    val elapsedMs: Long = 0,
    val totalMs: Long = 0,
    /** False when no voice is available and a guided session is showing timed captions instead. */
    val ttsAvailable: Boolean = true,
    /** The sound's sleep timer ran out, or the guided session reached its end. The mode stays until the next play or [AudioController.stop]. */
    val finished: Boolean = false,
    val error: String? = null,
) {
    /** True while there is something to show in a mini-player. */
    val active: Boolean get() = mode != PlaybackMode.IDLE && !finished
}

/** Emitted when playback ends for any reason, so time spent can be logged in one place. */
data class PlaybackEnded(val mode: PlaybackMode, val ref: String, val minutes: Int, val completed: Boolean)

interface AudioController {
    val state: StateFlow<PlaybackState>
    val ended: SharedFlow<PlaybackEnded>

    fun playSound(id: SoundId, volume: Float? = null, timerMinutes: Int? = null)
    fun playGuided(session: GuidedSession, withAmbient: Boolean)
    fun pause()
    fun resume()
    fun stop()
    fun setVolume(volume: Float)

    /** Starts (or, with null, cancels) a sleep timer for the sound that is playing. */
    fun setSleepTimer(minutes: Int?)
}
