# Journey 5 — Calm Mind

```json meta
{"id": "calm", "goalKey": "calm", "order": 5, "title": "Calm Mind",
 "subtitle": "Small practices for a steadier day", "emoji": "🧘", "ritualFocus": "EVENING"}
```

This is the source content for the fifth journey, in the format of [DESIGN_PHASE2.md §2](../DESIGN_PHASE2.md). It spreads small practices across all three rituals, one new habit at a time: slow breathing in the morning, a mood check-in, a walk or stretch and a grounding pause in the afternoon, and a gratitude pause, a kindness practice and a way to set worries down in the evening. It is a friendly coach for everyday stress, **not a treatment**. Every letter that touches mental health points readers to a doctor, a mental health professional, local emergency services or a crisis line when worry, low mood or panic is constant or gets in the way of life, or when someone feels unsafe or thinks about harming themselves. It never promises that a practice makes worry go away.

| # | Step | Goal type | Habit / action | Ritual | Target |
|---|---|---|---|---|---|
| — | Intro letter: *Small Practices, Steadier Days* | — | — | — | — |
| 1 | Slow Your Breath | Habit | Long-exhale breathing, tool Breathe (`long_exhale_breathing`) | Morning | 3 days in 5 |
| 2 | Check In | Habit | Mood check-in, tool Write (`mood_checkin`) | Afternoon | 4 days in 6 |
| 3 | Release Tension | Habit | Short walk or stretch, tool Start (`tension_release`) | Afternoon | 3 days in 5 |
| 4 | Ground Yourself | Habit | 5-4-3-2-1 grounding pause, tool Listen (`grounding_pause`) | Afternoon | 3 days in 5 |
| 5 | Gratitude Pause | Habit | Three good things, tool Write (`gratitude_pause`) | Evening | 3 days in 5 |
| 6 | Unload Your Worries | Habit | Guided worry unload, tool Listen (`unload_worries`) | Evening | 3 days in 5 |
| 7 | Kindness Practice | Habit | Guided kindness practice, tool Listen (`kindness_practice`) | Evening | 3 days in 5 |
| 8 | Your Calm Evening | Ritual | The full evening ritual | Evening | 5 days in 7 |
| — | Outro letter: *Steadier, One Practice at a Time* | — | — | — | — |

Habit `order` values follow the natural flow of an evening (gratitude, kindness, worries last, so the unload is the final thing before sleep). The app appends habits in the order they are added, so Step 8 tells the reader how to reorder them.

---

## Intro Letter — Small Practices, Steadier Days

> Dear {name},
>
> Picture a day that doesn't go to plan. A message lands the wrong way, the queue doesn't move, and your to-do list grows while you watch. Your shoulders creep up, your jaw tightens and your thoughts speed up. If that sounds like a normal week, you're in very good company. Everyday stress is part of being human.
>
> Stress lives in the body as well as the mind: in our breathing, our muscles and where our attention goes. Research suggests that small, regular practices can help many people feel a little steadier. Slow breathing, noticing how you feel, gentle movement, a pause to look around, remembering what went well and wishing people well all belong in this group. The effects are modest and differ from person to person, but they add up, and they're skills anyone can learn.
>
> Over the next few weeks we'll spread small practices across your whole day, one new habit at a time:
>
> - **Morning:** a couple of minutes of slow breathing
> - **Afternoon:** a mood check-in, a short walk or stretch, and a grounding pause
> - **Evening:** a gratitude pause, a kindness practice and a way to set your worries down before bed
>
> You'll practise each one for a few days before we add the next. Every habit has a smaller version for hard days, and seated, low-impact and eyes-open options are always welcome.
>
> **Please read this part carefully.** Calm Mind is a friendly coach for everyday stress. It is not a treatment, and it can't replace help from a professional. If worry, low mood or panic is constant, or if it's getting in the way of your life, please talk to a doctor or a mental health professional. If you ever feel unsafe, or think about harming yourself, contact your local emergency services or a crisis line right away, and reach out to someone you trust. Asking for help is a strong thing to do, and these habits can sit alongside that care.
>
> I also won't promise that any practice makes worries go away. What these habits can offer is a little more room to breathe, and a way back to steadiness when the day wobbles.
>
> Your first step is small and takes about two minutes. Let's begin.
>
> Gently,
> Your Fab Life coach

---

## Step 1 — Slow Your Breath

```json step
{
  "id": "calm.long_exhale",
  "goal": {"type": "habit",
           "habit": {"id": "long_exhale_breathing", "title": "Breathe out slowly for two minutes",
                     "description": "Slow breathing with a longer breath out than in, soon after you get up",
                     "icon": "breathe", "ritual": "MORNING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Take five slow breaths, breathing out a little longer than you breathe in",
                     "tool": {"type": "BREATHING", "ref": "calm_exhale"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Slow breathing\" to my morning",
  "tips": ["Sit or lie in any comfortable position. The edge of the bed or a chair is perfect.",
           "Breathe in gently, then out slowly, as if cooling a cup of hot chai.",
           "Keep it gentle, not deep. If you feel light-headed, breathe normally and rest.",
           "No phone nearby? Breathe in for 3 and out for 5, five times."],
  "completionTitle": "A calmer start 🧘",
  "completionMessage": "Three mornings of slow breathing. You've given your day a steadier beginning. Next, a gentle check-in with how you feel.",
  "gentleNote": null
}
```

### Letter — A Longer Breath Out

> Dear {name},
>
> The alarm goes off, you reach for your phone, and before your feet touch the floor your mind is already racing through the day. Your breathing may have turned quick and shallow without you noticing. Many of us start the morning with a low hum of hurry.
>
> Here's a small lever you carry everywhere: your breath. It runs by itself, but you can also steer it. Research suggests that slow breathing, especially with **a longer breath out than in**, can help many people feel calmer and more settled. One explanation is that a slow exhale may nudge your body's rest-and-settle system, though the science is still developing. The effects are modest and vary from person to person, but the practice is free and fits anywhere.
>
> Your first habit: **two minutes of long-exhale breathing in the morning.**
>
> Tap **Breathe** on the habit to open the breathing pacer. It guides you through the *Long exhale* pattern: in for 4 counts, out for 6, with no holding. Just follow the circle as it grows and shrinks. When you finish, the app offers to mark the habit as done.
>
> **Make it easy**
>
> - Sit, stand or lie down, whichever is comfortable. The edge of the bed or a chair is perfect.
> - Breathe in gently through your nose if that suits you, and out slowly, as if cooling a cup of hot chai.
> - Keep it gentle, not deep. If you feel light-headed or uncomfortable, breathe normally and rest. The counts are a guide, so go at a pace that feels kind.
> - No phone nearby? Breathe in for 3 and out for 5, five times.
> - Eyes open or closed, both are fine.
>
> If you have asthma, a heart or lung condition, or breathing exercises ever leave you feeling unwell, please check with your doctor before you continue.
>
> **Stack it:** *"After I switch off my alarm and sit up, I tap Breathe."* Your day starts around {wakeTime}, so that first stretch of the morning is a natural home for it.
>
> Your challenge: **do two minutes of long-exhale breathing on 3 of the next 5 days.**
>
> Two minutes. That's all.
>
> Breathe easy,
> Your Fab Life coach

---

## Step 2 — Check In

```json step
{
  "id": "calm.mood_checkin",
  "goal": {"type": "habit",
           "habit": {"id": "mood_checkin", "title": "Check in with your mood",
                     "description": "Pick a face from 1 to 5 and add a few words if you like",
                     "icon": "heart", "ritual": "AFTERNOON", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Name how you feel in one word, with no writing needed",
                     "tool": {"type": "JOURNAL", "ref": "MOOD"}},
           "targetDays": 4, "windowDays": 6},
  "buttonLabel": "Add \"Mood check-in\" to my afternoon",
  "tips": ["Any face is the right one. It's information, not a score.",
           "Try naming it in one word first: tired, hopeful, tense, flat, content.",
           "A few words of context help later, such as \"after the team call\" or \"slept badly\".",
           "Do it at the same moment each day, for example after lunch."],
  "completionTitle": "Noticing, not judging 💛",
  "completionMessage": "Four check-ins done. You're getting to know your own inner weather. Next, let some tension out through movement.",
  "gentleNote": null
}
```

### Letter — Name How You Feel

> Dear {name},
>
> "How are you?" "Fine, thanks." We say it a hundred times without thinking. But ask yourself the same question at three in the afternoon, and the honest answer might be: tired, a bit tense and strangely flat. Most of us rarely stop long enough to find out.
>
> That pause matters. Research suggests that putting feelings into words may help some people feel less swept along by them. In a well-known brain-imaging study, people who put a name to an emotion showed calmer activity in a region linked to alarm than people who didn't. It's one study among many, but it matches what many of us notice: "I'm feeling rushed" is easier to hold than a vague knot in the chest.
>
> There's a second benefit. Over a few weeks, a simple record can show you patterns, such as which days feel heavier, what tends to help and which situations wear you down. Patterns are easier to work with than guesses.
>
> Your new habit: **a mood check-in each afternoon.**
>
> Tap **Write** on the habit to open the Mood page. Choose one of five faces, from 1 to 5, and add a few words if you like. It takes under a minute. Your entries stay on your phone, you can delete them any time, and you can look back through them in the Journal in Explore → Mind.
>
> **How to do it**
>
> - Any face is the right face. A 2 isn't a bad mark and a 4 isn't a target. It's information, not a score.
> - Try naming it in one word first: tired, hopeful, tense, flat, content, restless.
> - A short note helps later: "after the team call", "slept badly", "rain all day", "time with family".
> - Pick a regular moment, such as after lunch, so it becomes automatic.
> - Not sure what you feel? Pick the face that's closest. That's enough.
>
> This is for noticing, not for judging yourself. If low days are piling up, or you feel low, worried or on edge most days and it's affecting your life, please talk to a doctor or a mental health professional. A mood log can help you tell them what you've noticed.
>
> **Stack it:** *"After lunch (or my afternoon chai), I tap Write and pick a face."*
>
> Your challenge: **check in with your mood on 4 of the next 6 days.**
>
> Be curious, not critical.
>
> Warmly,
> Your Fab Life coach

---

## Step 3 — Release Tension

```json step
{
  "id": "calm.tension_release",
  "goal": {"type": "habit",
           "habit": {"id": "tension_release", "title": "Release tension with a walk or stretch",
                     "description": "About five minutes outside, or a gentle stretch, whichever suits you today",
                     "icon": "walk", "ritual": "AFTERNOON", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Roll your shoulders and stretch your arms overhead for one minute",
                     "tool": {"type": "ROUTINE", "ref": "FLEXIBILITY"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Walk or stretch\" to my afternoon",
  "tips": ["Soften as you move: unclench your jaw, drop your shoulders and breathe out a little longer.",
           "Leave your phone in your pocket for the walk if you can.",
           "Seated is welcome. Shoulder rolls, slow neck turns and overhead reaches from a chair all count.",
           "Gentle is the goal. Stop if anything hurts."],
  "completionTitle": "Tension, released 🚶",
  "completionMessage": "Three afternoon breaks for your body. Next, a quick way to bring your mind back to the present.",
  "gentleNote": null
}
```

### Letter — Let the Tension Go

> Dear {name},
>
> Check in with your body right now. Are your shoulders up near your ears? Is your jaw clenched, your forehead tight, your hands gripping the phone or the steering wheel? Most of us carry stress in our muscles long before we notice it in our thoughts.
>
> Stress is physical as well as mental, so the body can be a way back. Research suggests that even short bouts of light movement, like a gentle walk or a few stretches, can lift mood and ease feelings of tension for some people, sometimes within minutes. Studies also suggest that time outdoors, especially around trees, grass or water, may add to the effect. It doesn't need to be a workout. The aim is to let tension out, not to burn calories.
>
> Your new habit: **release tension with a short walk or stretch, about five minutes each afternoon.** Choose whichever fits the day:
>
> - **Walk:** Step outside and stroll at an easy pace: round the block, along the verandah, through a courtyard or park, or down the lane. Let your arms swing and look up and around you.
> - **Stretch:** Tap **Start** on the habit to follow a gentle flexibility routine. The app picks one to match your level and movement needs.
> - **From a chair:** Roll your shoulders, turn your head slowly from side to side, reach your arms overhead, open and close your hands, and circle your ankles. If you use a wheelchair, a few minutes outside or by an open door counts as your walk.
>
> **Tips**
>
> - Soften as you move. Unclench your jaw, drop your shoulders and breathe out a little longer.
> - Leave your phone in your pocket for the walk if you can.
> - Choose a safe, well-lit place. Indoors is fine in rain or heat.
> - Gentle is the goal. Stop if anything hurts.
>
> If you have a medical condition, an injury or pain, or you haven't been active for a while, please check with your doctor about what kind of movement suits you.
>
> **Stack it:** *"After my mood check-in, I stand up and walk or stretch for five minutes."*
>
> Your challenge: **release tension with a short walk or stretch on 3 of the next 5 days.**
>
> One easy step at a time.
>
> Warmly,
> Your Fab Life coach

---

## Step 4 — Ground Yourself

```json step
{
  "id": "calm.grounding",
  "goal": {"type": "habit",
           "habit": {"id": "grounding_pause", "title": "Take a 5-4-3-2-1 grounding pause",
                     "description": "A three-minute guided pause using your five senses",
                     "icon": "meditate", "ritual": "AFTERNOON", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Name three things you can see, then take one slow breath out",
                     "tool": {"type": "GUIDED", "ref": "grounding_54321"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Grounding pause\" to my afternoon",
  "tips": ["Eyes open is fine. Do it anywhere safe, but never while driving or cycling.",
           "If one sense is limited, swap in another, such as more things you can feel or hear.",
           "For taste, a sip of water or tea works, or notice the taste already in your mouth.",
           "Try it before a tricky meeting or after a stressful message."],
  "completionTitle": "Back to now 🌿",
  "completionMessage": "Three grounding pauses done. You have a quick way back to the present moment. Now let's turn to your evenings.",
  "gentleNote": null
}
```

### Letter — Come Back to Now

> Dear {name},
>
> You're sitting at your desk, or standing at the stove, but your mind is somewhere else entirely. It's replaying what you said in this morning's meeting, or rehearsing a conversation that hasn't happened yet. Racing thoughts have a way of pulling us away from the moment we're actually in.
>
> One of the simplest ways back is through your senses, because they only work in the here and now. When you notice what you can see, feel and hear, your attention has something steady to rest on, and the loop of thoughts often loosens a little. Research suggests that mindfulness-style practices can help many people manage everyday stress, though the effects are modest and differ between people. Grounding is a short, simple way to try it.
>
> Your new habit: **a 5-4-3-2-1 grounding pause.** Slowly notice:
>
> 1. **5** things you can see
> 2. **4** things you can feel (your feet on the floor, the chair under you, the air on your skin)
> 3. **3** things you can hear
> 4. **2** things you can smell
> 5. **1** thing you can taste
>
> Tap **Listen** on the habit and a calm voice will guide you through it in about three minutes, with captions on screen. Eyes open is perfectly fine.
>
> **Tips**
>
> - Do it anywhere safe: at your desk, on a bench, in a queue or on a bus seat. Never while driving or cycling.
> - Adapt it to you. If one sense is limited, swap it for another, such as more things you can feel or hear.
> - For taste, a sip of water or tea works, or simply notice the taste already in your mouth.
> - Try it before a tricky meeting or after a stressful message.
>
> This is a short pause for everyday stress and not a treatment. If it brings up uncomfortable feelings, stop and look around at something neutral. If panic or distress is frequent or overwhelming, please talk to a doctor or a mental health professional.
>
> **Stack it:** *"After my walk or stretch, I tap Listen and take my grounding pause."*
>
> Your challenge: **take a 5-4-3-2-1 grounding pause on 3 of the next 5 days.**
>
> Here, now, breathing.
>
> Steadily yours,
> Your Fab Life coach

---

## Step 5 — Gratitude Pause

```json step
{
  "id": "calm.gratitude",
  "goal": {"type": "habit",
           "habit": {"id": "gratitude_pause", "title": "Take a gratitude pause",
                     "description": "Note three small things that went okay today",
                     "icon": "journal", "ritual": "EVENING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Think of one thing that went okay today, with no writing needed",
                     "tool": {"type": "JOURNAL", "ref": "GRATITUDE"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Gratitude pause\" to my evening",
  "tips": ["Keep it small and specific: the first sip of chai, a friendly wave, the rain stopping.",
           "Hard day? \"I got through today\" is a perfectly good line.",
           "Look for something new each night. Fresh details work better than repeating one list.",
           "Prefer to listen? Try the Gratitude pause in Explore → Mind."],
  "completionTitle": "Noticing the good 🌙",
  "completionMessage": "Three evenings of noticing what went okay. Next, a way to set your worries down before bed.",
  "gentleNote": null
}
```

### Letter — Notice What Went Okay

> Dear {name},
>
> Try this: think back over your day. What comes to mind first? For many of us it's the email we haven't answered, the awkward comment, the thing left undone. The good moments, such as the hot tea, the friendly wave or the bus that came on time, slip by almost unnoticed.
>
> There's a reason. Our minds are tuned to notice problems faster than pleasant things. Researchers call it the negativity bias. It helped our ancestors stay safe, but it can make evenings feel heavier than the day really was. Studies of gratitude practices, such as noting a few good things each day, suggest they may modestly lift mood and wellbeing for some people over several weeks. Results are mixed, and it isn't about forcing positivity.
>
> Your new habit: **an evening gratitude pause.** Note three small things that went okay today.
>
> Tap **Write** on the habit to open the Gratitude page, which gives you three short lines to fill in. Prefer listening? The *Gratitude pause* is a four-minute guided session in Explore → Mind, read aloud with captions.
>
> **Tips**
>
> - Keep it small and specific: "the first sip of chai", "my neighbour's smile", "the rain finally stopped".
> - Hard day? "I got through today" is a perfectly good line. Gratitude doesn't mean pretending things aren't difficult.
> - Look for something new each night. Fresh details work better than repeating the same list.
> - Prefer paper? A notebook works just as well. In the app, entries stay on your phone and you can delete them any time.
> - Be quick. It should take a couple of minutes, not become another task.
>
> If you can't find anything to write for many days, or low mood feels constant, please don't push through alone. Talk to a doctor, a mental health professional or someone you trust.
>
> **Stack it:** *"After dinner is cleared away, I tap Write and note three good things."* Or after you brush your teeth, whichever suits your evening.
>
> Your challenge: **take a gratitude pause on 3 of the next 5 days.**
>
> Look for the good, gently.
>
> Warmly,
> Your Fab Life coach

---

## Step 6 — Unload Your Worries

```json step
{
  "id": "calm.unload_worries",
  "goal": {"type": "habit",
           "habit": {"id": "unload_worries", "title": "Unload your worries",
                     "description": "A six-minute guided session to set the day's worries down before bed",
                     "icon": "moon", "ritual": "EVENING", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Write one worry and one small next step on paper, or take five slow breaths with a long breath out",
                     "tool": {"type": "GUIDED", "ref": "worry_unload"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Unload your worries\" to my evening",
  "tips": ["Keep pen and paper by the bed. If a thought tugs at you, jot one line and let it go.",
           "You're setting worries down, not solving them tonight.",
           "Lie down or sit, with eyes open or closed.",
           "Still awake afterwards? Resting quietly is still restful."],
  "completionTitle": "Lighter nights 🌙",
  "completionMessage": "Three evenings of setting worries down. One more practice to add: a little kindness.",
  "gentleNote": null
}
```

### Letter — Set the Day's Worries Down

> Dear {name},
>
> The lights are off, the house is quiet, and suddenly your mind opens its mail: tomorrow's meeting, a bill, a family worry, something you said last week. Quiet is exactly when unfinished business gets loud, and almost everyone has nights like this.
>
> Research suggests the mind tends to keep nagging about unfinished things until we make a plan for them, even a small one. In one study, people who wrote a specific plan for a goal they hadn't finished had fewer intrusive thoughts about it afterwards. A sleep study at Baylor University found that people who spent five minutes writing tomorrow's to-do list at bedtime tended to fall asleep faster. These are small studies and results vary, but the idea is kind and simple: you can set a worry down for the night without having solved it.
>
> Your new habit: **unload your worries before bed.**
>
> Tap **Listen** on the habit to play *Unload your worries*, a six-minute guided session read aloud by your phone's voice, with captions on screen. It's meant for the end of the day. Your bedtime is set to {bedTime}, so you might start about half an hour before. You can lie down or sit up, with eyes open or closed.
>
> **Tips**
>
> - Keep a pen and paper by your bed. If a thought tugs at you, jot one line, such as "Call the bank. Tomorrow," and let it go.
> - You're not solving anything tonight. You're putting worries down, not pretending they don't matter.
> - The player has a toggle for a quiet background sound, so use it or switch it off, whichever feels more soothing.
> - Still awake afterwards? Resting quietly in the dark is still restful. You don't have to force sleep.
>
> Everyone has worrying nights. But if worry is constant, keeps you awake for weeks or is getting in the way of your daily life, please talk to a doctor or a mental health professional. This habit is for everyday stress and is not a treatment. If you ever feel unsafe or think about harming yourself, contact local emergency services or a crisis line right away.
>
> **Stack it:** *"After my gratitude pause, I get comfortable in bed and tap Listen."*
>
> Your challenge: **unload your worries before bed on 3 of the next 5 days.**
>
> Whatever tonight holds, you don't have to carry it all.
>
> Rest easy,
> Your Fab Life coach

---

## Step 7 — Kindness Practice

```json step
{
  "id": "calm.kindness",
  "goal": {"type": "habit",
           "habit": {"id": "kindness_practice", "title": "Do a kindness practice",
                     "description": "A five-minute guided practice of silent good wishes for yourself and others",
                     "icon": "heart", "ritual": "EVENING", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Silently say \"May I be well\" once, then \"May you be well\" for someone you care about",
                     "tool": {"type": "GUIDED", "ref": "loving_kindness"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Kindness practice\" to my evening",
  "tips": ["Start with someone easy to care about: a child, a pet, a friend.",
           "Feels awkward? That's common. Use your own words or keep it very short.",
           "If it stirs up painful feelings, stop, open your eyes and look around. You can try again another day.",
           "Eyes open or closed, sitting or lying down, all are fine."],
  "completionTitle": "Kind to yourself 💛",
  "completionMessage": "You now have three evening practices. One final challenge brings them together.",
  "gentleNote": null
}
```

### Letter — A Little Kindness

> Dear {name},
>
> Imagine a friend telling you about a day just like yours: rushed, a bit overwhelmed, never quite enough time. What would you say to them? Probably something warm. Now think about what you said to yourself today. For many of us, those two voices sound very different.
>
> Research suggests that harsh self-criticism tends to go along with more stress, while self-kindness is linked with coping better when things are hard. Small studies of loving-kindness practice, where you silently offer good wishes to yourself and others, suggest it may increase positive feelings and a sense of connection for some people. The effects aren't instant or the same for everyone, but it's a skill you can practise like any other.
>
> Your new habit: **a kindness practice.** Tap **Listen** on the habit to play the five-minute *Kindness practice*. A calm voice invites you to send simple wishes, such as "May I be well. May you be safe. May we all be at ease," first to yourself, then to someone you care about, then more widely. Wishing others well is an old human practice, from the Buddhist idea of *metta* to the Sanskrit blessing *lokah samastah sukhino bhavantu* ("may all beings be happy"). No belief is needed. Plain, everyday words work just as well.
>
> **Tips**
>
> - Start with someone easy to care about: a child, a pet, a friend.
> - Feels awkward? That's very common. Use your own words, keep it short, or simply rest a hand on your arm or chest.
> - Eyes open or closed, sitting or lying down, all are fine.
> - If it stirs up painful feelings, stop, open your eyes and look around the room. You can try again another day.
>
> If painful feelings keep returning, or they feel hard to manage, please talk to someone you trust, a doctor or a mental health professional.
>
> **Stack it:** *"After my gratitude pause, I tap Listen and send a few kind wishes."* Your worry unload can follow in bed, so it stays the last thing before sleep.
>
> Your challenge: **do a kindness practice on 3 of the next 5 days.**
>
> Be as kind to yourself as you would be to a friend.
>
> With warmth,
> Your Fab Life coach

---

## Step 8 — Your Calm Evening

```json step
{
  "id": "calm.evening_ritual",
  "goal": {"type": "ritual", "ritual": "EVENING", "targetDays": 5, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": ["Tap Start ritual each evening and let the app take you through your habits one at a time.",
           "On tired or stressful nights, do the smaller version of every habit.",
           "Judge the trend over weeks, not a single day."],
  "completionTitle": "A calmer evening is yours 🏆",
  "completionMessage": "Your evening ritual is complete. Read your final letter.",
  "gentleNote": null
}
```

### Letter — Your Calm Evening

> Dear {name},
>
> This is the final step of your Calm Mind journey. Look at what you've put together for your evenings:
>
> 1. **A gratitude pause**, to notice what went okay
> 2. **A kindness practice**, to soften the way you speak to yourself
> 3. **Unloading your worries**, to set the day down before bed
>
> Your morning breathing, your afternoon check-in, your walk or stretch and your grounding pause aren't part of this challenge, but they keep working in the background. Think of them as a steady thread running through your day.
>
> Your challenge: **complete your whole evening ritual on 5 of the next 7 days.**
>
> Habits take time to become automatic. Research from University College London found it takes people anywhere from a few weeks to many months, around two months on average. You've been building this for weeks already.
>
> **A few things to remember**
>
> - **Use the ritual player.** Tap **Start ritual** and the app walks you through your evening habits, one at a time.
> - **Check the order.** The app lists habits in the order you added them, so unloading your worries may sit above your kindness practice. You can move habits up or down in your habits list so the evening flows the way you like: gratitude, then kindness, then worries last, so it's the final thing before sleep.
> - **Tired or stressful nights.** Do the smaller version of each habit: one line of gratitude, one kind wish, five slow breaths. A tiny ritual still counts.
> - **Don't grade single days.** Stress rises and falls with work, health, weather and life. Look at the trend over weeks. Your mood check-ins can help: how do your afternoon faces look compared with a month ago?
>
> If worry, low mood or panic stays constant or gets in the way of your life, or if you ever feel unsafe or think about harming yourself, please contact a doctor, a mental health professional, local emergency services or a crisis line right away. Asking for help is part of looking after yourself.
>
> Five evenings out of seven. You can do this.
>
> Gently,
> Your Fab Life coach

---

## Outro Letter — Steadier, One Practice at a Time

> Dear {name},
>
> Take a moment to notice what you've done: **you completed your Calm Mind journey.**
>
> A few weeks ago you started with two minutes of slow breathing. Since then you've learned to name how you feel, release tension through movement, come back to the present through your senses, notice what went well, set your worries down at night and wish yourself and others well. That's a toolkit for the whole day, built one small practice at a time.
>
> The most important lesson isn't about any single practice. It's this: **calm isn't something you achieve once. It's something you practise and return to.** Hard days will still come, and that's normal. What you've built is a set of ways back to steadiness.
>
> **What happens now?**
>
> Keep what helps most. You don't need to do everything every day. On a hectic day, five slow breaths, one honest mood check or one kind thought still counts. Your morning, afternoon and evening practices are there whenever you want them, and you can find all the tools again in Explore → Mind.
>
> These practices are for everyday stress. They won't make every worry disappear, and they aren't meant to. If worry, low mood or panic is constant, or if it's getting in the way of your life, please talk to a doctor or a mental health professional. And if you ever feel unsafe or think about harming yourself, contact your local emergency services or a crisis line right away. Reaching out is one of the wisest things anyone can do, and these habits can sit alongside that care.
>
> When you're ready, other journeys are waiting, and you can pick whichever matches what you want next. A steadier you is a good place to start from.
>
> For today, take one slow breath out and enjoy it. You've earned it.
>
> So proud of you,
> Your Fab Life coach

---

## Supporting Letter — Missed a Few Days?

> Dear {name},
>
> I noticed it's been a few days since you last checked in. First, the most important thing: **that's completely okay.**
>
> Calm practices tend to be the first thing to slide when life gets loud, and that's understandable. A packed week, a rough patch, travel, festivals, a poorly child or simply forgetting happens to everyone. Missing a few days doesn't undo what you've built. What counts is coming back.
>
> Here are a few gentle ways forward:
>
> - **Keep going.** Nothing needs to change. Today is a fresh start.
> - **Make it smaller.** Every habit has a smaller version: five slow breaths, a one-word mood check, one line of gratitude. A small practice you actually do beats a bigger one you skip.
> - **Pick just one.** Choose the easiest practice and do only that today.
>
> And a gentle reminder: these habits are for everyday stress. If worry, low mood or panic has been constant or is getting in the way of your life, please talk to a doctor or a mental health professional. If you ever feel unsafe or think about harming yourself, contact local emergency services or a crisis line right away.
>
> Here whenever you're ready,
> Your Fab Life coach

---

## Supporting Letter — Welcome Back

> Dear {name},
>
> Welcome back. I'm really glad you're here.
>
> Whatever kept you away, whether it was a busy stretch, a hard time or just life, there's no need to explain and nothing to feel guilty about. Opening the app again shows you still care about feeling steadier, and that's what matters.
>
> Here's what hasn't changed: **everything you've built is still here.** The steps you completed and the habits you learned are waiting for you.
>
> We'll restart your current step from the beginning, so there's no pressure to catch up. For today, try just one small thing: take five slow breaths, breathing out a little longer than you breathe in. That's plenty for a first day back.
>
> One more thing. If the time away was a difficult one, please be gentle with yourself. If worry, low mood or panic is constant or getting in the way of your life, a doctor or a mental health professional can help. And if you ever feel unsafe or think about harming yourself, contact local emergency services or a crisis line right away.
>
> Starting again isn't failing. Starting again is the habit.
>
> Glad to have you back,
> Your Fab Life coach
