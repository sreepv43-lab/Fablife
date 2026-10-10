package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.ContentParser
import io.github.sreepv43.fablife.content.GuidedSession
import io.github.sreepv43.fablife.content.GuidedStep
import io.github.sreepv43.fablife.content.SessionCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SessionTimelineTest {
    private fun session(vararg steps: GuidedStep) = GuidedSession("t", "T", 1, SessionCategory.CALM, "d", "b", null, steps.toList())

    @Test
    fun estimatesSpeechAtTwoPointTwoWordsASecondPlusThePause() {
        val step = GuidedStep("one two three four five six seven eight nine ten eleven", 4f)
        assertEquals(5_000L + 4_000L, SessionTimeline.estimateMs(step))
        assertEquals(2_000L, SessionTimeline.estimateMs(GuidedStep(null, 2f)))
        assertEquals(0L, SessionTimeline.speechMs(""))
    }

    @Test
    fun cumulativeStartsFractionsAndLookupAgree() {
        val tl = SessionTimeline(session(GuidedStep(null, 2f), GuidedStep(null, 3f), GuidedStep(null, 5f)))
        assertEquals(10_000L, tl.totalMs)
        assertEquals(3, tl.stepCount)
        assertEquals(0L, tl.startOfStepMs(0))
        assertEquals(2_000L, tl.startOfStepMs(1))
        assertEquals(5_000L, tl.startOfStepMs(2))
        assertEquals(10_000L, tl.startOfStepMs(3))
        assertEquals(0.5f, tl.fractionAtStart(2), 1e-6f)
        assertEquals(0, tl.stepAt(0))
        assertEquals(0, tl.stepAt(1_999))
        assertEquals(1, tl.stepAt(2_000))
        assertEquals(2, tl.stepAt(9_999))
        assertEquals(2, tl.stepAt(50_000))
    }

    @Test
    fun anEmptySessionIsHandled() {
        val tl = SessionTimeline(session())
        assertEquals(0L, tl.totalMs)
        assertEquals(0f, tl.fractionAtStart(0), 0f)
        assertEquals(0, tl.stepAt(1_000))
    }

    @Test
    fun captionsAlwaysGetAtLeastAReadableMoment() {
        assertEquals(1_500L, SessionTimeline.readingMs("Hi"))
        assertTrue(SessionTimeline.readingMs("word ".repeat(22).trim()) == 10_000L)
    }

    @Test
    fun everyShippedSessionLastsAboutItsDeclaredLength() {
        val sessions = ContentParser.audio(File("src/main/assets/audio.json").readText()).sessions
        for (s in sessions) {
            val minutes = SessionTimeline(s).totalMs / 60_000.0
            assertTrue("${s.id} is ${"%.1f".format(minutes)} min, declared ${s.minutes}", minutes in 0.8 * s.minutes..1.2 * s.minutes)
        }
    }
}
