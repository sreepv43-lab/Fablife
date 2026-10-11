package io.github.sreepv43.fablife.engine

enum class SegmentType { FOCUS, SHORT_BREAK, LONG_BREAK }

/** Lengths of a Pomodoro-style cycle, in minutes. A long break replaces every [roundsBeforeLongBreak]th short one. */
data class FocusSettings(
    val focusMinutes: Int = DEFAULT_FOCUS,
    val breakMinutes: Int = DEFAULT_BREAK,
    val longBreakMinutes: Int = DEFAULT_LONG_BREAK,
    val roundsBeforeLongBreak: Int = 4,
) {
    init {
        require(focusMinutes > 0 && breakMinutes > 0 && longBreakMinutes > 0 && roundsBeforeLongBreak > 0) { "lengths must be positive" }
    }

    /** The same settings with each length pulled into its allowed range (stored values may be stale or hand-edited). */
    fun coerced() = copy(
        focusMinutes = focusMinutes.coerceIn(FOCUS_RANGE),
        breakMinutes = breakMinutes.coerceIn(BREAK_RANGE),
        longBreakMinutes = longBreakMinutes.coerceIn(LONG_BREAK_RANGE),
    )

    companion object {
        const val DEFAULT_FOCUS = 25
        const val DEFAULT_BREAK = 5
        const val DEFAULT_LONG_BREAK = 15
        val FOCUS_RANGE = 10..60
        val BREAK_RANGE = 3..15
        val LONG_BREAK_RANGE = 10..30
    }
}

/** One block of the cycle. [round] counts focus blocks from 1; a break carries the round it follows. */
data class Segment(val index: Int, val type: SegmentType, val minutes: Int, val round: Int) {
    val durationMs: Long get() = minutes * 60_000L
}

object FocusPlan {
    /** Segment 0 is the first focus block, 1 the break after it, 2 the next focus block and so on, without end. */
    fun segment(settings: FocusSettings, index: Int): Segment {
        require(index >= 0) { "index must not be negative" }
        val round = index / 2 + 1
        if (index % 2 == 0) return Segment(index, SegmentType.FOCUS, settings.focusMinutes, round)
        return if (round % settings.roundsBeforeLongBreak == 0) Segment(index, SegmentType.LONG_BREAK, settings.longBreakMinutes, round)
        else Segment(index, SegmentType.SHORT_BREAK, settings.breakMinutes, round)
    }

    /** A focus block that is stopped early only counts once at least this many minutes were done. */
    const val MIN_PARTIAL_MINUTES = 5

    /** Focus minutes to record for [segment]: all of them if it [completed], else the whole minutes done if that is enough to count. */
    fun minutesToLog(segment: Segment, elapsedMs: Long, completed: Boolean): Int {
        if (segment.type != SegmentType.FOCUS) return 0
        if (completed) return segment.minutes
        val done = (elapsedMs / 60_000L).toInt().coerceIn(0, segment.minutes)
        return if (done >= MIN_PARTIAL_MINUTES) done else 0
    }
}

/**
 * A countdown measured against a clock that keeps running while the app is in the background. All times are in the
 * caller's clock (the app uses elapsed real time); nothing here reads a clock itself.
 */
data class SegmentTimer(val durationMs: Long, val remainingMs: Long = durationMs, val endAtMs: Long? = null) {
    val running: Boolean get() = endAtMs != null

    fun remaining(now: Long): Long = endAtMs?.let { (it - now).coerceIn(0, durationMs) } ?: remainingMs.coerceIn(0, durationMs)

    fun elapsed(now: Long): Long = durationMs - remaining(now)

    fun finished(now: Long): Boolean = remaining(now) <= 0

    fun start(now: Long): SegmentTimer = if (running) this else copy(endAtMs = now + remainingMs)

    fun pause(now: Long): SegmentTimer = if (!running) this else copy(remainingMs = remaining(now), endAtMs = null)
}
