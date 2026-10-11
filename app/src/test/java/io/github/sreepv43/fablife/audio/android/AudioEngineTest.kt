package io.github.sreepv43.fablife.audio.android

import io.github.sreepv43.fablife.audio.SleepTimerPlan
import io.github.sreepv43.fablife.audio.SoundId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AudioEngineTest {
    private class Recorder : SoundPlayer.Listener {
        val progress = mutableListOf<Pair<Long, Long?>>()
        var timerFinished = 0
        val errors = mutableListOf<String>()
        val errorLatch = CountDownLatch(1)

        override fun onProgress(elapsedMs: Long, timerRemainingMs: Long?) { progress += elapsedMs to timerRemainingMs }
        override fun onTimerFinished() { timerFinished++ }
        override fun onError(message: String) { errors += message; errorLatch.countDown() }
    }

    private val rate = 24_000
    private val blockMs = 2048 * 1000.0 / rate

    private fun engine(output: FakeOutput, threaded: Boolean = false) = AudioEngine({ output }, rate, threaded)

    private fun pumpFor(e: AudioEngine, ms: Long) {
        val end = e.playedMs + ms
        while (e.playedMs < end) assertTrue(e.pump())
    }

    @Test
    fun startsTheOutputAtTheSampleRateAndWritesWholeBlocks() {
        val out = FakeOutput()
        val e = engine(out)
        e.play(SoundId.PINK, volume = 1f, fadeInMs = 0, timer = null)
        assertTrue(out.started)
        assertEquals(rate, out.sampleRate)
        repeat(5) { assertTrue(e.pump()) }
        assertEquals(5, out.blocks.size)
        assertTrue(out.blocks.all { it.size == 2048 })
        assertEquals(5 * 2048L * 1000 / rate, e.playedMs)
        e.stop()
    }

    @Test
    fun fadesInInsteadOfStartingAtFullLevel() {
        val out = FakeOutput()
        val e = engine(out)
        e.play(SoundId.WHITE, volume = 1f, fadeInMs = 2_000, timer = null)
        pumpFor(e, 3_000)
        val first = out.blocks.first()
        val early = first.take(256).maxOf { kotlin.math.abs(it) }
        assertTrue("first samples are nearly silent ($early)", early < 0.02f)
        assertTrue("after the fade the sound is at level", out.peak(out.blocks.size - 1) > 0.15f)
        e.stop()
    }

    @Test
    fun volumeChangesGlideInsideTheNextBlockInsteadOfStepping() {
        val out = FakeOutput()
        val e = engine(out)
        e.play(SoundId.WHITE, volume = 1f, fadeInMs = 0, timer = null)
        pumpFor(e, 300)
        val before = out.peak(out.blocks.size - 1)
        e.setVolume(0.5f)
        assertTrue(e.pump())
        val glide = out.blocks.last()
        fun rmsOf(from: Int) = kotlin.math.sqrt(glide.slice(from until from + 128).sumOf { it.toDouble() * it } / 128)
        // Gain follows volume squared (1 -> 0.25) and ramps linearly across the block.
        val ratio = rmsOf(2048 - 128) / rmsOf(0)
        assertTrue("ratio $ratio", ratio in 0.15..0.45)
        pumpFor(e, 300)
        assertEquals(0.25f, out.peak(out.blocks.size - 1) / before, 0.08f)
        e.stop()
    }

    @Test
    fun duckingLowersTheLevelAndReleasingRestoresIt() {
        val out = FakeOutput()
        val e = engine(out)
        e.play(SoundId.WHITE, volume = 1f, fadeInMs = 0, timer = null)
        pumpFor(e, 300)
        val normal = out.peak(out.blocks.size - 1)
        e.setDucked(true)
        pumpFor(e, 300)
        assertTrue(out.peak(out.blocks.size - 1) < normal * 0.3f)
        e.setDucked(false)
        pumpFor(e, 300)
        assertTrue(out.peak(out.blocks.size - 1) > normal * 0.7f)
        e.stop()
    }

    @Test
    fun sleepTimerFadesOutThenStopsAndReportsOnce() {
        val out = FakeOutput()
        val listener = Recorder()
        val e = engine(out).also { it.listener = listener }
        e.play(SoundId.BROWN, volume = 1f, fadeInMs = 0, timer = SleepTimerPlan(1, fadeOutSeconds = 10))
        var blocks = 0
        while (e.pump()) blocks++
        assertEquals(1, listener.timerFinished)
        assertEquals(60_000.0, blocks * blockMs, 2 * blockMs)
        val last = out.peak(out.blocks.size - 1)
        val middle = out.peak(out.blocks.size / 2)
        assertTrue("last block is faded ($last vs $middle)", last < middle * 0.05f)
        assertEquals(0L, listener.progress.last().second)
        assertFalse("nothing more to play", e.pump())
        assertEquals("the timer is reported once", 1, listener.timerFinished)
    }

    @Test
    fun settingATimerLaterCountsFromThatMoment() {
        val out = FakeOutput()
        val listener = Recorder()
        val e = engine(out).also { it.listener = listener }
        e.play(SoundId.PINK, volume = 1f, fadeInMs = 0, timer = null)
        pumpFor(e, 20_000)
        e.setTimer(SleepTimerPlan(1, fadeOutSeconds = 5))
        pumpFor(e, 50_000)
        assertEquals(0, listener.timerFinished)
        assertEquals(10_000.0, listener.progress.last().second!!.toDouble(), 1_000.0)
        while (e.pump()) Unit
        assertEquals(1, listener.timerFinished)
        assertEquals(80_000.0, e.playedMs.toDouble(), 1_000.0)
    }

    @Test
    fun cancellingTheTimerKeepsPlaying() {
        val out = FakeOutput()
        val listener = Recorder()
        val e = engine(out).also { it.listener = listener }
        e.play(SoundId.PINK, volume = 1f, fadeInMs = 0, timer = SleepTimerPlan(1))
        pumpFor(e, 10_000)
        e.setTimer(null)
        pumpFor(e, 120_000)
        assertEquals(0, listener.timerFinished)
        assertNull(listener.progress.last().second)
        e.stop()
    }

    @Test
    fun reportsProgressRoughlyTwiceASecond() {
        val out = FakeOutput()
        val listener = Recorder()
        val e = engine(out).also { it.listener = listener }
        e.play(SoundId.PINK, volume = 0.5f, fadeInMs = 0, timer = null)
        pumpFor(e, 10_000)
        assertTrue("got ${listener.progress.size}", listener.progress.size in 17..22)
        assertTrue(listener.progress.zipWithNext().all { (a, b) -> b.first > a.first })
        e.stop()
    }

    @Test
    fun pauseAndResumePassThroughToTheOutput() {
        val out = FakeOutput()
        val e = engine(out)
        e.play(SoundId.PINK, volume = 0.5f, fadeInMs = 0, timer = null)
        e.pause()
        assertTrue(out.paused)
        e.resume()
        assertFalse(out.paused)
        e.stop()
        assertTrue(out.released)
    }

    @Test
    fun anOutputThatCannotStartReportsAnError() {
        val listener = Recorder()
        val out = FakeOutput(failStart = true)
        val e = engine(out).also { it.listener = listener }
        e.play(SoundId.PINK, volume = 0.5f, fadeInMs = 0, timer = null)
        assertEquals(listOf("no audio device"), listener.errors)
        assertTrue(out.released)
        assertFalse(e.pump())
    }

    @Test
    fun aThreadedEnginePlaysAndStopsCleanly() {
        val out = FakeOutput()
        val e = engine(out, threaded = true)
        e.play(SoundId.PINK, volume = 0.5f, fadeInMs = 0, timer = null)
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (out.blocks.size < 5 && System.nanoTime() < deadline) Thread.sleep(5)
        assertTrue("wrote ${out.blocks.size} blocks", out.blocks.size >= 5)
        e.stop()
        assertTrue(out.released)
        val written = out.blocks.size
        Thread.sleep(50)
        assertEquals("no writes after stop", written, out.blocks.size)
    }

    @Test
    fun aThreadedEngineWhoseDeviceFailsReportsTheError() {
        val listener = Recorder()
        val e = engine(FakeOutput(failWritesAfter = 3), threaded = true).also { it.listener = listener }
        e.play(SoundId.PINK, volume = 0.5f, fadeInMs = 0, timer = null)
        assertTrue(listener.errorLatch.await(5, TimeUnit.SECONDS))
        assertEquals(listOf("device lost"), listener.errors)
        e.stop()
    }
}
