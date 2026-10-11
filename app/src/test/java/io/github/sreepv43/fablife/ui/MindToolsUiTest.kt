package io.github.sreepv43.fablife.ui

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.JournalKind
import io.github.sreepv43.fablife.data.SessionKind
import io.github.sreepv43.fablife.data.TopTaskEntity
import io.github.sreepv43.fablife.engine.JournalPrompts
import io.github.sreepv43.fablife.engine.logicalDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/** The focus timer, journal and Top 3 screens, driven through the real app. */
class MindToolsUiTest : MindUiTestBase() {
    private val today get() = logicalDay(LocalDateTime.now(), 3)

    override suspend fun seed() {
        // Yesterday's list, for the carry-over offer.
        container.database.topTasks().upsert(TopTaskEntity(today.minusDays(1), 0, "Call the bank", done = false))
        container.database.topTasks().upsert(TopTaskEntity(today.minusDays(1), 1, "Water the plants", done = true))
    }

    private fun advanceClock(ms: Long) {
        app.clockMs += ms
        rule.mainClock.advanceTimeBy(300)
    }

    // Focus timer

    @Test
    fun focusBlocksCountDownLogTheirMinutesAndRollIntoABreak() {
        openMind()
        click("Focus timer")
        waitFor("Start focusing")
        click("Start focusing")
        waitFor("Focus · round 1")
        waitFor("25:00")

        advanceClock(10 * 60_000L)
        waitFor("15:00")

        click("Pause")
        advanceClock(8 * 60_000L)
        waitFor("15:00")
        click("Resume")
        advanceClock(60_000L)
        waitFor("14:00")

        advanceClock(15 * 60_000L)
        waitFor("Short break")
        waitFor("5:00")
        val logs = eventually(read = { container.database.sessionLogs().all() }) { it.isNotEmpty() }
        assertEquals(listOf(Triple(SessionKind.FOCUS, "pomodoro", 25)), logs.map { Triple(it.kind, it.ref, it.minutes) })

        click("Skip break")
        waitFor("Focus · round 2")
        waitFor("Ready when you are")
        click("Finish")
        waitFor("Good focus.")
        waitFor("You focused for 25 minutes", substring = true)
    }

    @Test
    fun theFocusSoundPlaysOnlyWhileFocusingAndStopsAtTheBreak() {
        openMind()
        click("Focus timer")
        click("Brown noise")
        click("Start focusing")
        rule.waitUntil(5_000) { fake.state.value.mode == PlaybackMode.SOUND }
        assertTrue(fake.calls.any { it.startsWith("playSound:brown") })

        click("Pause")
        rule.waitUntil(5_000) { fake.calls.contains("pause") }
        click("Resume")
        rule.waitUntil(5_000) { fake.calls.contains("resume") }

        advanceClock(26 * 60_000L)
        waitFor("Short break")
        rule.waitUntil(5_000) { fake.state.value.mode == PlaybackMode.IDLE }
    }

    @Test
    fun finishingAfterAFewMinutesLogsNothingAndReturnsToSetup() {
        openMind()
        click("Focus timer")
        click("Start focusing")
        waitFor("Focus · round 1")
        advanceClock(2 * 60_000L)
        click("Finish")
        waitFor("Start focusing")
        assertTrue(background { container.database.sessionLogs().all() }.isEmpty())
    }

    @Test
    fun finishingEarlyAfterEnoughMinutesLogsTheWholeMinutes() {
        openMind()
        click("Focus timer")
        click("Start focusing")
        advanceClock(12 * 60_000L + 40_000)
        click("Finish")
        waitFor("Good focus.")
        assertEquals(12, eventually(read = { container.database.sessionLogs().all() }) { it.isNotEmpty() }.single().minutes)
    }

    @Test
    fun theLengthsCanBeChangedAndAreRemembered() {
        openMind()
        click("Focus timer")
        waitFor("25 min")
        rule.onAllNodesWithContentDescription("Lengthen Focus").onFirst().performClick()
        waitFor("30 min")
        eventually(read = { container.prefs.current() }) { it.focusMinutes == 30 }
        click("Start focusing")
        waitFor("30:00")
    }

    // Journal

    @Test
    fun gratitudeIsSavedAsOneEntryAndListedInThePastEntries() {
        openMind()
        click("Journal")
        waitFor("Three small things you're grateful for")
        rule.onNodeWithText("I'm grateful for…").performTextInput("a warm cup of tea")
        rule.onAllNodesWithText("And…").onFirst().performTextInput("my sister's call")
        click("Save gratitude")
        waitFor("Saved ✓")
        reveal("• a warm cup of tea")
        reveal("• my sister's call")
        val entry = eventually(read = { container.database.journal().all() }) { it.isNotEmpty() }.single()
        assertEquals(JournalKind.GRATITUDE, entry.kind)
        assertEquals("a warm cup of tea\nmy sister's call", entry.text)
    }

    @Test
    fun theSaveButtonNeedsSomethingWritten() {
        openMind()
        click("Journal")
        waitFor("Save gratitude")
        rule.onNodeWithText("Save gratitude").assertIsNotEnabled()
    }

    @Test
    fun aReflectionKeepsThePromptWithTheAnswer() {
        openMind()
        click("Journal")
        click("Reflection")
        waitFor("Another prompt")
        rule.onNodeWithText("Your thoughts").performTextInput("A calm walk after lunch.")
        click("Save reflection")
        waitFor("Saved ✓")
        val entry = eventually(read = { container.database.journal().all() }) { it.isNotEmpty() }.single()
        assertEquals(JournalKind.REFLECTION, entry.kind)
        assertTrue(JournalPrompts.REFLECTION.any { entry.text.startsWith(it) })
        assertTrue(entry.text.endsWith("A calm walk after lunch."))
    }

    @Test
    fun aMoodNeedsAFaceAndMayHaveANote() {
        openMind()
        click("Journal")
        click("Mood")
        waitFor("How are you feeling right now?")
        rule.onNodeWithText("Save mood").assertIsNotEnabled()
        rule.onAllNodesWithContentDescription("Good").onFirst().performClick()
        rule.onNodeWithText("A note (optional)").performTextInput("Slept well")
        click("Save mood")
        waitFor("Saved ✓")
        val entry = eventually(read = { container.database.journal().all() }) { it.isNotEmpty() }.single()
        assertEquals(4, entry.mood)
        assertEquals("Slept well", entry.text)
        reveal("🙂 Good")
    }

    @Test
    fun anEntryCanBeDeletedAfterConfirming() {
        openMind()
        click("Journal")
        rule.onNodeWithText("I'm grateful for…").performTextInput("sunshine")
        click("Save gratitude")
        reveal("• sunshine")
        rule.onAllNodesWithContentDescription("Delete entry", substring = true).onFirst().performClick()
        waitFor("Delete this entry?")
        click("Keep it")
        assertEquals(1, background { container.database.journal().all() }.size)
        rule.onAllNodesWithContentDescription("Delete entry", substring = true).onFirst().performClick()
        click("Delete entry")
        eventually(read = { container.database.journal().all() }) { it.isEmpty() }
    }

    @Test
    fun journalOpenedFromAHabitOffersToCheckItInAfterSaving() {
        waitFor("Write gratitude")
        click("Write")
        waitFor("Three small things you're grateful for")
        rule.onNodeWithText("I'm grateful for…").performTextInput("quiet mornings")
        click("Save gratitude")
        waitFor("Mark “Write gratitude” as done")
        click("Mark “Write gratitude” as done")
        eventually(read = { container.database.checkIns().all() }) { rows -> rows.any { it.habitId == "write_test" && it.status == CheckInStatus.DONE } }
    }

    // Top 3

    private fun write(label: String, text: String) {
        rule.onNodeWithText(label).performScrollTo()
        rule.onNodeWithText(label).performTextInput(text)
        rule.onNodeWithText(label).performImeAction()
    }

    @Test
    fun threeTasksAreWrittenTickedOffAndCelebrated() {
        openMind()
        click("Top 3 for today")
        waitFor("Task 1")
        write("Task 1", "Send the invoice")
        write("Task 2", "Walk 20 minutes")
        write("Task 3", "Cook dinner")
        eventually(read = { container.database.topTasks().all() }) { rows -> rows.count { it.day == today } == 3 }

        for (n in 1..3) {
            rule.onAllNodesWithContentDescription("Done: task $n").onFirst().performScrollTo().performClick()
            eventually(read = { container.database.topTasks().all() }) { rows -> rows.count { r -> r.day == today && r.done } == n }
        }
        reveal("All three done", substring = true)
    }

    @Test
    fun anEmptySlotCannotBeTicked() {
        openMind()
        click("Top 3 for today")
        waitFor("Task 1")
        rule.onAllNodesWithContentDescription("Done: task 1").onFirst().assertIsNotEnabled()
    }

    @Test
    fun yesterdaysUnfinishedTaskIsOfferedAndCarriedOver() {
        openMind()
        click("Top 3 for today")
        waitFor("Unfinished from yesterday")
        waitFor("Call the bank")
        assertTrue("the finished one is not offered", rule.onAllNodesWithText("Water the plants").fetchSemanticsNodes().isEmpty())
        click("Carry over")
        eventually(read = { container.database.topTasks().all() }) { rows -> rows.any { it.day == today && it.text == "Call the bank" } }
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Unfinished from yesterday").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun planningFromTheHabitOffersToCheckItIn() {
        waitFor("Plan the day")
        click("Plan")
        waitFor("Task 1")
        write("Task 1", "Draft the report")
        reveal("Mark “Plan the day” as done")
        click("Mark “Plan the day” as done")
        eventually(read = { container.database.checkIns().all() }) { rows -> rows.any { it.habitId == "plan_test" && it.status == CheckInStatus.DONE } }
    }
}
