package io.github.sreepv43.fablife.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/** Welch power spectrum plus the few band measures the DSP tests need. Bin i sits at i * rate / size Hz. */
internal class Spectrum(x: FloatArray, val rate: Int, val size: Int = 4096) {
    val power = DoubleArray(size / 2 + 1)

    init {
        require(size and (size - 1) == 0 && x.size >= size)
        val window = DoubleArray(size) { 0.5 - 0.5 * cos(2 * PI * it / size) }
        val re = DoubleArray(size)
        val im = DoubleArray(size)
        var start = 0
        var segments = 0
        while (start + size <= x.size) {
            for (i in 0 until size) {
                re[i] = x[start + i] * window[i]
                im[i] = 0.0
            }
            fft(re, im)
            for (k in power.indices) power[k] += re[k] * re[k] + im[k] * im[k]
            start += size / 2
            segments++
        }
        for (k in power.indices) power[k] /= segments
    }

    private fun bin(hz: Double) = (hz * size / rate).toInt().coerceIn(0, power.size - 1)

    /** Total power from [lo] up to [hi] Hz. */
    fun band(lo: Double, hi: Double): Double = (bin(lo) until bin(hi)).sumOf { power[it] }

    /** Mean power per bin from [lo] up to [hi] Hz. */
    fun density(lo: Double, hi: Double): Double = band(lo, hi) / max(1, bin(hi) - bin(lo))

    /** Fitted slope of log10 density against log10 frequency over octave bands from [lo] to [hi]. 0 is white, -1 pink, -2 brown. */
    fun slope(lo: Double, hi: Double): Double {
        val xs = ArrayList<Double>()
        val ys = ArrayList<Double>()
        var f = lo
        while (f * 2 <= hi * 1.001) {
            xs += log10(f * 1.4142)
            ys += log10(density(f, f * 2))
            f *= 2
        }
        val mx = xs.average()
        val my = ys.average()
        return xs.indices.sumOf { (xs[it] - mx) * (ys[it] - my) } / xs.sumOf { (it - mx) * (it - mx) }
    }

    /** Power in the bins around [hz] relative to the mean density a little above and below it. */
    fun peakRatio(hz: Double): Double {
        val peak = ((bin(hz) - 2)..(bin(hz) + 2)).sumOf { power[it] }
        return peak / (5 * (density(hz * 0.5, hz * 0.8) + density(hz * 1.25, hz * 2.0)) / 2)
    }

    private companion object {
        fun fft(re: DoubleArray, im: DoubleArray) {
            val n = re.size
            var j = 0
            for (i in 1 until n) {
                var bit = n shr 1
                while (j and bit != 0) {
                    j = j xor bit
                    bit = bit shr 1
                }
                j = j xor bit
                if (i < j) {
                    re[i] = re[j].also { re[j] = re[i] }
                    im[i] = im[j].also { im[j] = im[i] }
                }
            }
            var len = 2
            while (len <= n) {
                val angle = -2 * PI / len
                for (start in 0 until n step len) {
                    for (k in 0 until len / 2) {
                        val wr = cos(angle * k)
                        val wi = sin(angle * k)
                        val a = start + k
                        val b = a + len / 2
                        val tr = re[b] * wr - im[b] * wi
                        val ti = re[b] * wi + im[b] * wr
                        re[b] = re[a] - tr
                        im[b] = im[a] - ti
                        re[a] += tr
                        im[a] += ti
                    }
                }
                len = len shl 1
            }
        }
    }
}

internal fun rms(x: FloatArray): Double = sqrt(x.sumOf { it.toDouble() * it } / x.size)

internal fun mean(x: FloatArray): Double = x.sumOf { it.toDouble() } / x.size

internal fun render(id: SoundId, seconds: Int, rate: Int = Soundscapes.DEFAULT_SAMPLE_RATE, seed: Long = 1L): FloatArray =
    FloatArray(seconds * rate).also { Soundscapes.create(id, rate, seed).render(it) }
