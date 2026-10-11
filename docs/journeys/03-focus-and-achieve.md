# Journey 3 — Focus & Achieve

**Journey id:** `focus` · **Rituals built:** Morning (Top 3) and Afternoon (focus ritual) · **Length:** 8 steps, about 4 to 6 weeks
**Subtitle:** Do what matters, with less overwhelm

This document is the source content for the third journey, in the format defined in [DESIGN_PHASE2.md §2](../DESIGN_PHASE2.md). It is parsed by `tools/journey_md.py` and checked by `tools/validate_content.py`.

```json meta
{"id": "focus", "goalKey": "focus", "order": 3, "title": "Focus & Achieve",
 "subtitle": "Do what matters, with less overwhelm", "emoji": "🎯", "ritualFocus": "AFTERNOON"}
```

## Journey Overview

| # | Step | Goal type | Habit / action | Target |
|---|---|---|---|---|
| — | Intro letter: *Focus Is a Set of Conditions* | — | — | — |
| 1 | Plan Your Top 3 | Habit | Choose your Top 3 (Morning, tool: Top 3) | 3 days in 5 |
| 2 | Your First Focus Block | Habit | One 25-minute focus block (Afternoon, tool: focus timer) | 3 days in 5 |
| 3 | Set Up Your Space | One-time action | Distracting notifications muted, phone face down, one task in front of you | Once |
| 4 | Move Between Blocks | Habit | A movement break (Afternoon, tool: flexibility routine) | 4 days in 6 |
| 5 | A Three-Minute Reset | Habit | Focus reset (Afternoon, tool: guided session) | 3 days in 5 |
| 6 | The Shutdown Note | Habit | Write tomorrow's first task (Afternoon, tool: reflection journal) | 3 days in 5 |
| 7 | Your Weekly Review | One-time action | Look back, let go, choose ahead | Once |
| 8 | The Focused Afternoon | Ritual | Complete the full afternoon ritual | 4 days in 7 |
| — | Outro letter: *You Built a Rhythm* | — | — | — |

Supporting letters: *Missed a Few Days?* and *Welcome Back*.

---

## Intro Letter — Focus Is a Set of Conditions

> Dear {name},
>
> It's mid-afternoon. You've been busy since morning, yet when you look back it's hard to say what you actually finished. A dozen things are half open, on your screen or in your head, your phone has buzzed more times than you can count, and the one thing that mattered is still waiting.
>
> If that sounds familiar, you're in very good company. Studies of modern work suggest that many of us are interrupted or switch tasks remarkably often, and that getting back into a task afterwards takes real time and effort. Your attention isn't broken. It's working hard in a world designed to pull it in a hundred directions. Whether your work is a job, studying, running a home, caring for someone or a project of your own, the pull is much the same.
>
> The encouraging part is that, for many people, focus seems to respond to conditions as much as to willpower. A clear plan, a quiet corner, short bursts of work with proper breaks, a little movement, a moment to reset and a way to put the day down at the end all help. None of them is complicated. Each one is small.
>
> Over the next few weeks we'll build a simple rhythm, one habit at a time:
>
> - **A morning Top 3**, so you decide what matters before the day decides for you.
> - **An afternoon ritual**: a focus block, a movement break, a three-minute reset and a shutdown note, supported by a distraction-free space and a weekly review.
>
> This journey isn't about squeezing more out of yourself. There's no hustle here, and rest is part of the plan. It's about spending your best attention on what truly matters to you, and feeling calmer while you do it.
>
> A few honest notes. Techniques like focus timers and short breaks help many people, but no method works for everyone.
>
> - **Shifts:** if you work shifts, "afternoon" simply means your working stretch. In **Settings → Rituals & reminders**, turn on the Afternoon reminder and set its time to match.
> - **Children:** if you're caring for children, your focus windows may be ten minutes long, and that counts.
> - **Attention differences:** if your attention works differently, for example with ADHD or other attention differences, shorter or longer blocks may suit you better. You can edit the focus and break lengths in the timer, and every habit has a smaller version.
> - **Support:** if trouble concentrating is persistent, or is affecting your daily life, work or mood, please talk to a doctor, a pharmacist, a community health worker or a counsellor, whoever you can reach. Poor sleep, stress and health conditions can all play a part, and you deserve proper support.
>
> Here's what I ask of you:
>
> - **Start small.** Smaller than you think you need to.
> - **Adapt freely.** Change any length, time or tool so it fits your real day.
> - **Be kind to yourself.** A scattered day tells you what to adjust, not what's wrong with you.
>
> Your first step takes about two minutes at the start of each day. Let's begin.
>
> With you on the journey,
> Your Fab Life coach

**Button:** *Let's begin*

---

## Step 1 — Plan Your Top 3

```json step
{
  "id": "focus.top3",
  "goal": {"type": "habit",
           "habit": {"id": "morning_top3", "title": "Plan your Top 3", "description": "Choose the three things that matter most today", "icon": "plan",
                     "ritual": "MORNING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Name just one thing that would make today a good day",
                     "tool": {"type": "TOP3"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Plan your Top 3\" to my morning",
  "tips": [
    "Write your Top 3 before you open email, social media or the group chat.",
    "Start each task with a verb, like \"open\", \"call\" or \"draft\". It's easier to begin.",
    "Not sure what matters most? Ask which task you'd regret leaving until tomorrow.",
    "Short on time? One task is a perfectly good Top 3."
  ],
  "completionTitle": "Your mornings have a plan! 📝",
  "completionMessage": "Three days of deciding what matters first. Next, let's give your important work a protected stretch of time.",
  "gentleNote": null
}
```

### Letter — Three Things, Not Thirty

> Dear {name},
>
> Picture the first thing you do each morning: you pick up your phone, or open your books, and see what's waiting. Often the problem isn't how hard we work. It's that the day was chosen for us by whatever arrived first: the inbox, the group chat, the loudest request.
>
> Your mind can only hold a handful of things at once, and an unfinished to-do list can hover in the background. The work of psychologist Peter Gollwitzer on "implementation intentions" suggests that deciding in advance what you'll do makes people more likely to follow through. And a study by E. J. Masicampo and Roy Baumeister found that making a specific plan for an unfinished goal reduced how much it kept intruding on people's thoughts. Writing it down lets your mind put it down.
>
> So our first habit is tiny: **choose your Top 3 each morning.** On nights or split shifts, your morning is whenever your day starts.
>
> Three is deliberate. It's few enough to feel doable and enough to make real progress. Your Top 3 aren't everything you could do. They're the things that, if done, would make today feel like a good day.
>
> **How to choose well**
>
> - Start with the one thing that matters most, then add two smaller ones. Mix kinds of tasks: "send the invoice", "revise one chapter", "call Amma or Abuela" and "pick up Grandpa's prescription" all belong.
> - Make each task small enough to finish today. "Finish the report" becomes "write the first page".
> - Leave out the automatic stuff. Brushing your teeth doesn't need a slot.
> - Not everything gets in, and that's the point. What isn't on the list isn't failed, just not today.
> - Tap **Plan** on the habit to open the Top 3 planner. Type three tasks and tick them off during the day. If yesterday's tasks weren't finished, it offers to carry them over. Prefer pen and paper, or your keyboard's microphone button? That counts too. Just tick the habit on Today once you've written your list.
>
> Finish all three and you've had a great day. Finish one and you've still had a real win. Either way, you knew what mattered.
>
> **Stack it:** *"After my first tea, coffee or glass of water, I tap Plan and write my Top 3."*
>
> Your challenge: **write your Top 3 on 3 of the next 5 days.**
>
> Three small things. You've got this.
> Your Fab Life coach

---

## Step 2 — Your First Focus Block

```json step
{
  "id": "focus.focus_block",
  "goal": {"type": "habit",
           "habit": {"id": "focus_block", "title": "Do a focus block", "description": "25 minutes on one task, then a 5-minute break", "icon": "timer",
                     "ritual": "AFTERNOON", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Focus on one task for just 10 minutes, then decide whether to stop or carry on",
                     "tool": {"type": "FOCUS_TIMER"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Focus block\" to my afternoon",
  "tips": [
    "Write the task on a sticky note and keep it in view until the timer ends.",
    "Say it aloud before you start: \"For 25 minutes, I'm only doing this.\"",
    "Fill your water bottle or cup before you press Focus, so nothing pulls you away halfway."
  ],
  "completionTitle": "Focus block unlocked! ⏱️",
  "completionMessage": "Three days of giving one task your full attention. Next, we'll make those blocks easier to protect.",
  "gentleNote": "For a sensitive stomach, keep break snacks soft and simple: a ripe banana, plain toast or a little plain yoghurt (curd)."
}
```

### Letter — Twenty-Five Minutes, One Thing

> Dear {name},
>
> You sit down to write one email, revise a chapter or sketch a page. Forty minutes later you've checked three apps, refilled your tea twice, and the page is still blank. That isn't a flaw in you. Attention drifts, especially when a task feels big or unclear.
>
> One popular answer is the Pomodoro technique, created by Francesco Cirillo in the late 1980s: work on a single task for about 25 minutes, then take a break of about 5. Some studies suggest that brief breaks can help people keep their attention steady over long tasks, and a clear start and finish can make a daunting job easier to begin, because you're only agreeing to 25 minutes.
>
> Treat 25 minutes as a starting point, not a rule. If it feels wrong, edit it in the timer: 10 minutes of focus and a 3-minute break, or 45 and 10, may fit you better. The number matters less than finding a rhythm you can keep. If your sharpest hours are in the morning, do your block then. The habit is the same wherever it lands in your day.
>
> So here is your new habit: **do one focus block, 25 minutes on one task, then a 5-minute break.**
>
> **Making it work**
>
> - Tap **Focus** on the habit to open the focus timer. It counts the minutes, signals the end and lets you edit the lengths. It can also play a quiet generated focus sound if you like background sound.
> - Choose the task before you start. Anything from your Top 3 is a great candidate.
> - If a stray thought pops up ("buy rice", "reply to Sam", "call Ravi back"), jot it on paper and go straight back.
> - Use the break to actually rest: shift position, sip water or tea, look out of the window. If you like a snack, choose what suits your stomach, budget, allergies and health needs, such as a ripe banana, roasted chickpeas (chana), plain toast or a little plain yoghurt or curd. You don't have to eat at every break, for example if you're fasting. If a doctor or dietitian has given you a meal plan, or you take medicines that need food at set times, keep to that.
> - If the timer rings and you're in the flow, you can carry on. The break will wait.
>
> *If long stretches at a screen leave you with sore eyes, headaches or aching shoulders and wrists, an optician or doctor can help.*
>
> **Stack it:** *"After lunch or my midday break, I pick one task from my Top 3 and tap Focus."*
>
> Your challenge: **complete one focus block on 3 of the next 5 days.**
>
> One thing at a time. That's plenty.
> Your Fab Life coach

---

## Step 3 — Set Up Your Space

```json step
{
  "id": "focus.clear_space",
  "goal": {"type": "action",
           "title": "Set up a distraction-free spot",
           "description": "Distracting notifications muted, phone face down, one task in front of you",
           "shortcuts": [{"label": "Open the focus timer", "target": "focus_timer"}]},
  "buttonLabel": "I'll set up my space",
  "tips": [],
  "completionTitle": "Your space is ready. 🛠️",
  "completionMessage": "Fewer interruptions, less effort. Now let's look after the body that does the focusing.",
  "gentleNote": null
}
```

### Letter — Make Distraction Harder

> Dear {name},
>
> Think about how often your hand reaches for your phone before you've decided to. Researchers who study interruptions, such as Gloria Mark at the University of California, Irvine, have found that people at work switch tasks frequently, and that it can take many minutes, sometimes more than twenty, to settle back into what they were doing. One well-known study also suggested that simply having your phone nearby can take up a little attention, even when it's silent, although later studies have found mixed results. Either way, it's easier to change your surroundings than to fight distraction with willpower.
>
> So today there's no habit to repeat. There's **one small action: set up a distraction-free spot.** Do it once, and every focus block gets easier.
>
> **Your five-part setup**
>
> 1. **Phone face down, timer running.** Start the focus timer and leave it open on screen, then lay the phone face down beside you or at arm's length. It chimes and vibrates at the end, so keep the volume up or vibration on, because silent mode can mute the chime. Prefer the phone out of the room? A kitchen timer, wall clock or watch does the job for that block.
> 2. **Distracting notifications muted.** Mute banners, badges and sounds for social media, shopping and news. Use Do Not Disturb if it suits you, and allow calls from the few people who must reach you. It may mute the timer's chime too, so test it once. Not sure how? Search "Do Not Disturb" in your phone's Settings, or ask someone to set it up for you.
> 3. **A clear space.** Put away anything the task doesn't need: extra tabs and apps, loose papers, the TV.
> 4. **A ready spot.** A glass of water, a pen and a scrap of paper for stray thoughts. Your spot might be a study corner, a corner of the kitchen table, a library desk or a quiet bench. Earplugs or headphones help if noise is the problem. Keep the volume low and comfortable, and leave one ear free if you need to hear a child, a door or traffic. The focus timer can also play a quiet generated sound to soften the noise.
> 5. **A heads-up.** If you share a room, say "I'm focusing for 25 minutes" or leave a note.
>
> **If your phone carries health or safety alerts (a glucose monitor, medication alarms, an emergency contact), or you're a parent, a carer or on call:** keep those alerts and calls on and the phone within hearing. Mute only the apps that distract you.
>
> You don't need a perfect office. If your home is busy, even a face-down phone is a big step, and a small step done is worth more than a perfect plan.
>
> The shortcut below opens the focus timer, so it's ready when you are. When your space is set up, come back and tap **Mark as done**.
>
> Your space, your rules,
> Your Fab Life coach

---

## Step 4 — Move Between Blocks

```json step
{
  "id": "focus.move_breaks",
  "goal": {"type": "habit",
           "habit": {"id": "hourly_movement_break", "title": "Take a movement break", "description": "A few minutes of gentle movement (stretch, walk or seated moves) at least once in your afternoon", "icon": "stretch",
                     "ritual": "AFTERNOON", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Roll your shoulders three times, standing or seated, or do three stretches from your chair",
                     "tool": {"type": "ROUTINE", "ref": "FLEXIBILITY"}},
           "targetDays": 4, "windowDays": 6},
  "buttonLabel": "Add \"Movement break\" to my afternoon",
  "tips": [
    "Keep your water bottle across the room, so refilling it becomes your movement.",
    "Seated and gentle options are always fine. Pick what suits your body today.",
    "No equipment needed. A wall and a chair are plenty."
  ],
  "completionTitle": "Moving on! 🚶",
  "completionMessage": "Four days of moving between blocks. Notice how your body and mind feel afterwards.",
  "gentleNote": null
}
```

### Letter — Your Body Is Part of Your Focus

> Dear {name},
>
> By mid-afternoon many of us are slumped over a laptop, a textbook or a workbench, neck tight, eyes tired, mind a little foggy. The instinct is to push on. Meanwhile your body is quietly asking to move.
>
> Studies of sedentary time suggest that long, unbroken sitting isn't ideal, and that breaking it up with a few minutes of light movement can help people feel more alert and less stiff. The effects are modest, but they cost almost nothing, and many people simply feel better for it.
>
> Your new habit: **take a movement break, a few minutes of gentle movement at least once in your afternoon, and more often if it helps.** One break a day counts toward your challenge.
>
> It doesn't need to look like exercise. Pick what suits your body and your space:
>
> | If you are… | Try… |
> |---|---|
> | At a desk or on a sofa | Seated march, shoulder rolls, gentle neck turns, wrist circles, a seated twist |
> | Using a wheelchair, or unable to stand | Arm circles, shoulder blade squeezes, chest opening, wrist and finger stretches |
> | Standing, low-impact | Slow side bends, hip circles, calf raises holding a wall, a stroll around the room |
> | Short of space | Walk to refill your water, or stretch beside your chair |
>
> Tap **Start** on the habit to follow a short flexibility routine picked for your level and needs, one move at a time, with an easier version for each move.
>
> **Making it stick**
>
> - Move gently. A stretch should feel mild, never painful.
> - Pair it with a breath of fresh air at a window, if you can.
> - Two minutes is enough. Any movement beats none.
>
> *If you have pain, an injury, a health condition, are pregnant, or haven't been active for a while, check with a doctor or physiotherapist about what movement is right for you. Stop if anything hurts or you feel dizzy or unwell, and stand up slowly.*
>
> **Stack it:** *"When my focus timer signals a break, I tap Start and stretch."*
>
> Your challenge: **take a movement break on 4 of the next 6 days.**
>
> Let's get moving, gently.
> Your Fab Life coach

---

## Step 5 — A Three-Minute Reset

```json step
{
  "id": "focus.reset",
  "goal": {"type": "habit",
           "habit": {"id": "afternoon_focus_reset", "title": "Do a 3-minute focus reset", "description": "A short guided pause to gather your attention", "icon": "focus",
                     "ritual": "AFTERNOON", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Take three slow breaths while resting your eyes on one spot",
                     "tool": {"type": "GUIDED", "ref": "focus_reset"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Focus reset\" to my afternoon",
  "tips": [
    "Sit back from your screen for it, so your eyes get a rest too.",
    "No headphones? Follow the on-screen captions quietly.",
    "Your mind will wander. Noticing and coming back is the practice, not a mistake."
  ],
  "completionTitle": "Reset button found! 🎧",
  "completionMessage": "Three days of gathering your attention on purpose. You now have a quick way to come back to the task at hand.",
  "gentleNote": null
}
```

### Letter — Come Back to Now

> Dear {name},
>
> You're reading the same paragraph for the third time. Your eyes are moving, but your mind is somewhere else: tomorrow's meeting, an old conversation, what's for dinner. Welcome to being human. A well-known study by Matthew Killingsworth and Daniel Gilbert, which checked in with thousands of people through their day, found that minds wandered for a large share of waking hours, nearly half in that sample.
>
> Wandering isn't the problem. Staying lost in it without noticing is. Studies of mindfulness suggest that short pauses where you notice your mind has drifted and gently bring it back may help some people feel more settled before a task, though the evidence is mixed and the effects tend to be modest. It's a small, free skill, and it gets easier with practice.
>
> Your new habit: **a 3-minute focus reset.**
>
> Tap **Listen** on the habit to play the Focus reset. It's read aloud with on-screen captions, so you can follow it silently in a shared space. You settle into a comfortable position (sitting is fine, eyes closed or lowered), take three slow breaths, picture the open tabs in your mind quietly closing, and choose the one small thing to do next. Noticing the clutter, letting it go quiet and choosing where to go is the whole skill. It works well right before a focus block, and there's nothing to get right.
>
> **When to use it**
>
> - Before a focus block, to arrive.
> - After a stressful message or meeting, to let go.
> - When the afternoon slump hits and you're about to reach for your phone.
>
> **Prefer counting breaths?** Open **Explore → Mind** for a breathing pacer. Some people like box breathing (4-4-4-4) for steadying themselves. The holds are optional, so skip or shorten them if they feel uncomfortable, or choose Long exhale or Coherent breathing, which have none, and never strain.
>
> *The reset is for relaxation and attention, not treatment. Use it somewhere safe, never while driving, cycling or operating machinery. Slow breathing can make some people feel light-headed, so if that happens, breathe normally. If you have a breathing or heart condition, or quiet sitting brings up anxious or upsetting feelings, talk to your doctor or a mental health professional first.*
>
> **Stack it:** *"When I notice my mind is scattered, I tap Listen for a 3-minute reset."*
>
> Your challenge: **do a 3-minute focus reset on 3 of the next 5 days.**
>
> Arriving back, one breath at a time.
> Your Fab Life coach

---

## Step 6 — The Shutdown Note

```json step
{
  "id": "focus.shutdown",
  "goal": {"type": "habit",
           "habit": {"id": "shutdown_note", "title": "Write a shutdown note", "description": "Close your day by writing tomorrow's first task", "icon": "journal",
                     "ritual": "AFTERNOON", "order": 4, "durationSeconds": null,
                     "smallerVersion": "Write one line: tomorrow's first task",
                     "tool": {"type": "JOURNAL", "ref": "REFLECTION"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Shutdown note\" to my afternoon",
  "tips": [
    "Open the journal a few minutes before you finish, so the note is already waiting.",
    "Set out what tomorrow's first task needs, such as the textbook, the file or the ingredients.",
    "Pick your closing signal first, such as shutting the laptop or putting the books away, then write the note just before it."
  ],
  "completionTitle": "A day well closed! ✍️",
  "completionMessage": "Three days of putting work down on purpose. Tomorrow's first task is already waiting, so your mind can rest.",
  "gentleNote": null
}
```

### Letter — Close the Day on Purpose

> Dear {name},
>
> You close the laptop, put down the books or switch off the machine, but the work doesn't close with you. At dinner you're mentally replying to messages. At midnight you remember what you forgot. For many of us, the working day never quite ends. Not in paid work? Your work is whatever you've been giving your attention to: study, housework, caring, a craft.
>
> Psychologists who study "psychological detachment" have found that people who mentally step away from work in their free time tend to feel more recovered. Writing things down seems to help. In the Masicampo and Baumeister study we met earlier, making a plan for unfinished tasks quietened the mind's nagging. Your mind may find it easier to relax once it trusts that tomorrow's start is safely on paper.
>
> Your new habit: **write a shutdown note at the end of your work, with tomorrow's first task.** It's about handing tomorrow's start to your future self. If worries keep you awake at night, the Sleep Better and Calm Mind journeys are there for that.
>
> It takes two minutes. Three lines are plenty:
>
> 1. One thing that went okay today. It doesn't need to be big.
> 2. Anything unfinished, parked on paper so it doesn't have to live in your head.
> 3. **Tomorrow's first task**, small and specific: "Open the budget sheet and fill in column one," or "Lay out my chapter notes and read the first page."
>
> Tap **Write** on the habit to open the reflection journal. Its prompt changes from day to day, so you can answer it or simply type your shutdown note in the box. A paper note works too: tick the habit on Today once it's written, and keep the note where you'll see it tomorrow. Journal entries stay on your phone and you can delete them whenever you like.
>
> Tomorrow morning, before you tap **Plan**, glance at last night's note (in your journal under **Explore → Mind**, or on your paper) and make that first task one of your Top 3.
>
> **Tips**
>
> - Choose a closing signal for the end of your working stretch, wherever it falls: shut the laptop, put the books away, wipe the table, switch off the kitchen light, or say "that's enough for today".
> - Then do something unrelated: stretch at a window, play some music, chat with a friend, tidy one shelf.
>
> *A journal is a tool, not therapy. If worries feel constant, or stress or low mood has been affecting your sleep or daily life for weeks, please talk to a doctor or a counsellor. If you ever feel unsafe or think about harming yourself, please contact your local emergency services or a crisis line right away, and reach out to someone you trust.*
>
> **Stack it:** *"After my last movement break of the working day, I tap Write and close the day."*
>
> Your challenge: **write a shutdown note on 3 of the next 5 days.**
>
> That's enough for today.
> Your Fab Life coach

---

## Step 7 — Your Weekly Review

```json step
{
  "id": "focus.weekly_review",
  "goal": {"type": "action",
           "title": "Hold your first weekly review",
           "description": "Ten minutes to look back, let go of one thing and choose what matters next week",
           "shortcuts": [{"label": "Write a reflection", "target": "journal:REFLECTION"},
                         {"label": "Plan with Top 3", "target": "top3"}]},
  "buttonLabel": "Let's do my weekly review",
  "tips": [],
  "completionTitle": "Week reviewed! 🗓️",
  "completionMessage": "You looked back with kindness and chose ahead with care. One final challenge brings it all together.",
  "gentleNote": null
}
```

### Letter — Look Back, Choose Ahead

> Dear {name},
>
> Weeks blur easily. Monday's plan is forgotten by Thursday, and by Sunday evening you can mostly remember what you didn't get done. That's a hard way to judge a week.
>
> Looking back on purpose changes that. Reflection seems to support learning: in one study of workplace trainees, people who spent fifteen minutes at the end of each day thinking about what they'd learned went on to perform better than those who used that time to practise. A short, regular look back helps you see what's working, so you can keep it.
>
> Today's step isn't a habit to repeat. It's **one action: hold your first weekly review.** It takes about ten minutes.
>
> Choose a time that marks the end of your week: Friday afternoon, Sunday evening, or the day before your days off if you work shifts. Then ask three questions, and keep each answer short:
>
> 1. **What went well?** Name two or three wins, including small ones. Glance at your Progress tab and this week's journal entries for clues.
> 2. **What drained me, or what would I change?** Be curious, not critical. Maybe errands or meetings ran long, or one app kept pulling you in. Pick one thing to simplify, shorten, move or ask for help with. If it isn't yours to change, just notice it and let it be information.
> 3. **What matters most next week?** Pick one to three things, and jot them in your journal. When the week begins, add the first to your Top 3.
>
> The buttons below open the reflection journal, for your answers, and the Top 3 planner, for when you're ready to plan a day.
>
> No review is perfect, and there are no marks. A messy week simply shows you what to adjust. If you notice you're feeling overwhelmed most weeks, that's worth sharing with someone you trust.
>
> When you've done your review, come back and tap **Mark as done**.
>
> Looking back, moving forward,
> Your Fab Life coach

---

## Step 8 — The Focused Afternoon

```json step
{
  "id": "focus.afternoon_ritual",
  "goal": {"type": "ritual", "ritual": "AFTERNOON", "targetDays": 4, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": [
    "Put a sticky note saying focus, move, reset, note where you'll see it at your usual afternoon time.",
    "Charge your phone and fill your water bottle before you begin.",
    "Tie the start to something you already do, like making tea, finishing lunch or the end of the school run."
  ],
  "completionTitle": "You did it! 🏆",
  "completionMessage": "Your focused afternoon is complete. Read your final letter.",
  "gentleNote": null
}
```

### Letter — Putting Your Rhythm Together

> Dear {name},
>
> Here it is: the final challenge, and the one that ties everything together.
>
> Look at what you've put together. In the morning you decide what matters with your Top 3. In the afternoon you now have a clear space, a focus block, a movement break, a three-minute reset and a shutdown note. Each piece was small, and you've already practised every one of them.
>
> Your challenge: **complete your whole afternoon ritual on 4 of the next 7 days.** Days off and non-working days are fine to skip. Four afternoons in a week is a real success.
>
> A focus rhythm is a pattern, not a streak, and patterns take time. Research from University College London found that new behaviours took anywhere from a few weeks to well over six months to feel automatic, around two months on average. So treat this week as a rehearsal of your rhythm, not an exam.
>
> **A few things to keep in mind**
>
> - **Two ways to run it.** Tap *Start ritual* and the app walks you through your afternoon habits one at a time, in the order they're listed on Today. Or tick each habit on Today as it happens through your afternoon. Both count toward your challenge.
> - **Mind the size.** At full size the whole ritual takes about 35 to 40 minutes. With the smaller versions it's closer to 10 to 15.
> - **Shape it to your day.** In **Settings → Rituals & reminders**, turn on the Afternoon reminder and set its time for when you'd like to start. It's only a gentle nudge. If you run the ritual in one sitting, pick a time when you have the 35 to 40 minutes and the energy for it. If you'd rather tick habits as you go, tick the shutdown note whenever you close your day. If your day comes in short or broken windows, because of shifts or children for example, the smaller versions count just as much.
> - **Keep your Top 3 going.** It isn't part of this afternoon ritual, but it feeds it. A clear morning makes the afternoon easier.
> - **Bad days happen.** A tiny ritual still counts: ten minutes on one task, three shoulder rolls, three slow breaths, one line of a shutdown note.
> - **Notice what changes.** At the end of the week, ask yourself: When did I feel most focused? What got in the way? What would I keep?
>
> Four afternoons out of seven. You've already done the hard part, which was starting.
>
> Cheering you on,
> Your Fab Life coach

---

## Outro Letter — You Built a Rhythm

> Dear {name},
>
> Take a moment to notice what you've done: **you completed the Focus & Achieve journey.**
>
> A few weeks ago you were juggling a long to-do list and a buzzing phone. Since then you've built a morning Top 3, a protected focus block, a calmer space, movement breaks, a three-minute reset, a shutdown note and a weekly review. None of those is dramatic. Together they form a way of working that's kinder to your attention and kinder to you.
>
> Here's the quiet lesson underneath: **your surroundings and habits can make focus easier, whatever your starting point.** Willpower was only ever part of it. A clear plan, fewer interruptions, real breaks and a way to finish the day all help, and you've shown you can create them.
>
> **What happens now?**
>
> Keep what works and let go of what doesn't. If the 25-minute block suits you, wonderful. If 45 minutes with a 10-minute break suits you better, that's just as valid. Nobody earns extra points for doing it exactly by the book. Your habits stay in the app, and your streaks keep counting.
>
> Some days will scatter anyway: illness, a poor night's sleep, a family emergency, a deadline that swallows everything. On those days, do the smaller version of one habit, or none at all, and begin again tomorrow. A messy day never erases the rhythm you've built.
>
> One last note. If you still find it hard to concentrate most days, or it's affecting your work, relationships or mood, please talk to a doctor or another health professional. Asking for support is nothing to be embarrassed about, and the habits you've built will be right here alongside it.
>
> When you feel ready, other journeys are waiting: more energy, better sleep, eating well, a calmer mind. Each pairs nicely with what you've built here, because energy, rest, food and calm all feed your focus too.
>
> For today, though, pause. Look at what you've finished. That counts.
>
> So proud of you,
> Your Fab Life coach

**Buttons:** *Celebrate 🎉* (replays celebration animation) · *See what's next*

---

## Supporting Letter — Missed a Few Days?

> Dear {name},
>
> A few days have slipped by since your last check-in, and I want you to know that **it's completely normal, and it's okay.**
>
> Focus habits are especially easy to lose, because the days that fall apart are often the busy, noisy ones. A deadline, a sick child, a night shift, a week of poor sleep: of course your routine wobbled. In a well-known study on habit formation, researchers found that missing a day here and there did not undo people's progress. What mattered was getting back to it.
>
> So let's restart gently. Which of these sounds most helpful?
>
> - **Keep going.** Nothing needs to change. Tomorrow is a fresh start.
> - **Make it smaller.** Try the smaller version of whichever habit you're on, such as a ten-minute focus block, a one-thing Top 3, three shoulder rolls or three slow breaths. A tiny habit you do beats a big one you skip.
> - **Change the timing.** Maybe your reminder sits at the wrong hour for your day. Change the reminder time to when your energy or schedule suits you better.
>
> One more gentle thought: a few quiet days is life happening, not a verdict on your focus.
>
> The goal isn't a perfect streak. It's showing up more often than not, and being kind to yourself when you don't.
>
> Here whenever you're ready,
> Your Fab Life coach

**Buttons:** *Keep going* · *Make it smaller* · *Change reminder time*

---

## Supporting Letter — Welcome Back

> Dear {name},
>
> Welcome back! I'm really glad you're here.
>
> Whatever kept you away, whether it was a busy stretch, a hard time or simply life, you don't need to explain and there's no reason to feel guilty. Opening this app again says something good: you still care about spending your attention on what matters to you.
>
> Here's what hasn't changed: **everything you've already built is still here.** Every step you completed and every habit you learned is part of you now. Your focus skills don't vanish after a break.
>
> We'll restart your current step from the beginning, so there's no pressure to catch up. Do the smaller version of that step. If you're not sure where to start, name one task for tomorrow and give it ten minutes, with the timer if you like. That's enough to begin again.
>
> Starting again isn't failing. Starting again *is* the habit.
>
> Glad to have you back,
> Your Fab Life coach

**Button:** *Let's start again*
