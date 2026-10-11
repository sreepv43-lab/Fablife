package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.GuidedSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Speaks lines for a [SessionRunner]. */
interface Speaker {
    /** Speaks [text] and returns true once it has been spoken, or false if speech is unavailable or failed. */
    suspend fun speak(text: String): Boolean

    /** Cuts off the current utterance, if any. */
    fun stop()
}

data class RunnerState(
    /** Index of the current step, or -1 before the session starts. */
    val index: Int = -1,
    val caption: String? = null,
    val speaking: Boolean = false,
    val paused: Boolean = false,
    val finished: Boolean = false,
    val stopped: Boolean = false,
)

/**
 * Runs a guided session line by line. If there is no [speaker], or a line fails to speak, its caption is shown for the
 * time it takes to read, so a session always works as timed captions. Pausing cuts off the current line, which is
 * repeated from its start on resume; silences are honoured exactly and can be paused part-way through.
 */
class SessionRunner(private val session: GuidedSession, private val speaker: Speaker?) {
    private val _state = MutableStateFlow(RunnerState())
    val state: StateFlow<RunnerState> = _state.asStateFlow()

    private var scope: CoroutineScope? = null
    private var job: Job? = null
    private var index = 0
    private var silenceLeftMs = 0L
    private var inSilence = false

    fun start(scope: CoroutineScope) {
        if (job?.isActive == true || _state.value.finished || _state.value.stopped) return
        this.scope = scope
        launchLoop(scope)
    }

    fun pause() {
        if (job?.isActive != true) return
        job?.cancel()
        speaker?.stop()
        _state.value = _state.value.copy(paused = true, speaking = false)
    }

    fun resume() {
        val s = scope ?: return
        if (!_state.value.paused) return
        _state.value = _state.value.copy(paused = false)
        launchLoop(s)
    }

    fun stop() {
        job?.cancel()
        speaker?.stop()
        _state.value = _state.value.copy(speaking = false, paused = false, stopped = true)
    }

    private fun launchLoop(scope: CoroutineScope) {
        job = scope.launch { run() }
    }

    private suspend fun run() {
        val steps = session.steps
        while (index < steps.size) {
            val step = steps[index]
            if (!inSilence) {
                val text = step.say
                if (text != null) {
                    _state.value = _state.value.copy(index = index, caption = text, speaking = true)
                    speak(text)
                } else {
                    _state.value = _state.value.copy(index = index, speaking = false)
                }
                silenceLeftMs = (step.pause * 1000).toLong()
                inSilence = true
            }
            _state.value = _state.value.copy(speaking = false)
            while (silenceLeftMs > 0) {
                val tick = minOf(TICK_MS, silenceLeftMs)
                delay(tick)
                silenceLeftMs -= tick
            }
            inSilence = false
            index++
        }
        _state.value = _state.value.copy(speaking = false, finished = true)
    }

    private suspend fun speak(text: String) {
        val spoken = try {
            speaker?.speak(text) ?: false
        } catch (e: CancellationException) {
            speaker?.stop()
            throw e
        } catch (e: Exception) {
            false
        }
        if (!spoken) delay(SessionTimeline.readingMs(text))
    }

    private companion object {
        const val TICK_MS = 100L
    }
}
