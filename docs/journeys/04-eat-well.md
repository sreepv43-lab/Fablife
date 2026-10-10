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
| 3 | Sip Smarter | Habit | Swap one sugary drink for water, buttermilk or unsweetened tea (Afternoon) | 4 days in 6 |
| 4 | A Plate That Keeps You Going | Habit | Half vegetables and fruit, a quarter protein, a quarter grains (Afternoon) | 3 days in 5 |
| 5 | Plan and Prep | One-time action | Pick a few meals, write a short list, prep one thing | Once |
| 6 | A Lighter, Earlier Dinner | Habit | A lighter dinner, finished a few hours before bed (Evening) | 3 days in 5 |
| 7 | Snacks That Satisfy | Habit | A snack with protein or fibre, eaten sitting down (Afternoon) | 3 days in 5 |
| 8 | The Nourished Afternoon | Ritual | Complete the full afternoon ritual | 5 days in 7 |
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
> **Please read this part.** If thinking about food feels stressful, if you have had an eating disorder, if you have a medical condition such as diabetes, kidney disease, IBS or coeliac disease, or if you are pregnant, please follow the advice of your doctor or dietitian first. Treat these letters as general ideas, not personal medical advice, and change or skip anything that doesn't suit you.
>
> Over the next few weeks we'll add one small habit at a time. Breakfast already has a home in the Feel More Energetic journey, so here we'll build an **afternoon ritual** for lunch and snacks, plus one lighter-dinner habit for the evening.
>
> Two things to know about the **Eat** tab in Explore. Every meal idea there comes with a recipe. And it has a **gentle mode**, "Gentle on the stomach (low-fibre)", which shows easy-to-digest meals if raw salads, beans or whole grains upset your stomach. Gentle eating is usually short term or guided by a doctor, and fibre is best added back slowly. You'll find gentle options throughout this journey.
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
           "habit": {"id": "mindful_meal", "title": "Eat one meal without screens", "description": "Put screens away and take your first few bites slowly", "icon": "plate",
                     "ritual": "AFTERNOON", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Take three slow, screen-free bites at the start of one meal",
                     "tool": {"type": "GUIDED", "ref": "mindful_eating"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Mindful meal\" to my afternoon",
  "tips": [
    "Put your phone face down and out of reach before you serve your food.",
    "Take your first three bites slowly: look, smell, taste, and chew until the flavour changes.",
    "Tap Listen for a five-minute guided mindful eating session whenever you'd like some company.",
    "Short on time? Three slow, screen-free bites still count."
  ],
  "completionTitle": "Your meals have room to breathe! 🍽️",
  "completionMessage": "Three days of tasting your food. Next, let's give vegetables a place on your plate.",
  "gentleNote": "Mindful eating works with any food. If your stomach is sensitive, choose a gentle meal such as curd rice, idli with curd, or white rice with strained moong dal and soft cooked vegetables, and chew each bite well. Gentle eating is usually short term or guided by a doctor, and fibre is best added back slowly."
}
```

### Letter — Slow Down for a Few Bites

> Dear {name},
>
> Have you ever finished a meal at your desk, in front of the TV or with your phone in one hand, and realised you barely tasted it? You're not alone. Meals often turn into something we do while doing something else.
>
> Research suggests this matters. A review of studies on distracted eating found that people who ate while watching or playing something tended to feel less satisfied afterwards, and often ate more later in the day. That doesn't mean you did anything wrong. Attention is part of how we notice flavour, hunger and fullness, and those signals take a little while to travel between your stomach and your brain.
>
> So our first habit asks for very little: **eat one meal a day without screens, starting with a few slow, mindful bites.**
>
> You don't have to eat the whole meal in silence. Just begin well.
>
> - Put your phone face down and out of reach, and switch off the TV for this meal.
> - Sit down with your food on a plate or in a bowl, even if you only have ten minutes.
> - Take your first three bites slowly. Look at the food, notice the smell, and chew until the flavour changes. Eating with your hands, chopsticks or a spoon, it works the same way.
> - Tap **Listen** on the habit for a five-minute guided mindful eating session, read aloud with on-screen captions. It's optional, and a nice way to try this out.
> - Eating with family or friends? Put the phones in a basket and let the conversation be the company.
>
> Mindful eating isn't about eating less, eating perfectly or feeling calm at every meal. Noticing is the whole point. And if your stomach is sensitive, slow bites and thorough chewing help whatever you're eating.
>
> *If thinking about food or hunger feels stressful, or you have had an eating disorder, treat the guided session as optional and follow your care team's advice. If you have trouble swallowing or ongoing stomach discomfort, please talk to a doctor.*
>
> **Stack it:** *"After I serve my lunch, I put my phone away and take three slow bites."*
>
> Your challenge: **eat one meal without screens, starting with three mindful bites, on 3 of the next 5 days.**
>
> Enjoy every bite,
> Your Fab Life coach

---

## Step 2 — Add Some Vegetables

```json step
{
  "id": "eat.add_veg",
  "goal": {"type": "habit",
           "habit": {"id": "add_vegetables", "title": "Add vegetables to one meal", "description": "A cupped handful of vegetables, fresh, frozen or tinned", "icon": "apple",
                     "ritual": "AFTERNOON", "order": 2, "durationSeconds": null,
                     "smallerVersion": "Add a few slices of cucumber, carrot or tomato, or a spoonful of cooked vegetables",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Vegetables\" to my afternoon",
  "tips": [
    "Keep a bag of frozen peas, spinach or mixed vegetables for days when you can't face chopping.",
    "Add to what you already cook: grated carrot in a paratha, spinach in dal, peas in rice or noodles.",
    "Buy what's in season. It's usually the cheapest and tastes the best.",
    "Don't love vegetables? Start with the ones you like and cook them with spices, garlic or lemon."
  ],
  "completionTitle": "A little more colour on your plate! 🥕",
  "completionMessage": "Three days of adding vegetables. Next, let's take a look at your drinks.",
  "gentleNote": "Choose well-cooked, peeled, soft vegetables such as lauki, pumpkin, carrot, ridge gourd or seedless zucchini, or blend cooked spinach into dal or soup, instead of raw salads. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly, one food at a time."
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
> - **Fresh, frozen and tinned all count.** Frozen peas, spinach or mixed vegetables are cheap, already chopped and keep for months. Tinned tomatoes and sweetcorn work too. Rinse tinned vegetables to wash off extra salt.
> - **Cook with the season.** Local, seasonal vegetables from the market are usually the best value.
> - **Add them to what you already eat.** Spinach in dal, peas in poha or fried rice, a tomato-cucumber kachumber on the side, greens in noodle soup, peppers in a sandwich.
> - **Cook once, eat twice.** A big pot of sabzi or stir-fried vegetables can cover two or three meals.
>
> **Gentle option.** If raw salads and fibrous vegetables upset your stomach, choose well-cooked, peeled, soft vegetables such as lauki (bottle gourd), pumpkin, carrot, ridge gourd or zucchini without seeds. Gentle mode in the **Eat** tab shows meals made this way, and every meal there has a recipe. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly.
>
> *If you take a blood-thinning medicine such as warfarin, or you have kidney disease, please ask your doctor before changing how much leafy green or high-potassium vegetable you eat.*
>
> **Stack it:** *"After I serve my lunch, I add a portion of vegetables to my plate."*
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
           "habit": {"id": "swap_sugary_drink", "title": "Swap one sugary drink", "description": "Choose water, buttermilk or unsweetened tea instead of a sugary drink", "icon": "water",
                     "ritual": "AFTERNOON", "order": 3, "durationSeconds": null,
                     "smallerVersion": "Halve the sugar in one drink, or pour a glass of water to have alongside it",
                     "tool": null},
           "targetDays": 4, "windowDays": 6},
  "buttonLabel": "Add \"Swap a sugary drink\" to my afternoon",
  "tips": [
    "Keep a bottle or glass of water where you can see it, and fill it first thing.",
    "Make your swap at the time you usually reach for the sweet drink.",
    "Step down gradually: halve the sugar in your tea for a week, then halve it again.",
    "Don't drink sugary drinks? Wonderful. Your habit is simply a glass of water or buttermilk with lunch."
  ],
  "completionTitle": "A smarter sip! 💧",
  "completionMessage": "Four days of choosing a lighter drink. Next, let's build a lunch plate that keeps you going.",
  "gentleNote": "Gentle drinks include water, buttermilk, weak tea, clear soups, rice kanji and coconut water. Fizzy drinks and large amounts of coffee can irritate a sensitive stomach. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly and keep an eye on how you feel."
}
```

### Letter — A Smarter Sip

> Dear {name},
>
> It's mid-afternoon, your energy dips, and a cold fizzy drink or a sweet chai looks like the answer. That's completely understandable. Sweet drinks are easy, familiar and often a small comfort.
>
> Here's what research suggests. Drinks with added sugar, such as fizzy drinks, packaged juices, sweetened tea and coffee, and energy drinks, are among the biggest sources of added sugar in many people's eating. Liquid sugar also tends to be less filling than the same amount in food, so it is easy to take in a lot without noticing. Health organisations, including the WHO, suggest keeping added sugars to a small part of what we eat, and many people go over that.
>
> So our next habit is a gentle swap: **replace one sugary drink with water, buttermilk or unsweetened tea.** Just one.
>
> This is not a ban. A sweet chai with a friend or a festival lassi is part of life. We're only looking at the automatic ones.
>
> **Swaps to try**
>
> - Water, plain or with lemon, mint, cucumber or a pinch of roasted cumin.
> - Buttermilk (chaas) or a plain lassi.
> - Unsweetened tea or coffee, or a herbal tea such as ginger or tulsi.
> - Sparkling water with a squeeze of lime.
> - Coconut water. It has natural sugars, so one glass is plenty.
>
> Taste buds adjust, too. Research suggests our liking for sweetness can shift over a few weeks, so if the first sip tastes plain, give it a little time.
>
> *If you have diabetes, kidney or heart problems, or you've been given advice about how much to drink, please follow your doctor's guidance. If dairy doesn't suit you, pick water or tea instead of buttermilk.*
>
> **Stack it:** *"After I sit down for lunch, I pour my water or chaas before I eat."*
>
> Your challenge: **swap one sugary drink (or add a glass of water or buttermilk) on 4 of the next 6 days.**
>
> Cheers to you,
> Your Fab Life coach

---

## Step 4 — A Plate That Keeps You Going

```json step
{
  "id": "eat.balanced_plate",
  "goal": {"type": "habit",
           "habit": {"id": "balanced_plate", "title": "Build a balanced lunch plate", "description": "Half vegetables and fruit, a quarter protein, a quarter grains", "icon": "plate",
                     "ritual": "AFTERNOON", "order": 4, "durationSeconds": null,
                     "smallerVersion": "Add one protein food to your lunch, such as dal, an egg, paneer, tofu, curd or fish",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Balanced plate\" to my afternoon",
  "tips": [
    "Serve the vegetables first, then add protein, then grains. The plate fills in the right order.",
    "Dal, eggs, curd, tinned beans and tofu are some of the most affordable proteins.",
    "A plate is a picture, not a measuring tool. Eyeballing it is fine.",
    "Eating out or packing a tiffin? Look for the same pattern: something green, something protein, something grain."
  ],
  "completionTitle": "A plate that works for you! 🍛",
  "completionMessage": "Three days of building a balanced lunch. Next, one small planning step makes all of this easier.",
  "gentleNote": "Gentle plate: white rice, idli or a soft phulka; strained or well-cooked moong dal, eggs, paneer, tofu, fish or skinless chicken; and soft cooked, peeled vegetables. Go easy on raw salads, beans, whole grains, seeds and skins. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly."
}
```

### Letter — A Plate That Keeps You Going

> Dear {name},
>
> You've sat down, added some vegetables and poured something refreshing. Now let's look at the lunch itself. A common pattern is a plate that's mostly rice, bread or noodles with a little something on the side, and then hunger returns by mid-afternoon.
>
> Research suggests protein helps meals feel more filling, and that eating carbohydrate foods together with protein and fibre slows how quickly they are digested, which tends to give steadier energy. Health guidelines in countries including India, the UK and the US use a similar "plate" picture to show this.
>
> So here's our habit: **build a lunch plate with about half vegetables and fruit, a quarter protein and a quarter grains.**
>
> - **Half: vegetables and fruit.** Sabzi, salad, stir-fried greens, soup, a piece of fruit.
> - **A quarter: protein.** Dal, rajma, chana, eggs, paneer, tofu, curd, fish or chicken.
> - **A quarter: grains.** Roti, rice, millets such as ragi or jowar, quinoa, bread or noodles.
> - **A little healthy fat** from a teaspoon of oil, nuts or seeds.
>
> It's a guide, not a rule. Some days it will be a bowl, a tiffin or a sandwich, and that works too.
>
> **Plates from different kitchens:** roti, dal, seasonal sabzi, cucumber and curd. Rice, sambar, poriyal and a boiled egg. Tinned chickpeas with rice and frozen vegetables. Tofu and vegetable stir-fry with noodles. Fish, rice and sautéed greens.
>
> **Gentle option.** Choose white rice or a soft phulka, strained moong dal, eggs, paneer, tofu or fish, and soft cooked vegetables. Gentle mode in the **Eat** tab shows lunch ideas like these, and every meal there has a recipe. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly.
>
> *If you have diabetes, kidney disease or another condition, or you are pregnant, your portions may differ. A doctor or dietitian can tailor the plate to you.*
>
> **Stack it:** *"After I pour my drink, I build my plate: vegetables first, then protein, then grains."*
>
> Your challenge: **build a balanced lunch plate on 3 of the next 5 days.**
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
           "description": "Pick three lunches and two dinners, write a short shopping list and prep one ingredient",
           "shortcuts": [{"label": "Browse meal ideas", "target": "explore_eat"}]},
  "buttonLabel": "I'll plan and prep",
  "tips": [],
  "completionTitle": "Future-you says thank you. 🛒",
  "completionMessage": "A little planning makes the easy choice the default. Next, let's make dinner a little lighter.",
  "gentleNote": "For a gentle week, pick meals with gentle mode switched on in the Eat tab, and prep soft foods: cook extra white rice, boil and peel potatoes, boil eggs or cook strained moong dal. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly."
}
```

### Letter — Make the Easy Choice the Default

> Dear {name},
>
> It's one o'clock, you're hungry, nothing is ready, and the quickest option wins. That isn't a lack of willpower. Deciding what to eat when you're tired and hungry is hard for anyone. Research suggests that people who plan their meals tend to eat a wider variety of foods and waste less, and planning takes the daily guesswork out.
>
> So today there's no habit to repeat. There's **one small action: plan a few meals and prep one thing.** It takes about twenty minutes, once.
>
> **Three parts**
>
> 1. **Pick your meals.** Tap the button below to open the **Eat** tab. Choose three lunches and two dinners that look good to you. Ideas are filtered by your diet type, every meal has a recipe with ingredients and steps, and gentle mode is there if your stomach needs it.
> 2. **Write a short list.** Think protein (dal, eggs, paneer, tofu, fish, tinned beans), vegetables (seasonal and frozen), grains (rice, millets, atta) and curd or buttermilk. Check what you already have first. Dried dal, seasonal produce and frozen vegetables keep costs down.
> 3. **Prep one thing.** Wash and chop vegetables, cook a pot of dal, boil a few eggs, cook rice or millet for two days, or make a chutney. Just one.
>
> **If cooking is hard right now:** because of low energy, pain, limited mobility, a shared kitchen or no kitchen at all, choose ready helpers such as frozen or pre-cut vegetables, tinned beans, boiled eggs, curd and fruit. Ask someone to help, or swap prep tasks with a friend. Small counts.
>
> *If you have a food allergy or a medical condition, check the recipes against the advice you've been given.*
>
> **Stack it:** *"After my weekly shop, or on Sunday evening with a cup of tea, I plan and prep."*
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
           "habit": {"id": "lighter_dinner", "title": "Have a lighter, earlier dinner", "description": "Finish eating a few hours before bed, with a lighter, easier-to-digest meal", "icon": "plate",
                     "ritual": "EVENING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "Finish dinner 30 minutes earlier than usual, or swap one heavy item for something lighter",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Lighter dinner\" to my evening",
  "tips": [
    "Count back two to three hours from your bedtime to find a good time to finish eating.",
    "Late-eating household or shift work? Keep dinner lighter rather than earlier. Either one helps.",
    "Hungry later? A banana or a small glass of warm milk is perfectly fine.",
    "Make lunch your biggest meal on busy days, then keep dinner simple."
  ],
  "completionTitle": "Dinner that lets you rest! 🌙",
  "completionMessage": "Three days of a lighter evening meal. Next, let's make afternoon snacks more satisfying.",
  "gentleNote": "Gentle dinner: soft moong dal khichdi with white rice and curd, idli with strained dal, a smooth vegetable soup with paneer cubes, or steamed fish with rice and cooked carrot. Skip raw salad at night. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly."
}
```

### Letter — Dinner That Lets You Rest

> Dear {name},
>
> Some evenings dinner arrives late, large and in a hurry, and then bedtime comes with a full, heavy stomach. It's a very common pattern after a long day, and it can make lying down less comfortable.
>
> Research suggests that eating very close to bedtime is linked with poorer sleep for some people, and with heartburn for those prone to reflux. Some studies also link late, heavy meals with higher blood sugar afterwards. The evidence is mixed and everyone differs, but many people find they feel better when they finish eating a couple of hours before bed and the meal is lighter.
>
> So here's our first evening habit: **have a lighter, earlier dinner.**
>
> "Lighter" doesn't mean smaller, or skipping food, or going hungry. It means easier to digest and not rushed: plenty of vegetables, a moderate portion of grains, some protein, and less that is deep-fried, very oily or very spicy. "Earlier" means a little earlier than usual, in a way that fits your household.
>
> - Your bedtime is set to **{bedTime}**. Count back two to three hours to find your ideal "finished eating" time. If that isn't realistic, keep dinner lighter instead.
> - Ideas from many kitchens: vegetable khichdi with curd; dal, one or two rotis and stir-fried vegetables; soup with paneer or tofu; grilled fish with vegetables; egg bhurji with roti; miso soup with rice and tofu.
> - Evenings busy? Eat your biggest meal at lunch and keep dinner simple.
> - Hungry later? A banana or a small glass of warm milk is fine.
>
> **Gentle option.** Soft moong dal khichdi, idli with strained dal, smooth vegetable soup or steamed fish with rice are easy on a sensitive stomach, and gentle mode in the **Eat** tab has more dinner ideas, each with a recipe. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly.
>
> *If you take diabetes medicine or insulin, please don't change when you eat without checking with your doctor. If you have frequent heartburn or are pregnant, ask your doctor what suits you.*
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
           "habit": {"id": "smart_snack", "title": "Choose a satisfying snack", "description": "Pair protein or fibre with something you enjoy, and eat it sitting down", "icon": "apple",
                     "ritual": "AFTERNOON", "order": 5, "durationSeconds": null,
                     "smallerVersion": "Add a piece of fruit or a small handful of roasted chana to whatever snack you were going to have",
                     "tool": null},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Satisfying snack\" to my afternoon",
  "tips": [
    "Put your snack on a plate or in a bowl rather than eating straight from the packet.",
    "Pack an easy snack with your lunch: roasted chana, a fruit, or a boiled egg.",
    "Ask yourself: am I hungry, tired, bored or thirsty? If you're hungry, eat.",
    "Favourite treats still belong. Just add a satisfying option beside them."
  ],
  "completionTitle": "Snack time, sorted! 🍎",
  "completionMessage": "Three days of snacks that satisfy. Now you have every piece of your afternoon ritual.",
  "gentleNote": "Gentle snacks: ripe banana, curd or buttermilk, plain crackers, peeled stewed apple, a boiled egg, or smooth peanut butter on white toast. Go easy on whole nuts, seeds, dried fruit and raw sprouts. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly."
}
```

### Letter — Snacks That Satisfy

> Dear {name},
>
> It's four o'clock. The meeting ran over, the children are home, and the biscuit tin is calling. Snacking is completely normal, and for many people snacks are a real and useful part of the day.
>
> Research suggests that snacks made only of refined carbohydrates, such as biscuits, white bread or sweets, are digested quickly and often leave people hungry again soon, while snacks that pair protein or fibre with a carbohydrate tend to be more filling. Studies also suggest that eating while distracted can lead to eating more, which is why step one helps here too.
>
> So here's our habit: **choose a snack that pairs protein or fibre with something you enjoy, and eat it sitting down.**
>
> A simple formula: one protein or fibre-rich food plus one fruit or carbohydrate.
>
> - Roasted chana or peanuts with a seasonal fruit.
> - A boiled egg with a slice of toast.
> - Curd or buttermilk with a banana.
> - Apple slices with peanut butter, or hummus with cucumber and carrot sticks.
> - Makhana or plain popcorn with a few nuts.
>
> Roasted chana, peanuts, eggs, curd and seasonal fruit are also among the most budget-friendly options.
>
> **Gentle option.** A ripe banana, curd or buttermilk, plain crackers, peeled stewed apple, a boiled egg or smooth peanut butter on white toast are all kind to a sensitive stomach. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly.
>
> This is not about giving up treats. A favourite sweet or samosa can sit comfortably beside a satisfying snack, and enjoying it is part of eating well.
>
> *If you have a nut allergy, swap nuts for roasted chana, curd or fruit. If you have diabetes, ask your care team which snacks and timings work best for you.*
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
  "goal": {"type": "ritual", "ritual": "AFTERNOON", "targetDays": 5, "windowDays": 7},
  "buttonLabel": "Start the final challenge",
  "tips": [
    "Tap Start ritual and let the app walk you through each habit.",
    "Pour your drink, build your plate, take three slow bites. They flow together at one meal.",
    "On hard days, do the smaller version of every habit."
  ],
  "completionTitle": "You did it! 🏆",
  "completionMessage": "Your nourished afternoon is complete. Read your final letter.",
  "gentleNote": "For a gentle afternoon, use your gentle choices throughout: soft cooked vegetables, white rice or idli, strained dal, eggs, paneer, tofu or fish, curd or buttermilk, and a ripe banana. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly."
}
```

### Letter — Putting Your Afternoon Together

> Dear {name},
>
> This is it: the final challenge of this journey.
>
> Look at what you've put together for your afternoon. You sit down away from screens and take a few mindful bites. You add vegetables, pour water or buttermilk, and build a plate that keeps you going. Later, you choose a snack that satisfies. Each piece was small, and you've already practised every one of them.
>
> Your challenge: **complete your whole afternoon ritual on 5 of the next 7 days.**
>
> Research from University College London found that new behaviours took anywhere from a few weeks to several months to feel automatic, and that missing the odd day didn't derail people. So please treat this week as practice, not a test.
>
> **A few things to keep in mind**
>
> - **Use the ritual player.** Tap *Start ritual* and the app walks you through each habit in order, so you don't have to remember.
> - **Shape it to your day.** "Afternoon" means whenever you have your main meal and snack. If you work nights, fast for religious or cultural reasons, or eat on a different schedule, move the habits to the times that suit you.
> - **Eating out or travelling?** Do what you can. Look for something green and some protein, sit down for a few bites, and let the rest go.
> - **Bad days happen.** Use the smaller versions: three slow bites, a few slices of cucumber, a glass of water, one protein food, a piece of fruit. A tiny ritual keeps the chain alive.
> - **Your dinner habit** keeps living in your evening ritual. It stays beside this one.
>
> If you're using gentle mode, keep going with it. It is usually short term or guided by a doctor, so add fibre back slowly when you feel ready. And if worries about food come up, please talk to your doctor or dietitian.
>
> Five afternoons out of seven. You've already done the hard part. Now you get to enjoy it.
>
> Cheering for you at every meal,
> Your Fab Life coach

---

## Outro Letter — A Kinder Way to Eat

> Dear {name},
>
> Stop for a moment and take this in: **you completed the Eat Well journey.**
>
> Look at what you did. You learned to eat a meal without screens, to add vegetables, to choose water or buttermilk more often, to build a lunch plate that keeps you going, to plan and prep, to enjoy a lighter dinner, and to choose snacks that satisfy. None of it needed a special diet, expensive foods or willpower of steel. It needed small steps, repeated kindly.
>
> The most important lesson isn't about vegetables or plates. It's that eating well can be simple and gentle, and you are allowed to enjoy it. No food is off the table, and no meal makes you a better or worse person. What you've built is a set of habits that make the easy choice a healthy one most of the time, and leave plenty of room for birthdays, festivals, favourite street food and family feasts.
>
> **What happens now?**
>
> - Your afternoon and evening habits stay with you in the app. Keep the ones that help, and shrink or drop any that don't fit. Adjusting is part of the process.
> - Keep using the **Eat** tab for fresh ideas. Every meal has a recipe, and gentle mode is there whenever your stomach needs it. Gentle eating is usually short term or guided by a doctor, so add fibre back slowly.
> - Your needs will change with the seasons, your health, your age and your budget. A doctor or registered dietitian can help you fine-tune things, especially if you have a medical condition or are pregnant.
> - If food ever starts to feel stressful, or you notice yourself worrying about eating, please reach out to a doctor or a mental health professional. Asking for support is a strength.
>
> There are more journeys waiting for you in the app whenever you feel ready. But for today, enjoy a meal you love, slowly, with good company if you can.
>
> So proud of you,
> Your Fab Life coach

**Buttons:** *Celebrate 🎉* · *See what's next*

---

## Supporting Letter — Missed a Few Days?

> Dear {name},
>
> I noticed it's been a few days since you checked in. First, the most important thing: **that's completely okay.**
>
> Food is tangled up with real life: busy weeks, travel, festivals, family gatherings, illness, tight budgets and tired evenings. Some days meals are rushed, and some days they're a celebration. Neither is a failure. Research on habit formation suggests that missing the odd day doesn't undo your progress. What matters is coming back.
>
> So let's make it easy to return. Which of these would help most?
>
> - **Keep going.** Nothing needs to change. Your next meal is a fresh start.
> - **Make it smaller.** Use the smaller version of your habit: three slow bites, a few slices of cucumber, a glass of water. A small habit you do beats a big one you skip.
> - **Use what's easy.** Frozen vegetables, a boiled egg, a bowl of curd or a banana all count. Gentle mode in the Eat tab has easy-to-digest ideas if your stomach isn't feeling great.
>
> And if worries about food have been taking up a lot of space lately, or you've been feeling unwell, please talk to your doctor or dietitian. You don't have to work it out alone.
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
> Whatever kept you away, whether it was a busy stretch, a trip, a festival season or a hard time, there's no need to explain and nothing to feel guilty about. The fact that you opened the app again says you still care about looking after yourself.
>
> Here's what hasn't changed: **everything you've already built is still here.** The habits you added and the steps you completed are waiting for you. Your body and your kitchen remember more than you think.
>
> We'll restart your current step from the beginning, so there's nothing to catch up on. Choose the smallest version: sit down for one meal and take three slow bites, or add one handful of vegetables. That's plenty for today.
>
> Starting again isn't failing. Starting again *is* the habit.
>
> Glad to have you back,
> Your Fab Life coach

**Button:** *Let's start again*
