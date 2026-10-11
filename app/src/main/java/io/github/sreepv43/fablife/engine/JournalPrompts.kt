package io.github.sreepv43.fablife.engine

import java.time.LocalDate

object JournalPrompts {
    val REFLECTION = listOf(
        "What went well today, even a little?",
        "What is one small thing you did for yourself today?",
        "What gave you energy today, and what took it away?",
        "What are you proud of this week?",
        "What would make tomorrow feel a little easier?",
        "Which habit felt natural today, and which felt hard? What might help?",
        "Who or what made you smile recently?",
        "What did you learn about yourself lately?",
        "What is one thing you can let go of tonight?",
        "When did you feel most like yourself today?",
        "What is one small step you could take tomorrow?",
        "What are you looking forward to?",
        "What does your body need right now?",
        "What is something kind you could say to yourself about today?",
    )

    /** The prompt for [day]. [shift] steps to the next ones when the user asks for another. Stable within a day. */
    fun forDay(day: LocalDate, shift: Int = 0): String {
        val i = Math.floorMod(day.toEpochDay() + shift, REFLECTION.size.toLong()).toInt()
        return REFLECTION[i]
    }

    /** Gratitude lines are stored as one entry, one line each; blank lines are dropped. */
    fun joinLines(lines: List<String>): String = lines.map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n")

    fun splitLines(text: String): List<String> = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
}

object Mood {
    val FACES = listOf("😞", "😕", "😐", "🙂", "😄")
    val LABELS = listOf("Very low", "Low", "Okay", "Good", "Great")

    fun face(mood: Int?): String = mood?.let { FACES.getOrNull(it - 1) } ?: ""
    fun label(mood: Int?): String = mood?.let { LABELS.getOrNull(it - 1) } ?: ""
}
