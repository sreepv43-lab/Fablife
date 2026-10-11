package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.Fades
import io.github.sreepv43.fablife.audio.SleepTimerPlan
import io.github.sreepv43.fablife.audio.SoundGenerator
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.Soundscapes

/** Plays one generated sound at a time. Callbacks arrive on the audio thread. */
interface SoundPlayer {
    interface Listener {
        /** [elapsedMs] of audio played; [timerRemainingMs] when a sleep timer is running. */
        fun onProgress(elapsedMs: Long, timerRemainingMs: Long?)

        /** The sleep timer ran out and the sound has faded out and stopped. */
        fun onTimerFinished()

        /** The audio output failed. The player has stopped. */
        fun onError(message: String)
    }

    var listener: Listener?

    fun play(id: SoundId, volume: Float, fadeInMs: Long, timer: SleepTimerPlan?)
    fun pause()
    fun resume()
    fun setVolume(volume: Float)
    fun setDucked(ducked: Boolean)

    /** Starts (or, with null, cancels) a sleep timer counted from now. */
    fun setTimer(timer: SleepTimerPlan?)

    fun stop()
}

/**
 * Renders a [SoundGenerator] into an [AudioOutput] on its own thread, with a fade-in, software volume that glides
 * between blocks (no clicks), brief ducking, and a sleep timer that fades out and stops.
 */
class AudioEngine(
    private val outputFactory: () -> AudioOutput,
    private val sampleRate: Int = Soundscapes.DEFAULT_SAMPLE_RATE,
    private val threaded: Boolean = true,
) : SoundPlayer {
    override var listener: SoundPlayer.Listener? = null

    private val lock = Object()
    private var output: AudioOutput? = null
    private var generator: SoundGenerator? = null
    private var thread: Thread? = null
    private var running = false
    private var paused = false
    private var volume = 0.6f
    private var ducked = false
    private var fadeInMs = 0L
    private var timer: SleepTimerPlan? = null
    private var timerStartSamples = 0L
    private var writtenSamples = 0L
    private var lastGain = 0f
    private var blocks = 0
    private val buffer = FloatArray(BLOCK)

    override fun play(id: SoundId, volume: Float, fadeInMs: Long, timer: SleepTimerPlan?) {
        stop()
        val out = outputFactory()
        try {
            out.start(sampleRate)
        } catch (e: Exception) {
            out.release()
            listener?.onError(e.message ?: "Couldn't start audio")
            return
        }
        synchronized(lock) {
            output = out
            generator = Soundscapes.create(id, sampleRate, seed = System.nanoTime())
            this.volume = volume.coerceIn(0f, 1f)
            ducked = false
            this.fadeInMs = fadeInMs
            this.timer = timer
            timerStartSamples = 0
            writtenSamples = 0
            lastGain = 0f
            blocks = 0
            paused = false
            running = true
        }
        if (threaded) thread = Thread(::loop, "fablife-audio").also { it.isDaemon = true; it.start() }
    }

    override fun pause() {
        val out: AudioOutput?
        synchronized(lock) {
            if (!running || paused) return
            paused = true
            out = output
        }
        out?.pause()
    }

    override fun resume() {
        val out: AudioOutput?
        synchronized(lock) {
            if (!running || !paused) return
            paused = false
            out = output
            lock.notifyAll()
        }
        out?.resume()
    }

    override fun setVolume(volume: Float) = synchronized(lock) { this.volume = volume.coerceIn(0f, 1f) }

    override fun setDucked(ducked: Boolean) = synchronized(lock) { this.ducked = ducked }

    override fun setTimer(timer: SleepTimerPlan?) = synchronized(lock) {
        this.timer = timer
        timerStartSamples = writtenSamples
    }

    override fun stop() {
        val out: AudioOutput?
        val t: Thread?
        synchronized(lock) {
            out = output
            t = thread
            running = false
            paused = false
            output = null
            generator = null
            thread = null
            lock.notifyAll()
        }
        out?.release()
        if (t != null && t !== Thread.currentThread()) t.join(JOIN_MS)
    }

    private fun loop() {
        try {
            while (true) {
                synchronized(lock) {
                    while (paused && running) lock.wait()
                    if (!running) return
                }
                if (!pump()) return
            }
        } catch (e: Exception) {
            val wasRunning = synchronized(lock) { running }
            if (wasRunning) {
                stop()
                listener?.onError(e.message ?: "Audio stopped unexpectedly")
            }
        }
    }

    /** Renders and writes one block. Returns false once the sound has ended. Internal so tests can drive it without a thread. */
    internal fun pump(): Boolean {
        val gen: SoundGenerator
        val out: AudioOutput
        val target: Float
        val timerNow: SleepTimerPlan?
        val elapsedMs: Long
        val timerElapsedMs: Long
        synchronized(lock) {
            if (!running) return false
            gen = generator ?: return false
            out = output ?: return false
            elapsedMs = writtenSamples * 1000 / sampleRate
            timerElapsedMs = (writtenSamples - timerStartSamples) * 1000 / sampleRate
            timerNow = timer
            val fade = Fades.gain(elapsedMs, fadeInMs, null, 0)
            val timerGain = timerNow?.gain(timerElapsedMs) ?: 1f
            val level = volume * (if (ducked) DUCK else 1f)
            target = level * level * fade * timerGain
        }
        gen.render(buffer, 0, BLOCK)
        val start = lastGain
        for (i in 0 until BLOCK) buffer[i] *= start + (target - start) * (i + 1) / BLOCK
        lastGain = target
        out.write(buffer, BLOCK)
        synchronized(lock) { writtenSamples += BLOCK }
        blocks++
        val now = (writtenSamples * 1000 / sampleRate)
        val timerRemaining = timerNow?.remainingMs((writtenSamples - timerStartSamples) * 1000 / sampleRate)
        if (blocks % PROGRESS_EVERY == 0) listener?.onProgress(now, timerRemaining)
        if (timerNow != null && timerNow.isFinished((writtenSamples - timerStartSamples) * 1000 / sampleRate)) {
            synchronized(lock) { running = false }
            listener?.onProgress(now, 0)
            listener?.onTimerFinished()
            return false
        }
        return true
    }

    internal val playedMs: Long get() = synchronized(lock) { writtenSamples * 1000 / sampleRate }

    private companion object {
        const val BLOCK = 2048
        const val PROGRESS_EVERY = 6 // about every 0.5 s at 24 kHz
        const val DUCK = 0.3f
        const val JOIN_MS = 500L
    }
}
