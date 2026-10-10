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
| 3 | Set Up Your Space | One-time action | Phone out of reach, notifications off | Once |
| 4 | Move Between Blocks | Habit | A movement break (Afternoon, tool: flexibility routine) | 4 days in 6 |
| 5 | A Three-Minute Reset | Habit | Focus reset (Afternoon, tool: guided session) | 3 days in 5 |
| 6 | The Shutdown Note | Habit | Write tomorrow's first task (Afternoon, tool: reflection journal) | 3 days in 5 |
| 7 | Your Weekly Review | One-time action | Look back, let go, choose ahead | Once |
| 8 | The Focused Afternoon | Ritual | Complete the full afternoon ritual | 5 days in 7 |
| — | Outro letter: *You Built a Rhythm* | — | — | — |

Supporting letters: *Missed a Few Days?* and *Welcome Back*.

---

## Intro Letter — Focus Is a Set of Conditions

> Dear {name},
>
> It's mid-afternoon. You've been busy since morning, yet when you look back it's hard to say what you actually finished. A dozen tabs are open, your phone has buzzed more times than you can count, and the one thing that mattered is still waiting.
>
> If that sounds familiar, you're in very good company. Research on modern work suggests that many of us are interrupted or switch tasks remarkably often, and that getting back into a task afterwards takes real time and effort. Your attention isn't broken. It's working hard in a world designed to pull it in a hundred directions.
>
> The encouraging part is that focus responds to conditions more than to willpower. A clear plan, a quiet corner, short bursts of work with proper breaks, a little movement, a moment to reset and a way to put the day down at the end all help. None of them is hard. Each one is small.
>
> Over the next few weeks we'll build a simple rhythm, one habit at a time:
>
> - **A morning Top 3**, so you decide what matters before the day decides for you.
> - **An afternoon ritual**: a focus block, a movement break, a three-minute reset and a shutdown note, supported by a distraction-free space and a weekly review.
>
> This journey isn't about squeezing more out of yourself. There's no hustle here, and rest is part of the plan. It's about spending your best attention on what truly matters to you, and feeling calmer while you do it.
>
> A few honest notes. Techniques like focus timers and short breaks help many people, but no method works for everyone. If you work shifts, "afternoon" simply means the middle of your working stretch. If you're caring for children, your focus windows may be ten minutes long, and that counts. If your attention works differently, for example with ADHD or other attention differences, shorter or longer blocks may suit you better, and you can adjust every length we use. And if trouble concentrating is persistent, or is affecting your daily life, work or mood, please talk to a doctor or another health professional. Poor sleep, stress and health conditions can all play a part, and you deserve proper support.
>
> Here's what I ask of you:
>
> - **Start small.** Smaller than you think you need to.
> - **Adapt freely.** Change any length, time or tool so it fits your real day.
> - **Be kind to yourself.** A scattered day is information, never a failure.
>
> Your first step takes about two minutes each morning. Let's begin.
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
    "Make each task small enough to finish today, such as \"write the first page\" instead of \"finish the report\".",
    "A personal or caring task can sit beside a work one. Your Top 3 is about your day, not just your job.",
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
> Ever finished a busy day and wondered, "What did I actually do?" Often the problem isn't how hard we worked. It's that the day was chosen for us by whatever arrived first: the inbox, the group chat, the loudest request.
>
> Your mind can only hold a handful of things at once, and an unfinished to-do list hovers in the background, nagging. Research on "implementation intentions" by psychologist Peter Gollwitzer suggests that deciding in advance what you'll do makes people more likely to follow through. And a study by E. J. Masicampo and Roy Baumeister found that making a specific plan for an unfinished goal reduced how much it kept intruding on people's thoughts. Writing it down lets your mind put it down.
>
> So our first habit is tiny: **choose your Top 3 each morning.**
>
> Three is deliberate. It's few enough to feel doable and enough to make real progress. Your Top 3 aren't everything you could do. They're the things that, if done, would make today feel like a good day.
>
> **How to choose well**
>
> - Start with the one thing that matters most, then add two smaller ones. Mix kinds of tasks: "send the invoice", "call Amma" and "cook a proper dinner" all belong.
> - Make each task small enough to finish today. "Finish the report" becomes "write the first page".
> - Leave out the automatic stuff. Brushing your teeth doesn't need a slot.
> - Not everything gets in, and that's the point. What isn't on the list isn't failed, just not today.
> - Tap **Plan** on the habit to open the Top 3 planner. Type three tasks and tick them off during the day. If yesterday's tasks weren't finished, it offers to carry them over.
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
    "Choose the task before you press Focus. Anything from your Top 3 is a great candidate.",
    "Keep a scrap of paper nearby to park stray thoughts, then go straight back to the task.",
    "Use the break to really rest: stand up, sip water and look into the distance instead of scrolling.",
    "Lengths too long or too short for you? Edit them in the timer."
  ],
  "completionTitle": "Focus block unlocked! ⏱️",
  "completionMessage": "Three days of giving one task your full attention. Next, we'll make those blocks easier to protect.",
  "gentleNote": null
}
```

### Letter — Twenty-Five Minutes, One Thing

> Dear {name},
>
> You sit down to write one email. Forty minutes later you've checked three apps, refilled your tea twice, and the email is still blank. That isn't a flaw in you. Attention drifts, especially when a task feels big or unclear.
>
> One popular answer is the Pomodoro technique, created by Francesco Cirillo in the late 1980s: work on a single task for about 25 minutes, then take a break of about 5. Research suggests that brief breaks can help people keep their attention steady over long tasks, and a clear start and finish can make a daunting job easier to begin, because you're only agreeing to 25 minutes.
>
> I want to be honest, though. This is a technique that helps many people, not a rule that fits everyone. If you work shifts, look after children, or your attention works differently (for example with ADHD), other lengths may suit you better, such as 10 minutes of focus and a 2-minute break, or 45 minutes and 10. The number matters less than finding a rhythm that works for you. If your sharpest hours are in the morning, do your block then. The habit is the same wherever it lands in your day.
>
> So here is your new habit: **do one focus block, 25 minutes on one task, then a 5-minute break.**
>
> **Making it work**
>
> - Tap **Focus** on the habit to open the focus timer. It counts the minutes, signals the end and lets you edit the lengths. It can also play a quiet generated focus sound if you like background sound.
> - Choose the task before you start. Anything from your Top 3 is a great candidate.
> - If a stray thought pops up ("buy rice", "reply to Ravi"), jot it on paper and go straight back.
> - Use the break to actually rest: stand up, sip water or tea, look out of the window. A ripe banana or a handful of nuts is fine too, or a little curd if your stomach prefers gentle foods.
> - If the timer rings and you're in the flow, you can carry on. The break will wait.
>
> *If long stretches at a screen leave you with sore eyes, headaches or aching shoulders and wrists, an optician or doctor can help.*
>
> **Stack it:** *"After lunch, I pick one task from my Top 3 and tap Focus."*
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
           "description": "Phone out of reach, notifications off, one task in front of you",
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
> 1. **Phone out of reach.** Put it in a bag, a drawer or another room, or at least face down at arm's length. If you're a parent, a carer or on call, let calls from a few important people through and silence the rest.
> 2. **Notifications off.** Mute banners, badges and sounds for social media, shopping and news. Use Do Not Disturb or flight mode during focus blocks.
> 3. **A clear screen.** Close extra tabs and apps. Keep only what the task needs.
> 4. **A ready space.** A glass of water, a pen and a scrap of paper for stray thoughts. Your space might be a corner of the dining table, a library desk or a quiet bench. Earplugs or headphones help if noise is the problem, and the focus timer can play a quiet generated sound to soften it.
> 5. **A heads-up.** If you share your space, say "I'm focusing for 25 minutes" or leave a note.
>
> You don't need a perfect office. If your home is busy, even phone-down and notifications-off is a big step, and a small step done is worth more than a perfect plan.
>
> The shortcut below opens the focus timer so it's ready when you are. When your space is set up, come back and tap **Mark as done**.
>
> Your space, your rules,
> Your Fab Life coach

---

## Step 4 — Move Between Blocks

```json step
{
  "id": "focus.move_breaks",
  "goal": {"type": "habit",
           "habit": {"id": "hourly_movement_break", "title": "Take a movement break", "description": "Stand, stretch or walk for a few minutes, about once an hour", "icon": "stretch",
                     "ritual": "AFTERNOON", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Stand up and roll your shoulders three times, or do three stretches from your chair",
                     "tool": {"type": "ROUTINE", "ref": "FLEXIBILITY"}},
           "targetDays": 4, "windowDays": 6},
  "buttonLabel": "Add \"Movement break\" to my afternoon",
  "tips": [
    "Let the end of a focus block be your cue to stand up.",
    "Seated and gentle options are always fine. Pick what suits your body today.",
    "Move gently. A stretch should feel mild, never painful.",
    "Two minutes is enough. Any movement beats none."
  ],
  "completionTitle": "Moving on! 🚶",
  "completionMessage": "Four days of getting up and moving between blocks. Notice how your body and mind feel afterwards.",
  "gentleNote": null
}
```

### Letter — Your Body Is Part of Your Focus

> Dear {name},
>
> By mid-afternoon many of us are slumped over a laptop, neck tight, eyes tired, mind a little foggy. The instinct is to push on. Meanwhile your body is quietly asking to move.
>
> Research suggests that long, unbroken sitting isn't ideal, and that breaking it up with a few minutes of light movement can help people feel more alert and may help with blood sugar after meals. The effects are modest, but they cost almost nothing, and many people simply feel better for it.
>
> Your new habit: **take a movement break, a few minutes of gentle movement about once an hour or between focus blocks.**
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
> - Let the end of a focus block be your cue.
> - Move gently. A stretch should feel mild, never painful.
> - Pair it with water, or a breath of fresh air at a window.
> - Two minutes is enough. Any movement beats none.
>
> *If you have pain, an injury, a health condition, or haven't been active for a while, check with a doctor or physiotherapist about what movement is right for you, and stop if anything hurts.*
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
    "Try it before a focus block, or when the afternoon slump hits.",
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
> Wandering isn't the problem. Staying lost in it without noticing is. Research suggests that short attention practices, where you notice your mind has drifted and gently bring it back, may help some people concentrate a little better, though the evidence is mixed and the effects tend to be modest. It's a small, free skill, and it gets easier with practice.
>
> Your new habit: **a 3-minute focus reset.**
>
> Tap **Listen** on the habit to play the Focus reset. It's read aloud with on-screen captions, so you can follow it silently in a shared space. You settle into a comfortable position (sitting is fine, eyes open or closed), notice your breath, and practise coming back whenever your mind wanders. There's nothing to get right.
>
> **When to use it**
>
> - Before a focus block, to arrive.
> - After a stressful message or meeting, to let go.
> - When the afternoon slump hits and you're about to reach for your phone.
> - Prefer counting breaths? **Explore → Mind** has a breathing pacer, and box breathing (4-4-4-4) is made for steadying your focus.
>
> *The reset is for relaxation and attention, not treatment. Slow breathing can make some people feel light-headed, so if that happens, breathe normally. If you have a breathing or heart condition, or quiet sitting brings up anxious or upsetting feelings, talk to your doctor or a mental health professional first.*
>
> **Stack it:** *"After my movement break, I tap Listen for a 3-minute reset before the next block."*
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
    "Pick a closing signal, like shutting the laptop or saying \"that's enough for today\".",
    "Make tomorrow's first task small and specific, so it's easy to start.",
    "Unfinished work isn't a failure. It's parked safely until tomorrow."
  ],
  "completionTitle": "A day well closed! ✍️",
  "completionMessage": "Three days of putting work down on purpose. Tomorrow's first task is already waiting, so your mind can rest.",
  "gentleNote": null
}
```

### Letter — Close the Day on Purpose

> Dear {name},
>
> You close the laptop, but the work doesn't close with you. At dinner you're mentally replying to emails. At midnight you remember what you forgot. For many of us, the working day never quite ends.
>
> Research on "psychological detachment" suggests that people who mentally step away from work in their free time tend to feel more recovered. Writing things down seems to help. In the Masicampo and Baumeister study we met earlier, making a plan for unfinished tasks quietened the mind's nagging. And in a small study at Baylor University, young adults who spent five minutes writing a to-do list before bed fell asleep faster, on average, than those who wrote about what they'd already done. It was a small study, but the idea makes sense: your mind can relax once it trusts the plan is safely on paper.
>
> Your new habit: **write a shutdown note at the end of your work, with tomorrow's first task.**
>
> It takes two minutes. Three lines are plenty:
>
> 1. One thing that went okay today. It doesn't need to be big.
> 2. Anything unfinished or worrying, parked on paper so it doesn't have to live in your head.
> 3. **Tomorrow's first task**, small and specific: "Open the budget sheet and fill in column one."
>
> Tap **Write** on the habit to open the reflection journal. Its prompt changes from day to day, so you can answer it or simply type your shutdown note in the box. Entries stay on your phone and you can delete them whenever you like.
>
> **Tips**
>
> - Choose a closing signal: shut the laptop, say "that's enough for today", step outside.
> - Shift worker or parent? Your "end of work" is the end of your working stretch, wherever it falls.
> - Then do something unrelated: a walk, cooking, a call with a friend.
>
> *A journal is a tool, not therapy. If worries feel constant, or stress or low mood has been affecting your sleep or daily life for weeks, please talk to a doctor or a counsellor.*
>
> **Stack it:** *"When I finish my last focus block of the day, I tap Write and close the day."*
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
> Looking back on purpose changes that. Research on reflection suggests it supports learning: in one study of workplace trainees, people who spent fifteen minutes at the end of each day thinking about what they'd learned went on to perform better than those who used that time to practise. A short, regular look back helps you see what's working, so you can keep it.
>
> Today's step isn't a habit to repeat. It's **one action: hold your first weekly review.** It takes about ten minutes.
>
> Choose a time that marks the end of your week: Friday afternoon, Sunday evening, or the day before your days off if you work shifts. Then ask three questions, and keep each answer short:
>
> 1. **What went well?** Name two or three wins, including small ones. Glance at your habit check-ins and your Top 3 for clues.
> 2. **What drained me, or what would I change?** Be curious, not critical. Maybe meetings ran long or one app kept pulling you in. Choose one thing to drop, delegate, delay or say no to.
> 3. **What matters most next week?** Pick one to three things, and put the first into your Top 3 planner.
>
> The buttons below open the reflection journal and the Top 3 planner, so you can write your answers and plan the first day.
>
> No review is perfect, and there are no marks. A messy week is simply useful information. If you notice feeling overwhelmed most weeks, that's worth sharing with someone you trust, or with a doctor or counsellor.
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
  "goal": {"type": "ritual", "ritual": "AFTERNOON", "targetDays": 5, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": [
    "Tap Start ritual and let the app walk you through each habit.",
    "Shape the ritual around your real working hours, whenever they fall.",
    "On hard days, do the smaller version of every habit."
  ],
  "completionTitle": "You did it! 🏆",
  "completionMessage": "Your focused afternoon is complete. Read your final letter.",
  "gentleNote": null
}
```

### Letter — Putting Your Rhythm Together

> Dear {name},
>
> This is it: the final challenge of this journey.
>
> Look at what you've put together. In the morning you decide what matters with your Top 3. In the afternoon you now have a clear space, a focus block, a movement break, a three-minute reset and a shutdown note. Each piece was small, and you've already practised every one of them.
>
> Your challenge: **complete your whole afternoon ritual on 5 of the next 7 days.**
>
> This is where a collection of habits turns into a rhythm. Research from University College London found that new behaviours took anywhere from a few weeks to several months to feel automatic, around two months on average, and that missing the odd day didn't derail people. So please treat this week as practice, not a test.
>
> **A few things to keep in mind**
>
> - **Use the ritual player.** Tap *Start ritual* and the app walks you through each habit, one at a time, so you don't have to remember the order.
> - **Shape it to your day.** The ritual works whenever your main working stretch falls. If you work nights, care for children or have a day off, use the smaller versions or move the habits to the hours that suit you. Smaller versions count.
> - **Keep your Top 3 going.** It isn't part of this afternoon ritual, but it feeds it. A clear morning makes the afternoon easier.
> - **Bad days happen.** A tiny ritual still counts: ten minutes on one task, three shoulder rolls, three slow breaths, one line of a shutdown note.
> - **Notice what changes.** At the end of the week, ask yourself: When did I feel most focused? What got in the way? What would I keep?
>
> If concentration still feels hard most days despite all this, that's not a failing. It's a good reason to talk to a doctor or another professional.
>
> Five afternoons out of seven. You've already done the hard part, which was starting.
>
> Cheering you on,
> Your Fab Life coach

---

## Outro Letter — You Built a Rhythm

> Dear {name},
>
> Take a moment to notice what you've done: **you completed the Focus & Achieve journey.**
>
> A few weeks ago you were juggling tabs and notifications. Since then you've built a morning Top 3, a protected focus block, a calmer space, movement breaks, a three-minute reset, a shutdown note and a weekly review. None of those is dramatic. Together they form a way of working that's kinder to your attention and kinder to you.
>
> Here's the quiet lesson underneath: **focus is something you set up, not something you're born with or without.** You didn't need more willpower. You needed a clear plan, fewer interruptions, real breaks and a way to finish the day. And you've shown you can create all of those.
>
> **What happens now?**
>
> Keep what works and let go of what doesn't. If the 25-minute block suits you, wonderful. If 40 minutes with a 10-minute break suits you better, that's just as valid. Nobody earns extra points for doing it exactly by the book. Your habits stay in the app, and your streaks keep counting.
>
> Some days will scatter anyway: illness, a poor night's sleep, a family emergency, a deadline that swallows everything. On those days, do the smaller version of one habit, or none at all, and begin again tomorrow. A messy day never erases the rhythm you've built.
>
> One last honest note. If you still find it hard to concentrate most days, or it's affecting your work, relationships or mood, please talk to a doctor or another health professional. Asking for support is nothing to be embarrassed about, and the habits you've built will be right here alongside it.
>
> When you feel ready, other journeys are waiting: sleeping better, eating well, a calmer mind. Each pairs nicely with what you've built here, because rest, food and calm all feed your focus too.
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
> I noticed it's been a few days since you checked in. First, the most important thing: **that's completely normal, and it's okay.**
>
> Focus habits are especially easy to lose, because the days that fall apart are often the busy, noisy ones. A deadline, a sick child, a night shift, a week of poor sleep: of course your routine wobbled. In a well-known study on habit formation, researchers found that missing a day here and there did not undo people's progress. What mattered was getting back to it.
>
> So let's restart gently. Which of these sounds most helpful?
>
> - **Keep going.** Nothing needs to change. Tomorrow is a fresh start.
> - **Make it smaller.** Try just the smaller version: a ten-minute focus block, a one-thing Top 3, three slow breaths. A tiny habit you do beats a big one you skip.
> - **Change the timing.** Maybe the habit sits at the wrong hour for your day. Move it to when your energy or schedule suits you better.
>
> One more gentle thought: if you've found it hard to concentrate for a long time, or things feel overwhelming, it's worth mentioning to a doctor or another professional. You don't have to handle that alone.
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
> We'll restart your current step from the beginning, so there's no pressure to catch up. For tomorrow, do just one thing: choose a single task and give it ten minutes, with the timer if you like. That's enough to begin again.
>
> Starting again isn't failing. Starting again *is* the habit.
>
> Glad to have you back,
> Your Fab Life coach

**Button:** *Let's start again*
