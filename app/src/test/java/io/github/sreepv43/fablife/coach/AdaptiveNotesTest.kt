package io.github.sreepv43.fablife.coach

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AdaptiveNotesTest {
    private val evenDay = LocalDate.ofEpochDay(20_000) // epoch day parity decides whether an obstacle tip or the time note shows
    private val oddDay = evenDay.plusDays(1)

    private fun step(
        goal: GoalKind = GoalKind.HABIT, phase: StepPhase = StepPhase.ACTIVE, done: Int = 1, target: Int = 3, active: Int = 2,
        smaller: String? = "Drink one sip.", theme: StepTheme = StepTheme.OTHER,
    ) = CoachStep("Drink water", goal, phase, done, target, active, smaller, theme)

    private fun ctx(
        day: LocalDate = oddDay, hour: Int = 9, name: String? = "Priya", step: CoachStep? = step(), complete: Boolean = false,
        streak: Int = 1, since: Int? = 0, obstacles: Set<String> = emptySet(), gentleEating: Boolean = false, gentleMovement: Boolean = false,
    ) = CoachContext(day, hour, name, "Feel more energetic", step, complete, streak, since, obstacles, gentleEating, gentleMovement)

    private fun rule(c: CoachContext) = AdaptiveNotes.note(c).rule

    // Rule choice

    @Test
    fun aFinishedJourneyComesFirstOfAll() {
        assertEquals(NoteRule.COMPLETE, rule(ctx(complete = true, since = 10, streak = 7, step = null)))
    }

    @Test
    fun threeQuietDaysTriggerAGentleComeback() {
        assertEquals(NoteRule.COMEBACK, rule(ctx(since = 3)))
        assertEquals(NoteRule.COMEBACK, rule(ctx(since = 12, streak = 0)))
        assertNotEquals(NoteRule.COMEBACK, rule(ctx(since = 2)))
    }

    @Test
    fun aBrandNewUserWithNoCheckInsIsNotWelcomedBack() {
        assertNotEquals(NoteRule.COMEBACK, rule(ctx(since = null, streak = 0)))
    }

    @Test
    fun streakMilestonesAreCelebratedOnTheExactDay() {
        listOf(3, 7, 14, 21, 30).forEach { assertEquals(NoteRule.MILESTONE, rule(ctx(streak = it))) }
        listOf(1, 2, 4, 8, 15).forEach { assertNotEquals(NoteRule.MILESTONE, rule(ctx(streak = it))) }
    }

    @Test
    fun aHabitWithNoProgressAfterThreeDaysOffersTheSmallerVersion() {
        val c = ctx(step = step(done = 0, active = 3))
        assertEquals(NoteRule.STRUGGLING, rule(c))
        assertTrue(AdaptiveNotes.note(c).text.contains("Drink one sip"))
    }

    @Test
    fun aHabitThatIsHalfwayThereIsNotStruggling() {
        assertNotEquals(NoteRule.STRUGGLING, rule(ctx(step = step(done = 2, target = 3, active = 4))))
        assertEquals(NoteRule.STRUGGLING, rule(ctx(step = step(done = 1, target = 5, active = 5))))
    }

    @Test
    fun strugglingWithoutASmallerVersionStillGetsKindAdvice() {
        val n = AdaptiveNotes.note(ctx(step = step(done = 0, active = 4, smaller = null)))
        assertEquals(NoteRule.STRUGGLING, n.rule)
        assertFalse(n.text.contains("{"))
        assertFalse(n.text.contains(": ."))
    }

    @Test
    fun anUnreadLetterIsPointedOut() {
        assertEquals(NoteRule.NEW_LETTER, rule(ctx(step = step(phase = StepPhase.UNREAD, done = 0, active = 0))))
    }

    @Test
    fun aPendingOneTimeStepNamesTheStep() {
        val n = AdaptiveNotes.note(ctx(step = step(goal = GoalKind.ACTION, done = 0, active = 1)))
        assertEquals(NoteRule.ACTION_PENDING, n.rule)
        assertTrue(n.text.contains("Drink water"))
    }

    @Test
    fun foodStepsReflectGentleEatingOnlyWhenItIsOn() {
        val food = step(theme = StepTheme.FOOD)
        assertEquals(NoteRule.GENTLE_FOOD, rule(ctx(step = food, gentleEating = true)))
        assertNotEquals(NoteRule.GENTLE_FOOD, rule(ctx(step = food, gentleEating = false)))
        assertNotEquals(NoteRule.GENTLE_FOOD, rule(ctx(step = step(), gentleEating = true)))
    }

    @Test
    fun movementStepsReflectGentleMovementOnlyWhenItIsOn() {
        val move = step(theme = StepTheme.MOVEMENT)
        assertEquals(NoteRule.MOVEMENT_ADAPTED, rule(ctx(step = move, gentleMovement = true)))
        assertNotEquals(NoteRule.MOVEMENT_ADAPTED, rule(ctx(step = move, gentleMovement = false)))
    }

    @Test
    fun obstacleTipsAndTheTimeNoteTakeTurnsByDate() {
        val obstacles = setOf(AdaptiveNotes.OBSTACLE_BUSY)
        assertEquals(NoteRule.OBSTACLE_BUSY, rule(ctx(day = evenDay, obstacles = obstacles)))
        assertEquals(NoteRule.MORNING, rule(ctx(day = oddDay, obstacles = obstacles)))
    }

    @Test
    fun eachChosenObstacleGetsItsOwnTipAcrossTheDays() {
        val all = setOf(AdaptiveNotes.OBSTACLE_BUSY, AdaptiveNotes.OBSTACLE_MOTIVATION, AdaptiveNotes.OBSTACLE_FORGET, AdaptiveNotes.OBSTACLE_BIG)
        val seen = (0 until 16).map { rule(ctx(day = evenDay.plusDays(it.toLong()), obstacles = all)) }.filter { it.name.startsWith("OBSTACLE") }.toSet()
        assertEquals(setOf(NoteRule.OBSTACLE_BUSY, NoteRule.OBSTACLE_MOTIVATION, NoteRule.OBSTACLE_FORGET, NoteRule.OBSTACLE_BIG), seen)
    }

    @Test
    fun theTimeOfDayDecidesTheDefaultNote() {
        assertEquals(NoteRule.MORNING, rule(ctx(hour = 6)))
        assertEquals(NoteRule.AFTERNOON, rule(ctx(hour = 14)))
        assertEquals(NoteRule.EVENING, rule(ctx(hour = 21)))
        assertEquals(NoteRule.EVENING, rule(ctx(hour = 2)))
    }

    @Test
    fun everyRuleCanBeReached() {
        val reached = mutableSetOf<NoteRule>()
        reached += rule(ctx(complete = true))
        reached += rule(ctx(since = 5))
        reached += rule(ctx(streak = 7))
        reached += rule(ctx(step = step(done = 0, active = 4)))
        reached += rule(ctx(step = step(phase = StepPhase.UNREAD)))
        reached += rule(ctx(step = step(goal = GoalKind.ACTION)))
        reached += rule(ctx(step = step(theme = StepTheme.FOOD), gentleEating = true))
        reached += rule(ctx(step = step(theme = StepTheme.MOVEMENT), gentleMovement = true))
        AdaptiveNotes.OBSTACLE_BUSY.let { reached += rule(ctx(day = evenDay, obstacles = setOf(it))) }
        reached += rule(ctx(day = evenDay, obstacles = setOf(AdaptiveNotes.OBSTACLE_MOTIVATION)))
        reached += rule(ctx(day = evenDay, obstacles = setOf(AdaptiveNotes.OBSTACLE_FORGET)))
        reached += rule(ctx(day = evenDay, obstacles = setOf(AdaptiveNotes.OBSTACLE_BIG)))
        reached += listOf(6, 14, 21).map { rule(ctx(hour = it)) }
        assertEquals(NoteRule.entries.toSet(), reached)
    }

    // Wording

    @Test
    fun theNoteIsStableWithinADayAndVariesOverDays() {
        val c = ctx(hour = 9)
        assertEquals(AdaptiveNotes.note(c), AdaptiveNotes.note(c))
        val texts = (0 until 30).map { AdaptiveNotes.note(ctx(day = oddDay.plusDays(it.toLong() * 2), hour = 9)).text }.toSet()
        assertTrue("only ${texts.size} different morning notes", texts.size >= 3)
    }

    @Test
    fun theNameIsUsedWhenGivenAndDroppedGracefullyWhenNot() {
        val named = AdaptiveNotes.note(ctx(since = 5, name = "Priya", day = oddDay)).text
        val anon = AdaptiveNotes.note(ctx(since = 5, name = null, day = oddDay)).text
        assertFalse(named.contains("{"))
        assertFalse(anon.contains("{"))
        assertFalse(anon.contains("friend,") && anon.contains(", ,"))
        assertTrue(anon.first().isUpperCase())
        assertFalse(anon.contains(" ,"))
        assertFalse(anon.contains(",,"))
    }

    @Test
    fun noTemplateLeavesAPlaceholderOrBreaksTheSentenceWhateverTheNameOrData() {
        val rules = NoteRule.entries
        for (rule in rules) {
            val variants = AdaptiveNotes.TEMPLATES.getValue(rule)
            assertTrue("$rule needs at least 3 variants", variants.size >= 3)
            for (offset in variants.indices) {
                for (name in listOf("Priya", null, "  ", "A-very-long-name-indeed")) {
                    // Find a day on which this rule and variant are chosen by forcing the choice through the same maths.
                    val day = LocalDate.ofEpochDay((offset - rule.ordinal).toLong() + variants.size * 1000L)
                    val text = AdaptiveNotesTestHooks.fill(variants[offset], ctx(day = day, name = name, streak = 7, step = step()))
                    assertFalse("$rule/$offset: $text", text.contains("{") || text.contains("}"))
                    assertFalse("$rule/$offset: $text", text.contains(" ,") || text.contains(",,") || text.contains("  "))
                    assertTrue(text.first().isUpperCase() || text.first().isDigit())
                }
            }
        }
    }

    @Test
    fun everyNoteIsShortAndWarm() {
        val shout = Regex("[A-Z]{4,}")
        for ((rule, variants) in AdaptiveNotes.TEMPLATES) {
            for (v in variants) {
                val words = v.replace("{name}", "Priya").replace("{journey}", "Feel more energetic").replace("{smaller}", "drink one sip").replace("{title}", "Drink water").split(Regex("\\s+")).size
                assertTrue("$rule is $words words: $v", words <= 70)
                assertFalse("$rule shouts: $v", shout.containsMatchIn(v))
                assertFalse("$rule has an exclamation storm: $v", v.count { it == '!' } > 1)
            }
        }
    }

    @Test
    fun noNoteMakesAMedicalClaimOrShamesTheUser() {
        val banned = listOf("cure", "treat ", "treatment", "diagnos", "disease", "disorder", "lose weight", "burn fat", "calorie", "lazy", "failure", "should have", "you failed", "guilty", "streak is lost", "broke your")
        for ((rule, variants) in AdaptiveNotes.TEMPLATES) {
            for (v in variants) banned.forEach { assertFalse("$rule contains '$it': $v", v.lowercase().contains(it)) }
        }
    }

    @Test
    fun noNotePromisesFeaturesTheAppDoesNotHave() {
        val banned = listOf("freeze", "leaderboard", "friends", "share your", "wearable", "badge", "points")
        for ((rule, variants) in AdaptiveNotes.TEMPLATES) {
            for (v in variants) banned.forEach { assertFalse("$rule promises '$it': $v", v.lowercase().contains(it)) }
        }
    }
}

/** Reaches the private template filler through a note whose rule is known, without widening the production API. */
internal object AdaptiveNotesTestHooks {
    fun fill(template: String, ctx: CoachContext): String {
        val method = AdaptiveNotes::class.java.getDeclaredMethod("fill", String::class.java, CoachContext::class.java)
        method.isAccessible = true
        return method.invoke(AdaptiveNotes, template, ctx) as String
    }
}
