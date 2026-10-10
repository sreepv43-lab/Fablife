package io.github.sreepv43.fablife.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// Per-sample state machines only (no block processing), so the output is identical however render() is chunked.
// Gains were measured at 24 kHz so every sound lands near 0.10 to 0.14 RMS. Noise through filters fixed in Hz changes
// level with the sample rate (its power spreads over more or fewer bins), which the sqrt(rate) factors compensate.

private const val DC_BLOCK_HZ = 20.0
private const val REFERENCE_RATE = 24_000.0

internal abstract class Sound(final override val sampleRate: Int) : SoundGenerator {
    private val dcBlock = HighPass(DC_BLOCK_HZ, sampleRate)

    /** The next sample, already at its target level. */
    protected abstract fun next(): Double

    final override fun render(out: FloatArray, offset: Int, count: Int) {
        require(offset >= 0 && count >= 0 && count <= out.size - offset) { "range out of bounds" }
        for (i in offset until offset + count) out[i] = softLimit(dcBlock.process(next())).toFloat()
    }
}

internal class WhiteNoise(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val rng = Rng(seed)

    override fun next() = rng.nextSigned() * GAIN

    private companion object {
        const val GAIN = 0.10 * 1.7320508075688772 // uniform noise has RMS 1/sqrt(3)
    }
}

internal class PinkNoise(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val noise = VossPink(seed)

    override fun next() = noise.next() * GAIN

    private companion object {
        const val GAIN = 0.135
    }
}

/** A leaky integrator of white noise; the base class high-pass keeps it centred. */
internal class BrownNoise(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val rng = Rng(seed)
    private val leak = exp(-2.0 * PI * LEAK_HZ / sampleRate)
    private val gain = GAIN * sqrt(REFERENCE_RATE / sampleRate)
    private var y = 0.0

    override fun next(): Double {
        y = y * leak + rng.nextSigned()
        return y * gain
    }

    private companion object {
        const val LEAK_HZ = 10.0
        const val GAIN = 0.025
    }
}

/** Filtered pink noise with sparse, quiet droplet pings (decaying sines) on top. */
internal class Rain(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val seeds = Rng(seed)
    private val bed = VossPink(seeds.nextLong())
    private val bedHigh = HighPass(BED_LOW_HZ, sampleRate)
    private val bedLow = LowPass(min(BED_HIGH_HZ, 0.3 * sampleRate), sampleRate)
    private val rng = Rng(seeds.nextLong())
    private val meanGap = sampleRate / DROPS_PER_SECOND
    private val maxPingHz = min(PING_HIGH_HZ, 0.4 * sampleRate)
    private val sinState = DoubleArray(VOICES)
    private val cosState = DoubleArray(VOICES)
    private val rotCos = DoubleArray(VOICES)
    private val rotSin = DoubleArray(VOICES)
    private val decay = DoubleArray(VOICES)
    private var nextVoice = 0
    private var untilDrop = gap()

    private fun gap(): Int = (-ln(1.0 - rng.nextUnit()) * meanGap).toInt() + 1

    private fun startDrop() {
        val v = nextVoice
        nextVoice = (nextVoice + 1) % VOICES
        val u = rng.nextUnit()
        val w = 2.0 * PI * PING_LOW_HZ * exp(ln(maxPingHz / PING_LOW_HZ) * rng.nextUnit()) / sampleRate
        sinState[v] = 0.0
        cosState[v] = PING_AMPLITUDE * (0.25 + 0.75 * u * u)
        rotCos[v] = cos(w)
        rotSin[v] = sin(w)
        decay[v] = exp(-1.0 / (rng.nextRange(PING_MIN_SECONDS, PING_MAX_SECONDS) * sampleRate))
    }

    override fun next(): Double {
        if (--untilDrop <= 0) {
            startDrop()
            untilDrop = gap()
        }
        var drops = 0.0
        for (v in 0 until VOICES) {
            val s = sinState[v]
            val c = cosState[v]
            if (s == 0.0 && c == 0.0) continue
            val ns = decay[v] * (s * rotCos[v] + c * rotSin[v])
            val nc = decay[v] * (c * rotCos[v] - s * rotSin[v])
            val dead = ns * ns + nc * nc < 1e-10
            sinState[v] = if (dead) 0.0 else ns
            cosState[v] = if (dead) 0.0 else nc
            drops += ns
        }
        return bedLow.process(bedHigh.process(bed.next())) * BED_GAIN + drops
    }

    private companion object {
        const val VOICES = 8
        const val DROPS_PER_SECOND = 30.0
        const val BED_LOW_HZ = 450.0
        const val BED_HIGH_HZ = 7000.0
        const val BED_GAIN = 0.22
        const val PING_LOW_HZ = 1200.0
        const val PING_HIGH_HZ = 4800.0
        const val PING_MIN_SECONDS = 0.004
        const val PING_MAX_SECONDS = 0.014
        const val PING_AMPLITUDE = 0.13
    }
}

/**
 * Low, filtered noise whose level swells with an irregular period of 8 to 12 seconds (each swell gets its own length,
 * height and lean), with a brighter wash on the crest.
 */
internal class Ocean(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val seeds = Rng(seed)
    private val body = Rng(seeds.nextLong())
    private val wash = Rng(seeds.nextLong())
    private val swell = Rng(seeds.nextLong())
    private val bodyLow1 = LowPass(BODY_HZ, sampleRate)
    private val bodyLow2 = LowPass(BODY_HZ, sampleRate)
    private val washHigh = HighPass(WASH_LOW_HZ, sampleRate)
    private val washLow = LowPass(WASH_HIGH_HZ, sampleRate)
    private val bodyGain = BODY_GAIN * sqrt(sampleRate / REFERENCE_RATE)
    private val washGain = WASH_GAIN * sqrt(sampleRate / REFERENCE_RATE)
    private var phase = swell.nextUnit()
    private var increment = 0.0
    private var peak = 1.0
    private var lean = 0.0

    init {
        newSwell()
    }

    private fun newSwell() {
        increment = 1.0 / (swell.nextRange(MIN_PERIOD_SECONDS, MAX_PERIOD_SECONDS) * sampleRate)
        peak = swell.nextRange(0.7, 1.0)
        lean = swell.nextRange(-0.6, 0.6)
    }

    override fun next(): Double {
        phase += increment
        if (phase >= 1.0) {
            phase -= 1.0
            newSwell()
        }
        // Warp the phase so swells rise faster or slower, then take a raised cosine: 0 at both ends of the cycle.
        val warped = phase - lean * SineTable.at(phase) / (2.0 * PI)
        val rise = 0.5 - 0.5 * SineTable.at(warped + 0.25 - if (warped + 0.25 >= 1.0) 1.0 else 0.0)
        val crest = rise * sqrt(rise)
        val level = FLOOR + (1.0 - FLOOR) * peak * crest
        val low = bodyLow2.process(bodyLow1.process(body.nextSigned()))
        val foam = washLow.process(washHigh.process(wash.nextSigned()))
        return level * low * bodyGain + crest * crest * foam * washGain
    }

    private companion object {
        const val MIN_PERIOD_SECONDS = 8.0
        const val MAX_PERIOD_SECONDS = 12.0
        const val FLOOR = 0.3
        const val BODY_HZ = 500.0
        const val WASH_LOW_HZ = 1200.0
        const val WASH_HIGH_HZ = 5000.0
        const val BODY_GAIN = 1.84
        const val WASH_GAIN = 0.28
    }
}

/** Low band-limited noise with a steady 100 Hz hum and a fainter one an octave above. */
internal class Fan(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private val seeds = Rng(seed)
    private val noise = Rng(seeds.nextLong())
    private val low1 = LowPass(NOISE_HZ, sampleRate)
    private val low2 = LowPass(NOISE_HZ, sampleRate)
    private val noiseGain = NOISE_GAIN * sqrt(sampleRate / REFERENCE_RATE)
    private val hum = Oscillator(seeds.nextUnit())
    private val harmonic = Oscillator(seeds.nextUnit())
    private val humStep = HUM_HZ / sampleRate

    override fun next(): Double =
        low2.process(low1.process(noise.nextSigned())) * noiseGain +
            hum.next(humStep) * HUM_AMPLITUDE + harmonic.next(2 * humStep) * HARMONIC_AMPLITUDE

    private companion object {
        const val NOISE_HZ = 600.0
        const val NOISE_GAIN = 0.93
        const val HUM_HZ = 100.0
        const val HUM_AMPLITUDE = 0.08
        const val HARMONIC_AMPLITUDE = 0.03
    }
}

/**
 * A drone of five sine partials around A2 (no melody): the fundamental is doubled by a slightly sharp copy that beats
 * slowly, each partial's pitch wanders by a few cents and its level drifts, all on periods of tens of seconds.
 */
internal class FocusPad(sampleRate: Int, seed: Long) : Sound(sampleRate) {
    private class Partial(val hz: Double, val amplitude: Double, val driftHz: Double, val driftDepth: Double, val swellHz: Double)

    private val rng = Rng(seed)
    private val oscillators = Array(PARTIALS.size) { Oscillator(rng.nextUnit()) }
    private val driftPhase = DoubleArray(PARTIALS.size) { rng.nextUnit() }
    private val swellPhase = DoubleArray(PARTIALS.size) { rng.nextUnit() }
    private val increment = DoubleArray(PARTIALS.size)
    private val amplitude = DoubleArray(PARTIALS.size)
    private var tick = 0

    private fun updateControls() {
        val seconds = CONTROL_INTERVAL.toDouble() / sampleRate
        for (k in PARTIALS.indices) {
            val p = PARTIALS[k]
            driftPhase[k] = (driftPhase[k] + p.driftHz * seconds) % 1.0
            swellPhase[k] = (swellPhase[k] + p.swellHz * seconds) % 1.0
            increment[k] = p.hz * (1.0 + p.driftDepth * SineTable.at(driftPhase[k])) / sampleRate
            amplitude[k] = GAIN * p.amplitude * (1.0 + SWELL_DEPTH * SineTable.at(swellPhase[k]))
        }
    }

    override fun next(): Double {
        if (tick == 0) updateControls()
        tick = (tick + 1) and (CONTROL_INTERVAL - 1)
        var sum = 0.0
        for (k in oscillators.indices) sum += oscillators[k].next(increment[k]) * amplitude[k]
        return sum
    }

    private companion object {
        const val CONTROL_INTERVAL = 64 // samples between updates of the slow drifts (a power of two)
        const val SWELL_DEPTH = 0.25
        const val GAIN = 0.11
        val PARTIALS = arrayOf(
            Partial(110.0, 1.00, 0.037, 0.0020, 0.021),
            Partial(110.4, 0.75, 0.051, 0.0018, 0.033),
            Partial(164.8, 0.40, 0.043, 0.0022, 0.027),
            Partial(220.2, 0.55, 0.029, 0.0016, 0.045),
            Partial(329.6, 0.16, 0.061, 0.0025, 0.038),
        )
    }
}
