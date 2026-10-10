#!/usr/bin/env python3
"""Generates the app's JSON content assets from the Markdown docs.

Letters and meal tables are read from docs/ so the app always ships exactly the reviewed text;
step metadata, exercise moves and routines are defined here.

Usage: python3 tools/build_content.py   (from the repository root)
"""
import json
import re
from pathlib import Path

from recipes import RECIPES

ROOT = Path(__file__).resolve().parent.parent
DOCS = ROOT / "docs"
ASSETS = ROOT / "app" / "src" / "main" / "assets"


def letter_after(md: str, heading: str) -> dict:
    """Returns the first blockquote after the line starting with `heading` as {title, body}."""
    lines = md.splitlines()
    start = next(i for i, l in enumerate(lines) if l.startswith(heading))
    parts = re.split(r"[:—]", lines[start], maxsplit=1)
    title = (parts[1] if len(parts) > 1 else parts[0].lstrip("# ")).strip()
    body, in_quote = [], False
    for line in lines[start + 1:]:
        if line.startswith(">"):
            in_quote = True
            body.append(line[2:] if line.startswith("> ") else line[1:])
        elif in_quote:
            break
    return {"title": title, "body": "\n".join(body).strip()}


# ---------------------------------------------------------------- Journey 1

def build_journey() -> dict:
    md = (DOCS / "journeys" / "01-feel-more-energetic.md").read_text()

    def habit(id, title, description, icon, order, smaller, duration=None):
        return {"id": id, "title": title, "description": description, "icon": icon, "ritual": "MORNING",
                "order": order, "durationSeconds": duration, "smallerVersion": smaller}

    steps = [
        {
            "id": "energy.water", "title": "Drink Water", "heading": "### Letter: The Ten-Second Habit",
            "goal": {"type": "habit", "habit": habit("drink_water", "Drink a glass of water", "Right after you wake up",
                                                     "water", 1, "Take three sips of water"),
                     "targetDays": 3, "windowDays": 5},
            "buttonLabel": "Add \"Drink water\" to my morning",
            "tips": ["Fill a glass tonight and leave it where you'll see it in the morning.",
                     "Drink it before you check your phone.",
                     "Missed it on waking? Drink it now. It still counts."],
            "completionTitle": "3 days of water — done! 💧",
            "completionMessage": "Your morning ritual has officially begun. Next up: fuel that lasts.",
        },
        {
            "id": "energy.breakfast", "title": "A Great Breakfast", "heading": "### Letter: Fuel, Not Just Food",
            "goal": {"type": "habit", "habit": habit("great_breakfast", "Eat a great breakfast", "One protein + one fibre",
                                                     "breakfast", 2, "Eat a handful of nuts or a piece of fruit"),
                     "targetDays": 3, "windowDays": 5},
            "buttonLabel": "Add \"Great breakfast\" to my morning",
            "tips": ["Prepare overnight oats or boil eggs the evening before.",
                     "Keep breakfast simple: the same two or three options all week is fine.",
                     "Eat sitting down, away from your phone, even for five minutes."],
            "completionTitle": "Breakfast champion! 🍳",
            "completionMessage": "Water, then fuel. Your mornings are starting to have a shape.",
            "gentleNote": "Gentle mode is on: choose one protein + one easy-to-digest carb, like idli with curd, "
                          "scrambled eggs on white toast, curd with ripe banana, or white toast with smooth peanut butter. "
                          "Smaller version: a ripe banana or a small bowl of curd.",
        },
        {
            "id": "energy.prepare", "title": "Set Yourself Up", "heading": "### Letter: Win the Morning the Night Before",
            "goal": {"type": "action", "title": "Prepare tonight for tomorrow morning",
                     "description": "Water by the bed, breakfast ready, movement clothes laid out"},
            "buttonLabel": "I'll do it tonight",
            "completionTitle": "Tomorrow-you says thank you. 🌙",
            "completionMessage": "Try making this five-minute evening prep a regular thing. It makes every morning easier.",
        },
        {
            "id": "energy.move", "title": "Move Your Body", "heading": "### Letter: Five Minutes That Wake You Up",
            "goal": {"type": "habit", "habit": habit("morning_movement", "Move for 5 minutes",
                                                     "Stretch, walk, dance: anything that gets you moving",
                                                     "movement", 3, "Move for 1 minute", 300),
                     "targetDays": 4, "windowDays": 6},
            "buttonLabel": "Add \"5-minute movement\" to my morning",
            "tips": ["Put on your movement clothes right after breakfast so you're ready.",
                     "Play the same upbeat song every time. It becomes your cue to move.",
                     "Done is better than perfect: one minute still counts."],
            "completionTitle": "You're on fire! 🔥",
            "completionMessage": "Four days of moving your body. Notice how you feel afterwards. That's the energy we're building.",
        },
        {
            "id": "energy.light", "title": "Step Into the Light", "heading": "### Letter: Set Your Body Clock",
            "goal": {"type": "habit", "habit": habit("morning_light", "Get 5 minutes of daylight",
                                                     "Step outside or sit by an open window", "light", 4,
                                                     "Open the curtains and stand by the window for one minute", 300),
                     "targetDays": 3, "windowDays": 5},
            "buttonLabel": "Add \"Morning light\" to my morning",
            "tips": ["Walk outside for your five-minute movement and complete two habits at once.",
                     "Open all the curtains as soon as you wake up.",
                     "Have your coffee or tea outside."],
            "completionTitle": "Sunshine unlocked! ☀️",
            "completionMessage": "Your body clock thanks you. You may find you're getting sleepy at a more regular time at night.",
        },
        {
            "id": "energy.celebrate", "title": "Celebrate Yourself", "heading": "### Letter: The Secret Ingredient",
            "goal": {"type": "habit", "habit": habit("celebrate", "Celebrate!",
                                                     "A fist pump, a smile, a \"Yes!\": feel proud for a moment",
                                                     "celebrate", 5, "Smile and say \"I did it\""),
                     "targetDays": 3, "windowDays": 5},
            "buttonLabel": "Add \"Celebrate\" to my morning",
            "tips": ["Celebrate after any habit, not just at the end. More celebration, faster habits.",
                     "Pick one celebration and use it every time so it becomes automatic.",
                     "Notice the feeling. That's the point."],
            "completionTitle": "Look at you celebrating! 🎉",
            "completionMessage": "Your ritual is almost complete. One more step to make it truly yours.",
        },
        {
            "id": "energy.design", "title": "Design Your Morning", "heading": "### Letter: Make It Yours",
            "goal": {"type": "action", "title": "Arrange your morning ritual and set a consistent wake-up time",
                     "description": "Reorder your habits, then confirm your wake-up time in Settings"},
            "buttonLabel": "Let's design it",
            "completionTitle": "Your ritual, your way. ✍️",
            "completionMessage": "One final challenge stands between you and a morning ritual that runs on autopilot.",
        },
        {
            "id": "energy.full_ritual", "title": "The Morning You", "heading": "### Letter: Putting It All Together",
            "goal": {"type": "ritual", "ritual": "MORNING", "targetDays": 5, "windowDays": 7},
            "buttonLabel": "Start the final challenge",
            "tips": ["Tap Start ritual every morning. Let the app guide you.",
                     "Prepare the night before (water, breakfast, clothes).",
                     "On hard days, do the smaller version of every habit."],
            "completionTitle": "You did it! 🏆",
            "completionMessage": "Your morning ritual is complete. Read your final letter.",
        },
    ]
    for step in steps:
        step["letter"] = letter_after(md, step.pop("heading"))

    return {
        "id": "energy",
        "title": "Feel More Energetic",
        "subtitle": "Build a morning ritual that powers your whole day",
        "introLetter": letter_after(md, "## Intro Letter"),
        "outroLetter": letter_after(md, "## Outro Letter"),
        "missedDaysLetter": letter_after(md, "## Supporting Letter — Missed a Few Days?"),
        "welcomeBackLetter": letter_after(md, "## Supporting Letter — Welcome Back"),
        "steps": steps,
    }


# ---------------------------------------------------------------- Exercise

def move(id, name, how_to, easier, harder, seated=False, jumping=False):
    return {"id": id, "name": name, "howTo": how_to, "easier": easier, "harder": harder,
            "seated": seated, "jumping": jumping}


MOVES = [
    move("march", "March in place", "Lift knees to hip height, swing your arms, stand tall.", "Lower knees, hold a chair", "High knees, faster pace"),
    move("high_knee_march", "High-knee march", "March briskly, bringing knees up high and pumping your arms.", "Regular march", "Jog in place"),
    move("squat", "Squat", "Feet hip-width, sit hips back as if onto a chair, knees follow toes, chest up.", "Sit-to-stand from a chair", "Slow 3-second lowering"),
    move("sit_to_stand", "Sit-to-stand", "Sit at the edge of a chair, stand up tall without using your hands, sit back down slowly.", "Use your hands on your thighs", "Hold a water bottle at your chest"),
    move("wall_pushup", "Wall push-up", "Hands on the wall at shoulder height, body straight, bend elbows to bring your chest to the wall.", "Stand closer to the wall", "Incline push-up on a table"),
    move("incline_pushup", "Incline push-up", "Hands on a sturdy table or counter, body straight, lower your chest and push back up.", "Wall push-up", "Floor push-up"),
    move("pushup", "Push-up", "Hands under shoulders, body straight from head to heels (or knees), lower and push up.", "Knees down", "Slower lowering"),
    move("glute_bridge", "Glute bridge", "Lie on your back, knees bent, feet flat; squeeze glutes and lift hips; lower slowly.", "Smaller lift", "Single-leg bridge"),
    move("bird_dog", "Bird-dog", "On hands and knees, extend opposite arm and leg, keep your back flat, switch sides.", "Extend only an arm or a leg", "Hold 5 seconds each side"),
    move("dead_bug", "Dead bug", "On your back, arms up, knees bent at 90°; lower opposite arm and leg, keep lower back on the floor.", "Move only your legs", "Slower, fully extend"),
    move("plank", "Plank", "Forearms on the floor, body in a straight line from head to heels, breathe steadily.", "Knees down, or hands on a table", "Lift one foot"),
    move("reverse_lunge", "Reverse lunge", "Step one foot back, lower both knees toward 90°, push back up. Alternate legs.", "Hold a chair, smaller step", "Alternate without pausing"),
    move("calf_raise", "Calf raise", "Rise onto the balls of your feet, then lower slowly.", "Hold a wall", "Single leg"),
    move("arm_circles", "Arm circles", "Arms out to the side, small circles forward, then backward.", "Smaller circles", "Bigger, faster circles"),
    move("shoulder_rolls", "Shoulder rolls", "Roll your shoulders up, back and down in slow circles.", "Smaller circles", "Add arm circles"),
    move("side_bend", "Standing side bend", "Reach one arm overhead and lean gently to the opposite side. Switch halfway.", "Hand on hip", "Hold each side longer"),
    move("cat_cow", "Cat–cow", "Hands on knees (standing) or on all fours: round your back, then gently arch, moving with your breath.", "Seated version", "Slower, deeper breaths"),
    move("step_jacks", "Step jacks", "Step one foot out to the side while raising your arms, step back in, alternate sides.", "Arms only to shoulder height", "Jumping jacks"),
    move("jumping_jacks", "Jumping jacks", "Jump feet out while raising arms overhead, jump back in.", "Step jacks", "Faster pace", jumping=True),
    move("mountain_climbers", "Mountain climbers", "From a high plank, drive knees toward your chest one at a time.", "Slow, step the feet", "Faster pace", jumping=True),
    move("jump_squat", "Jump squat", "Squat down, then jump up softly and land with bent knees.", "Regular squat", "Pause at the bottom", jumping=True),
    move("reach_fold", "Reach and fold", "Reach both arms up tall, then fold forward with soft knees and let your head hang.", "Fold only halfway", "Hold the fold longer"),
    move("deep_breaths", "Deep breaths", "Breathe in slowly through your nose as you raise your arms, breathe out as you lower them.", "Arms by your sides", "Longer exhale"),
    move("seated_march", "Seated march", "Sit tall at the edge of a chair, lift knees alternately and swing your arms.", "Arms only", "Faster pace", seated=True),
    move("seated_leg_extension", "Seated leg extension", "Sit tall, straighten one knee, hold 2 seconds, lower. Alternate legs.", "Smaller range", "Hold longer", seated=True),
    move("seated_twist", "Seated torso twist", "Sit tall, hand on the opposite knee, rotate gently. Switch sides.", "Smaller rotation", "Hold 15 seconds", seated=True),
    move("seated_punches", "Seated punches", "Sit tall and punch forward, alternating arms, keeping your core engaged.", "Slower punches", "Faster punches", seated=True),
    move("neck_turns", "Gentle neck turns", "Slowly turn your head to look left, then right. Never force it.", "Smaller turns", "Add a slow nod", seated=True),
    move("overhead_press", "Overhead press", "Hold a water bottle in each hand at shoulders and press up overhead, then lower.", "No bottles", "Heavier bottles", seated=True),
]


def item(move_id, seconds=None, reps=None):
    return {"move": move_id, "seconds": seconds, "reps": reps}


ALL_LEVELS = ["BEGINNER", "INTERMEDIATE", "ACTIVE"]

ROUTINES = [
    {"id": "R1", "title": "Gentle Wake-Up", "minutes": 5, "levels": ["BEGINNER"], "needs": ["STANDARD"],
     "place": "HOME", "goal": "WAKE_UP", "description": "Easy moves to shake off sleepiness. No equipment.",
     "items": [item("march", 60), item("arm_circles", 30), item("side_bend", 30), item("squat", reps=10),
               item("wall_pushup", reps=10), item("calf_raise", reps=15), item("deep_breaths", reps=5)]},
    {"id": "R2", "title": "Energy Boost", "minutes": 5, "levels": ["INTERMEDIATE", "ACTIVE"], "needs": ["STANDARD"],
     "place": "HOME", "goal": "WAKE_UP", "description": "A brisk full-body wake-up for people who are already a bit active.",
     "items": [item("high_knee_march", 45), item("squat", reps=15), item("incline_pushup", reps=10),
               item("reverse_lunge", reps=10), item("plank", 30), item("step_jacks", 45), item("reach_fold", 30)]},
    {"id": "R3", "title": "Joint-Friendly Wake-Up", "minutes": 5, "levels": ALL_LEVELS, "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "HOME", "goal": "WAKE_UP", "description": "Low-impact, no jumping. Keep a chair nearby for support.",
     "items": [item("march", 60), item("shoulder_rolls", 30), item("sit_to_stand", reps=8), item("wall_pushup", reps=8),
               item("calf_raise", reps=10), item("cat_cow", 30), item("deep_breaths", reps=5)]},
    {"id": "R4", "title": "Seated Wake-Up", "minutes": 5, "levels": ALL_LEVELS, "needs": ["SEATED", "LOW_IMPACT"],
     "place": "HOME", "goal": "WAKE_UP", "description": "Everything is done sitting in a sturdy chair.",
     "items": [item("seated_march", 60), item("arm_circles", 30), item("seated_leg_extension", reps=10),
               item("seated_twist", 30), item("seated_punches", 30), item("neck_turns", 30), item("deep_breaths", reps=5)]},
    {"id": "R6", "title": "Morning Walk", "minutes": 5, "levels": ALL_LEVELS, "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "OUTDOORS", "goal": "WAKE_UP", "restSeconds": 0,
     "description": "A brisk walk where you can talk but not sing. Counts as morning daylight too.",
     "items": [{"label": "Brisk walk outside", "seconds": 300}]},
    {"id": "S1", "title": "Beginner Full Body", "minutes": 10, "levels": ["BEGINNER"], "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "HOME", "goal": "STRENGTH", "restSeconds": 15, "description": "Two rounds of simple strength moves. No equipment.",
     "items": [item("sit_to_stand", reps=10), item("wall_pushup", reps=10), item("glute_bridge", reps=12),
               item("bird_dog", reps=12), item("calf_raise", reps=15), {"label": "Rest", "seconds": 60},
               item("sit_to_stand", reps=10), item("wall_pushup", reps=10), item("glute_bridge", reps=12),
               item("bird_dog", reps=12), item("calf_raise", reps=15)]},
    {"id": "S2", "title": "Intermediate Full Body", "minutes": 20, "levels": ["INTERMEDIATE", "ACTIVE"], "needs": ["STANDARD"],
     "place": "HOME", "goal": "STRENGTH", "restSeconds": 15, "description": "Three rounds for all major muscle groups.",
     "items": sum([[item("squat", reps=15), item("pushup", reps=10), item("reverse_lunge", reps=20),
                    item("glute_bridge", reps=15), item("plank", 40), item("dead_bug", reps=16),
                    {"label": "Rest", "seconds": 75}] for _ in range(3)], [])[:-1]},
    {"id": "S3", "title": "Active Full Body", "minutes": 30, "levels": ["ACTIVE"], "needs": ["STANDARD"],
     "place": "HOME", "goal": "STRENGTH", "restSeconds": 15, "description": "Four tough rounds with some jumping.",
     "items": sum([[item("jump_squat", reps=15), item("pushup", reps=15), item("reverse_lunge", reps=20),
                    item("glute_bridge", reps=20), item("plank", 60), item("mountain_climbers", 30),
                    {"label": "Rest", "seconds": 60}] for _ in range(4)], [])[:-1]},
    {"id": "S4", "title": "Seated Strength", "minutes": 10, "levels": ALL_LEVELS, "needs": ["SEATED"],
     "place": "HOME", "goal": "STRENGTH", "restSeconds": 15, "description": "Two rounds of chair-based strength. Water bottles optional.",
     "items": sum([[item("seated_leg_extension", reps=20), item("seated_march", 45), item("seated_punches", 30),
                    item("overhead_press", reps=10), item("seated_twist", 30), {"label": "Rest", "seconds": 60}]
                   for _ in range(2)], [])[:-1]},
    {"id": "C3", "title": "Indoor Low-Impact Cardio", "minutes": 10, "levels": ["BEGINNER", "INTERMEDIATE"],
     "needs": ["STANDARD", "LOW_IMPACT"], "place": "HOME", "goal": "CARDIO", "restSeconds": 20,
     "description": "Get your heart rate up without jumping.",
     "items": sum([[item("march", 40), item("step_jacks", 40), item("high_knee_march", 40), item("seated_punches", 40)]
                   for _ in range(3)], [])},
    {"id": "C4", "title": "Interval Walk", "minutes": 18, "levels": ["INTERMEDIATE", "ACTIVE"], "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "OUTDOORS", "goal": "CARDIO", "restSeconds": 0, "description": "Alternate easy and fast walking.",
     "items": sum([[{"label": "Easy walk", "seconds": 120}, {"label": "Fast walk", "seconds": 60}] for _ in range(6)], [])},
    {"id": "C2", "title": "Dance Break", "minutes": 10, "levels": ALL_LEVELS, "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "HOME", "goal": "CARDIO", "restSeconds": 0, "description": "Put on 3 songs you love and move however feels good.",
     "items": [{"label": "Dance to your favourite songs", "seconds": 600}]},
    {"id": "F1", "title": "Desk Reset", "minutes": 5, "levels": ALL_LEVELS, "needs": ["STANDARD", "LOW_IMPACT", "SEATED"],
     "place": "HOME", "goal": "FLEXIBILITY", "restSeconds": 5, "description": "Undo stiffness from sitting.",
     "items": [item("neck_turns", 30), item("shoulder_rolls", 30), item("seated_twist", 30), item("side_bend", 30),
               item("neck_turns", 30), item("shoulder_rolls", 30), item("seated_twist", 30), item("deep_breaths", reps=5)]},
    {"id": "F2", "title": "Evening Stretch", "minutes": 10, "levels": ALL_LEVELS, "needs": ["STANDARD", "LOW_IMPACT"],
     "place": "HOME", "goal": "WIND_DOWN", "restSeconds": 5, "description": "Slow stretches to relax before bed.",
     "items": [item("cat_cow", 60), {"label": "Child's pose", "seconds": 60}, {"label": "Hamstring stretch, left", "seconds": 30},
               {"label": "Hamstring stretch, right", "seconds": 30}, {"label": "Lying twist, left", "seconds": 30},
               {"label": "Lying twist, right", "seconds": 30}, {"label": "Legs up the wall", "seconds": 120},
               item("deep_breaths", 60)]},
]


# ---------------------------------------------------------------- Food

DIETS = {"Vegan": "VEGAN", "Vegetarian": "VEGETARIAN", "Eggetarian": "EGGETARIAN", "Non-veg": "NON_VEG"}
MEALS = {"Breakfast": "BREAKFAST", "Lunch": "LUNCH", "Dinner": "DINNER", "Snacks": "SNACK"}
ALLERGEN_WORDS = {
    "DAIRY": ["curd", "yoghurt", "milk", "paneer", "buttermilk", "raita", "chaas", "thayir"],
    "EGGS": ["egg", "omelette"],
    "NUTS": ["nut", "peanut"],
    "SOY": ["tofu", "soy"],
    "SEAFOOD": ["fish"],
    "GLUTEN": ["bread", "toast", "roti", "phulka", "pasta", "wheat", "rava", "sooji", "semolina", "oats", "crackers", "wrap"],
}
NOT_ALLERGEN = {"NUTS": ["coconut", "no peanuts"], "DAIRY": ["coconut milk", "soy milk", "lactose-free"]}


def allergens_for(title: str) -> list:
    t = title.lower()
    found = []
    for allergen, words in ALLERGEN_WORDS.items():
        cleaned = t
        for w in NOT_ALLERGEN.get(allergen, []):
            cleaned = cleaned.replace(w, "")
        if any(w in cleaned for w in words):
            found.append(allergen)
    return found


def slug(text: str) -> str:
    return re.sub(r"[^a-z0-9]+", "_", text.lower()).strip("_")[:48]


def build_food() -> dict:
    md = (DOCS / "content" / "FOOD_GUIDE.md").read_text()
    meals, seen = [], set()
    section = None
    for line in md.splitlines():
        m = re.match(r"### [35]\.\d (\w+)", line)
        if m:
            section = (m.group(1), "GENTLE" if line.startswith("### 5") else "REGULAR")
            continue
        if line.startswith("## "):
            section = None
        if not section or not line.startswith("| ") or line.startswith("| Meal") or line.startswith("| Snack |"):
            continue
        cells = [c.strip() for c in line.strip("|").split("|")]
        if len(cells) < 2 or cells[1] not in DIETS:
            continue
        title, diet = cells[0], DIETS[cells[1]]
        prep = None
        if len(cells) > 2:
            p = re.match(r"(\d+)", cells[2])
            prep = int(p.group(1)) if p else None
        meal_type, fibre = MEALS[section[0]], section[1]
        id_ = f"{fibre.lower()}_{slug(title)}"
        if id_ in seen:
            continue
        seen.add(id_)
        ingredients, steps, tip = RECIPES[title]
        meals.append({"id": id_, "title": title, "meal": meal_type, "diet": diet, "fibre": fibre,
                      "allergens": allergens_for(title), "prepMinutes": prep,
                      "ingredients": ingredients, "steps": steps, "tip": tip})

    unused = set(RECIPES) - {m["title"] for m in meals}
    assert not unused, f"recipes without a meal: {unused}"

    principles = []
    in_principles = False
    for line in md.splitlines():
        if line.startswith("## 1."):
            in_principles = True
            continue
        if in_principles and line.startswith("## "):
            break
        m = re.match(r"\d+\. \*\*(.+?)\*\* (.+)", line)
        if in_principles and m:
            principles.append({"title": m.group(1).rstrip("."), "body": m.group(2)})

    gentle = letter_after(md, "### 4.1 Who it is for")
    gentle["title"] = "Gentle on the stomach"
    return {"principles": principles, "gentleGuidance": gentle, "meals": meals}


def main():
    ASSETS.mkdir(parents=True, exist_ok=True)
    outputs = {
        "journey_energy.json": build_journey(),
        "exercise.json": {"moves": MOVES, "routines": [{"restSeconds": 10, **r} for r in ROUTINES]},
        "food.json": build_food(),
    }
    move_ids = {m["id"] for m in MOVES}
    for r in ROUTINES:
        for it in r["items"]:
            assert it.get("move") is None or it["move"] in move_ids, (r["id"], it)
    for name, data in outputs.items():
        (ASSETS / name).write_text(json.dumps(data, ensure_ascii=False, indent=1) + "\n")
        print("wrote", name)


if __name__ == "__main__":
    main()
