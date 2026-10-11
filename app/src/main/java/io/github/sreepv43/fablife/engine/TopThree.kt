package io.github.sreepv43.fablife.engine

import io.github.sreepv43.fablife.data.TopTaskEntity
import java.time.LocalDate

object TopThree {
    const val SLOTS = 3

    /** The day's tasks by slot; empty slots are null. */
    fun slots(tasks: List<TopTaskEntity>, day: LocalDate): List<TopTaskEntity?> =
        List(SLOTS) { slot -> tasks.firstOrNull { it.day == day && it.slot == slot } }

    /**
     * Yesterday's unfinished tasks that can be offered for today: not done, not already on today's list, and no more
     * than there are free slots.
     */
    fun carryOver(tasks: List<TopTaskEntity>, today: LocalDate): List<String> {
        val current = slots(tasks, today)
        val free = current.count { it == null }
        if (free == 0) return emptyList()
        val have = current.filterNotNull().map { it.text.trim().lowercase() }.toSet()
        return slots(tasks, today.minusDays(1)).filterNotNull()
            .filter { !it.done && it.text.isNotBlank() && it.text.trim().lowercase() !in have }
            .map { it.text.trim() }
            .take(free)
    }

    /** Where a carried-over task goes: the lowest free slot, or null if all three are taken. */
    fun firstFreeSlot(tasks: List<TopTaskEntity>, today: LocalDate): Int? = slots(tasks, today).indexOfFirst { it == null }.takeIf { it >= 0 }

    fun allDone(tasks: List<TopTaskEntity>, today: LocalDate): Boolean {
        val s = slots(tasks, today)
        return s.all { it != null } && s.all { it!!.done }
    }
}
