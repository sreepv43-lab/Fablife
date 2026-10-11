package io.github.sreepv43.fablife.audio.android

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import io.github.sreepv43.fablife.audio.Speaker
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

/** Text-to-speech as the speech player needs it. The real one wraps Android's engine; tests use a fake. */
interface TtsEngine {
    /** Starts the engine; [onReady] gets true when it can speak in some language. */
    fun init(onReady: (Boolean) -> Unit)

    /** Speaks [text]; [onDone] gets true if it finished, false if it failed or was interrupted. */
    fun speak(text: String, utteranceId: String, onDone: (Boolean) -> Unit)

    fun stop()
    fun shutdown()
}

class AndroidTtsEngine(private val context: Context) : TtsEngine {
    private var tts: TextToSpeech? = null
    private val pending = ConcurrentHashMap<String, (Boolean) -> Unit>()

    override fun init(onReady: (Boolean) -> Unit) {
        tts = try {
            TextToSpeech(context) { status ->
                val engine = tts
                onReady(status == TextToSpeech.SUCCESS && engine != null && configure(engine))
            }
        } catch (e: Exception) {
            onReady(false)
            null
        }
    }

    private fun configure(engine: TextToSpeech): Boolean {
        fun usable(r: Int) = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
        if (!usable(engine.setLanguage(Locale.getDefault())) && !usable(engine.setLanguage(Locale.US))) return false
        engine.setSpeechRate(SPEECH_RATE)
        engine.setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build(),
        )
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) = finish(utteranceId, true)

            @Deprecated("Replaced by the version with an error code")
            override fun onError(utteranceId: String?) = finish(utteranceId, false)
            override fun onError(utteranceId: String?, errorCode: Int) = finish(utteranceId, false)
            override fun onStop(utteranceId: String?, interrupted: Boolean) = finish(utteranceId, false)
        })
        return true
    }

    private fun finish(id: String?, spoken: Boolean) {
        id?.let { pending.remove(it) }?.invoke(spoken)
    }

    override fun speak(text: String, utteranceId: String, onDone: (Boolean) -> Unit) {
        val engine = tts
        if (engine == null) {
            onDone(false)
            return
        }
        pending[utteranceId] = onDone
        if (engine.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), utteranceId) == TextToSpeech.ERROR) finish(utteranceId, false)
    }

    override fun stop() {
        tts?.stop()
    }

    override fun shutdown() {
        tts?.shutdown()
        tts = null
        pending.keys.toList().forEach { finish(it, false) }
    }

    private companion object {
        const val SPEECH_RATE = 0.9f
    }
}

/** The voice for guided sessions. If there is no usable speech engine it reports so and every line fails, so sessions fall back to captions. */
class SpeechPlayer(private val engine: TtsEngine, private val initTimeoutMs: Long = 5_000) : Speaker {
    private val ready = CompletableDeferred<Boolean>()
    private val _available = MutableStateFlow(true)
    private var counter = 0

    /** False once the engine is known to be missing or broken. */
    val available: StateFlow<Boolean> = _available.asStateFlow()

    init {
        engine.init { ok ->
            if (!ok) _available.value = false
            ready.complete(ok)
        }
    }

    override suspend fun speak(text: String): Boolean {
        if (withTimeoutOrNull(initTimeoutMs) { ready.await() } != true) {
            _available.value = false
            return false
        }
        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { engine.stop() }
            engine.speak(text, "line-${counter++}") { spoken -> if (cont.isActive) cont.resume(spoken) }
        }
    }

    override fun stop() = engine.stop()

    fun shutdown() = engine.shutdown()
}
