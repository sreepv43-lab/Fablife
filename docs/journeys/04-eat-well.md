# Journey 4 — Eat Well

**Journey id:** `eat` · **Rituals built:** Afternoon (lunch and snacks) and Evening (a lighter dinner) · **Length:** 8 steps, about 4 to 6 weeks
**Subtitle:** Simple, kind, healthy eating for everyday life

This document is the source content for the fourth journey, in the format defined in [DESIGN_PHASE2.md §2](../DESIGN_PHASE2.md). It is parsed by `tools/journey_md.py` and checked by `tools/validate_content.py`. Breakfast has its own place in journey 1 (Feel More Energetic), so this journey looks at lunch, snacks and dinner. It is not about weight loss, calories or dieting, and it never labels foods as good or bad.

```json meta
{"id": "eat", "goalKey": "eat", "order": 4, "title": "Eat Well",
 "subtitle": "Simple, kind, healthy eating for everyday life", "emoji": "🥗", "ritualFocus": "AFTERNOON"}
```

## Journey Overview

| # | Step | Goal type | Habit / action | Target |
|---|---|---|---|---|
| — | Intro letter: *Eating Well, Kindly* | — | — | — |
| 1 | Eat One Meal Mindfully | Habit | One meal without screens, a few mindful bites (Afternoon, tool: Mindful eating) | 3 days in 5 |
| 2 | Add Some Vegetables | Habit | Vegetables in one meal (Afternoon) | 3 days in 5 |
| 3 | Sip Smarter | Habit | Swap one sugary drink for water, buttermilk or unsweetened tea (Afternoon) | 3 days in 5 |
| 4 | A Plate That Keeps You Going | Habit | Round out your lunch plate: vegetables plus a protein and a grain (Afternoon) | 3 days in 5 |
| 5 | Plan and Prep | One-time action | Pick a few meals, write a short list, prep one thing | Once |
| 6 | A Lighter, Earlier Dinner | Habit | A lighter or earlier dinner, finished a few hours before bed (Evening) | 3 days in 5 |
| 7 | Snacks That Satisfy | Habit | A snack with protein or fibre, eaten sitting down (Afternoon) | 3 days in 5 |
| 8 | The Nourished Afternoon | Ritual | Complete the full afternoon ritual | 4 days in 7 |
| — | Outro letter: *A Kinder Way to Eat* | — | — | — |

Every food step carries a `gentleNote`: a low-fibre alternative for sensitive stomachs. Supporting letters: *Missed a Few Days?* and *Welcome Back*.

---

## Intro Letter — Eating Well, Kindly

> Dear {name},
>
> Think of the last time you ate lunch while answering messages, scrolling or watching something, and then looked down to find the plate empty. Most of us have been there. On a busy day, food becomes something we fit in around everything else.
>
> Eating well doesn't need to be complicated, expensive or stressful. It doesn't need a special diet or a cupboard of unusual ingredients. A good deal of it comes down to a few small, repeatable habits: paying a little attention while you eat, giving vegetables a place on your plate, choosing water or buttermilk more often, building a lunch that keeps you going, and eating a lighter dinner. Everyday foods from many kitchens already do much of the work: dal, curd, millets, eggs, seasonal vegetables, tinned beans, frozen vegetables, paneer, tofu and fish.
>
> **What this journey is not.** It isn't about weight loss, calories or dieting, and no food is "good" or "bad". There are no rules to break and nothing to feel ashamed of. Food is also culture, family, celebration and comfort, and all of that belongs here.
>
> **Please read this part.** If thinking about food feels stressful, if you have had an eating disorder, if you have a medical condition such as diabetes, kidney disease, IBS or coeliac disease, or if you are pregnant, please follow the advice of your doctor, dietitian or mental health professional first. Treat these letters as general ideas, not personal medical advice, and change or skip anything that doesn't suit you.
>
> Over the next few weeks we'll add one small habit at a time. Breakfast already has a home in the Feel More Energetic journey, so here we'll build an **afternoon ritual** for lunch and snacks, plus one lighter-dinner habit for the evening.
>
> Two things to know about the **Eat** tab in Explore. Every meal idea there comes with a recipe. And it has a **gentle mode**, "Gentle on the stomach (low-fibre)", which shows easy-to-digest meals if raw salads, beans or whole grains upset your stomach. A gentle, low-fibre way of eating is usually only needed for a short time, or on a doctor's advice. If you have been told to eat this way, ask your doctor or dietitian when and how to add fibre back. If you have diabetes, coeliac disease or kidney disease, ask them which gentle foods suit you. And if you have stomach pain that doesn't settle, blood in your stool, black stools, unexplained weight loss, ongoing vomiting or diarrhoea, or trouble swallowing, please see a doctor rather than relying on food changes.
>
> Here's what I ask of you:
>
> - **Start small.** Smaller than you think you need to.
> - **Adapt freely.** Swap any food for one that suits your culture, budget, health and taste.
> - **Be kind to yourself.** A rushed or imperfect meal is simply a meal.
>
> Your first step takes about five minutes, at a meal you were having anyway. Let's begin.
>
> With you on the journey,
> Your Fab Life coach

**Button:** *Let's begin*

---

## Step 1 — Eat One Meal Mindfully

```json step
{
  "id": "eat.mindful_meal",
  "goal": {"type": "habit",
           "habit": {"id": "mindful_meal", "title": "Eat one meal without screens", "description": "Put screens away and take your first few bites slowly", "icon": "meditate",
                     "ritual": "AFTERNOON", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Take three slow, screen-free bites at the start of one meal",
                     "tool": {"type": "GUIDED", "ref": "mindful_eating"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Mindful meal\" to my afternoon",
  "tips": [
    "Put your phone face down, and out of reach if you can, before you serve your food.",
    "Take your first three bites slowly: look, smell, taste, and chew until the flavour changes.",
    "Try the five-minute Listen session once, before a meal or with a snack, to learn the feel. The meal itself stays screen-free.",
    "Short on time? Three slow, screen-free bites still count."
  ],
  "completionTitle": "Your meals have room to breathe! 🍽️",
  "completionMessage": "Three days of tasting your food. Next, let's give vegetables a place on your plate.",
  "gentleNote": "Mindful eating can be done with any food. For a sensitive stomach, try curd rice, idli with curd, congee, or white rice with strained moong dal and soft cooked vegetables, and chew each bite well. A gentle, low-fibre way of eating is usually only needed for a short time, or on a doctor's advice. If you have been told to eat this way, ask your doctor or dietitian when and how to add fibre back. If you have diabetes, coeliac disease or kidney disease, ask them which gentle foods suit you."
}
```

### Letter — Slow Down for a Few Bites

> Dear {name},
>
> Think of the last time you reached the final bite of a samosa or a sandwich and couldn't quite remember the first one. You're not alone. On busy days, the noticing is the first thing to go.
>
> Research suggests this matters. Reviews of studies on distracted eating find that people who ate while distracted tended to eat a little more at that meal or at later snacks, and remembered the meal less well. The effects are small and vary from person to person. That doesn't mean you did anything wrong. Attention is part of how we notice flavour, hunger and fullness, and those signals take a little while to travel between your stomach and your brain.
>
> So our first habit asks for very little: **eat one meal a day without screens, starting with a few slow, mindful bites.**
>
> You don't have to eat the whole meal in silence. Just begin well.
>
> - Put your phone face down, out of reach if you can, and switch off the TV for this meal. On call, caring for someone, or using your phone for health monitoring? Keep it face down and let only calls or alerts through.
> - Sit down with your food on a plate or in a bowl, even if you only have ten minutes. Feeding a little one? Three slow bites while they eat still count.
> - Take your first three bites slowly. Look at the food, notice the smell, and chew until the flavour changes. Eating with your hands, chopsticks or a spoon, it works the same way.
> - Tap **Listen** on the habit for a five-minute guided mindful eating session, read aloud with on-screen captions. It's a practice, not part of the screen-free habit: try it once before a meal, or with a snack, to learn the feel, then enjoy the meal itself without screens.
> - Eating with family or friends? Put the phones in a basket and let the conversation be the company.
>
> Mindful eating isn't about eating less, eating perfectly or feeling calm at every meal. Noticing is the whole point. And if your stomach is sensitive, slow bites and thorough chewing may help whatever you're eating.
>
> One more thing: in Settings, under Rituals & reminders, you can move your Afternoon reminder to just before your main meal, so the nudge arrives while there is still a meal to enjoy.
>
> *If thinking about food or hunger feels stressful, or you have had an eating disorder, treat the guided session as optional and follow your care team's advice. If you have trouble swallowing or ongoing stomach discomfort, please talk to a doctor.*
>
> **Stack it:** *"After I serve my lunch, I put my phone away and take three slow bites."*
>
> Your challenge: **eat one meal without screens, starting with three mindful bites, on 3 of the next 5 days.**
>
> One slow bite at a time,
> Your Fab Life coach

---

## Step 2 — Add Some Vegetables

```json step
{
  "id": "eat.add_veg",
  "goal": {"type": "habit",
           "habit": {"id": "add_vegetables", "title": "Add vegetables to one meal", "description": "A cupped handful of vegetables, fresh, frozen or tinned", "icon": "apple",
                     "ritual": "AFTERNOON", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Add a spoonful of cooked vegetables, or a few slices of cucumber or carrot",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Vegetables\" to my afternoon",
  "tips": [
    "Keep something easy to hand for days when you can't face chopping: frozen peas or mixed vegetables, tinned tomatoes, or cabbage, carrot and pumpkin, which keep for days without a fridge.",
    "Add to what you already cook: grated carrot in a paratha, spinach in dal, peas in rice or noodles.",
    "Buy what's in season. It's usually the cheapest and tastes the best.",
    "Don't love vegetables? Start with the ones you like and cook them with spices, garlic or lemon."
  ],
  "completionTitle": "A little more colour on your plate! 🥕",
  "completionMessage": "Three days of adding vegetables. Next, let's take a look at your drinks.",
  "gentleNote": "Try well-cooked, peeled, soft vegetables such as lauki, pumpkin, carrot, peeled ridge gourd or seedless zucchini, cooked until a fork mashes them, instead of raw salads. A doctor or dietitian can tell you how long to eat this way."
}
```

### Letter — A Little More Colour

> Dear {name},
>
> Look back at your last few meals. How many had a vegetable you could actually point to? When we're busy or tired of chopping, vegetables are often the first thing to slip off the plate.
>
> Health bodies such as the World Health Organization suggest aiming for at least 400 grams of vegetables and fruit a day, roughly five handfuls, and surveys in many countries find that most adults eat less than that. Large studies have linked eating more vegetables and fruit with better health over the years, although studies like these can't prove cause. Vegetables also bring fibre, vitamins, minerals and water, and they add volume and flavour to a meal.
>
> So our next habit is small: **add vegetables to one meal a day.** One meal, not all three. A portion is about a cupped handful.
>
> **Making it easy**
>
> - **Fresh, frozen and tinned all count.** Frozen peas, spinach or mixed vegetables are cheap, already chopped and keep for months, if you have a freezer. No freezer? Cabbage, carrot, onion and pumpkin keep for days without a fridge. Tinned tomatoes and sweetcorn work too. Choose no-added-salt versions where you can, and rinse tinned beans and sweetcorn to wash off extra salt.
> - **Cook with the season.** Local, seasonal vegetables from the market are usually the best value.
> - **Add them to what you already eat.** Spinach in dal, peas in poha or fried rice, a tomato-cucumber kachumber on the side, greens in noodle soup, peppers in a sandwich.
> - **Cook once, eat twice.** A big pot of sabzi or stir-fried vegetables can cover two or three meals if you keep it in the fridge. Cool it quickly, refrigerate it within an hour or two, eat it within two to three days, and reheat it until steaming hot. No fridge? Cook just enough for the same day.
>
> **Gentle option.** Raw salads and fibrous vegetables can be hard going when your stomach is sensitive, but cooking changes that. Steam or simmer vegetables until soft, peel and deseed where you can, and serve them mashed, in a smooth soup or in a mild sabzi. Even a few spoonfuls count.
>
> *If you take a blood-thinning medicine such as warfarin, or you have kidney disease, please ask your doctor before changing how much leafy green or high-potassium vegetable you eat.*
>
> **Stack it:** *"While I put my lunch together, I add a portion of vegetables."*
>
> Your challenge: **add vegetables to one meal on 3 of the next 5 days.**
>
> Colourfully yours,
> Your Fab Life coach

---

## Step 3 — Sip Smarter

```json step
{
  "id": "eat.swap_drink",
  "goal": {"type": "habit",
           "habit": {"id": "swap_sugary_drink", "title": "Swap one sugary drink", "description": "Choose water, buttermilk or unsweetened tea instead of an everyday sugary drink. Keep any sweet drink you need for low blood sugar.", "icon": "water",
                     "ritual": "AFTERNOON", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Halve the sugar in one drink, or pour a glass of water to have alongside it",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Swap a sugary drink\" to my afternoon",
  "tips": [
    "Keep a bottle or glass of safe drinking water, boiled and cooled or filtered if needed, where you can see it, and fill it first thing.",
    "Make your swap at the time you usually reach for the sweet drink, or with lunch if that's easier.",
    "Step down gradually: halve the sugar in your tea for a week, then halve it again.",
    "Don't drink sugary drinks? Wonderful. Your habit is simply a glass of water or buttermilk with lunch."
  ],
  "completionTitle": "A smarter sip! 💧",
  "completionMessage": "Three days of choosing a lighter drink. Next, let's build a lunch plate that keeps you going.",
  "gentleNote": "Sip slowly. Warm or plain water, thin buttermilk, weak tea and rice kanji are kind to a sensitive stomach, while fizzy drinks and strong coffee can irritate it. If a doctor has given you advice about drinks, follow that."
}
```

### Letter — A Smarter Sip

> Dear {name},
>
> The kettle clicks off, the fridge door hangs open, and a cold fizzy drink or a sweet chai suddenly looks like the obvious answer. That's completely understandable. Sweet drinks are easy, familiar and often a small comfort.
>
> Here's what studies have found. In many countries, drinks with added sugar, such as fizzy drinks, sweetened packaged juices, sweetened tea and coffee, and energy drinks, are among the biggest sources of added sugar in people's eating. Liquid sugar also tends to be less filling than the same amount in food, so it is easy to take in a lot without noticing. Health organisations, including the WHO, suggest keeping added sugars to a small part of what we eat, and many people go over that.
>
> That makes this a good place for a gentle swap: **replace one sugary drink with water, buttermilk or unsweetened tea.** Just one.
>
> This is not a ban. A sweet chai with a friend or a festival lassi is part of life. We're only looking at the automatic ones.
>
> **Swaps to try**
>
> - Water, plain or with lemon, mint, cucumber or a pinch of roasted cumin.
> - Buttermilk (chaas) or a plain lassi.
> - Unsweetened tea or coffee, or a herbal tea such as ginger or tulsi. Coffee and strong tea sit better earlier in the day.
> - Sparkling water with a squeeze of lime, if you like it. Plain water is just as good.
> - Coconut water. It has natural sugars, so one glass is plenty.
>
> Taste buds adjust, too. Research suggests our liking for sweetness can shift over several weeks to a few months, and it varies from person to person, so if the first sip tastes plain, give it a little time.
>
> **Gentle option.** If your stomach is sensitive, fizzy drinks and strong coffee may not sit well. Warm water, thin buttermilk, weak tea or rice kanji are usually kinder.
>
> *If you have diabetes, please follow your care team's advice. If you keep a sweet drink to treat low blood sugar, keep doing that: this habit is about everyday sipping, not emergencies. If you have kidney or heart problems, or you've been given advice about how much to drink, please follow it. Coconut water and bananas are high in potassium, so if you have kidney disease, check with your doctor first.*
>
> *If caffeine makes you anxious or affects your sleep, or you are pregnant, choose water, buttermilk or a caffeine-free drink, and ask your doctor about caffeine limits. If you are pregnant or take regular medicines, check herbal teas with your doctor or pharmacist first. If dairy doesn't suit you, pick water or tea instead of buttermilk.*
>
> **Stack it:** *"When I reach for my usual sweet drink, I pour a glass of water or chaas first. If I don't have one, I pour it when I sit down for lunch."*
>
> Your challenge: **swap one sugary drink (or add a glass of water or buttermilk) on 3 of the next 5 days.**
>
> Sip by sip,
> Your Fab Life coach

---

## Step 4 — A Plate That Keeps You Going

```json step
{
  "id": "eat.balanced_plate",
  "goal": {"type": "habit",
           "habit": {"id": "balanced_plate", "title": "Round out your lunch plate", "description": "Vegetables, plus a protein and a grain", "icon": "plate",
                     "ritual": "AFTERNOON", "order": 4, "durationSeconds": null,
                     "smallerVersion": "Add one protein food to your lunch, such as dal, beans, an egg, paneer, tofu, curd or fish",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Lunch plate\" to my afternoon",
  "tips": [
    "Someone else cooks, or you eat at a canteen or mess? Pick the most protein-rich thing on offer, and ask for the vegetables you're already adding.",
    "Dal, eggs, curd, tinned beans and tofu are some of the most affordable proteins.",
    "A plate is a picture, not a measuring tool. Eyeballing it is fine.",
    "Eating out or packing a tiffin? Look for the same pattern: something green, something protein, something grain."
  ],
  "completionTitle": "A plate that works for you! 🍛",
  "completionMessage": "Three days of rounding out your lunch. Next, one small planning step makes all of this easier.",
  "gentleNote": "Gentle plate: white rice, idli or a soft phulka (rice or idli if you avoid gluten); strained moong dal, eggs, paneer, tofu or fish; soft cooked, peeled vegetables. Go easy on raw salads, beans, seeds and skins. With diabetes, coeliac or kidney disease, ask your doctor which suit you."
}
```

### Letter — A Plate That Keeps You Going

> Dear {name},
>
> You've sat down, added some vegetables and poured something refreshing. Now let's look at the lunch itself. A common pattern is a plate that's mostly rice, bread or noodles with a little something on the side, and then hunger returns by mid-afternoon.
>
> In meal studies, protein tends to make a meal feel more filling, and eating carbohydrate foods together with protein and fibre slows how quickly they are digested. That means a slower rise in blood sugar and, for many people, steadier energy. Health guidelines in countries including India, the UK and the US use a similar "plate" picture to show this.
>
> Your vegetables are already doing their part, thanks to the habit from step two. Here's what we'll add: **round out your lunch plate with a protein and a grain, beside your vegetables.**
>
> Picture the plate like this, as a guide and not a rule:
>
> - **About half vegetables, and fruit if you like.** This is your step-two habit. Sabzi, salad, stir-fried greens, soup, a piece of fruit.
> - **About a quarter protein.** This is the new part. Dal, rajma, chana, beans, eggs, paneer, tofu, curd, fish or chicken.
> - **About a quarter grains.** Roti, rice, millets such as ragi or jowar, quinoa, bread, noodles, tortillas, ugali or couscous.
> - **A little oil, nuts or seeds,** if you like.
>
> Some days it will be a bowl, a tiffin or a sandwich, and that works too.
>
> **Plates from different kitchens:** roti, dal, seasonal sabzi, cucumber and curd. Rice, sambar, poriyal and a boiled egg. Tinned chickpeas with rice and frozen vegetables. Tofu and vegetable stir-fry with noodles. Fish, rice and sautéed greens. Beans with tortillas or rice. Injera or ugali with lentils and greens. Couscous with chickpeas and roasted vegetables.
>
> **Gentle option.** The plate picture works for gentle eating too. Soften each part: cooked, peeled vegetables instead of raw salad, a tender protein, and a soft grain, with congee (jook) a much-loved classic.
>
> *If you have diabetes, kidney disease or another condition, or you are pregnant, your portions may differ, and a doctor or dietitian can tailor the plate to you. If you have a small appetite, are underweight, growing, very active or recovering from illness, you may need more grains or protein, so adjust freely, and if you've lost weight without trying, please talk to your doctor. If thinking about food feels stressful, or you are in recovery from an eating disorder, please skip this step and follow the plan from your care team.*
>
> **Stack it:** *"After I pour my drink, I add a protein and a grain to the vegetables on my plate."*
>
> Your challenge: **round out your lunch plate with a protein and a grain on 3 of the next 5 days.**
>
> Bon appétit,
> Your Fab Life coach

---

## Step 5 — Plan and Prep

```json step
{
  "id": "eat.plan_prep",
  "goal": {"type": "action",
           "title": "Plan a few meals and prep one thing",
           "description": "Pick a few meals and prep one thing",
           "shortcuts": [{"label": "Browse meal ideas", "target": "explore_eat"}]},
  "buttonLabel": "I'll plan and prep",
  "tips": [],
  "completionTitle": "Future-you says thank you. 🛒",
  "completionMessage": "A little planning makes the easy choice the default. Next, let's make dinner a little lighter.",
  "gentleNote": "For a gentle week, switch on gentle mode in the Eat tab and prep soft foods: a little extra white rice for tomorrow, boiled and peeled potatoes, boiled eggs or strained moong dal. Check each recipe against your doctor's advice."
}
```

### Letter — Make the Easy Choice the Default

> Dear {name},
>
> It's one o'clock, you're hungry, nothing is ready, and the quickest option wins. That isn't a lack of willpower. Deciding what to eat when you're tired and hungry is hard for anyone. Studies of household meal planning suggest that people who plan their meals tend to eat a wider variety of foods and waste less, and planning takes the daily guesswork out.
>
> So today there's no habit to repeat. There's **one small action: plan a few meals and prep one thing.** It takes ten to twenty minutes, once. Short on time or energy? Pick just two meals and prep one thing, or only write the list.
>
> **Three parts**
>
> 1. **Pick your meals.** After you tap *I'll plan and prep*, use the **Browse meal ideas** button on this step's card on Today (or open Explore, then Eat). Choose three lunches, two dinners and, if you like, a snack or two. Dinners can be light and simple, since that's our next step, and you can change your mind later. Ideas are filtered by your diet type, every meal has a recipe with ingredients and steps, and gentle mode is there if your stomach needs it.
> 2. **Write a short list.** Think protein (dal, eggs, paneer, tofu, fish, tinned beans), vegetables (seasonal, plus frozen if you have a freezer), grains (rice, millets, atta) and curd or buttermilk. Check what you already have first. Dried dal and seasonal produce keep costs down. If you buy food daily, planning just tomorrow counts.
> 3. **Prep one thing.** Wash and chop vegetables, cook a pot of dal, boil a few eggs, cook a little extra rice or millet for tomorrow, or make a chutney. Just one.
>
> **Keep it safe.** Cool cooked food quickly and get it into the fridge within an hour or two. Eat rice within a day and other dishes within two to three days, and reheat them only once, until steaming hot. Cooked rice especially shouldn't sit out. No fridge, or a hot day? Cook just enough for today.
>
> **If cooking is hard right now:** because of low energy, pain, limited mobility, a shared kitchen or no kitchen at all, choose helpers that need no cooking or fridge, such as fruit, curd, bread, rinsed tinned chickpeas, roasted chana or peanuts, shop-bought boiled eggs, and cabbage, carrot, onion or pumpkin. Ask someone to help, or swap prep tasks with a friend. Small counts.
>
> **If someone else cooks,** or you eat at a canteen, mess or hostel, share your picks with them. Or choose the most vegetable- and protein-rich things on offer, and prep one thing you control, such as a fruit or curd to carry.
>
> *If you have a food allergy or a medical condition, check the recipes against the advice you've been given.*
>
> **Stack it:** *"The next time I shop, or on my day off with a cup of tea, I plan and prep."*
>
> When you've planned your meals and prepped one thing, come back and tap **Mark as done**.
>
> Planning ahead, kindly,
> Your Fab Life coach

---

## Step 6 — A Lighter, Earlier Dinner

```json step
{
  "id": "eat.light_dinner",
  "goal": {"type": "habit",
           "habit": {"id": "lighter_dinner", "title": "Have a lighter, earlier dinner", "description": "Finish eating a few hours before bed, with a lighter, easier-to-digest meal. Either lighter or earlier counts.", "icon": "moon",
                     "ritual": "EVENING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Finish dinner 30 minutes earlier than usual, or swap one heavy item for something lighter",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Lighter dinner\" to my evening",
  "tips": [
    "Count back two to three hours from your bedtime to find a good time to finish eating.",
    "Late-eating household or shift work? Keep dinner lighter rather than earlier. Either one is a good place to start.",
    "Hungry later? A banana, curd or a small glass of warm milk (or soy milk) is perfectly fine.",
    "Make lunch your biggest meal on busy days if you can, then keep dinner simple."
  ],
  "completionTitle": "Dinner that lets you rest! 🌙",
  "completionMessage": "Three days of a lighter evening meal. Next, let's make afternoon snacks more satisfying.",
  "gentleNote": "Gentle dinner: soft moong dal khichdi with white rice and curd, idli with strained dal, congee with egg, a smooth vegetable soup with paneer cubes, or steamed fish with rice and cooked carrot. Skip raw salad at night. If a doctor has advised gentle eating, follow their plan."
}
```

### Letter — Dinner That Lets You Rest

> Dear {name},
>
> Some evenings dinner arrives late, large and in a hurry, and then bedtime comes with a full, heavy stomach. It's a very common pattern after a long day, and it can make lying down less comfortable.
>
> Some studies link eating very close to bedtime with poorer sleep for some people, and with heartburn for those prone to reflux. Others link late, heavy meals with higher blood sugar afterwards. The evidence is mixed and everyone differs, but many people find they feel better when they finish eating a couple of hours before bed and the meal is lighter.
>
> Which brings us to our one evening habit: **have a lighter, earlier dinner.** Either one counts, lighter or earlier, or both.
>
> "Lighter" doesn't mean smaller, or skipping food, or going hungry. It means easier to digest and not rushed: plenty of vegetables, a moderate portion of grains, some protein, and less that is deep-fried, very oily or very spicy. "Earlier" means a little earlier than usual, in a way that fits your household.
>
> - Your bedtime is set to **{bedTime}**. Count back two to three hours to find your ideal "finished eating" time. If that isn't realistic, keep dinner lighter instead.
> - Ideas from many kitchens: vegetable khichdi with curd; dal, one or two rotis and stir-fried vegetables; soup with paneer or tofu; grilled fish with vegetables; egg bhurji with roti; miso soup with rice and tofu; lentil soup with bread.
> - Evenings busy? Eat your biggest meal at lunch if you can, and keep dinner simple.
> - Hungry later? A banana, curd or a small glass of warm milk or soy milk is fine.
> - Your Evening reminder starts an hour before bedtime, which may be after you've eaten. If you'd like a nudge at dinner time, move it in Settings, or simply tick this off any time later in the evening.
>
> **Gentle option.** Keep it soft, warm and simple, and leave raw salad for lunchtime if your stomach is sensitive.
>
> *If you take diabetes medicine or insulin, please don't change when you eat without checking with your doctor. If you have frequent heartburn or are pregnant, ask your doctor what suits you. If you break a fast in the evening, or dinner is your family's main meal, follow what is customary for you; this step is optional. If your appetite is small, or you've lost weight without trying, lighter is not the goal, so eat little and often and talk to your doctor. If thinking about food feels stressful, or you are recovering from an eating disorder, please skip the earlier-dinner part, or follow the meal plan your care team gave you.*
>
> **Stack it:** *"After I finish my evening chores, I sit down to a lighter dinner."*
>
> Your challenge: **have a lighter dinner on 3 of the next 5 days, finishing a few hours before bed where you can.**
>
> Sweet dreams ahead,
> Your Fab Life coach

---

## Step 7 — Snacks That Satisfy

```json step
{
  "id": "eat.smart_snack",
  "goal": {"type": "habit",
           "habit": {"id": "smart_snack", "title": "Choose a satisfying snack", "description": "Pair protein or fibre with something you enjoy, and eat it sitting down. Not a snacker? A sit-down tea break counts.", "icon": "apple",
                     "ritual": "AFTERNOON", "order": 5, "durationSeconds": null,
                     "smallerVersion": "Add a ripe banana, a small bowl of curd or a piece of fruit to whatever snack you were going to have",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Satisfying snack\" to my afternoon",
  "tips": [
    "Put your snack on a plate or in a bowl rather than eating straight from the packet.",
    "Pack an easy snack with your lunch: a fruit, a boiled egg or roasted chana. If chewing is hard, choose something soft like banana, papaya or curd.",
    "Ask yourself: am I hungry, tired, bored or thirsty? If you're hungry, eat. If you're not, skipping a snack is fine.",
    "If a favourite treat is today's snack, enjoy it sitting down, and add something satisfying beside it if you'd like."
  ],
  "completionTitle": "Snack time, sorted! 🍎",
  "completionMessage": "Three days of snacks that satisfy. Now you have every piece of your afternoon ritual.",
  "gentleNote": "Gentle snacks: ripe banana, curd or buttermilk, a boiled egg, plain crackers or white toast (rice or idli if you avoid gluten), peeled stewed apple, or smooth peanut butter on soft bread. Go easy on whole nuts, seeds, dried fruit and raw sprouts. A doctor or dietitian can say which suit you."
}
```

### Letter — Snacks That Satisfy

> Dear {name},
>
> The meeting ran over, the children are home, and the biscuit tin is calling. Snacking is completely normal, and for many people snacks are a real and useful part of the day.
>
> Nutrition studies suggest that snacks made only of refined carbohydrates, such as biscuits, white bread or sweets, are digested quickly and often leave people hungry again soon, while snacks that pair protein or fibre with a carbohydrate tend to be more filling. Studies also suggest that eating while distracted can lead to eating more, which is why step one helps here too.
>
> Our last new habit is a snack with a purpose: **choose a snack that pairs protein or fibre with something you enjoy, and eat it sitting down.**
>
> A simple formula: one protein or fibre-rich food plus one fruit or carbohydrate.
>
> - Roasted chana or peanuts with a seasonal fruit.
> - A boiled egg with a slice of toast.
> - Curd or buttermilk with a banana.
> - Apple slices with peanut butter, or hummus with cucumber and carrot sticks.
> - Makhana or plain popcorn with a few nuts.
>
> Roasted chana, peanuts, eggs, curd and seasonal fruit are among the most budget-friendly options. Makhana and hummus are nice extras, not needs.
>
> **If chewing is hard,** choose soft snacks: banana or papaya, curd, a boiled egg, soft idli, hummus on soft bread, or soaked or mashed chana.
>
> **Don't usually snack?** Then this habit is simply a sit-down pause with your usual tea or a glass of water, with a piece of fruit on days you're hungry. Skipping a snack when you're not hungry is fine too.
>
> **Gentle option.** If whole nuts, seeds and raw fruit are hard on your stomach, the same formula still works. Pair a soft protein with a soft carbohydrate, and peel or stew your fruit.
>
> *If you have a nut or peanut allergy, skip the nuts and peanut butter and choose curd, fruit or a boiled egg. Check other swaps, such as chana or hummus, with your allergy doctor. If you have diabetes, ask your care team which snacks and timings work best for you.*
>
> **Stack it:** *"After my afternoon tea or work break, I choose a snack with protein or fibre and sit down with it."*
>
> Your challenge: **choose a satisfying snack on 3 of the next 5 days.**
>
> Snack happily,
> Your Fab Life coach

---

## Step 8 — The Nourished Afternoon

```json step
{
  "id": "eat.afternoon_ritual",
  "goal": {"type": "ritual", "ritual": "AFTERNOON", "targetDays": 4, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": [
    "Tap Start ritual and let the app walk you through each habit. Tick them as they happen, and let the snack wait until snack time.",
    "Move your Afternoon reminder to just before your main meal in Settings, and tick things off on Today as the afternoon goes. No need to do it all in one sitting.",
    "On hard days, do the smaller version of every habit."
  ],
  "completionTitle": "You did it! 🏆",
  "completionMessage": "Your nourished afternoon is complete. Read your final letter.",
  "gentleNote": "For a gentle afternoon, use your gentle choices throughout: soft cooked vegetables, white rice or idli, strained dal, eggs, paneer, tofu or fish, curd or buttermilk, and a ripe banana. Ask your doctor or dietitian how long to eat this way."
}
```

### Letter — Putting Your Afternoon Together

> Dear {name},
>
> This is it: the final challenge of this journey.
>
> Look at what you've put together for your afternoon. You sit down away from screens and take a few mindful bites. You add vegetables, pour water or buttermilk, and round out a plate that keeps you going. Later, you choose a snack that satisfies. Each piece was small, and you've already practised every one of them.
>
> Your challenge: **complete your whole afternoon ritual on 4 of the next 7 days.**
>
> Research from University College London found that new behaviours took anywhere from a few weeks to several months to feel automatic, and that missing the odd day didn't derail people. So please treat this week as practice, not a test.
>
> **A few things to keep in mind**
>
> - **Use the ritual player as a checklist.** Tap *Start ritual* and the app lists each habit in the order you added them. At one meal they tend to flow like this: put lunch together with vegetables, pour your drink, round out the plate, then take your first slow bites. If you'd like that order, open *My habits* in Settings and use the arrows to rearrange. Either way, tick each one as it happens or all together after you eat, and let the snack wait until snack time.
> - **Shape it to your day.** "Afternoon" means whenever you have your main meal and snack. If you work nights, fast for religious or cultural reasons, or eat on a different schedule, change the reminder times and reorder the habits to suit your day. Set your Afternoon reminder to just before your main meal, and tick items off on Today as the day goes. If you fast and have diabetes, are pregnant or take regular medicine, please check your plan with your doctor.
> - **When a habit doesn't fit the day.** Tick the smaller version, or whatever honestly fits: water or chaas if you don't drink sweet drinks, a sit-down pause with your tea if you don't snack. On a fasting day, aim for the habits that fit. Smaller versions count, and you only need four days out of seven.
> - **Eating out or travelling?** Do what you can. Look for something green and some protein, sit down for a few bites, and let the rest go.
> - **Bad days happen.** Use the smaller versions: three slow bites, a spoonful of cooked vegetables or a few cucumber slices, a glass of water, one protein food, a banana or a bowl of curd. A tiny ritual keeps the chain alive.
> - **Your dinner habit** keeps living in your evening ritual. It stays beside this one.
>
> Gentle, low-fibre eating is usually only needed for a short time, or on a doctor's advice. If you're using gentle mode, keep going with it for as long as it helps, and ask your doctor or dietitian when and how to add fibre back. And if worries about food come up, please talk to your doctor, dietitian or a mental health professional.
>
> Four afternoons out of seven. You've already done the hard part. Now you get to enjoy it.
>
> Cheering for you at every meal,
> Your Fab Life coach

---

## Outro Letter — A Kinder Way to Eat

> Dear {name},
>
> Stop for a moment and take this in: **you completed the Eat Well journey.**
>
> Look at what you did. You learned to eat a meal without screens, to add vegetables, to choose water or buttermilk more often, to round out a lunch plate that keeps you going, to plan and prep, to enjoy a lighter dinner, and to choose snacks that satisfy. None of it needed a special diet, expensive foods or willpower of steel. It needed small steps, repeated kindly.
>
> The most important lesson isn't about vegetables or plates. It's that eating well can be simple and gentle, and you are allowed to enjoy it. No food is off the table, and no meal makes you a better or worse person. What you've built is a set of habits that make the easy choice a healthy one most of the time, and leave plenty of room for birthdays, festivals, favourite street food and family feasts.
>
> **What happens now?**
>
> - Your afternoon and evening habits stay with you in the app. Keep the ones that help, and shrink or drop any that don't fit. Adjusting is part of the process.
> - Keep using the **Eat** tab for fresh ideas. Every meal has a recipe, and gentle mode is there whenever your stomach needs it. A gentle, low-fibre way of eating is usually only needed for a short time, or on a doctor's advice. If you have been told to eat this way, ask your doctor or dietitian when and how to add fibre back. If you have diabetes, coeliac disease or kidney disease, ask them which gentle foods suit you.
> - Your needs will change with the seasons, your health, your age and your budget. A doctor or registered dietitian can help you fine-tune things, especially if you have a medical condition or are pregnant.
> - If food ever starts to feel stressful, or you notice yourself skipping meals, bingeing, making yourself sick, or feeling out of control around food, please reach out to a doctor, a mental health professional or an eating-disorder support service in your country. Asking for support is a strength. And if you ever feel unsafe, please contact your local emergency number or a crisis line.
>
> There are more journeys waiting for you in the app whenever you feel ready. But for today, enjoy a meal you love, slowly, with good company if you can.
>
> Proud of every small step,
> Your Fab Life coach

**Buttons:** *Celebrate 🎉* · *Choose my next journey*

---

## Supporting Letter — Missed a Few Days?

> Dear {name},
>
> It's been a few days since we last spoke, and I wanted to say something first: **that's completely okay.**
>
> Food is tangled up with real life: busy weeks, travel, festivals, family gatherings, illness, tight budgets and tired evenings. Some days meals are rushed, and some days they're a celebration. Neither is a failure. Research on habit formation suggests that missing the odd day doesn't undo your progress. What matters is coming back.
>
> So let's make it easy to return. Which of these would help most?
>
> - **Keep going.** Nothing needs to change. Your next meal is a fresh start.
> - **Make it smaller.** Use the smaller version of your habit: three slow bites, a spoonful of cooked vegetables or a few slices of cucumber, a glass of water. A small habit you do beats a big one you skip.
> - **Use what's easy.** A boiled egg, a bowl of curd, a banana or a handful of cooked vegetables all count. Gentle mode in the Eat tab has easy-to-digest ideas if your stomach isn't feeling great.
> - **Change your reminder time.** If the nudge arrives at the wrong moment, move it in Settings, under Rituals & reminders.
>
> And if worries about food have been taking up a lot of space lately, or you've been feeling unwell, please talk to your doctor, dietitian or a mental health professional. You don't have to work it out alone.
>
> Be kind to yourself today,
> Your Fab Life coach

**Buttons:** *Keep going* · *Make it smaller* · *Change reminder time*

---

## Supporting Letter — Welcome Back

> Dear {name},
>
> Welcome back to the table. I'm glad you're here.
>
> Whatever kept you away, whether it was a busy stretch, a trip, a festival season or a hard time, there's no need to explain and nothing to feel guilty about. The fact that you opened the app again says you still care about looking after yourself.
>
> Here's what hasn't changed: **the habits you built are still here.** The ones you added and the steps you completed are waiting for you, and so is your kitchen. Starting again is easier than it feels.
>
> We'll restart your current step from the beginning, so there's nothing to catch up on. Choose the smallest version: sit down for one meal and take three slow bites, or add one spoonful of cooked vegetables. That's plenty for today.
>
> Starting again isn't failing. It's how every habit gets built.
>
> Your seat at the table is saved,
> Your Fab Life coach

**Button:** *Let's start again*
