package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.SleepTimerPlan
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.content.GuidedSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** A controller that only records what the UI asks of it, for screen tests. */
class FakeAudioController : AudioController {
    val mutableState = MutableStateFlow(PlaybackState())
    val mutableEnded = MutableSharedFlow<PlaybackEnded>(extraBufferCapacity = 8)
    override val state: StateFlow<PlaybackState> = mutableState.asStateFlow()
    override val ended: SharedFlow<PlaybackEnded> = mutableEnded.asSharedFlow()
    val calls = mutableListOf<String>()

    override fun playSound(id: SoundId, volume: Float?, timerMinutes: Int?) {
        calls += "playSound:${id.key}:${volume ?: "-"}:${timerMinutes ?: "-"}"
        mutableState.value = PlaybackState(
            PlaybackMode.SOUND, soundId = id, title = id.key, isPlaying = true, volume = volume ?: mutableState.value.volume,
            sleepTimerRemainingMs = timerMinutes?.let { it * 60_000L },
        )
    }

    override fun playGuided(session: GuidedSession, withAmbient: Boolean) {
        calls += "playGuided:${session.id}:$withAmbient"
        mutableState.value = PlaybackState(PlaybackMode.GUIDED, sessionId = session.id, title = session.title, isPlaying = true, totalMs = session.minutes * 60_000L)
    }

    override fun pause() {
        calls += "pause"
        mutableState.value = mutableState.value.copy(isPlaying = false)
    }

    override fun resume() {
        calls += "resume"
        mutableState.value = mutableState.value.copy(isPlaying = true)
    }

    override fun stop() {
        calls += "stop"
        mutableState.value = PlaybackState(volume = mutableState.value.volume)
    }

    override fun setVolume(volume: Float) {
        calls += "volume:$volume"
        mutableState.value = mutableState.value.copy(volume = volume)
    }

    override fun setSleepTimer(minutes: Int?) {
        calls += "timer:${minutes ?: "off"}"
        mutableState.value = mutableState.value.copy(sleepTimerRemainingMs = minutes?.let { it * 60_000L })
    }
}

class FakeOutput(private val failWritesAfter: Int = Int.MAX_VALUE, private val failStart: Boolean = false) : AudioOutput {
    var sampleRate = 0
    var started = false
    var paused = false
    var released = false
    val blocks = mutableListOf<FloatArray>()

    override fun start(sampleRate: Int) {
        check(!failStart) { "no audio device" }
        this.sampleRate = sampleRate
        started = true
    }

    @Synchronized
    override fun write(buffer: FloatArray, count: Int) {
        check(!released) { "closed" }
        check(blocks.size < failWritesAfter) { "device lost" }
        blocks += buffer.copyOf(count)
    }

    override fun pause() { paused = true }
    override fun resume() { paused = false }
    override fun release() { released = true }

    fun peak(block: Int): Float = blocks[block].maxOf { kotlin.math.abs(it) }
}

class FakePlayer : SoundPlayer {
    override var listener: SoundPlayer.Listener? = null
    val calls = mutableListOf<String>()
    var lastVolume = -1f
    var lastFadeInMs = -1L
    var lastTimer: SleepTimerPlan? = null
    var duckLevel = false

    override fun play(id: SoundId, volume: Float, fadeInMs: Long, timer: SleepTimerPlan?) {
        calls += "play:${id.key}"
        lastVolume = volume
        lastFadeInMs = fadeInMs
        lastTimer = timer
    }

    override fun pause() { calls += "pause" }
    override fun resume() { calls += "resume" }
    override fun setVolume(volume: Float) { calls += "volume"; lastVolume = volume }
    override fun setDucked(ducked: Boolean) { duckLevel = ducked }
    override fun setTimer(timer: SleepTimerPlan?) { calls += "timer:${timer?.minutes ?: "off"}"; lastTimer = timer }
    override fun stop() { calls += "stop" }
}

class FakeFocus(var grant: Boolean = true) : FocusGate {
    var listener: FocusGate.Listener? = null
    var requests = 0
    var abandoned = 0

    override fun request(listener: FocusGate.Listener): Boolean {
        requests++
        if (grant) this.listener = listener
        return grant
    }

    override fun abandon() { abandoned++ }
}

class FakeWakeLock : WakeLockGate {
    var held = false
    var acquires = 0
    override fun acquire(timeoutMs: Long) { held = true; acquires++ }
    override fun release() { held = false }
}

class FakeNoisy : NoisyGate {
    var callback: (() -> Unit)? = null
    override fun register(onNoisy: () -> Unit) { callback = onNoisy }
    override fun unregister() { callback = null }
}

class FakeServiceGate : ServiceGate {
    var running = false
    var starts = 0
    override fun start() { running = true; starts++ }
    override fun stop() { running = false }
}

/** A voice that takes [speakMs] of virtual time per line. */
class FakeVoice(private val speakMs: Long = 500, available: Boolean = true) : Voice {
    override val available = MutableStateFlow(available)
    val spoken = mutableListOf<String>()
    var shutdowns = 0

    override suspend fun speak(text: String): Boolean {
        spoken += text
        if (!available.value) return false
        delay(speakMs)
        return true
    }

    override fun stop() = Unit
    override fun shutdown() { shutdowns++ }
}

class FakeTts(private val ready: Boolean = true, private val autoReady: Boolean = true) : TtsEngine {
    private var onReady: ((Boolean) -> Unit)? = null
    private val pending = mutableMapOf<String, (Boolean) -> Unit>()
    val spoken = mutableListOf<String>()
    var stops = 0
    var shutdowns = 0
    var speakResult: Boolean? = true

    override fun init(onReady: (Boolean) -> Unit) {
        this.onReady = onReady
        if (autoReady) onReady(ready)
    }

    fun becomeReady(ok: Boolean = true) = onReady?.invoke(ok)

    override fun speak(text: String, utteranceId: String, onDone: (Boolean) -> Unit) {
        spoken += text
        val result = speakResult
        if (result != null) onDone(result) else pending[utteranceId] = onDone
    }

    fun finishAll(result: Boolean) {
        val all = pending.values.toList()
        pending.clear()
        all.forEach { it(result) }
    }

    override fun stop() { stops++ }
    override fun shutdown() { shutdowns++ }
}
