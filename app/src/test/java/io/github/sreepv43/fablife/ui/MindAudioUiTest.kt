package io.github.sreepv43.fablife.ui

import android.app.Application
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import io.github.sreepv43.fablife.MainActivity
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.audio.android.TestAudioApp
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.HabitEntity
import io.github.sreepv43.fablife.data.SessionKind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Drives the audio-backed Mind screens on Robolectric with a fake [io.github.sreepv43.fablife.audio.android.AudioController]. */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = TestAudioApp::class)
class MindAudioUiTest {
    @get:Rule
    val rule = createEmptyComposeRule()
    private lateinit var scenario: ActivityScenario<MainActivity>

    private val app get() = ApplicationProvider.getApplicationContext<Application>() as TestAudioApp
    private val fake get() = app.fake
    private val container get() = app.container

    /** Reads while the app is running: off the main thread, with the test pumping it, so nothing can deadlock. */
    private fun <T> background(block: suspend () -> T): T {
        val result = CoroutineScope(Dispatchers.Default).async { block() }
        rule.waitUntil(10_000) { result.isCompleted }
        return result.getCompleted()
    }

    /** Seeds an onboarded app (with one habit, so the intro letter doesn't take over the screen), then starts the activity. */
    @Before
    fun start() {
        runBlocking {
            container.repository.completeOnboarding { it.copy(goal = container.content.journeys.first().goalKey) }
            container.database.habits().insert(
                HabitEntity(
                    id = "breathe_test", title = "Breathe slowly", description = null, icon = "breath", ritual = Ritual.MORNING, order = 0,
                    durationSeconds = null, smallerVersion = null, sourceStepId = null, createdAtMillis = 0, toolType = "BREATHING", toolRef = "box",
                ),
            )
        }
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun stop() {
        scenario.close()
    }

    private fun waitFor(text: String, substring: Boolean = false) =
        rule.waitUntilAtLeastOneExists(hasText(text, substring = substring), timeoutMillis = 10_000)

    /** Waits for [text], first scrolling the screen's list to it: a lazy list only composes the rows on screen. */
    private fun reveal(text: String) {
        if (rule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty() && rule.onAllNodes(hasScrollAction()).fetchSemanticsNodes().isNotEmpty()) {
            rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText(text))
        }
        waitFor(text)
    }

    private fun click(text: String) {
        reveal(text)
        // The small test screen clips content in a plain scrolling column; a click on a clipped node would silently miss.
        runCatching { rule.onAllNodesWithText(text).onFirst().performScrollTo() }
        rule.onAllNodesWithText(text).onFirst().performClick()
    }

    private fun openMind() {
        click("Explore")
        click("Mind")
        waitFor("Box breathing")
    }

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

        val logs = background { container.database.sessionLogs().all() }
        assertEquals(listOf(SessionKind.BREATHING to "box"), logs.map { it.kind to it.ref })
        assertEquals(1, logs.single().minutes)

        click("Mark “Breathe slowly” as done")
        rule.waitUntil(5_000) {
            background { container.database.checkIns().all() }.any { it.habitId == "breathe_test" && it.status == CheckInStatus.DONE }
        }
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
