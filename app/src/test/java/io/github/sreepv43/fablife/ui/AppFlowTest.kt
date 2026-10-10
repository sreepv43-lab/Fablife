package io.github.sreepv43.fablife.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import io.github.sreepv43.fablife.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Drives the real app on Robolectric: onboarding → intro letter → step 1 → first check-in. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun waitFor(text: String, substring: Boolean = false) =
        rule.waitUntilAtLeastOneExists(hasText(text, substring = substring), timeoutMillis = 10_000)

    private fun click(text: String) {
        waitFor(text)
        rule.onNodeWithText(text).performClick()
    }

    @Test
    fun onboardingThroughFirstCheckIn() {
        click("Get started")
        click("Continue") // goal
        click("Continue") // obstacles
        click("Continue") // times
        click("Yes") // high-fibre foods upset my stomach -> gentle mode
        click("Continue")
        click("Low-impact / joint-friendly (no jumping)")
        click("Continue")
        waitFor("Your first name (optional)")
        rule.onNodeWithText("Your first name (optional)").performTextInput("Priya")
        click("Continue")
        waitFor("Press\n& hold")
        rule.onNodeWithText("Press\n& hold").performTouchInput { longClick(durationMillis = 2_000) }
        waitFor("Signed ✓")
        click("Continue")
        click("Not now")

        // Intro letter opens automatically, personalised.
        waitFor("Your Energy Is a Skill")
        waitFor("Dear Priya,", substring = true)
        click("Let's begin")

        // Step 1 letter, then the habit joins the morning ritual.
        waitFor("The Ten-Second Habit")
        click("Add \"Drink water\" to my morning")
        waitFor("Good", substring = true)
        waitFor("Drink a glass of water: 0 of 3 days")
        click("Drink a glass of water")
        waitFor("Drink a glass of water: 1 of 3 days")

        // The ritual (only one habit so far) is complete, so the celebration shows.
        waitFor("Morning ritual complete!")
        click("Celebrate 🎉")

        // Explore shows gentle (low-fibre) meals because of the onboarding answer.
        click("Explore")
        click("Eat")
        waitFor("Gentle on the stomach (low-fibre)")
        // The outer page list is the first scrollable node (the tips row inside it is the second).
        rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText("🌿 Gentle", substring = true))

        // A meal card opens its recipe.
        val meal = "Idli with a little strained moong dal or curd"
        rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText(meal))
        click(meal)
        waitFor("How to make it")
        waitFor("Ingredients (for one)")

        // Settings has the day-start control.
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        click("Settings")
        rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText("Your day"))
        waitFor("Midnight")
    }
}
