package io.github.sreepv43.fablife.audio.android

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.max

/** Where generated audio goes. The real one is an [AudioTrack]; tests use a fake. */
interface AudioOutput {
    fun start(sampleRate: Int)

    /** Writes [count] mono float samples, blocking until they are queued. Throws if the output has failed. */
    fun write(buffer: FloatArray, count: Int)

    fun pause()
    fun resume()
    fun release()
}

class AudioTrackOutput : AudioOutput {
    @Volatile private var track: AudioTrack? = null

    override fun start(sampleRate: Int) {
        val minBytes = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT)
        check(minBytes > 0) { "Audio output isn't available on this device" }
        val bytes = max(minBytes, sampleRate * Float.SIZE_BYTES / 2) // about half a second
        val t = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
            )
            .setAudioFormat(
                AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_FLOAT).setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
            )
            .setBufferSizeInBytes(bytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        check(t.state == AudioTrack.STATE_INITIALIZED) { "Audio output failed to start" }
        t.play()
        track = t
    }

    override fun write(buffer: FloatArray, count: Int) {
        val t = track ?: throw IllegalStateException("Audio output is closed")
        var offset = 0
        while (offset < count) {
            val written = t.write(buffer, offset, count - offset, AudioTrack.WRITE_BLOCKING)
            check(written >= 0) { "Audio output failed ($written)" }
            offset += written
        }
    }

    override fun pause() {
        track?.pause()
    }

    override fun resume() {
        track?.play()
    }

    override fun release() {
        val t = track ?: return
        track = null
        runCatching { t.stop() }
        t.release()
    }
}
