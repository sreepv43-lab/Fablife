package io.github.sreepv43.fablife.ui

import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import io.github.sreepv43.fablife.health.DayHealth
import io.github.sreepv43.fablife.health.HealthAvailability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import io.github.sreepv43.fablife.engine.logicalDay

/** The Health screen and the Progress card in every state, with a fake Health Connect. */
class HealthUiTest : MindUiTestBase() {
    private val today get() = logicalDay(LocalDateTime.now(), 3)
    private val health get() = app.health

    private fun openHealthFromSettings() {
        click("Settings")
        click("Health Connect")
        waitFor("Steps and sleep")
    }

    @Test
    fun aDeviceWithoutHealthConnectSaysSoAndNothingBreaks() {
        health.availability = HealthAvailability.UNAVAILABLE
        openHealthFromSettings()
        waitFor("Not available on this device")
        waitFor("For your information only", substring = true)
    }

    @Test
    fun aMissingHealthConnectAppOffersToInstallIt() {
        health.availability = HealthAvailability.NOT_INSTALLED
        openHealthFromSettings()
        waitFor("Health Connect isn't installed")
        waitFor("Get Health Connect")
    }

    @Test
    fun permissionIsAskedForOnlyWhenTheUserChoosesToConnect() {
        openHealthFromSettings()
        waitFor("See your steps and sleep here")
        assertEquals(0, health.permissionRequests)
        click("Connect Health Connect")
        waitFor("Steps today")
        assertEquals(1, health.permissionRequests)
        waitFor("No steps recorded yet today.")
        waitFor("No sleep recorded for last night.")
    }

    @Test
    fun grantedDataShowsTodaysStepsAgainstTheGoalAndLastNightsSleep() {
        health.granted = true
        health.days = mapOf(
            today to DayHealth(today, 5_432, 7 * 60 + 5),
            today.minusDays(1) to DayHealth(today.minusDays(1), 8_100, 390),
        )
        openHealthFromSettings()
        waitFor("5,432")
        waitFor("of your 7,000 step goal")
        waitFor("7 h 05 min")
        reveal("Yesterday")
        reveal("8,100 steps · 6 h 30 min sleep")
    }

    @Test
    fun theStepGoalCanBeChangedAndIsRemembered() {
        health.granted = true
        health.days = mapOf(today to DayHealth(today, 1_000, null))
        openHealthFromSettings()
        waitFor("of your 7,000 step goal")
        rule.onAllNodesWithContentDescription("Raise step goal").onFirst().performClick()
        waitFor("of your 7,500 step goal")
        eventually(read = { container.prefs.current() }) { it.stepGoal == 7_500 }
    }

    @Test
    fun aFailureFromHealthConnectOffersToTryAgain() {
        health.granted = true
        health.failWith = SecurityException("revoked")
        openHealthFromSettings()
        waitFor("Couldn't read Health Connect")
        health.failWith = null
        click("Try again")
        waitFor("Steps today")
    }

    @Test
    fun theProgressTabShowsASummaryWhenConnectedAndAnInvitationWhenNot() {
        health.granted = true
        health.days = mapOf(today to DayHealth(today, 3_210, 480))
        click("Progress")
        waitFor("3,210 steps today · 8 h 00 min last night")
        click("3,210 steps today · 8 h 00 min last night")
        waitFor("Steps today")
    }

    @Test
    fun theProgressTabInvitesButDoesNotNagWhenNotConnected() {
        click("Progress")
        waitFor("Steps and sleep (optional)")
        waitFor("Connect Health Connect to see them here.")
    }

    @Test
    fun theProgressTabShowsNothingAboutHealthOnADeviceThatCannotUseIt() {
        health.availability = HealthAvailability.UNAVAILABLE
        click("Progress")
        waitFor("Your progress")
        rule.waitForIdle()
        assertTrue(rule.onAllNodesWithText("Steps and sleep", substring = true).fetchSemanticsNodes().isEmpty())
    }
}
