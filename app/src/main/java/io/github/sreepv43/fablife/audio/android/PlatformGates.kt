package io.github.sreepv43.fablife.audio.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.PowerManager
import androidx.core.content.ContextCompat

// Thin seams over the platform services the audio controller uses, so its logic can be tested with fakes.

interface FocusGate {
    interface Listener {
        fun onFocusLost()
        fun onFocusLostTransient()
        fun onFocusDuck()
        fun onFocusGained()
    }

    /** Asks for audio focus. Returns false if another app (for example a call) won't give it up. */
    fun request(listener: Listener): Boolean
    fun abandon()
}

interface WakeLockGate {
    fun acquire(timeoutMs: Long)
    fun release()
}

interface NoisyGate {
    fun register(onNoisy: () -> Unit)
    fun unregister()
}

interface ServiceGate {
    fun start()
    fun stop()
}

class AndroidFocusGate(context: Context) : FocusGate {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private var request: AudioFocusRequest? = null

    override fun request(listener: FocusGate.Listener): Boolean {
        abandon()
        val r = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setOnAudioFocusChangeListener { change ->
                when (change) {
                    AudioManager.AUDIOFOCUS_LOSS -> listener.onFocusLost()
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> listener.onFocusLostTransient()
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> listener.onFocusDuck()
                    AudioManager.AUDIOFOCUS_GAIN -> listener.onFocusGained()
                }
            }
            .build()
        request = r
        return audioManager.requestAudioFocus(r) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun abandon() {
        request?.let { audioManager.abandonAudioFocusRequest(it) }
        request = null
    }
}

class AndroidWakeLockGate(context: Context) : WakeLockGate {
    private val lock = context.getSystemService(PowerManager::class.java)
        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "fablife:audio").apply { setReferenceCounted(false) }

    override fun acquire(timeoutMs: Long) = lock.acquire(timeoutMs)

    override fun release() {
        if (lock.isHeld) lock.release()
    }
}

class AndroidNoisyGate(private val context: Context) : NoisyGate {
    private var receiver: BroadcastReceiver? = null

    override fun register(onNoisy: () -> Unit) {
        unregister()
        val r = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = onNoisy()
        }
        ContextCompat.registerReceiver(context, r, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY), ContextCompat.RECEIVER_NOT_EXPORTED)
        receiver = r
    }

    override fun unregister() {
        receiver?.let { runCatching { context.unregisterReceiver(it) } }
        receiver = null
    }
}

class AndroidServiceGate(private val context: Context) : ServiceGate {
    override fun start() {
        // Playback must keep working even if Android refuses a foreground service (for example when started from the background).
        runCatching { ContextCompat.startForegroundService(context, Intent(context, AudioService::class.java)) }
    }

    override fun stop() {
        context.stopService(Intent(context, AudioService::class.java))
    }
}
