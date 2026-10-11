package io.github.sreepv43.fablife.health

import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * Turns raw sleep sessions into sleep per day:
 * - overlapping sessions (the same night recorded by two apps) are merged, so no minute counts twice;
 * - when a session has stages, awake stages are left out; without stages the whole session counts;
 * - a night belongs to the day it ends on, even if it crosses midnight;
 * - a short session in the daytime is a nap and is reported separately from the night.
 */
object SleepMath {
    /** A cluster of sleep that is shorter than this, starts after NAP_FROM and ends before NAP_UNTIL (local time) is a nap. */
    private val NAP_MAX = Duration.ofHours(3)
    private val NAP_FROM = LocalTime.of(9, 0)
    private val NAP_UNTIL = LocalTime.of(21, 0)

    private class Interval(val start: Instant, val end: Instant)

    fun perDay(sessions: List<RawSleepSession>, zone: ZoneId): List<SleepDay> {
        val clusters = clusters(sessions.filter { it.end.isAfter(it.start) })
        val nights = HashMap<java.time.LocalDate, Long>()
        val naps = HashMap<java.time.LocalDate, Long>()
        for (cluster in clusters) {
            val asleep = merge(cluster.sessions.flatMap(::asleepIntervals))
            val minutes = asleep.sumOf { Duration.between(it.start, it.end).toMinutes() }
            if (minutes <= 0) continue
            val day = cluster.end.atZone(zone).toLocalDate()
            val startLocal = cluster.start.atZone(zone)
            val isNap = Duration.between(cluster.start, cluster.end) < NAP_MAX &&
                startLocal.toLocalDate() == day && startLocal.toLocalTime() >= NAP_FROM && cluster.end.atZone(zone).toLocalTime() <= NAP_UNTIL
            (if (isNap) naps else nights).merge(day, minutes, Long::plus)
        }
        return (nights.keys + naps.keys).sorted().map { day -> SleepDay(day, (nights[day] ?: 0).toInt(), (naps[day] ?: 0).toInt()) }
    }

    private class Cluster(val sessions: List<RawSleepSession>) {
        val start: Instant = sessions.minOf { it.start }
        val end: Instant = sessions.maxOf { it.end }
    }

    /** Groups sessions that overlap in time, so two recordings of one night are treated as one night. */
    private fun clusters(sessions: List<RawSleepSession>): List<Cluster> {
        val sorted = sessions.sortedBy { it.start }
        val out = ArrayList<MutableList<RawSleepSession>>()
        var currentEnd: Instant? = null
        for (s in sorted) {
            if (currentEnd != null && s.start.isBefore(currentEnd)) {
                out.last() += s
                if (s.end.isAfter(currentEnd)) currentEnd = s.end
            } else {
                out += mutableListOf(s)
                currentEnd = s.end
            }
        }
        return out.map(::Cluster)
    }

    private fun asleepIntervals(s: RawSleepSession): List<Interval> {
        if (s.stages.isEmpty()) return listOf(Interval(s.start, s.end))
        return s.stages.filter { it.type != SleepStageType.AWAKE && it.end.isAfter(it.start) }
            .map { Interval(maxOf(it.start, s.start), minOf(it.end, s.end)) }
            .filter { it.end.isAfter(it.start) }
    }

    private fun merge(intervals: List<Interval>): List<Interval> {
        val sorted = intervals.sortedBy { it.start }
        val out = ArrayList<Interval>()
        for (i in sorted) {
            val last = out.lastOrNull()
            if (last != null && !i.start.isAfter(last.end)) {
                if (i.end.isAfter(last.end)) out[out.lastIndex] = Interval(last.start, i.end)
            } else out += i
        }
        return out
    }
}
