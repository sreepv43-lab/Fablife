package io.github.sreepv43.fablife.audio

import io.github.sreepv43.fablife.content.GuidedSession
import io.github.sreepv43.fablife.content.GuidedStep
import kotlin.math.max

/** Estimated timing of a guided session, for progress bars and for reading time when no voice is available. */
class SessionTimeline(session: GuidedSession) {
    private val durations: List<Long> = session.steps.map(::estimateMs)

    /** Start of each step in ms, plus the end as the last entry. */
    private val starts: List<Long> = durations.runningFold(0L) { acc, d -> acc + d }

    val totalMs: Long get() = starts.last()
    val stepCount: Int get() = durations.size

    fun startOfStepMs(index: Int): Long = starts[index.coerceIn(0, durations.size)]

    /** 0..1 progress for a bar when step [index] begins. */
    fun fractionAtStart(index: Int): Float = if (totalMs == 0L) 0f else startOfStepMs(index).toFloat() / totalMs

    fun stepAt(elapsedMs: Long): Int {
        if (durations.isEmpty()) return 0
        val i = starts.indexOfLast { it <= elapsedMs }
        return i.coerceIn(0, durations.size - 1)
    }

    companion object {
        const val WORDS_PER_SECOND = 2.2
        private const val MIN_READING_MS = 1_500L

        fun speechMs(text: String): Long = (wordCount(text) / WORDS_PER_SECOND * 1000).toLong()

        /** How long to show a caption when nothing is spoken: long enough to read, never flashing past. */
        fun readingMs(text: String): Long = max(MIN_READING_MS, speechMs(text))

        fun estimateMs(step: GuidedStep): Long = (step.say?.let(::speechMs) ?: 0L) + (step.pause * 1000).toLong()

        private fun wordCount(text: String) = text.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
    }
}
