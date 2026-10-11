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
import io.github.sreepv43.fablife.audio.android.TestAudioApp
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.HabitEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Starts the real app on Robolectric with a fake audio controller and a hand-moved clock, already onboarded and with
 * one habit per tool so the intro letter doesn't take over the screen. Seed anything else in [seed], which runs before
 * the activity starts (blocking the main thread while the app is live can deadlock it, so later changes go through
 * [background]).
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = TestAudioApp::class)
abstract class MindUiTestBase {
    @get:Rule
    val rule = createEmptyComposeRule()
    protected lateinit var scenario: ActivityScenario<MainActivity>

    protected val app get() = ApplicationProvider.getApplicationContext<Application>() as TestAudioApp
    protected val fake get() = app.fake
    protected val container get() = app.container

    /** Extra habits to start with, besides the tool habits below. */
    protected open fun habits(): List<HabitEntity> = emptyList()

    protected open suspend fun seed() = Unit

    protected fun toolHabit(id: String, title: String, tool: String, ref: String?, order: Int) = HabitEntity(
        id = id, title = title, description = null, icon = "breath", ritual = Ritual.MORNING, order = order,
        durationSeconds = null, smallerVersion = null, sourceStepId = null, createdAtMillis = 0, toolType = tool, toolRef = ref,
    )

    /** Reads while the app is running: off the main thread, with the test pumping it, so nothing can deadlock. */
    protected fun <T> background(block: suspend () -> T): T {
        val result = CoroutineScope(Dispatchers.Default).async { block() }
        rule.waitUntil(10_000) { result.isCompleted }
        return result.getCompleted()
    }

    /** Reads from the database until [ok] accepts what it finds, and fails showing the last value if it never does. */
    protected fun <T> eventually(timeoutMs: Long = 10_000, read: suspend () -> T, ok: (T) -> Boolean): T {
        val end = System.nanoTime() + timeoutMs * 1_000_000
        while (true) {
            val value = background(read)
            if (ok(value)) return value
            if (System.nanoTime() > end) throw AssertionError("Condition not met in $timeoutMs ms. Last value: $value")
            rule.waitForIdle()
            Thread.sleep(25)
        }
    }

    @Before
    fun start() {
        runBlocking {
            container.repository.completeOnboarding { it.copy(goal = container.content.journeys.first().goalKey) }
            (listOf(
                toolHabit("breathe_test", "Breathe slowly", "BREATHING", "box", 0),
                toolHabit("write_test", "Write gratitude", "JOURNAL", "GRATITUDE", 1),
                toolHabit("focus_test", "Focus block", "FOCUS_TIMER", null, 2),
                toolHabit("plan_test", "Plan the day", "TOP3", null, 3),
            ) + habits()).forEach { container.database.habits().insert(it) }
            seed()
        }
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun stop() {
        scenario.close()
        // The preferences store is a process-wide singleton that outlives each test's app, so leave it as found.
        runBlocking { container.repository.resetAll() }
    }

    /**
     * Waits for [text] to be on screen, scrolling the first list to it while it isn't: a lazy list only composes the rows
     * that are showing. (Scrolling a plain column to a node is not supported and is simply skipped.)
     */
    protected fun reveal(text: String, substring: Boolean = false) {
        val end = System.nanoTime() + 10_000_000_000L
        while (rule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isEmpty()) {
            if (System.nanoTime() > end) throw AssertionError("'$text' never appeared")
            if (rule.onAllNodes(hasScrollAction()).fetchSemanticsNodes().isNotEmpty()) {
                runCatching { rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText(text, substring = substring)) }
            }
            rule.waitForIdle()
            Thread.sleep(25)
        }
    }

    protected fun waitFor(text: String, substring: Boolean = false) = reveal(text, substring)

    protected fun click(text: String) {
        reveal(text)
        // The small test screen clips content in a plain scrolling column; a click on a clipped node would silently miss.
        runCatching { rule.onAllNodesWithText(text).onFirst().performScrollTo() }
        rule.onAllNodesWithText(text).onFirst().performClick()
    }

    protected fun openMind() {
        click("Explore")
        click("Mind")
        waitFor("Box breathing")
    }
}
