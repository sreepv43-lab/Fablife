package io.github.sreepv43.fablife.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class JournalPromptsTest {
    private val day = LocalDate.of(2026, 10, 10)

    @Test
    fun thePromptIsStableWithinADayAndChangesWithTheDay() {
        assertEquals(JournalPrompts.forDay(day), JournalPrompts.forDay(day))
        assertNotEquals(JournalPrompts.forDay(day), JournalPrompts.forDay(day.plusDays(1)))
    }

    @Test
    fun askingForAnotherStepsThroughEveryPromptAndWraps() {
        val seen = (0 until JournalPrompts.REFLECTION.size).map { JournalPrompts.forDay(day, it) }.toSet()
        assertEquals(JournalPrompts.REFLECTION.toSet(), seen)
        assertEquals(JournalPrompts.forDay(day), JournalPrompts.forDay(day, JournalPrompts.REFLECTION.size))
        assertTrue(JournalPrompts.forDay(day, -3) in JournalPrompts.REFLECTION)
    }

    @Test
    fun noPromptAsksForDiagnosisOrDwellsOnTheNegative() {
        val banned = listOf("diagnos", "disorder", "depress", "anxiety", "worst", "fail", "regret")
        JournalPrompts.REFLECTION.forEach { p -> banned.forEach { assertTrue("$p contains $it", !p.lowercase().contains(it)) } }
        assertTrue(JournalPrompts.REFLECTION.all { it.endsWith("?") })
    }

    @Test
    fun gratitudeLinesAreTrimmedAndBlanksDropped() {
        assertEquals("tea\nmy sister", JournalPrompts.joinLines(listOf("  tea ", "", "my sister  ")))
        assertEquals(listOf("tea", "my sister"), JournalPrompts.splitLines("tea\n\n my sister \n"))
        assertEquals("", JournalPrompts.joinLines(listOf(" ", "")))
    }

    @Test
    fun moodFacesMatchTheScale() {
        assertEquals(5, Mood.FACES.size)
        assertEquals(5, Mood.LABELS.size)
        assertEquals("😞", Mood.face(1))
        assertEquals("Great", Mood.label(5))
        assertEquals("", Mood.face(null))
        assertEquals("", Mood.label(9))
    }
}
