package io.github.sreepv43.fablife.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

private val GOLDEN = 0x9E3779B97F4A7C15uL.toLong()
private val MIX1 = 0xBF58476D1CE4E5B9uL.toLong()
private val MIX2 = 0x94D049BB133111EBuL.toLong()
private const val INV_2_23 = 1.0 / 8388608.0
private const val INV_2_53 = 1.0 / 9007199254740992.0

/** SplitMix64: tiny, allocation free, and the same sequence on every JVM and ART. */
internal class Rng(seed: Long) {
    private var state = seed

    fun nextLong(): Long {
        state += GOLDEN
        var z = state
        z = (z xor (z ushr 30)) * MIX1
        z = (z xor (z ushr 27)) * MIX2
        return z xor (z ushr 31)
    }

    /** Uniform integer in [-2^23, 2^23). */
    fun next24(): Int = (nextLong() ushr 40).toInt() - 0x800000

    /** Uniform in [-1, 1). */
    fun nextSigned(): Double = next24() * INV_2_23

    /** Uniform in [0, 1). */
    fun nextUnit(): Double = (nextLong() ushr 11) * INV_2_53

    fun nextRange(lo: Double, hi: Double): Double = lo + (hi - lo) * nextUnit()
}

internal object SineTable {
    private const val SIZE = 4096
    private val table = FloatArray(SIZE + 2) { sin(2.0 * PI * it / SIZE).toFloat() }

    /** sin(2 pi phase) for phase in [0, 1], linearly interpolated (error below 3e-7). */
    fun at(phase: Double): Double {
        val x = phase * SIZE
        val i = x.toInt()
        val a = table[i]
        return a + (table[i + 1] - a) * (x - i)
    }
}

/** Sine oscillator with a caller-supplied increment (cycles per sample), so the pitch can drift. */
internal class Oscillator(private var phase: Double) {
    fun next(increment: Double): Double {
        val s = SineTable.at(phase)
        phase += increment
        if (phase >= 1.0) phase -= 1.0
        return s
    }
}

internal class LowPass(cutoffHz: Double, sampleRate: Int) {
    private val a = 1.0 - exp(-2.0 * PI * cutoffHz / sampleRate)
    private var y = 0.0

    fun process(x: Double): Double {
        y += a * (x - y)
        return y
    }
}

internal class HighPass(cutoffHz: Double, sampleRate: Int) {
    private val lp = LowPass(cutoffHz, sampleRate)

    fun process(x: Double): Double = x - lp.process(x)
}

/**
 * Voss-McCartney pink noise with unit variance: [ROWS] sample-and-hold rows, row k refreshed every 2^(k+1)
 * samples, plus one white row. The running sum is an exact integer, so it can't drift.
 */
internal class VossPink(seed: Long) {
    private val rng = Rng(seed)
    private val rows = IntArray(ROWS)
    private var sum = 0
    private var counter = 0

    init {
        for (i in rows.indices) {
            rows[i] = rng.next24()
            sum += rows[i]
        }
    }

    fun next(): Double {
        counter++
        val k = counter.countTrailingZeroBits()
        if (k < ROWS) {
            val v = rng.next24()
            sum += v - rows[k]
            rows[k] = v
        }
        return (sum + rng.next24()) * SCALE
    }

    private companion object {
        const val ROWS = 12

        // Each row is uniform with variance (2^23)^2 / 3, and there are ROWS + 1 independent ones.
        val SCALE = INV_2_23 / sqrt((ROWS + 1) / 3.0)
    }
}

/** Transparent below the knee, then bends smoothly towards (never reaching) +-1. */
internal fun softLimit(x: Double): Double {
    val a = abs(x)
    if (a <= KNEE) return x
    val over = (a - KNEE) / (1.0 - KNEE)
    val y = KNEE + (1.0 - KNEE) * over / (1.0 + over)
    return if (x < 0) -y else y
}

private const val KNEE = 0.6
