package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.BreathingPattern
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

enum class BreathPhase { INHALE, HOLD_IN, EXHALE, HOLD_OUT }

data class TimedPhase(val phase: BreathPhase, val startMs: Long, val durationMs: Long)

/**
 * What to show at one moment of a breathing session. [scale] is for the animated circle: 0 after a full breath out,
 * 1 after a full breath in, eased in between.
 */
data class PacerState(
    val phase: BreathPhase,
    val progress: Float,
    val secondsLeft: Int,
    val cycle: Int,
    val scale: Float,
    val finished: Boolean,
)

object BreathingPacer {
    private fun ms(seconds: Float) = (seconds * 1000f).roundToInt().toLong()

    /** The phases of one breath, skipping holds of zero length. */
    fun phases(p: BreathingPattern): List<TimedPhase> {
        val parts = listOf(
            BreathPhase.INHALE to ms(p.inhale), BreathPhase.HOLD_IN to ms(p.hold1),
            BreathPhase.EXHALE to ms(p.exhale), BreathPhase.HOLD_OUT to ms(p.hold2),
        ).filter { it.second > 0 }
        var start = 0L
        return parts.map { (phase, d) -> TimedPhase(phase, start, d).also { start += d } }
    }

    fun cycleMs(p: BreathingPattern): Long = phases(p).sumOf { it.durationMs }

    /** Whole breaths that fit [minutes], at least one. */
    fun roundsFor(p: BreathingPattern, minutes: Int): Int = max(1, (minutes * 60_000.0 / cycleMs(p)).roundToInt())

    fun totalMs(p: BreathingPattern, rounds: Int): Long = cycleMs(p) * rounds

    fun stateAt(p: BreathingPattern, elapsedMs: Long, rounds: Int): PacerState {
        val phases = phases(p)
        val cycleMs = phases.sumOf { it.durationMs }
        val total = cycleMs * rounds
        if (elapsedMs >= total) {
            val last = phases.last()
            return PacerState(last.phase, 1f, 0, rounds - 1, scaleFor(last.phase, 1f), finished = true)
        }
        val t = max(0L, elapsedMs)
        val cycle = (t / cycleMs).toInt()
        val within = t % cycleMs
        val current = phases.last { it.startMs <= within }
        val phaseElapsed = within - current.startMs
        val progress = phaseElapsed.toFloat() / current.durationMs
        val secondsLeft = ceil((current.durationMs - phaseElapsed) / 1000.0).toInt()
        return PacerState(current.phase, progress, secondsLeft, cycle, scaleFor(current.phase, progress), finished = false)
    }

    private fun scaleFor(phase: BreathPhase, progress: Float): Float = when (phase) {
        BreathPhase.INHALE -> ease(progress)
        BreathPhase.HOLD_IN -> 1f
        BreathPhase.EXHALE -> 1f - ease(progress)
        BreathPhase.HOLD_OUT -> 0f
    }

    private fun ease(x: Float): Float = x * x * (3 - 2 * x)
}
