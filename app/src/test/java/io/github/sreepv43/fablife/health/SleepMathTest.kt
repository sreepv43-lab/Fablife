package io.github.sreepv43.fablife.health

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class SleepMathTest {
    private val zone = ZoneId.of("Asia/Kolkata")

    private fun at(date: String, time: String): Instant = LocalDateTime.parse("${date}T$time").atZone(zone).toInstant()
    private fun session(startDate: String, start: String, endDate: String, end: String, vararg stages: RawStage) =
        RawSleepSession(at(startDate, start), at(endDate, end), stages.toList())

    private fun stage(date: String, start: String, endDate: String, end: String, type: SleepStageType) = RawStage(at(date, start), at(endDate, end), type)

    @Test
    fun aNightThatCrossesMidnightBelongsToTheDayItEndsOn() {
        val days = SleepMath.perDay(listOf(session("2026-10-09", "23:00:00", "2026-10-10", "06:30:00")), zone)
        assertEquals(listOf(SleepDay(LocalDate.of(2026, 10, 10), nightMinutes = 450, napMinutes = 0)), days)
    }

    @Test
    fun aNightThatDoesNotCrossMidnightStillCountsForItsOwnDay() {
        val days = SleepMath.perDay(listOf(session("2026-10-10", "00:30:00", "2026-10-10", "07:00:00")), zone)
        assertEquals(390, days.single().nightMinutes)
        assertEquals(LocalDate.of(2026, 10, 10), days.single().day)
    }

    @Test
    fun awakeStagesAreLeftOutWhenStagesExist() {
        val s = session(
            "2026-10-09", "23:00:00", "2026-10-10", "07:00:00",
            stage("2026-10-09", "23:00:00", "2026-10-10", "00:00:00", SleepStageType.LIGHT),
            stage("2026-10-10", "00:00:00", "2026-10-10", "00:30:00", SleepStageType.AWAKE),
            stage("2026-10-10", "00:30:00", "2026-10-10", "03:30:00", SleepStageType.DEEP),
            stage("2026-10-10", "03:30:00", "2026-10-10", "07:00:00", SleepStageType.REM),
        )
        assertEquals(60 + 180 + 210, SleepMath.perDay(listOf(s), zone).single().nightMinutes)
    }

    @Test
    fun aSessionWithoutStagesCountsInFull() {
        assertEquals(480, SleepMath.perDay(listOf(session("2026-10-09", "23:00:00", "2026-10-10", "07:00:00")), zone).single().nightMinutes)
    }

    @Test
    fun theSameNightFromTwoAppsIsCountedOnce() {
        val a = session("2026-10-09", "23:00:00", "2026-10-10", "07:00:00")
        val b = session("2026-10-09", "23:10:00", "2026-10-10", "06:50:00")
        assertEquals(480, SleepMath.perDay(listOf(a, b), zone).single().nightMinutes)
    }

    @Test
    fun overlappingStagesFromTwoSourcesDoNotDoubleCount() {
        val a = session("2026-10-10", "00:00:00", "2026-10-10", "06:00:00", stage("2026-10-10", "00:00:00", "2026-10-10", "06:00:00", SleepStageType.SLEEPING))
        val b = session("2026-10-10", "01:00:00", "2026-10-10", "07:00:00", stage("2026-10-10", "01:00:00", "2026-10-10", "07:00:00", SleepStageType.LIGHT))
        assertEquals(420, SleepMath.perDay(listOf(a, b), zone).single().nightMinutes)
    }

    @Test
    fun aSecondSourcesAwakeStageDoesNotRemoveSleepAnotherSourceRecorded() {
        val a = session("2026-10-10", "00:00:00", "2026-10-10", "06:00:00")
        val b = session(
            "2026-10-10", "00:00:00", "2026-10-10", "06:00:00",
            stage("2026-10-10", "00:00:00", "2026-10-10", "06:00:00", SleepStageType.AWAKE),
        )
        assertEquals(360, SleepMath.perDay(listOf(a, b), zone).single().nightMinutes)
    }

    @Test
    fun anAfternoonNapIsReportedSeparatelyFromTheNight() {
        val night = session("2026-10-09", "23:00:00", "2026-10-10", "06:00:00")
        val nap = session("2026-10-10", "14:00:00", "2026-10-10", "14:40:00")
        val day = SleepMath.perDay(listOf(night, nap), zone).single()
        assertEquals(420, day.nightMinutes)
        assertEquals(40, day.napMinutes)
    }

    @Test
    fun aLongDaytimeSleepIsNotANap() {
        val long = session("2026-10-10", "10:00:00", "2026-10-10", "15:00:00")
        val day = SleepMath.perDay(listOf(long), zone).single()
        assertEquals(300, day.nightMinutes)
        assertEquals(0, day.napMinutes)
    }

    @Test
    fun aShiftWorkersDaytimeSleepAfterANightShiftIsNotANap() {
        val s = session("2026-10-10", "08:30:00", "2026-10-10", "15:30:00")
        assertEquals(420, SleepMath.perDay(listOf(s), zone).single().nightMinutes)
    }

    @Test
    fun severalNightsAreReportedByDayInOrder() {
        val sessions = listOf(
            session("2026-10-10", "22:30:00", "2026-10-11", "06:30:00"),
            session("2026-10-08", "23:00:00", "2026-10-09", "07:00:00"),
        )
        val days = SleepMath.perDay(sessions, zone)
        assertEquals(listOf(LocalDate.of(2026, 10, 9), LocalDate.of(2026, 10, 11)), days.map { it.day })
        assertEquals(listOf(480, 480), days.map { it.nightMinutes })
    }

    @Test
    fun emptyAndZeroLengthInputGivesNothing() {
        assertTrue(SleepMath.perDay(emptyList(), zone).isEmpty())
        val zero = RawSleepSession(at("2026-10-10", "03:00:00"), at("2026-10-10", "03:00:00"))
        assertTrue(SleepMath.perDay(listOf(zero), zone).isEmpty())
    }

    @Test
    fun aSessionOfOnlyAwakeStagesCountsAsNoSleep() {
        val s = session("2026-10-10", "01:00:00", "2026-10-10", "03:00:00", stage("2026-10-10", "01:00:00", "2026-10-10", "03:00:00", SleepStageType.AWAKE))
        assertTrue(SleepMath.perDay(listOf(s), zone).isEmpty())
    }

    @Test
    fun stagesOutsideTheSessionAreClipped() {
        val s = session("2026-10-10", "01:00:00", "2026-10-10", "03:00:00", stage("2026-10-10", "00:00:00", "2026-10-10", "04:00:00", SleepStageType.LIGHT))
        assertEquals(120, SleepMath.perDay(listOf(s), zone).single().nightMinutes)
    }

    @Test
    fun theDayIsDecidedByTheUsersTimeZoneNotUtc() {
        // 23:30 UTC on the 9th is 05:00 on the 10th in India.
        val s = RawSleepSession(Instant.parse("2026-10-09T20:00:00Z"), Instant.parse("2026-10-09T23:30:00Z"))
        assertEquals(LocalDate.of(2026, 10, 10), SleepMath.perDay(listOf(s), zone).single().day)
        assertEquals(LocalDate.of(2026, 10, 9), SleepMath.perDay(listOf(s), ZoneId.of("UTC")).single().day)
    }

    @Test
    fun aNightAcrossADaylightSavingChangeUsesRealElapsedTime() {
        val ny = ZoneId.of("America/New_York") // clocks go back on 2026-11-01
        val start = LocalDateTime.parse("2026-10-31T23:00:00").atZone(ny).toInstant()
        val end = LocalDateTime.parse("2026-11-01T07:00:00").atZone(ny).toInstant()
        assertEquals(9 * 60, SleepMath.perDay(listOf(RawSleepSession(start, end)), ny).single().nightMinutes)
    }
}
