package io.github.sreepv43.fablife.data

import androidx.test.core.app.ApplicationProvider
import io.github.sreepv43.fablife.FabLifeApp
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.Letter
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.content.Step
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MultiJourneyTest {
    private val app = ApplicationProvider.getApplicationContext<FabLifeApp>()
    private val container get() = app.container
    private val repo get() = container.repository
    private val journeys get() = container.content.journeys

    @After
    fun reset() = runBlocking { repo.resetAll() }

    private suspend fun stepRows(journeyId: String) = container.database.steps().all().filter { it.journeyId == journeyId }
    private suspend fun activeId() = container.prefs.current().activeJourneyId

    @Test
    fun theChosenGoalStartsThatJourneyAndOnlyThatJourney() = runBlocking {
        val target = journeys.last()
        repo.completeOnboarding { it.copy(goal = target.goalKey) }

        assertEquals(target.id, activeId())
        val rows = container.database.steps().all()
        assertEquals(target.steps.size, rows.size)
        assertTrue(rows.all { it.journeyId == target.id })
        assertEquals(StepState.UNLOCKED, rows.single { it.stepId == target.steps.first().id }.state)
        assertTrue(rows.filter { it.stepId != target.steps.first().id }.all { it.state == StepState.LOCKED })
    }

    @Test
    fun anUnknownGoalFallsBackToTheFirstJourney() = runBlocking {
        repo.completeOnboarding { it.copy(goal = "does-not-exist") }
        assertEquals(journeys.first().id, activeId())
    }

    @Test
    fun switchingJourneysKeepsProgressInBoth() = runBlocking {
        val first = journeys.first()
        repo.completeOnboarding { it.copy(goal = first.goalKey) }
        val firstStep = first.steps.first()
        repo.acceptStep(firstStep.id)
        assertEquals(StepState.ACTIVE, stepRows(first.id).single { it.stepId == firstStep.id }.state)

        val other = journeys.firstOrNull { it.id != first.id } ?: return@runBlocking
        repo.startJourney(other.id)
        assertEquals(other.id, activeId())
        assertEquals(StepState.UNLOCKED, stepRows(other.id).single { it.stepId == other.steps.first().id }.state)

        repo.startJourney(first.id)
        assertEquals(first.id, activeId())
        assertEquals("starting again must not reset progress", StepState.ACTIVE, stepRows(first.id).single { it.stepId == firstStep.id }.state)
        assertEquals(first.steps.size, stepRows(first.id).size)
    }

    @Test
    fun startingAnUnknownJourneyChangesNothing() = runBlocking {
        repo.completeOnboarding { it.copy(goal = journeys.first().goalKey) }
        repo.startJourney("nope")
        assertEquals(journeys.first().id, activeId())
    }

    @Test
    fun acceptingAHabitStepAddsItsHabitWithItsTool() = runBlocking {
        val journey = journeys.first { j -> j.steps.any { (it.goal as? Goal.Habit)?.habit?.tool != null } }
        repo.completeOnboarding { it.copy(goal = journey.goalKey) }
        val step = journey.steps.first { (it.goal as? Goal.Habit)?.habit?.tool != null }
        // Unlock it directly: skipping earlier steps keeps their habits and unlocks the next one.
        for (earlier in journey.steps.takeWhile { it !== step }) repo.skipStep(earlier.id)
        repo.acceptStep(step.id)

        val habit = (step.goal as Goal.Habit).habit
        val saved = container.database.habits().all().single { it.id == habit.id }
        assertEquals(habit.tool!!.type.name, saved.toolType)
        assertEquals(habit.tool!!.ref, saved.toolRef)
        assertEquals(step.id, saved.sourceStepId)
    }

    @Test
    fun habitsFromBeforeToolsExistPickUpTheirToolOnAppOpen() = runBlocking {
        val energy = journeys.first { it.id == "energy" }
        repo.completeOnboarding { it.copy(goal = energy.goalKey) }
        container.database.habits().insert(
            HabitEntity("morning_movement", "Move for 5 minutes", null, "movement", Ritual.MORNING, 1, 300, "Move for 1 minute", false, "energy.move", 1L),
        )

        repo.onAppOpen()

        val habit = container.database.habits().all().single()
        assertEquals("ROUTINE", habit.toolType)
        assertEquals("WAKE_UP", habit.toolRef)
    }

    @Test
    fun aRitualChallengeCountsOnlyTheHabitsItsJourneyAdded() {
        fun habit(id: String, source: String?) = HabitEntity(id, id, null, "check", Ritual.MORNING, 1, null, null, false, source, 1L)
        val habits = listOf(habit("a", "calm.one"), habit("b", "calm.two"), habit("other_journey", "energy.water"), habit("custom", null))
        val step = Step("calm.final", "Final", Letter("t", "b"), Goal.RitualChallenge(Ritual.MORNING, 2, 3), "go", completionTitle = "x", completionMessage = "y")
        val day = LocalDate.of(2026, 10, 5)
        fun done(id: String) = CheckInEntity(id, day, CheckInStatus.DONE, 1L)

        // Doing only this journey's habits completes the day, even though other habits exist in the ritual.
        assertEquals(setOf(day), FabRepository.doneDaysFor(step, habits, listOf(done("a"), done("b"))))
        // Missing one of this journey's habits does not.
        assertTrue(FabRepository.doneDaysFor(step, habits, listOf(done("a"), done("other_journey"), done("custom"))).isEmpty())
    }
}
