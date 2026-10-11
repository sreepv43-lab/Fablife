package io.github.sreepv43.fablife.engine

import io.github.sreepv43.fablife.data.TopTaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TopThreeTest {
    private val today = LocalDate.of(2026, 10, 10)
    private val yesterday = today.minusDays(1)
    private fun task(day: LocalDate, slot: Int, text: String, done: Boolean = false) = TopTaskEntity(day, slot, text, done)

    @Test
    fun slotsAreFilledByDayAndPosition() {
        val tasks = listOf(task(today, 1, "B"), task(yesterday, 0, "old"), task(today, 0, "A"))
        val s = TopThree.slots(tasks, today)
        assertEquals(listOf("A", "B", null), s.map { it?.text })
    }

    @Test
    fun onlyYesterdaysUnfinishedTasksAreOffered() {
        val tasks = listOf(task(yesterday, 0, "Done one", done = true), task(yesterday, 1, "Call the bank"), task(yesterday, 2, "Book dentist"))
        assertEquals(listOf("Call the bank", "Book dentist"), TopThree.carryOver(tasks, today))
    }

    @Test
    fun theOfferNeverExceedsTheFreeSlotsOrRepeatsATaskAlreadyListed() {
        val tasks = listOf(
            task(yesterday, 0, "Call the bank"), task(yesterday, 1, "Book dentist"), task(yesterday, 2, "Write report"),
            task(today, 0, "call the bank "), task(today, 1, "Something new"),
        )
        assertEquals(listOf("Book dentist"), TopThree.carryOver(tasks, today))
    }

    @Test
    fun nothingIsOfferedWhenTheListIsFull() {
        val tasks = listOf(task(yesterday, 0, "x"), task(today, 0, "a"), task(today, 1, "b"), task(today, 2, "c"))
        assertTrue(TopThree.carryOver(tasks, today).isEmpty())
    }

    @Test
    fun olderDaysAreNotCarriedOver() {
        assertTrue(TopThree.carryOver(listOf(task(today.minusDays(2), 0, "ancient")), today).isEmpty())
    }

    @Test
    fun theFirstFreeSlotSkipsTakenOnes() {
        assertEquals(0, TopThree.firstFreeSlot(emptyList(), today))
        assertEquals(1, TopThree.firstFreeSlot(listOf(task(today, 0, "a")), today))
        assertEquals(0, TopThree.firstFreeSlot(listOf(task(today, 1, "b"), task(today, 2, "c")), today))
        assertNull(TopThree.firstFreeSlot(listOf(task(today, 0, "a"), task(today, 1, "b"), task(today, 2, "c")), today))
    }

    @Test
    fun allDoneNeedsThreeTasksAllTickedOff() {
        val two = listOf(task(today, 0, "a", true), task(today, 1, "b", true))
        assertFalse(TopThree.allDone(two, today))
        assertTrue(TopThree.allDone(two + task(today, 2, "c", true), today))
        assertFalse(TopThree.allDone(two + task(today, 2, "c", false), today))
    }
}
