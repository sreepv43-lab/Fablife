package io.github.sreepv43.fablife.ui

import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.SessionKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Drives the audio-backed Mind screens on Robolectric with a fake [io.github.sreepv43.fablife.audio.android.AudioController]. */
class MindAudioUiTest : MindUiTestBase() {
    @Test
    fun mindTabListsBreathingGuidedSessionsSoundsAndTools() {
        openMind()
        reveal("4-7-8 breathing")
        reveal("Guided: Sleep")
        reveal("Body scan")
        reveal("Guided: Calm")
        reveal("Ambient sounds")
        reveal("Focus timer")
        reveal("Journal")
        reveal("Top 3 for today")
    }

    @Test
    fun aSoundIsChosenFromTheLibraryAndPlayedWithATimer() {
        openMind()
        click("Ambient sounds")
        waitFor("Generated ambient sounds", substring = true)
        click("Rain")
        waitFor("Stop after")
        click("30 min")
        click("Play")
        rule.waitUntil(5_000) { fake.calls.contains("playSound:rain:-:30") }
        waitFor("Pause")
        click("Stop")
        rule.waitUntil(5_000) { fake.calls.contains("stop") }
        waitFor("Play")
    }

    @Test
    fun changingTheTimerWhilePlayingUpdatesThePlayer() {
        openMind()
        click("Ambient sounds")
        click("Ocean waves")
        click("Play")
        rule.waitUntil(5_000) { fake.state.value.mode == PlaybackMode.SOUND }
        click("15 min")
        rule.waitUntil(5_000) { fake.calls.contains("timer:15") }
        waitFor("Stops in about 15 min", substring = true)
    }

    @Test
    fun aGuidedSessionStartsWithItsAmbientBedAndShowsCaptionsThenFinishes() {
        openMind()
        click("Body scan")
        waitFor("Soft background sound")
        click("Begin")
        rule.waitUntil(5_000) { fake.calls.any { it.startsWith("playGuided:body_scan:") } }
        assertTrue(fake.calls.contains("playGuided:body_scan:true"))

        fake.mutableState.value = fake.mutableState.value.copy(captionText = "Let your shoulders drop.", elapsedMs = 60_000, totalMs = 600_000)
        waitFor("Let your shoulders drop.")
        waitFor("Pause")
        click("Pause")
        rule.waitUntil(5_000) { fake.calls.contains("pause") }
        waitFor("Resume")

        fake.mutableState.value = fake.mutableState.value.copy(isPlaying = false, finished = true)
        waitFor("Well done.")
    }

    @Test
    fun aMissingVoiceIsExplained() {
        openMind()
        click("Two-minute stress reset")
        click("Begin")
        rule.waitUntil(5_000) { fake.state.value.mode == PlaybackMode.GUIDED }
        fake.mutableState.value = fake.mutableState.value.copy(ttsAvailable = false, captionText = "Breathe.")
        waitFor("No voice is available on this device", substring = true)
    }

    @Test
    fun theMiniPlayerAppearsOnTheTabsWhileSomethingPlaysAndControlsIt() {
        fake.playSound(SoundId.RAIN)
        waitFor("Pause")
        click("Pause")
        rule.waitUntil(5_000) { fake.calls.contains("pause") }
        waitFor("Resume")
        click("Stop")
        rule.waitUntil(5_000) { fake.calls.contains("stop") }
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Resume").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun breathingRunsToTheEndLogsTheTimeAndOffersTheHabitCheckIn() {
        waitFor("Breathe slowly")
        click("Breathe")
        waitFor("Begin")
        click("1 min")
        // The pacer runs on the frame clock, which the test only drives by hand (infinite animations are off while it auto-advances).
        rule.mainClock.autoAdvance = false
        rule.onAllNodesWithText("Begin").onFirst().performClick()
        rule.mainClock.advanceTimeBy(500)
        rule.onAllNodesWithText("Breathe in").onFirst().assertExists()
        rule.mainClock.advanceTimeBy(70_000)
        rule.mainClock.autoAdvance = true
        waitFor("Nicely done.")
        waitFor("Mark “Breathe slowly” as done")

        val logs = eventually(read = { container.database.sessionLogs().all() }) { it.isNotEmpty() }
        assertEquals(listOf(SessionKind.BREATHING to "box"), logs.map { it.kind to it.ref })
        assertEquals(1, logs.single().minutes)

        click("Mark “Breathe slowly” as done")
        eventually(read = { container.database.checkIns().all() }) { rows -> rows.any { it.habitId == "breathe_test" && it.status == CheckInStatus.DONE } }
    }

    @Test
    fun endingBreathingEarlyLogsNothing() {
        openMind()
        click("Box breathing")
        click("Begin")
        waitFor("Breathe in")
        click("End")
        waitFor("How long?")
        assertTrue(background { container.database.sessionLogs().all() }.isEmpty())
    }
}
