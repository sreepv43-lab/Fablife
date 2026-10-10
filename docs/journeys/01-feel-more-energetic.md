# Journey 1 — Feel More Energetic

**Journey id:** `energy` · **Ritual built:** Morning · **Length:** 8 steps, about 4–6 weeks
**Subtitle:** Build a morning ritual that powers your whole day

This document is the source content for the first journey. It will be converted to `app/src/main/assets/journeys/energy.en.json` following the content model in [PRODUCT_SPEC.md §7](../PRODUCT_SPEC.md#7-content-model).

Placeholders: `{name}` = user's name or "friend", `{wakeTime}` = user's wake-up time.

---

## Journey Overview

| # | Step | Goal type | Habit / action | Target |
|---|---|---|---|---|
| — | Intro letter: *Your Energy Is a Skill* | — | — | — |
| 1 | Drink Water | Habit | Drink a glass of water (Morning) | 3 days in 5 |
| 2 | A Great Breakfast | Habit | Eat a protein + fibre breakfast (Morning) | 3 days in 5 |
| 3 | Set Yourself Up | One-time action | Prepare tonight for tomorrow morning | Once |
| 4 | Move Your Body | Habit | 5-minute morning movement (Morning, timer 5:00) | 4 days in 6 |
| 5 | Step Into the Light | Habit | 5 minutes of daylight (Morning) | 3 days in 5 |
| 6 | Celebrate Yourself | Habit | Celebrate after your ritual (Morning, last) | 3 days in 5 |
| 7 | Design Your Morning | One-time action | Write your morning ritual and fix your wake-up time | Once |
| 8 | The Morning You | Ritual | Complete the full morning ritual | 5 days in 7 |
| — | Outro letter: *You Did This* | — | — | — |

Supporting letters: *Missed a Few Days?* (struggle card) and *Welcome Back* (7+ days inactive).

> **Spec note:** Step 8 uses goal type `ritual` — the step completes when **all** habits in the target ritual are checked in on `targetDays` days within `windowDays`. This extends the goal types in the spec (`habit`, `action`, `ritual`).

---

## Intro Letter — Your Energy Is a Skill

**Shown:** at the end of onboarding, before Step 1.

> Dear {name},
>
> Think about the last time you felt truly energetic. Not wired on coffee, not forcing yourself through the afternoon, but properly *awake*: clear-headed, light, ready for whatever the day brought.
>
> Most of us treat energy like the weather. Some days we have it, some days we don't, and there's nothing to be done. But energy is much less random than it feels. A big part of it comes from what you do in the first hour after you wake up. It depends on whether you drink, eat, move and see daylight, and on whether you start the day reacting or with some intention.
>
> That's good news, because habits can be learned.
>
> Over the next few weeks we're going to build a **morning ritual** together. Not a two-hour routine copied from a productivity guru. A short, realistic sequence of small habits that fits *your* mornings. We'll add one habit at a time. You'll practise each one for a few days, and only when it starts to feel natural will we add the next.
>
> This may feel slow, and it's meant to. Big changes often fail because they ask too much at once and run on willpower alone. Small habits, repeated and stacked on top of each other, keep going without it. After a while they stop being something you try to do and become part of who you are.
>
> Here's what I ask of you:
>
> - **Start small.** Smaller than you think you need to.
> - **Show up, not perfectly, but often.** Missing a day is normal. Coming back is what counts.
> - **Be kind to yourself.** You're not fixing something broken. You're investing in someone who matters.
>
> Your first step is the simplest habit in this whole journey. It takes about ten seconds. Let's begin.
>
> With you on the journey,
> **Your Fab Life coach**

**Button:** *Let's begin*

---

## Step 1 — Drink Water

### Letter: The Ten-Second Habit

> Dear {name},
>
> Tonight, while you sleep, your body will keep working for seven or eight hours. It repairs tissue, consolidates memories, regulates hormones and breathes out moisture with every breath. What it won't get during all that time is a single drop to drink.
>
> So most of us start the day mildly dehydrated. Then we reach for coffee and wonder why we still feel foggy.
>
> Water makes up around 60% of your body, and your brain is especially sensitive to it. Studies have found that even mild dehydration, losing just one or two percent of body weight in fluid, is linked to lower mood, more tiredness, headaches and weaker concentration. That's a level most people don't even notice as thirst.
>
> The fix couldn't be simpler: **drink a glass of water as soon as you get up.**
>
> That's it. That's the whole habit. It's deliberately tiny, for two reasons:
>
> 1. **It works.** Rehydrating first thing is one of the easiest ways to feel more awake.
> 2. **It's a foundation.** This glass of water is the first brick in your morning ritual. Every habit we add later will be stacked on top of it. Small and easy is exactly what a first brick should be.
>
> **How to make it stick**
>
> - Link it to something you already do: *"After my feet touch the floor, I drink my water."* or *"After I switch off my alarm…"*
> - Make it visible. A glass or bottle you can see is a reminder you can't ignore.
> - Room temperature is easier to drink quickly than ice-cold water.
> - Add a slice of lemon if you like, but don't let "perfect" get in the way of "done."
>
> Your challenge: **drink a glass of water after waking on 3 of the next 5 days.**
>
> Ten seconds. You've got this.
>
> Cheers to you,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `habit` |
| Habit id | `drink_water` |
| Title | Drink a glass of water |
| Description | Right after you wake up |
| Icon | `water_drop` |
| Ritual | MORNING (order 1) |
| Smaller version | Take three sips of water |
| Target / window | 3 days / 5 days |

**Letter button:** *Add "Drink water" to my morning*

### Tips (shown on the step card, rotating)
- Fill a glass tonight and leave it where you'll see it in the morning.
- Drink it before you check your phone.
- Missed it on waking? Drink it now. It still counts.

### Completion message
> **3 days of water — done! 💧**
> Your morning ritual has officially begun. Next up: fuel that lasts.

### Notification copy
- Ritual reminder: "💧 Good morning! Start with a glass of water."
- Nudge: "Just one glass today. That's all it takes to keep going."

---

## Step 2 — A Great Breakfast

### Letter: Fuel, Not Just Food

> Dear {name},
>
> Three mornings with water. Your ritual has a foundation. Now let's give your body something to run on.
>
> You've probably heard that breakfast is "the most important meal of the day." The truth is more nuanced. Research hasn't settled whether *everyone* needs breakfast, and some people feel great eating later. But for many of us, the problem isn't *whether* we eat in the morning. It's *what* we eat.
>
> A pastry, sugary cereal or white toast with jam is quick energy. It's also quick to disappear. Your blood sugar rises fast and drops fast, and by mid-morning you're tired, hungry and reaching for a snack or another coffee.
>
> A breakfast built on **protein and fibre** works differently. It digests slowly and releases energy steadily, so you stay full and focused until lunch.
>
> **The simple formula: one protein + one fibre-rich food.**
>
> | Protein | Fibre |
> |---|---|
> | Eggs | Oats |
> | Greek yoghurt or curd | Fruit (berries, apple, banana) |
> | Paneer or tofu | Whole-grain bread or roti |
> | Nuts, seeds or nut butter | Vegetables |
> | Milk or soy milk | Chia or flax seeds |
>
> A few combinations that take under five minutes:
> - Greek yoghurt with berries and a handful of nuts
> - Overnight oats with milk and chia seeds, made the night before
> - Two boiled eggs and a piece of fruit
> - Whole-grain toast with peanut butter and banana
> - A vegetable omelette, or besan chilla with curd
>
> **Does fibre upset your stomach?** Some people find salads, beans, whole grains or seeds cause bloating or discomfort. If that's you, choose a **gentle breakfast: one protein + one easy-to-digest carb.**
>
> | Protein | Gentle carb |
> |---|---|
> | Eggs (boiled or scrambled) | Idli or plain dosa |
> | Curd/yoghurt or buttermilk | Ripe banana or papaya |
> | Paneer or silken tofu | White toast or rava upma |
> | Smooth peanut butter | Soft poha or semolina porridge |
>
> Try: idli with curd, scrambled eggs on white toast, curd with ripe banana, or white toast with smooth peanut butter. Cook vegetables until soft, peel fruit and eat slowly. When your stomach feels settled, you can add fibre back slowly, starting with small amounts of oats or a peeled cooked apple. *If stomach problems continue, please see a doctor.*
>
> You'll find many more meal ideas, both regular and gentle, in the **Eat** section of the app.
>
> **Not hungry in the morning?** That's okay. Start with something small, like a handful of nuts or a banana with peanut butter. Your appetite will often adjust within a week or two. If you genuinely do better eating later, choose a protein + fibre meal whenever your first meal is.
>
> **Stack it:** *"After I drink my water, I eat my breakfast."* Your ritual now has two links in the chain.
>
> Your challenge: **eat a protein + fibre breakfast on 3 of the next 5 days.**
>
> Enjoy every bite,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `habit` |
| Habit id | `great_breakfast` |
| Title | Eat a great breakfast |
| Description | One protein + one fibre (gentle mode: one protein + one easy-to-digest carb) |
| Icon | `egg_alt` |
| Ritual | MORNING (order 2) |
| Smaller version | Eat a handful of nuts or a piece of fruit (gentle mode: a ripe banana or a small bowl of curd) |
| Target / window | 3 days / 5 days |

**Letter button:** *Add "Great breakfast" to my morning*

**Gentle mode:** when *Gentle on the stomach (low-fibre)* is on, the app shows the protein + gentle carb table first and replaces the "five minutes" examples with: idli with curd, scrambled eggs on white toast, curd with ripe banana, white toast with smooth peanut butter.

### Tips
- Prepare overnight oats or boil eggs the evening before. (Gentle: keep idli batter or boiled eggs ready.)
- Keep breakfast simple: the same two or three options all week is fine.
- Eat sitting down, away from your phone, even for five minutes.

### Completion message
> **Breakfast champion! 🍳**
> Water, then fuel. Your mornings are starting to have a shape.

### Notification copy
- Ritual reminder: "☀️ Water, then a great breakfast. 2 small steps."
- Nudge: "Even a banana and a few nuts counts as a great breakfast today."

---

## Step 3 — Set Yourself Up

### Letter: Win the Morning the Night Before

> Dear {name},
>
> Here's a secret most people with "perfect" mornings won't tell you: they don't have more willpower than you. They just have fewer decisions to make.
>
> In the first minutes after waking, your brain is still booting up. Scientists even have a name for that groggy state: *sleep inertia*. It isn't a great time to decide what to eat, where your water bottle is or whether you can be bothered. Every small obstacle in the morning is a chance for an old habit to win.
>
> So today we're doing something different. There's no habit to repeat. There's **one small action, done tonight**, to make tomorrow morning effortless.
>
> **Tonight, take five minutes to:**
>
> 1. **Fill your water glass or bottle** and put it next to your bed, or right beside the kettle.
> 2. **Prepare your breakfast**, or at least decide on it. Soak the oats, boil the eggs or put the fruit on the counter.
> 3. **Lay out comfortable clothes** you can move in. You'll need them soon. Our next habit is about moving your body.
>
> Behaviour scientists call this *designing your environment*. Instead of relying on motivation, you change your surroundings so the healthy choice becomes the easy, obvious choice. It's one of the most reliable ways to change behaviour, because it works even on days when you don't feel motivated at all.
>
> Think of it as a gift from tonight-you to tomorrow-you.
>
> Once you've done it, come back and tap **Mark as done**.
>
> Sweet dreams,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `action` |
| Title | Prepare tonight for tomorrow morning |
| Description | Water by the bed, breakfast ready, movement clothes laid out |
| Icon | `nightlight` |

**Letter button:** *I'll do it tonight*

### Completion message
> **Tomorrow-you says thank you. 🌙**
> Try making this five-minute evening prep a regular thing. It makes every morning easier.

### Notification copy
- Evening reminder (sent once, at bed time − 60 min, only for this step): "🌙 5 minutes tonight = an easier morning tomorrow."

---

## Step 4 — Move Your Body

### Letter: Five Minutes That Wake You Up

> Dear {name},
>
> When you feel tired, exercise is probably the last thing you want to do. It seems obvious that moving would use up energy you don't have.
>
> But your body doesn't work like a battery. It works more like a fire: the more you stoke it, the more it gives back.
>
> Movement raises your heart rate and sends more oxygen-rich blood to your brain and muscles. It also releases chemicals that lift mood and alertness. Researchers studying people who felt constantly tired found that regular, *gentle* exercise, not intense workouts, was enough to make them feel noticeably more energetic within a few weeks.
>
> So we're not talking about the gym. We're talking about **five minutes of movement in the morning**. That's enough to shake off sleepiness and tell your body the day has started.
>
> **Choose whatever feels good:**
> - A gentle stretching or yoga flow
> - A brisk five-minute walk, even around your home
> - Dancing to two of your favourite songs
> - A simple circuit: 10 squats, 10 wall push-ups, 20 marching steps, repeat
> - Sun salutations (Surya Namaskar), at your own pace
>
> **Not sure what to do? Try one of these five-minute routines.** The app picks one for you based on your settings:
>
> | If you are… | Try | What's in it |
> |---|---|---|
> | New to exercise | **Gentle Wake-Up** | March in place, arm circles, side bends, 10 squats (or sit-to-stands), 10 wall push-ups, calf raises, deep breaths |
> | Already a bit active | **Energy Boost** | High-knee march, squats, incline push-ups, lunges, a 30-second plank, step jacks, stretch |
> | Easy on the joints | **Joint-Friendly Wake-Up** | Supported marching, shoulder rolls, sit-to-stands, wall push-ups, supported calf raises, standing cat–cow |
> | Moving from a chair | **Seated Wake-Up** | Seated march, arm circles, leg extensions, torso twists, punches, gentle neck turns |
> | Feeling outdoorsy | **Morning Walk** | A brisk 5-minute walk, which also counts as daylight for Step 5! |
>
> Tap **Start** on the habit to follow a routine with the built-in timer, one move at a time. Every move has an easier version. More routines for strength, cardio and stretching are waiting in the **Move** section of the app.
>
> **How much is enough over time?** Health guidelines suggest adults aim for about **150 minutes of moderate activity a week** (like brisk walking) plus **strength exercises on 2 days**. That sounds like a lot, but it's only about 20 minutes a day. Today we start with five. You'll build up from there.
>
> **Stack it:** *"After my water and breakfast, I move for five minutes."* (Some people prefer moving *before* breakfast. Rearrange your ritual any way you like.)
>
> **Make it easy:** you laid out your movement clothes in the last step. Keep doing that. If even five minutes feels like too much today, do one minute. One minute is infinitely more than zero, and it keeps the habit alive.
>
> *If you have a medical condition, an injury or haven't exercised in a long time, check with your doctor about what kind of movement is right for you.*
>
> Your challenge: **move for five minutes on 4 of the next 6 days.**
>
> Let's get moving,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `habit` |
| Habit id | `morning_movement` |
| Title | Move for 5 minutes |
| Description | Stretch, walk, dance: anything that gets you moving |
| Icon | `directions_run` |
| Ritual | MORNING (order 3) |
| Duration | 300 seconds (timer) |
| Recommended routine | By movement needs and level: R1 Gentle Wake-Up (Beginner), R2 Energy Boost (Intermediate/Active), R3 Joint-Friendly (Low-impact), R4 Seated (Seated). See [Exercise library §4.1](../content/EXERCISE_LIBRARY.md#41-wake-up-routines-used-by-journey-1-step-4) |
| Smaller version | Move for 1 minute |
| Target / window | 4 days / 6 days |

**Letter button:** *Add "5-minute movement" to my morning*

### Tips
- Put on your movement clothes right after breakfast so you're ready.
- Play the same upbeat song every time. It becomes your cue to move.
- Done is better than perfect: one minute still counts.

### Completion message
> **You're on fire! 🔥**
> Four days of moving your body. Notice how you feel afterwards. That's the energy we're building.

### Notification copy
- Ritual reminder: "🏃 Water, breakfast, 5 minutes of movement. Your ritual is growing!"
- Nudge: "Just one minute of movement today. Put on a song and go."

---

## Step 5 — Step Into the Light

### Letter: Set Your Body Clock

> Dear {name},
>
> Inside your brain there's a tiny cluster of cells that acts as your body's master clock. It decides when you feel sleepy, when you feel alert, when your body temperature rises and falls, and when hormones are released. Scientists call this your *circadian rhythm*.
>
> Your inner clock doesn't keep perfect time on its own. Every day it needs resetting, and its most powerful signal is **light, especially morning daylight**.
>
> When bright light reaches your eyes in the morning, it tells your brain: *the day has started.* That helps you feel alert now. It also starts a countdown that makes you naturally sleepy about 14 to 16 hours later. Morning light gives you energy in the morning *and* helps you sleep at night.
>
> The trouble is that most of us spend our mornings indoors, where the light is far dimmer than outside. Even on a cloudy day, outdoor light is many times brighter than a typical room.
>
> **Your new habit: spend five minutes in daylight each morning.**
>
> - Step onto a balcony, into the garden or just outside your door.
> - Combine it with your movement: a five-minute walk outside covers both habits!
> - Drink your water or eat breakfast by an open window or outside.
> - No sunglasses for these few minutes if you can comfortably manage it. *(Never look directly at the sun.)*
> - Cloudy or rainy? Still go. It still works.
>
> **Stack it:** *"After my morning movement, I step outside for five minutes of light."*
>
> Your challenge: **get five minutes of morning daylight on 3 of the next 5 days.**
>
> Here comes the sun,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `habit` |
| Habit id | `morning_light` |
| Title | Get 5 minutes of daylight |
| Description | Step outside or sit by an open window |
| Icon | `wb_sunny` |
| Ritual | MORNING (order 4) |
| Duration | 300 seconds (optional timer) |
| Smaller version | Open the curtains and stand by the window for one minute |
| Target / window | 3 days / 5 days |

**Letter button:** *Add "Morning light" to my morning*

### Tips
- Walk outside for your five-minute movement and complete two habits at once.
- Open all the curtains as soon as you wake up.
- Have your coffee or tea outside.

### Completion message
> **Sunshine unlocked! ☀️**
> Your body clock thanks you. You may find you're getting sleepy at a more regular time at night.

### Notification copy
- Ritual reminder: "☀️ Your morning ritual: water, breakfast, movement, light."
- Nudge: "Open a window and look outside for a minute. It counts."

---

## Step 6 — Celebrate Yourself

### Letter: The Secret Ingredient

> Dear {name},
>
> You've now built a morning ritual with four habits. Take a moment to really notice that. A few weeks ago, this was just an idea.
>
> Today's habit might seem strange at first, but research suggests it may be one of the most important of all: **celebrate**.
>
> Behaviour scientist BJ Fogg, who has studied habit formation at Stanford for many years, puts it simply: *emotions create habits.* Repetition matters, but it's the positive feeling *right after* a behaviour that tells your brain "this is good, let's do it again." Many of us skip this completely. We finish something and immediately move on to the next task, or worse, criticise ourselves for not doing it better.
>
> So we're going to add a tiny moment of celebration at the **end of your morning ritual**.
>
> **Find a celebration that feels natural to you:**
> - A fist pump and a quiet "Yes!"
> - Smiling and saying "I did it" or "That's like me"
> - A little victory dance
> - Placing a hand on your heart and taking one deep breath
> - Imagining a crowd cheering for you (it sounds silly until you try it)
>
> The key is to *feel* something positive, even for two seconds. It may feel awkward at first. That's okay. Do it anyway, and notice the small spark of pride. That spark is what wires the habit into your brain.
>
> When you complete your full morning ritual in the app, you'll see a celebration screen too. But the celebration that matters most is the one *you* feel.
>
> Your challenge: **celebrate after your morning ritual on 3 of the next 5 days.**
>
> You deserve it. 🎉
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `habit` |
| Habit id | `celebrate` |
| Title | Celebrate! |
| Description | A fist pump, a smile, a "Yes!": feel proud for a moment |
| Icon | `celebration` |
| Ritual | MORNING (order 5, always last) |
| Smaller version | Smile and say "I did it" |
| Target / window | 3 days / 5 days |

**Letter button:** *Add "Celebrate" to my morning*

### Tips
- Celebrate after *any* habit, not just at the end. More celebration, faster habits.
- Pick one celebration and use it every time so it becomes automatic.
- Notice the feeling. That's the point.

### Completion message
> **Look at you celebrating! 🎉**
> Your ritual is almost complete. One more step to make it truly yours.

### Notification copy
- Ritual reminder: "🎉 5 habits, 1 amazing morning. Don't forget to celebrate!"
- Nudge: "Do one habit, then celebrate it. That's a win today."

---

## Step 7 — Design Your Morning

### Letter: Make It Yours

> Dear {name},
>
> Water, breakfast, movement, light, celebration. You now have all the ingredients of an energising morning. Before the final challenge, let's turn these separate habits into **one ritual that's truly yours**.
>
> Today's step is a one-time action with two parts.
>
> **Part 1: Write your ritual.**
>
> Look at your morning habits in the app and arrange them in the order that fits your real mornings. You can drag them to reorder. There's no "right" order. Some people move before breakfast, some combine light and movement into a walk, and some drink their water while the kettle boils.
>
> Then say your ritual out loud, or write it on a sticky note for your mirror:
>
> > *"When I wake up, I drink my water. Then I ______. Then I ______. Then I ______. And then I celebrate."*
>
> Turning it into a chain like this helps each habit trigger the next, until the whole ritual flows without thinking.
>
> **Part 2: Choose a consistent wake-up time.**
>
> Your body clock loves regularity. Waking at the same time each day, *including weekends*, within about an hour, is one of the most powerful things you can do for steady energy. Sleeping in on Saturday and Sunday and then dragging yourself up on Monday can feel a lot like jet lag.
>
> Your current wake-up time is set to **{wakeTime}**. Is that realistic seven days a week? If not, adjust it in Settings. A time you can keep beats an ambitious time you can't.
>
> When you've arranged your ritual and confirmed your wake-up time, tap **Mark as done**.
>
> Designing your life, one morning at a time,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `action` |
| Title | Arrange your morning ritual and set a consistent wake-up time |
| Description | Reorder your habits, then confirm your wake-up time |
| Icon | `edit_note` |
| In-app shortcut | Button *Arrange my ritual* opens the ritual editor; button *Check wake-up time* opens Settings → Ritual times |

**Letter button:** *Let's design it*

### Completion message
> **Your ritual, your way. ✍️**
> One final challenge stands between you and a morning ritual that runs on autopilot.

---

## Step 8 — The Morning You

### Letter: Putting It All Together

> Dear {name},
>
> This is it: the final step of your first journey.
>
> Think back to where you started. You were drinking a single glass of water. Now you have a full morning ritual: hydration, real fuel, movement, daylight and a moment of pride. You've designed it to fit your life, and you've already practised every single part of it.
>
> Your final challenge is to **complete your whole morning ritual on 5 of the next 7 days.**
>
> This is where habits start to become automatic. Research from University College London found that it takes people anywhere from about three weeks to several months for a new behaviour to feel automatic, around two months on average. You've been building this for weeks already. You're further along than you think.
>
> **A few things to remember:**
>
> - **Use the ritual player.** Tap *Start ritual* and the app will walk you through each habit, one at a time. No thinking required.
> - **Bad days happen.** If a morning goes wrong, do the smallest version of each habit: a few sips of water, a piece of fruit, one minute of movement, one look out the window, one smile. A tiny ritual still keeps the chain alive.
> - **Notice the difference.** At the end of the week, ask yourself: *How do my mornings feel now compared with a month ago? How is my energy in the afternoon?*
>
> Five mornings out of seven. You've already done the hard part. Now it's time to become the person who simply starts every day this way.
>
> Cheering for you every morning,
> **Your Fab Life coach**

### Goal
| Field | Value |
|---|---|
| Type | `ritual` |
| Ritual | MORNING (all habits) |
| Target / window | 5 days / 7 days |

**Letter button:** *Start the final challenge*

### Tips
- Tap *Start ritual* every morning. Let the app guide you.
- Prepare the night before (water, breakfast, clothes).
- On hard days, do the smaller version of every habit.

### Completion message
> **You did it! 🏆**
> Your morning ritual is complete. Read your final letter.

### Notification copy
- Ritual reminder: "🌅 Your morning ritual is waiting. Tap to begin."
- Nudge: "Mini ritual today? Sip, bite, stretch, look outside, smile. Done."

---

## Outro Letter — You Did This

**Shown:** on the *Journey complete* screen, below the summary stats (days active, habits built, longest streak).

> Dear {name},
>
> Stop for a moment and take this in: **you completed your first journey.**
>
> A few weeks ago you started with one glass of water. Since then you've built a morning ritual that hydrates you, fuels you, moves you, sets your body clock and celebrates you. Most people who decide to "get healthier" never get this far. You did, and not through some dramatic transformation, but one small step at a time.
>
> That's the most important lesson of this journey. It's not about water or breakfast or five minutes of stretching. It's this: **you can change your life through small actions repeated with kindness.** You've proven it to yourself.
>
> **What happens now?**
>
> Your morning ritual stays with you. Keep doing it. It's yours now, and the app will keep tracking your streaks. Over time it will stop feeling like something you *do* and become simply how your mornings *are*.
>
> Some days you'll miss it. That's fine. A missed day never erases the progress you've made. Just begin again the next morning.
>
> When you feel ready, there are more journeys ahead: better sleep, deeper focus, calmer days. Each one builds on the foundation you've created.
>
> But for today, celebrate. Really celebrate. You've earned it.
>
> So proud of you,
> **Your Fab Life coach**

**Buttons:** *Celebrate 🎉* (replays celebration animation) · *See what's next*

---

## Supporting Letter — Missed a Few Days?

**Shown:** as a card on Today when the active habit challenge has had no check-ins for 3 consecutive days (spec §6.6).

> Dear {name},
>
> I noticed it's been a few days since you checked in. First, the most important thing: **that's completely normal, and it's okay.**
>
> Life gets busy. People get sick, travel, have rough nights and sometimes simply forget. In a well-known study on habit formation, researchers found that missing a day here and there did *not* undo people's progress. What mattered was getting back on track.
>
> So let's get back on track, gently. Which of these would help most?
>
> - **Keep going.** Nothing needs to change. Tomorrow is a fresh start.
> - **Make it smaller.** If the habit feels like too much right now, shrink it. A smaller habit you actually do beats a bigger one you skip.
> - **Change the reminder time.** Maybe your reminder arrives at the wrong moment. Let's move it.
>
> Remember: the goal isn't perfection. It's showing up more often than not.
>
> Here whenever you're ready,
> **Your Fab Life coach**

**Buttons:** *Keep going* · *Make it smaller* · *Change reminder time*

---

## Supporting Letter — Welcome Back

**Shown:** on app open after 7+ days of no check-ins (spec §6.6). Active step progress resets; completed steps and habits remain.

> Dear {name},
>
> Welcome back! I'm really glad you're here.
>
> Whatever kept you away, whether it was a busy stretch, a hard time or just life, there's no need to explain and no reason to feel guilty. The fact that you opened this app again says something important: you still care about feeling your best.
>
> Here's what hasn't changed: **everything you've already built is still here.** Every step you completed and every habit you learned is waiting for you. Your body remembers more than you think.
>
> We'll restart your current step from the beginning, so there's no pressure to catch up. Just do the first habit tomorrow morning. Only that. Let the rest follow.
>
> Starting again isn't failing. Starting again *is* the habit.
>
> Glad to have you back,
> **Your Fab Life coach**

**Button:** *Let's start again*

---

## Content Guidelines (for future journeys)

- **Voice:** warm, encouraging, like a knowledgeable friend. Never guilt, shame or pressure.
- **Structure of a step letter:** hook (a relatable moment) → why it matters (plain-language science) → the habit, stated in bold → how to make it stick (3–5 practical tips) → "Stack it" line → challenge statement → sign-off.
- **Length:** 250–450 words per letter.
- **Science:** state findings carefully ("research suggests", "studies have found"). No medical claims; add a doctor note where physical health could be affected.
- **Inclusivity:** offer food and movement examples across cultures, budgets and abilities.
- **Every habit has a smaller version** for bad days.
