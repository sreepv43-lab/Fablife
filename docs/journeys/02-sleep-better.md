# Journey 2 — Sleep Better

```json meta
{"id": "sleep", "goalKey": "sleep", "order": 2, "title": "Sleep Better",
 "subtitle": "Wind down well and wake up rested", "emoji": "🌙", "ritualFocus": "EVENING"}
```

This is the source content for the second journey, in the format of [DESIGN_PHASE2.md §2](../DESIGN_PHASE2.md). It builds a calm **evening ritual** with one new small habit at a time, plus one afternoon habit (the caffeine cut-off) and a one-time bedroom action. It is a friendly coach, not a treatment for insomnia or any sleep condition, and it recommends no supplements or sleeping pills.

| # | Step | Goal type | Habit / action | Ritual | Target |
|---|---|---|---|---|---|
| — | Intro letter: *Better Nights Start in the Evening* | — | — | — | — |
| 1 | Steady Bedtime | Habit | Head to bed on time (`steady_bedtime`) | Evening | 3 days in 5 |
| 2 | A Sleep-Friendly Bedroom | One-time action | Make the bedroom darker, cooler, quieter | — | Once |
| 3 | Screens Away | Habit | Screens away before bed (`screens_off`) | Evening | 3 days in 5 |
| 4 | Caffeine Cut-Off | Habit | Last caffeinated drink by early afternoon (`caffeine_cutoff`) | Afternoon | 4 days in 6 |
| 5 | Gentle Evening Stretch | Habit | Slow stretches, tool Start (`bedtime_stretch`) | Evening | 3 days in 5 |
| 6 | Evening Brain Dump | Habit | Write it down, tool Write (`bedtime_journal`) | Evening | 3 days in 5 |
| 7 | Sleep Wind-Down | Habit | Guided wind-down, tool Listen (`night_winddown`) | Evening | 3 days in 5 |
| 8 | Your Evening Ritual | Ritual | The full evening ritual | Evening | 5 days in 7 |
| — | Outro letter: *Rested, One Evening at a Time* | — | — | — | — |

Habit `order` values follow the natural flow of an evening (brain dump, screens away, stretch, wind-down, bed). The app appends habits in the order they are added, so Step 8 tells the reader how to reorder them.

---

## Intro Letter — Better Nights Start in the Evening

> Dear {name},
>
> It's 2 a.m. You check the clock, work out how many hours are left, and your mind helpfully starts listing everything you forgot to do. If that sounds familiar, you're in very good company. Almost everyone has nights like this.
>
> Here's the gentle truth about sleep: you can't force it. Trying harder to fall asleep usually backfires. What you *can* do is set up the conditions in which sleep tends to arrive on its own. Research suggests that a steady rhythm, a calm bedroom, less late-night stimulation and a quiet wind-down help many people sleep more easily. Those are habits, and habits can be learned.
>
> Over the next few weeks we'll build a calm **evening ritual**, one small habit at a time, plus a single afternoon habit and a one-time bedroom reset. You'll practise each step for a few days before we add the next.
>
> A few things worth knowing before we begin:
>
> - **Sleep needs vary.** Many adults feel best with around seven to nine hours, but some need a little less or more, and sleep often changes with age. The goal is to feel rested, not to match anyone else's number.
> - **Consistency matters more than perfection.** A good rhythm most nights beats a flawless week followed by a collapse.
> - **Lying awake is common.** Being awake and worrying in the night is not a failure, and it doesn't mean you're doing this wrong. We'll look at kind ways to handle it.
> - **Real life counts.** If a baby, a night shift or caring for someone breaks up your nights, we'll look for whatever fits your life.
>
> One important note. This journey is a friendly coach, not a treatment for insomnia or any other sleep condition. It uses everyday habits only, with no pills or supplements. Please talk to a doctor if your sleep trouble has lasted for weeks, if you snore loudly or you (or someone who sleeps near you) notice that you stop breathing at night, or if you feel very sleepy during the day. They can look into what's going on and point you to the right help, and these habits can sit alongside that care. If you already take medicine for sleep, please don't change it without asking your doctor.
>
> Your first step is small: choosing a steady rhythm. Let's begin.
>
> Sleep well,
> Your Fab Life coach

---

## Step 1 — Steady Bedtime

```json step
{
  "id": "sleep.steady_bedtime",
  "goal": {"type": "habit",
           "habit": {"id": "steady_bedtime", "title": "Head to bed on time",
                     "description": "Get into bed within about 30 minutes of your bedtime",
                     "icon": "bed", "ritual": "EVENING", "order": 5, "durationSeconds": null,
                     "smallerVersion": "Get into bed within an hour of your bedtime, or simply keep your wake-up time steady"},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Head to bed on time\" to my evening",
  "tips": ["Pick your wake-up time first, then count back to find your bedtime.",
           "At weekends, try to stay within about an hour of your usual times.",
           "Not sleepy? Do something quiet in dim light, then head to bed when you feel drowsy.",
           "Being in bed is the goal. Sleep arrives in its own time."],
  "completionTitle": "A steady rhythm begins 🌙",
  "completionMessage": "Your body clock is getting a clearer signal. Next, let's make your bedroom a place that says sleep.",
  "gentleNote": null
}
```

### Letter — Your Body Loves a Rhythm

> Dear {name},
>
> Think about how your body handles lunch. If you eat at about the same time every day, you start feeling hungry right on schedule. Sleep works in a similar way. Deep in your brain sits a master clock that runs on a roughly 24-hour cycle, and it learns when to make you sleepy and when to make you alert.
>
> When bedtime and wake-up time jump around, that clock gets mixed signals. Studies of students and workers have linked irregular sleep schedules with poorer sleep quality and more daytime tiredness. Many people know the feeling of "social jet lag": sleeping in at the weekend, then feeling groggy and out of step on Monday.
>
> So our first habit isn't about sleeping more. It's about giving your body a clearer pattern: **head to bed on time, within about 30 minutes of your bedtime.**
>
> Your bedtime is currently set to **{bedTime}** and your wake-up time to **{wakeTime}**. Look at the hours between them. Do they leave enough time for you to feel rested? Many adults do well with seven to nine hours, but needs vary, so trust how you feel. If those times don't match your real life, change them in Settings. A time you can truly keep beats an ambitious one you can't.
>
> **How to make it stick**
>
> - Choose your wake-up time first. It's the easier of the two to keep steady, and bedtime tends to follow.
> - At weekends, aim to stay within about an hour of your usual times.
> - Not sleepy at bedtime? Do something quiet in dim light, like reading a few pages, and head to bed when you feel drowsy. Watching the clock rarely helps.
> - If shifts, a baby or caring for someone make fixed times impossible, pick the steadiest pattern your life allows. Even one anchor, such as your wake-up time, helps.
> - Being in bed is the goal, not being asleep. Sleep arrives in its own time.
>
> **Stack it:** *"After I brush my teeth, I get into bed."*
>
> Your challenge: **be in bed within about 30 minutes of {bedTime} on 3 of the next 5 days.**
>
> Some nights will run late, and that's completely fine. We're building a pattern, not a perfect record. *If poor sleep has lasted for weeks, please mention it to a doctor. This habit is a good companion to that care, not a replacement for it.*
>
> Sleep well,
> Your Fab Life coach

---

## Step 2 — A Sleep-Friendly Bedroom

```json step
{
  "id": "sleep.bedroom",
  "goal": {"type": "action", "title": "Make your bedroom darker, cooler and quieter",
           "description": "Choose one or two small changes tonight: block light, cool the room, soften noise",
           "shortcuts": [{"label": "Try Fan hum", "target": "sound:fan"}]},
  "buttonLabel": "I'll do it tonight",
  "completionTitle": "Your room is ready 🌙",
  "completionMessage": "A dark, cool, quiet room gives sleep an easier path. Next we'll look at the screens that keep us up.",
  "gentleNote": null
}
```

### Letter — Make Your Room Say Sleep

> Dear {name},
>
> Have you ever slept wonderfully somewhere dark, cool and still, like a quiet guest room or a hill-station homestay, and wondered why you can't sleep like that at home? Your surroundings talk to your body all night, even when you're not aware of it.
>
> Research suggests that three things matter most. **Light** comes first: even fairly dim light can tell your brain it's still daytime and make sleep lighter. **Temperature** is second: your body temperature naturally drops as you fall asleep, and a very warm room can get in the way. **Noise** is third: sudden sounds can nudge you out of deep sleep even when you don't fully wake.
>
> Today there's no habit to repeat. It's **one small action, done tonight: make your bedroom a little darker, cooler and quieter.** You don't need to do everything, and you don't need to spend anything. Pick the one or two changes that suit your home.
>
> 1. **Darker.** Draw the curtains, hang a thicker cloth or sheet over the window, or wear a comfortable eye mask. Turn glowing clocks and chargers away, or cover them with a cloth.
> 2. **Cooler.** Open a window earlier in the evening, use a ceiling fan or table fan, choose a thin cotton sheet over a heavy blanket and wear light clothes. Sleep experts often suggest a bedroom around 18°C (65°F), but in a hot climate the aim is simply as cool as you comfortably can.
> 3. **Quieter.** Close doors, add soft furnishings or try earplugs. A steady background hum can also soften sudden noises like traffic, neighbours or a snoring partner. Use the **Try Fan hum** shortcut on this step, then tap **Play**. It's a generated ambient sound, and you can set a sleep timer so it fades out by itself.
>
> Share a room, or sleep in a living space? Small steps still count. An eye mask and earplugs cost very little, and agreeing a "lights down" time with the people around you can help everyone.
>
> If you often wake up coughing, wheezing or gasping, or someone notices that you stop breathing in the night, please see a doctor rather than relying on room tweaks.
>
> Once you've made your changes, come back and tap **Mark as done**.
>
> Sweet dreams,
> Your Fab Life coach

---

## Step 3 — Screens Away

```json step
{
  "id": "sleep.screens_off",
  "goal": {"type": "habit",
           "habit": {"id": "screens_off", "title": "Screens away before bed",
                     "description": "Put phones, TV and tablets away 30 to 60 minutes before bed",
                     "icon": "phone_off", "ritual": "EVENING", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Put your phone down 10 minutes before bed"},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Screens away\" to my evening",
  "tips": ["Charge your phone across the room, or outside the bedroom.",
           "Fill the gap with something screen-free: a paper book, soft music, a chat or a light tidy-up.",
           "On call or caring for someone? Silence everything except the people who truly need you.",
           "Need an alarm? A cheap alarm clock does the job without the temptation."],
  "completionTitle": "Screens say goodnight 📵",
  "completionMessage": "Your mind gets a calmer runway into the night. Next, a little movement to ease your body.",
  "gentleNote": null
}
```

### Letter — Let the Screens Say Goodnight

> Dear {name},
>
> "Just one more episode." "I'll check one message." Twenty minutes later you're deep in a comment thread, wide awake, wondering where the evening went. Screens are designed to hold our attention, and getting pulled in isn't a personal failing. It's clever design doing its job.
>
> Two things seem to be going on. First, bright light from phones, tablets and TVs, especially in a dark room and close to your face, can delay the release of melatonin, a hormone that helps signal night-time to your body. How strongly it affects you depends on brightness, distance and how long you look. Second, the content itself keeps your mind busy: news, messages, work emails and videos are stimulating. Researchers are still working out how much comes from the light and how much from the engagement, but putting screens away helps with both.
>
> Your new habit: **put screens away 30 to 60 minutes before bed.** Start with 30. If that sounds like a lot, the smaller version is just 10 minutes.
>
> **Making it practical**
>
> - Charge your phone across the room, or outside the bedroom. Out of reach is easier than out of sight.
> - Need an alarm? A cheap alarm clock does the job, or leave your phone face-down across the room.
> - Fill the gap with something gentle: a paper book, a radio programme, soft music, a chat with family, a slow walk around the house or a light tidy-up. Audio is fine as long as the screen stays dark. Fab Life's sounds keep playing with the screen off, so you can start one and put the phone down.
> - On call, or caring for someone? Silence everything except the people who truly need to reach you.
> - Dimming the brightness or using night mode can help a little, but they don't replace putting the screen away.
>
> If late-night scrolling is how you cope with worry or loneliness, you're far from alone. Talking with someone you trust, or a counsellor or doctor, can help with whatever sits underneath.
>
> **Stack it:** *"After I finish my last task of the evening, I plug my phone in across the room."*
>
> Your challenge: **put screens away for 30 minutes before bed on 3 of the next 5 days.**
>
> Sleep well,
> Your Fab Life coach

---

## Step 4 — Caffeine Cut-Off

```json step
{
  "id": "sleep.caffeine_cutoff",
  "goal": {"type": "habit",
           "habit": {"id": "caffeine_cutoff", "title": "Caffeine cut-off",
                     "description": "Have your last caffeinated drink by early afternoon, about 8 hours before bed",
                     "icon": "timer", "ritual": "AFTERNOON", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Swap just your last caffeinated drink of the day for water, herbal tea or decaf"},
           "targetDays": 4, "windowDays": 6},
  "buttonLabel": "Add \"Caffeine cut-off\" to my afternoon",
  "tips": ["Count back about 8 hours from your bedtime to find your cut-off.",
           "Cut down gradually if you drink a lot. Swap one cup at a time.",
           "Keep a caffeine-free favourite ready: herbal or ginger tea, decaf, warm milk or buttermilk.",
           "Check labels. Cola, energy drinks, some teas, chocolate and some headache tablets contain caffeine."],
  "completionTitle": "Cut-off kept ☕",
  "completionMessage": "Your afternoons are now kinder to your nights. Next, a gentle stretch to ease your body at the end of the day.",
  "gentleNote": null
}
```

### Letter — The Cup That Follows You to Bed

> Dear {name},
>
> It's 4 p.m. The post-lunch slump arrives, and a hot cup of chai or coffee, or a cold can, looks like the perfect rescue. It often is, for a while. But the cup that carries you through the afternoon may still be quietly at work at bedtime.
>
> Caffeine works partly by blocking adenosine, a natural chemical that builds up during the day and makes you feel sleepy. On average it takes around five hours for your body to clear half the caffeine from a drink, though this varies a lot from person to person. Pregnancy, some medicines and your genes can all slow it down. One research study found that caffeine taken even six hours before bed reduced sleep time, and people often didn't notice, because they still fell asleep, just a little more lightly.
>
> Your new habit is the only afternoon one in this journey: **a caffeine cut-off.** Have your last coffee, strong tea, cola or energy drink by early afternoon, about 8 hours before bed. Count back 8 hours from {bedTime} and that's your cut-off. Keep your morning cup if you love it. This is about moving the last one earlier, not giving it up.
>
> **Tips**
>
> - Cut down gradually if you drink a lot. Stopping suddenly can bring headaches and tiredness for a few days.
> - Keep a favourite caffeine-free option ready: herbal or ginger tea, decaf, warm milk, buttermilk or fruit-infused water.
> - Beat the slump another way: a glass of water, five minutes outdoors in daylight, a short walk or desk stretch, or a small snack like curd, a banana or a few nuts.
> - Check labels. Cola, energy drinks, some teas, chocolate and some headache tablets contain caffeine too.
> - Work nights? The idea is the same: count back from *your* sleep time.
>
> If you're pregnant, have a heart rhythm problem, high blood pressure or anxiety, or take regular medicines, ask your doctor how much caffeine is right for you. If caffeine makes you jittery or gives you palpitations, cut back and mention it to a doctor.
>
> **Stack it:** *"After I finish my early-afternoon cup, I fill a glass of water."*
>
> Your challenge: **keep your caffeine cut-off on 4 of the next 6 days.**
>
> Cheers to calmer evenings,
> Your Fab Life coach

---

## Step 5 — Gentle Evening Stretch

```json step
{
  "id": "sleep.evening_stretch",
  "goal": {"type": "habit",
           "habit": {"id": "bedtime_stretch", "title": "Gentle evening stretch",
                     "description": "5 to 10 minutes of slow stretching to ease the day out of your body",
                     "icon": "stretch", "ritual": "EVENING", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Stretch for one minute: reach up, roll your shoulders and breathe out slowly",
                     "tool": {"type": "ROUTINE", "ref": "WIND_DOWN"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Evening stretch\" to my evening",
  "tips": ["Move slowly and stop at a mild pull. Stretching should never hurt.",
           "On the floor, in bed or in a chair all work. Choose what feels comfortable.",
           "Keep the lights low and let each breath out be a little longer than the breath in.",
           "Tap Start on the habit to follow the Evening Stretch routine, with an easier version of every move."],
  "completionTitle": "Loose and easy 🧘",
  "completionMessage": "Your body now has a gentle way to put the day down. Next, a way to quiet the thoughts.",
  "gentleNote": null
}
```

### Letter — Let Your Body Unclench

> Dear {name},
>
> By evening, many of us are carrying the day in our bodies: shoulders creeping towards our ears, a stiff neck from the desk, tight hips from sitting, tired legs from standing. A body that feels tense can be hard to settle at night.
>
> Research suggests that people who are regularly active tend to sleep better, and some small studies of gentle practices like stretching and yoga have found modest improvements in sleep quality. The evidence isn't perfect, but the risk is low and many people find it helps. A few slow minutes also work as a signal: they tell your body, and your mind, that the day is winding down.
>
> Your new habit: **a gentle evening stretch of 5 to 10 minutes.** Keep it slow and easy. This isn't a workout, and you shouldn't feel out of breath.
>
> **How to do it your way**
>
> - Tap **Start** on the habit to open the Evening Stretch routine. It's a slow sequence with a built-in timer and an easier version of every move, and most of it is done on the floor or bed, so a mat or blanket helps.
> - Prefer to sit? Try your own mini-routine in a chair: slow neck turns, shoulder rolls, a gentle seated twist, side bends and a few long breaths out.
> - In bed? Hug your knees gently towards your chest, let your knees drop to one side for a slow lying twist, and circle your ankles and wrists.
> - Move into a mild pull, never into pain. Hold each stretch for about 20 to 30 seconds and breathe out as you ease a little further.
> - Lower the lights and your screen brightness, and let your breathing slow down.
>
> If you have an injury, back or joint problems, are pregnant, or often feel dizzy, check with a doctor or physiotherapist about which stretches are right for you. Skip anything that doesn't feel good.
>
> **Stack it:** *"After I put my screens away, I stretch for five minutes."*
>
> Your challenge: **stretch gently for 5 minutes on 3 of the next 5 days.**
>
> On a tired night, one minute of reaching up and breathing out still counts.
>
> Slow and easy,
> Your Fab Life coach

---

## Step 6 — Evening Brain Dump

```json step
{
  "id": "sleep.journal",
  "goal": {"type": "habit",
           "habit": {"id": "bedtime_journal", "title": "Evening brain dump",
                     "description": "Write down what's on your mind, plus one thing that went well today",
                     "icon": "journal", "ritual": "EVENING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Write just one line: one thing that went okay today, or one thing for tomorrow",
                     "tool": {"type": "JOURNAL", "ref": "REFLECTION"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Evening brain dump\" to my evening",
  "tips": ["Two minutes is plenty. Set a timer if it helps.",
           "Write tomorrow's to-dos as a short list, then close the notebook.",
           "The Fab Life journal is on your phone, so write before your screens-away time, or use paper later.",
           "No grammar needed. Any language, any style."],
  "completionTitle": "Mind emptied, page filled ✍️",
  "completionMessage": "You now have a place to put your thoughts down for the night. Next, a guided way to wind down.",
  "gentleNote": null
}
```

### Letter — Empty Your Mind onto the Page

> Dear {name},
>
> You lie down, the room goes quiet, and suddenly your mind plays everything at once: tomorrow's meeting, the bill, the thing you said three days ago. Quiet is exactly when unfinished business gets loud.
>
> Researchers who study sleep find that a busy, worried mind is one of the most common reasons people struggle to drop off. In one small study at Baylor University, people who spent five minutes writing a to-do list before bed tended to fall asleep faster than people who wrote about what they had already done. Other research suggests that noting what you're grateful for may help some people feel calmer and sleep a little better. These are small studies and results vary, but the idea makes sense: a page can hold what your head keeps trying to hold.
>
> Your new habit: **an evening brain dump.** Spend two minutes writing whatever is on your mind, then add one thing that went well today.
>
> **How to do it**
>
> - Tap **Write** on the habit to open the journal. The Reflection page gives you a free text box, and you can write whatever you like, whatever the prompt says. For something quicker, open the Gratitude page in Explore → Mind → Journal and write three short lines.
> - Any format works: a list for tomorrow, a few sentences about a worry, or one honest line like "Today was hard, and I got through it."
> - You don't need to solve anything. For a worry you can't act on tonight, write "Not tonight. I'll look at this tomorrow," and let it rest.
> - Your phone is a screen, so do this before your screens-away time, or use a paper notebook beside the bed. Entries in Fab Life stay on your phone and you can delete them any time.
> - Write in any language. Nobody is marking it.
>
> If worries feel heavy or constant, or they're affecting your daily life, please talk to a doctor, a counsellor or someone you trust. A notebook can help, but it isn't a substitute for support.
>
> **Stack it:** *"After dinner is cleared away, I take two minutes to write."*
>
> Your challenge: **do your evening brain dump on 3 of the next 5 days.**
>
> Gently,
> Your Fab Life coach

---

## Step 7 — Sleep Wind-Down

```json step
{
  "id": "sleep.winddown",
  "goal": {"type": "habit",
           "habit": {"id": "night_winddown", "title": "Sleep wind-down",
                     "description": "A 10-minute guided wind-down in bed or a comfortable chair",
                     "icon": "moon", "ritual": "EVENING", "order": 4, "durationSeconds": null,
                     "smallerVersion": "Listen for just a few minutes, or take five slow breaths with a long breath out",
                     "tool": {"type": "GUIDED", "ref": "sleep_winddown"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Sleep wind-down\" to my evening",
  "tips": ["Get comfortable first. Lying down is lovely, and a chair is fine too.",
           "Still awake afterwards? Resting quietly is still restful. You don't need to force sleep.",
           "Racing mind? Try Unload your worries, a 6-minute session in Explore → Mind.",
           "Pair it with a quiet sound and the sleep timer if that feels soothing."],
  "completionTitle": "Landing gently 🌙",
  "completionMessage": "Your evening now has a calm ending. One last step brings it all together.",
  "gentleNote": null
}
```

### Letter — Teach Your Mind to Land

> Dear {name},
>
> The lights are off, your body is tired, and your mind has just opened forty-seven tabs. If you've ever lain awake going over your day, you know the feeling. It's one of the most common human experiences, and it isn't a failure of willpower.
>
> Here's the helpful paradox: the harder we try to fall asleep, the more alert we tend to become. So instead of trying, we practise letting go and giving attention somewhere gentle to rest. Research suggests that relaxation practices such as slow breathing, body scans and mindfulness may help some people feel calmer at bedtime, though the effects are modest and differ from person to person.
>
> Your new habit: **a sleep wind-down.** In bed or a comfortable chair, tap **Listen** on the habit to hear the 10-minute Sleep wind-down, a calm guided session read aloud by your phone's voice, with captions on screen. You can close your eyes and just listen.
>
> **For a racing mind, you have more tools in Explore → Mind**
>
> - **Listen** to *Unload your worries* (6 minutes), a guided session for when thoughts keep circling.
> - **Breathe** with *4-7-8 breathing*. Beginners can start with just three rounds. If the pauses feel uncomfortable, shorten them or choose *Long exhale*, which has no hold.
> - **Play** a quiet sound such as Rain, Ocean waves, Fan hum or Brown noise. They're generated sounds, not recordings, and a sleep timer lets them fade out on their own.
>
> And when you're lying awake anyway? That's normal, and nothing to fear. Resting quietly in the dark is still restful, so try not to check the clock. If you feel wide awake and frustrated after a good while, many people find it helps to get up, sit somewhere dim, read a few quiet pages, and return to bed when sleepy.
>
> If you have asthma, a lung or heart condition, or you feel light-headed when you breathe slowly or hold your breath, check with your doctor before trying breathing exercises. And if sleep trouble keeps going for weeks, please talk to a doctor.
>
> **Stack it:** *"After my stretch, I get comfortable in bed and tap Listen."*
>
> Your challenge: **do a sleep wind-down on 3 of the next 5 days.**
>
> Rest easy,
> Your Fab Life coach

---

## Step 8 — Your Evening Ritual

```json step
{
  "id": "sleep.evening_ritual",
  "goal": {"type": "ritual", "ritual": "EVENING", "targetDays": 5, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": ["Tap Start ritual each evening and let the app take you through your habits one at a time.",
           "On tired nights, do the smaller version of every habit.",
           "Judge your sleep over a few weeks, not by a single night."],
  "completionTitle": "A calmer evening is yours 🏆",
  "completionMessage": "Your evening ritual is complete. Read your final letter.",
  "gentleNote": null
}
```

### Letter — Putting Your Evening Together

> Dear {name},
>
> This is the final step of your Sleep Better journey. Look at what you've put together for your evenings:
>
> 1. **A brain dump**, to empty your head onto the page
> 2. **Screens away**, to let your mind slow down
> 3. **A gentle stretch**, to ease your body
> 4. **A sleep wind-down**, to help your mind land
> 5. **Heading to bed on time**, so your body clock knows the plan
>
> Your afternoon caffeine cut-off keeps working in the background. It isn't part of this challenge, but it's part of the picture, and your bedroom is already set up for sleep.
>
> Your challenge: **complete your whole evening ritual on 5 of the next 7 days.**
>
> Habits take time to become automatic. Research from University College London found it takes people anywhere from a few weeks to many months, about two months on average. You've been building this for weeks already.
>
> **A few things to remember**
>
> - **Use the ritual player.** Tap **Start ritual** and the app walks you through your evening habits, one at a time.
> - **Check the order.** The app lists habits in the order you added them, so "Head to bed on time" may sit at the top. You can move habits up or down in your habits list, so the evening flows the way your night does.
> - **Tired nights.** Do the smaller version of each habit. A tiny ritual still counts.
> - **Don't grade single nights.** Sleep naturally shifts with stress, heat, illness and busy days. Look at the trend over a few weeks instead: how do you feel in the morning, and how is your energy?
> - **Optional numbers.** If you like data, Fab Life can show last night's sleep from Health Connect, but only if you choose to connect it. Phone and watch sleep estimates are rough, so how you feel matters more. If tracking makes you anxious about sleep, skip it entirely.
>
> If, after weeks of steady habits, you still sleep poorly, snore loudly, stop breathing at night or feel very sleepy in the day, please see a doctor. That isn't failing the journey. It's looking after yourself.
>
> Five evenings out of seven. You can do this.
>
> Sleep well,
> Your Fab Life coach

---

## Outro Letter — Rested, One Evening at a Time

> Dear {name},
>
> Take a moment to notice what you've done: **you completed your Sleep Better journey.**
>
> A few weeks ago you started with one simple idea, a steady time for bed. Since then you've shaped a bedroom that invites sleep, put your screens down, moved your last coffee earlier, stretched your body, emptied your mind onto a page and learned to land with a guided wind-down. That's a real evening ritual, built one small habit at a time.
>
> The most important lesson isn't about any single habit. It's this: **you can't force sleep, but you can make room for it.** Everything you've practised does exactly that.
>
> **What happens now?**
>
> Keep the parts that help most. You don't have to do all of it every night. On a late evening, a one-minute stretch or five slow breaths still tells your body what comes next. Rough nights will visit again sometimes: stress, travel, illness, hot weather, a baby, a big day. They're normal, they don't undo your progress, and your ritual will be waiting whenever you want it.
>
> If you find yourself lying awake worrying, remember what you've learned. It's common, it's not a failure, and quiet rest is still rest. Try *Unload your worries*, *4-7-8 breathing* or a gentle sound, and be kind to yourself.
>
> And please keep an eye on the bigger picture. If sleep trouble lasts for weeks, if you snore loudly or stop breathing at night, or if you feel very sleepy in the day, talk to a doctor. Asking for help is one of the wisest habits of all.
>
> When you're ready, more journeys are waiting, and a rested you is a great place to start Focus & Achieve or Calm Mind.
>
> For tonight, though, simply enjoy getting into bed. You've earned it.
>
> So proud of you,
> Your Fab Life coach

---

## Supporting Letter — Missed a Few Days?

> Dear {name},
>
> I noticed it's been a few days since you last checked in. First, the most important thing: **that's completely okay.**
>
> Evenings are the busiest, most unpredictable part of many days. Late meetings, family, festivals, travel, a poorly child or a rough night can all knock a routine off course. Missing a few evenings doesn't undo what you've built, and sleep habits recover quickly when you return to them. What counts is coming back, not never slipping.
>
> Here are a few gentle ways forward:
>
> - **Keep going.** Nothing needs to change. Tonight is a fresh start.
> - **Make it smaller.** Every habit has a smaller version: ten minutes without screens, a one-minute stretch, one line in your journal. A small habit you actually do beats a big one you skip.
> - **Pick just one.** Choose the easiest evening habit and do only that tonight.
>
> And if poor sleep itself has been getting in the way, and it has lasted for weeks, please mention it to a doctor. You deserve support, and these habits will still be here.
>
> Here whenever you're ready,
> Your Fab Life coach

---

## Supporting Letter — Welcome Back

> Dear {name},
>
> Welcome back. I'm really glad you're here.
>
> Whatever kept you away, whether it was a busy stretch, a hard time or just life, there's no need to explain and nothing to feel guilty about. Opening the app again shows you still care about resting well, and that's what matters.
>
> Here's what hasn't changed: **everything you've built is still here.** The steps you completed and the habits you learned are waiting for you. Your body remembers more than you think.
>
> We'll restart your current step from the beginning, so there's no pressure to catch up. For tonight, try just one small thing: put your phone down ten minutes before bed, or take five slow breaths with a long breath out once you're in bed. That's plenty for a first evening back.
>
> Starting again isn't failing. Starting again is the habit.
>
> Glad to have you back,
> Your Fab Life coach
