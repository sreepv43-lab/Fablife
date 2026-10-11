package io.github.sreepv43.fablife.coach

import java.time.LocalDate

enum class GoalKind { HABIT, ACTION, RITUAL }

/** Whether the step's letter hasn't been read yet or its goal is under way. */
enum class StepPhase { UNREAD, ACTIVE }

enum class StepTheme { FOOD, MOVEMENT, OTHER }

data class CoachStep(
    val title: String,
    val goalKind: GoalKind,
    val phase: StepPhase,
    val doneDays: Int,
    val targetDays: Int,
    /** Days since the step's challenge began, counting the first day. */
    val daysActive: Int,
    val smallerVersion: String?,
    val theme: StepTheme = StepTheme.OTHER,
)

/** Everything the on-device coach knows. All of it stays on the device. */
data class CoachContext(
    val day: LocalDate,
    val hour: Int,
    val name: String?,
    val journeyTitle: String,
    val step: CoachStep?,
    val journeyComplete: Boolean,
    /** Current streak of the user's fullest ritual. */
    val streak: Int,
    /** Days since any habit was last checked off; null if never. */
    val daysSinceCheckIn: Int?,
    val obstacles: Set<String>,
    val gentleEating: Boolean,
    val gentleMovement: Boolean,
)

enum class NoteRule {
    COMPLETE, COMEBACK, MILESTONE, STRUGGLING, NEW_LETTER, ACTION_PENDING, GENTLE_FOOD, MOVEMENT_ADAPTED,
    OBSTACLE_BUSY, OBSTACLE_MOTIVATION, OBSTACLE_FORGET, OBSTACLE_BIG, MORNING, AFTERNOON, EVENING,
}

data class CoachNote(val text: String, val rule: NoteRule)

/**
 * Picks one short, warm note from templates using the user's progress. Free, instant and private: no network, nothing
 * leaves the device. The choice is stable within a day (the date seeds it), so the note doesn't change on every look.
 */
object AdaptiveNotes {
    val MILESTONES = setOf(3, 7, 14, 21, 30, 60, 100)
    const val COMEBACK_AFTER_DAYS = 3

    const val OBSTACLE_BUSY = "I'm too busy"
    const val OBSTACLE_MOTIVATION = "I lose motivation"
    const val OBSTACLE_FORGET = "I forget"
    const val OBSTACLE_BIG = "I start too big"

    fun note(ctx: CoachContext): CoachNote {
        val rule = chooseRule(ctx)
        val variants = TEMPLATES.getValue(rule).let { v -> if (rule == NoteRule.STRUGGLING && ctx.step?.smallerVersion == null) STRUGGLING_PLAIN else v }
        val index = Math.floorMod(ctx.day.toEpochDay() + rule.ordinal, variants.size.toLong()).toInt()
        return CoachNote(fill(variants[index], ctx), rule)
    }

    internal fun chooseRule(ctx: CoachContext): NoteRule {
        val step = ctx.step
        if (ctx.journeyComplete) return NoteRule.COMPLETE
        if (ctx.daysSinceCheckIn != null && ctx.daysSinceCheckIn >= COMEBACK_AFTER_DAYS) return NoteRule.COMEBACK
        if (ctx.streak in MILESTONES) return NoteRule.MILESTONE
        if (step != null) {
            if (step.goalKind == GoalKind.HABIT && step.phase == StepPhase.ACTIVE && struggling(step)) return NoteRule.STRUGGLING
            if (step.phase == StepPhase.UNREAD) return NoteRule.NEW_LETTER
            if (step.goalKind == GoalKind.ACTION && step.phase == StepPhase.ACTIVE) return NoteRule.ACTION_PENDING
            if (step.theme == StepTheme.FOOD && ctx.gentleEating) return NoteRule.GENTLE_FOOD
            if (step.theme == StepTheme.MOVEMENT && ctx.gentleMovement) return NoteRule.MOVEMENT_ADAPTED
        }
        val obstacles = listOfNotNull(
            NoteRule.OBSTACLE_BUSY.takeIf { OBSTACLE_BUSY in ctx.obstacles },
            NoteRule.OBSTACLE_MOTIVATION.takeIf { OBSTACLE_MOTIVATION in ctx.obstacles },
            NoteRule.OBSTACLE_FORGET.takeIf { OBSTACLE_FORGET in ctx.obstacles },
            NoteRule.OBSTACLE_BIG.takeIf { OBSTACLE_BIG in ctx.obstacles },
        )
        // Obstacle tips and the time-of-day note take turns by date, so the card doesn't always say the same thing.
        if (obstacles.isNotEmpty() && ctx.day.toEpochDay() % 2 == 0L) return obstacles[Math.floorMod(ctx.day.toEpochDay() / 2, obstacles.size.toLong()).toInt()]
        return when (ctx.hour) {
            in 4..11 -> NoteRule.MORNING
            in 12..16 -> NoteRule.AFTERNOON
            else -> NoteRule.EVENING
        }
    }

    /** Three or more days into a habit challenge with little to show for it. */
    private fun struggling(step: CoachStep): Boolean =
        (step.daysActive >= 3 && step.doneDays == 0) || (step.daysActive >= 5 && step.doneDays * 2 < step.targetDays)

    private fun fill(template: String, ctx: CoachContext): String {
        val name = ctx.name?.trim().orEmpty()
        var text = template
        text = if (name.isNotEmpty()) text.replace("{name}", name) else text.replace("{name}, ", "").replace(", {name}", "").replace("{name}", "friend")
        text = text.replace("{journey}", ctx.journeyTitle)
            .replace("{streak}", ctx.streak.toString())
            .replace("{smaller}", ctx.step?.smallerVersion.orEmpty().trimEnd('.'))
            .replace("{title}", ctx.step?.title.orEmpty())
        return text.replaceFirstChar { it.uppercase() }
    }

    private val STRUGGLING_PLAIN = listOf(
        "If this step feels hard, shrink it. Make it so small it's almost silly, then build from there.",
        "This one is taking a while, and that's okay. Try doing only the first minute of it today.",
        "Hard weeks happen. Choose the easiest version of this habit and let that be enough for today.",
    )

    internal val TEMPLATES: Map<NoteRule, List<String>> = mapOf(
        NoteRule.COMPLETE to listOf(
            "{name}, you finished {journey}. That took real, steady effort. Keep the habits that fit your life, and pick your next journey whenever you're ready.",
            "You've completed {journey}. Notice what has changed, even a little. The habits stay in your rituals, so you can keep them going at your own pace.",
            "{name}, {journey} is complete. Be proud of how you showed up. When you want a fresh focus, the journey list has more to explore.",
        ),
        NoteRule.COMEBACK to listOf(
            "{name}, welcome back. A few quiet days happen to everyone. Pick the smallest step on your list and do just that today.",
            "No catching up needed. Start again with one small habit today and let the rest follow when it's ready.",
            "Missing a few days doesn't undo what you've built. Do the easiest thing on your list now, even a tiny version of it.",
            "{name}, it's good to see you. Restart gently: one small step is enough for today.",
        ),
        NoteRule.MILESTONE to listOf(
            "{streak} days in a row, {name}. That's real consistency. Notice how it feels, and keep today's step small and steady.",
            "A {streak}-day streak! Habits grow from repetition like this. Enjoy it, and remember that a missed day is only a pause.",
            "{streak} days of showing up. Be kind to yourself and keep going at the same easy pace.",
        ),
        NoteRule.STRUGGLING to listOf(
            "This one is taking longer, and that's fine. Try the smaller version: {smaller}. Doing less still counts.",
            "If this step feels hard, shrink it to the smaller version: {smaller}. A small yes beats a big maybe.",
            "Hard weeks happen. Today, just do the smaller version: {smaller}. That is enough.",
        ),
        NoteRule.NEW_LETTER to listOf(
            "A new letter from your coach is waiting. It takes a couple of minutes to read and explains exactly what to do next.",
            "Your next step is ready. Read the letter first, then choose a time today that fits.",
            "There's a new letter for you. Read it when you have a calm minute.",
        ),
        NoteRule.ACTION_PENDING to listOf(
            "Today's step is a one-time job: {title}. When it's done, open the letter and tap Mark as done.",
            "One thing to do for this step: {title}. It only needs doing once, then you can tap Mark as done.",
            "Nothing to repeat this time. Do {title}, then mark it done in the letter.",
        ),
        NoteRule.GENTLE_FOOD to listOf(
            "Your meal ideas are set to be gentle on the stomach. Go slowly and listen to your body. If symptoms keep bothering you, please talk to a doctor.",
            "Your food suggestions stay gentle on your stomach. If something doesn't sit well, skip it and try another.",
            "Eat what feels comfortable. Small portions of plain, soft foods are a fine place to start.",
        ),
        NoteRule.MOVEMENT_ADAPTED to listOf(
            "Your moves are chosen to suit you, with no jumping. Go at your own pace and stop if anything hurts.",
            "Everything you're shown fits your movement settings. Gentle counts. A few minutes of moving is a win.",
            "Move in the way that feels good today. Skipping a move or using the easier version is always okay.",
        ),
        NoteRule.OBSTACLE_BUSY to listOf(
            "Short on time? Tie your habit to something you already do, like brushing your teeth, and keep it to a minute.",
            "Busy days still have a minute in them. Do the smallest version and call it a win.",
            "You don't need a free hour. Pick one moment in your day and put the habit right after it.",
        ),
        NoteRule.OBSTACLE_MOTIVATION to listOf(
            "Motivation comes and goes, so lean on routine. Do the first tiny bit and let the rest follow.",
            "On low-energy days, do the two-minute version. Showing up matters more than doing it perfectly.",
            "You don't have to feel like it. Start for one minute, then notice how you feel.",
        ),
        NoteRule.OBSTACLE_FORGET to listOf(
            "Forgetting is normal. Put the habit right after something you never miss, and set a reminder in Settings.",
            "Try a visible cue: leave your water bottle or trainers where you'll see them.",
            "A reminder at the right time helps. You can set one for each ritual in Settings.",
        ),
        NoteRule.OBSTACLE_BIG to listOf(
            "Starting small is the clever move. If it feels like a lot, halve it, then halve it again.",
            "Make the habit so easy you can't say no. You can always do more once you're moving.",
            "Big plans can wait. Today, do the smallest version and stop there if you like.",
        ),
        NoteRule.MORNING to listOf(
            "Good morning. Start with the first small step of your morning ritual and let the day build from there.",
            "A fresh day. One small thing done early makes the rest feel easier.",
            "Morning is a good time to begin. Pick one step and do it before the day gets busy.",
        ),
        NoteRule.AFTERNOON to listOf(
            "Halfway through the day. A short pause for your afternoon ritual can help you reset.",
            "If the morning didn't go to plan, the afternoon is a fresh start. One small step is enough.",
            "Take a minute for yourself this afternoon. Stand up, stretch, drink some water.",
        ),
        NoteRule.EVENING to listOf(
            "As the day winds down, check in with your evening ritual. A calm routine is a kind way to end the day.",
            "Look back at one thing that went well today, however small, then finish your evening steps.",
            "Evening is for slowing down. Do your last small step, then rest. You did enough today.",
        ),
    )
}
