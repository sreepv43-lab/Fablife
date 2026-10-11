package io.github.sreepv43.fablife.health

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HealthLoadTest {
    private val today = LocalDate.of(2026, 10, 10)
    private fun load(repo: FakeHealthRepository) = runBlocking { loadHealth(repo, today) }

    @Test
    fun anUnsupportedDeviceIsReportedAsSuch() {
        assertEquals(HealthUi.Unavailable, load(FakeHealthRepository().apply { availability = HealthAvailability.UNAVAILABLE }))
    }

    @Test
    fun aMissingHealthConnectAppIsReportedAsNotInstalled() {
        assertEquals(HealthUi.NotInstalled, load(FakeHealthRepository().apply { availability = HealthAvailability.NOT_INSTALLED }))
    }

    @Test
    fun withoutPermissionNothingIsRead() {
        val repo = FakeHealthRepository().apply { granted = false }
        assertEquals(HealthUi.NeedsPermission, load(repo))
    }

    @Test
    fun withPermissionTheLastSevenDaysAreReadOldestFirst() {
        val repo = FakeHealthRepository().apply {
            granted = true
            days = mapOf(today to DayHealth(today, 4_200, 450))
        }
        val ready = load(repo) as HealthUi.Ready
        assertEquals(HEALTH_DAYS, ready.summary.days.size)
        assertEquals(today.minusDays(6), ready.summary.days.first().day)
        assertEquals(today, ready.summary.days.last().day)
        assertEquals(DayHealth(today, 4_200, 450), ready.summary.forDay(today))
        assertEquals(null, ready.summary.forDay(today.minusDays(1))?.steps)
    }

    @Test
    fun anyFailureFromHealthConnectBecomesAFailedStateNotACrash() {
        val repo = FakeHealthRepository().apply { granted = true; failWith = SecurityException("permission revoked") }
        assertEquals(HealthUi.Failed, load(repo))
    }

    @Test
    fun cancellationIsNotSwallowed() {
        val repo = FakeHealthRepository().apply { granted = true; failWith = kotlinx.coroutines.CancellationException("stop") }
        var thrown: Throwable? = null
        try {
            load(repo)
        } catch (e: kotlinx.coroutines.CancellationException) {
            thrown = e
        }
        assertTrue(thrown != null)
    }

    @Test
    fun sleepIsFormattedInHoursAndMinutes() {
        assertEquals("7 h 05 min", formatSleep(425))
        assertEquals("45 min", formatSleep(45))
        assertEquals("8 h 00 min", formatSleep(480))
    }
}
