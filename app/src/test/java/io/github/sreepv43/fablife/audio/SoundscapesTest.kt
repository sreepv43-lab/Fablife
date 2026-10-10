package io.github.sreepv43.fablife.audio

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.log10

class SoundscapesTest {
    @Test
    fun keysMatchTheContentCatalog() {
        assertEquals(listOf("white", "pink", "brown", "rain", "ocean", "fan", "focus_pad"), SoundId.entries.map { it.key })
        assertEquals(SoundId.FOCUS_PAD, SoundId.fromKey("focus_pad"))
        assertEquals(null, SoundId.fromKey("nope"))
        assertEquals(null, SoundId.fromKey(null))
    }

    @Test
    fun sameSeedGivesIdenticalAudioAndOtherSeedsDiffer() {
        for (id in SoundId.entries) {
            val a = render(id, 2, seed = 7)
            val b = render(id, 2, seed = 7)
            val c = render(id, 2, seed = 8)
            assertArrayEquals("$id deterministic", a, b, 0f)
            assertFalse("$id seed matters", a.contentEquals(c))
        }
    }

    @Test
    fun theSplitOfRenderCallsNeverChangesTheOutput() {
        for (id in SoundId.entries) {
            val whole = render(id, 3)
            for (chunk in intArrayOf(1, 7, 333, 4096)) {
                val g = Soundscapes.create(id)
                val pieces = FloatArray(whole.size)
                var at = 0
                while (at < pieces.size) {
                    val n = minOf(chunk, pieces.size - at)
                    g.render(pieces, at, n)
                    at += n
                }
                assertArrayEquals("$id chunk $chunk", whole, pieces, 0f)
            }
        }
    }

    @Test
    fun everySoundIsFiniteUnclippedCentredAndSimilarlyLoud() {
        for (id in SoundId.entries) for (rate in intArrayOf(16_000, 24_000, 44_100)) {
            val x = render(id, 20, rate)
            assertTrue("$id@$rate has a bad sample", x.all { it.isFinite() })
            assertTrue("$id@$rate clips (peak ${x.maxOf { abs(it) }})", x.all { abs(it) <= 0.98f })
            assertTrue("$id@$rate off-centre (${mean(x)})", abs(mean(x)) < 0.01)
            val level = rms(x)
            assertTrue("$id@$rate rms $level outside 0.08..0.16", level in 0.08..0.16)
        }
    }

    @Test
    fun theLevelsOfAllSoundsAreWithinAboutThreeDecibels() {
        val levels = SoundId.entries.map { rms(render(it, 20)) }
        val spread = 20 * log10(levels.max() / levels.min())
        assertTrue("spread $spread dB", spread < 3.0)
    }

    @Test
    fun noiseColoursFallOffAtTheRightRate() {
        val white = Spectrum(render(SoundId.WHITE, 60), 24_000).slope(200.0, 8000.0)
        val pink = Spectrum(render(SoundId.PINK, 60), 24_000).slope(200.0, 8000.0)
        val brown = Spectrum(render(SoundId.BROWN, 60), 24_000).slope(200.0, 8000.0)
        assertTrue("white slope $white", abs(white) < 0.25)
        assertTrue("pink slope $pink", pink in -1.3..-0.7)
        assertTrue("brown slope $brown", brown in -2.5..-1.6)
        assertTrue(white > pink && pink > brown)
    }

    @Test
    fun rainIsBrighterThanBrownNoiseAndOceanIsDarkerThanRain() {
        fun highShare(id: SoundId): Double {
            val s = Spectrum(render(id, 60), 24_000)
            return s.band(1000.0, 11000.0) / s.band(20.0, 11000.0)
        }
        val rain = highShare(SoundId.RAIN)
        val ocean = highShare(SoundId.OCEAN)
        val brown = highShare(SoundId.BROWN)
        assertTrue("rain $rain vs brown $brown", rain > 5 * brown)
        assertTrue("rain $rain vs ocean $ocean", rain > ocean)
    }

    @Test
    fun theFanHumsNearOneHundredHertzAndThePadIsTonal() {
        val fan = Spectrum(render(SoundId.FAN, 60), 24_000)
        val pad = Spectrum(render(SoundId.FOCUS_PAD, 60), 24_000)
        assertTrue("fan hum ${fan.peakRatio(100.0)}", fan.peakRatio(100.0) > 3.0)
        assertTrue("pad tone ${pad.peakRatio(110.0)}", pad.peakRatio(110.0) > 30.0)
        assertTrue("pad is mostly low (${pad.band(20.0, 700.0) / pad.band(20.0, 11000.0)})", pad.band(20.0, 700.0) / pad.band(20.0, 11000.0) > 0.95)
    }

    @Test
    fun theOceanSwellsAndRecedesEveryEightToTwelveSeconds() {
        val rate = 24_000
        val x = render(SoundId.OCEAN, 120, rate)
        val window = rate / 2
        val levels = DoubleArray(x.size / window) { w -> rms(x.copyOfRange(w * window, (w + 1) * window)) }
        val smooth = DoubleArray(levels.size) { i -> (maxOf(0, i - 1)..minOf(levels.size - 1, i + 1)).map { levels[it] }.average() }
        // Crests: local maxima at least 40% above the quietest point since the previous crest.
        val crests = ArrayList<Int>()
        var troughSinceCrest = smooth[0]
        for (i in 1 until smooth.size - 1) {
            troughSinceCrest = minOf(troughSinceCrest, smooth[i])
            if (smooth[i] >= smooth[i - 1] && smooth[i] > smooth[i + 1] && smooth[i] > 1.4 * troughSinceCrest) {
                crests += i
                troughSinceCrest = smooth[i]
            }
        }
        assertTrue("found ${crests.size} crests", crests.size >= 8)
        val meanGapSeconds = crests.zipWithNext { a, b -> (b - a) * 0.5 }.average()
        assertTrue("mean swell period $meanGapSeconds s", meanGapSeconds in 7.0..13.0)
        assertTrue("swell depth ${smooth.max() / smooth.min()}", smooth.max() / smooth.min() > 1.5)
    }

    @Test
    fun badArgumentsAreRejected() {
        val bad = runCatching { Soundscapes.create(SoundId.WHITE, sampleRate = 100) }
        assertTrue(bad.exceptionOrNull() is IllegalArgumentException)
        val g = Soundscapes.create(SoundId.WHITE)
        assertTrue(runCatching { g.render(FloatArray(10), 5, 10) }.exceptionOrNull() is IllegalArgumentException)
        assertNotEquals(0, Soundscapes.DEFAULT_SAMPLE_RATE)
    }
}
