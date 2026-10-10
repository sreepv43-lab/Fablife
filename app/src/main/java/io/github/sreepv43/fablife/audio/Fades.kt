package io.github.sreepv43.fablife.audio

import kotlin.math.max
import kotlin.math.min

object Fades {
    /** Smooth 0..1 envelope: fades in over [fadeInMs] from the start and, when [totalMs] is given, out over its last [fadeOutMs]. */
    fun gain(elapsedMs: Long, fadeInMs: Long, totalMs: Long?, fadeOutMs: Long): Float {
        if (elapsedMs < 0) return 0f
        var g = if (fadeInMs <= 0) 1.0 else smooth(elapsedMs.toDouble() / fadeInMs)
        if (totalMs != null) {
            if (elapsedMs >= totalMs) return 0f
            g *= if (fadeOutMs <= 0) 1.0 else smooth((totalMs - elapsedMs).toDouble() / fadeOutMs)
        }
        return g.toFloat()
    }

    private fun smooth(x: Double): Double {
        val t = min(1.0, max(0.0, x))
        return t * t * (3 - 2 * t)
    }
}

/** A sleep timer that fades the sound out over the last [fadeOutSeconds] and then stops it. */
class SleepTimerPlan(val minutes: Int, val fadeOutSeconds: Int = 30) {
    init {
        require(minutes > 0) { "minutes must be positive" }
    }

    val totalMs: Long = minutes * 60_000L
    private val fadeOutMs: Long = min(fadeOutSeconds * 1000L, totalMs)

    fun remainingMs(elapsedMs: Long): Long = max(0L, totalMs - elapsedMs)

    fun gain(elapsedMs: Long): Float = Fades.gain(elapsedMs, fadeInMs = 0, totalMs = totalMs, fadeOutMs = fadeOutMs)

    fun isFinished(elapsedMs: Long): Boolean = elapsedMs >= totalMs
}
