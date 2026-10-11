"""Shared ids and descriptions for journeys, tools and audio. See docs/DESIGN_PHASE2.md section 1.

The validator, the generator and the app all rely on these exact ids.
"""

JOURNEYS = {
    "energy": {"goalKey": "energy", "order": 1, "title": "Feel More Energetic", "emoji": "☀️"},
    "sleep": {"goalKey": "sleep", "order": 2, "title": "Sleep Better", "emoji": "🌙"},
    "focus": {"goalKey": "focus", "order": 3, "title": "Focus & Achieve", "emoji": "🎯"},
    "eat": {"goalKey": "eat", "order": 4, "title": "Eat Well", "emoji": "🥗"},
    "calm": {"goalKey": "calm", "order": 5, "title": "Calm Mind", "emoji": "🧘"},
}

RITUALS = ["MORNING", "AFTERNOON", "EVENING"]

ICONS = [
    "water", "breakfast", "movement", "light", "celebrate",
    "moon", "bed", "book", "journal", "breathe", "meditate", "focus", "timer", "plan",
    "phone_off", "plate", "apple", "walk", "stretch", "heart", "sound", "check",
]

TOOL_TYPES = ["ROUTINE", "BREATHING", "GUIDED", "SOUND", "FOCUS_TIMER", "JOURNAL", "TOP3"]
ROUTINE_GOALS = ["WAKE_UP", "STRENGTH", "CARDIO", "FLEXIBILITY", "WIND_DOWN"]
JOURNAL_KINDS = ["GRATITUDE", "REFLECTION", "MOOD"]
PLACEHOLDERS = {"name", "wakeTime", "bedTime"}

# Habit ids already used by journey 1 (docs/journeys/01-feel-more-energetic.md).
JOURNEY1_HABIT_IDS = ["drink_water", "great_breakfast", "morning_movement", "morning_light", "celebrate"]

BREATHING = {
    "box": {"name": "Box breathing", "inhale": 4, "hold1": 4, "exhale": 4, "hold2": 4, "defaultMinutes": 3,
            "description": "Breathe in, hold, breathe out, hold, each for four counts. Good for steadying your focus."},
    "relax_478": {"name": "4-7-8 breathing", "inhale": 4, "hold1": 7, "exhale": 8, "hold2": 0, "defaultMinutes": 3,
                  "description": "A slow pattern with a long exhale for winding down. Beginners can start with three rounds."},
    "coherent": {"name": "Coherent breathing", "inhale": 5, "hold1": 0, "exhale": 5, "hold2": 0, "defaultMinutes": 5,
                 "description": "A calm, even rhythm of about six breaths a minute."},
    "calm_exhale": {"name": "Long exhale", "inhale": 4, "hold1": 0, "exhale": 6, "hold2": 0, "defaultMinutes": 3,
                    "description": "Breathe out a little longer than you breathe in. A gentle first pattern for easing tension."},
}

SOUNDS = {
    "white": {"name": "White noise", "description": "An even hiss that covers background sounds."},
    "pink": {"name": "Pink noise", "description": "Softer than white noise, with more low tones."},
    "brown": {"name": "Brown noise", "description": "A deep, rumbling noise that many people find calming."},
    "rain": {"name": "Rain", "description": "A steady, generated rainfall texture."},
    "ocean": {"name": "Ocean waves", "description": "Slow, generated waves rolling in and out."},
    "fan": {"name": "Fan hum", "description": "A low, steady fan-like hum."},
    "focus_pad": {"name": "Focus tone", "description": "A soft, slowly shifting drone for concentration."},
}

SESSIONS = {
    "body_scan": {"minutes": 10, "category": "SLEEP"},
    "sleep_winddown": {"minutes": 10, "category": "SLEEP"},
    "worry_unload": {"minutes": 6, "category": "SLEEP"},
    "morning_intention": {"minutes": 3, "category": "MORNING"},
    "stress_reset": {"minutes": 3, "category": "CALM"},
    "grounding_54321": {"minutes": 3, "category": "CALM"},
    "gratitude": {"minutes": 4, "category": "CALM"},
    "loving_kindness": {"minutes": 5, "category": "CALM"},
    "focus_reset": {"minutes": 3, "category": "FOCUS"},
    "mindful_eating": {"minutes": 5, "category": "EAT"},
}
SESSION_CATEGORIES = ["SLEEP", "MORNING", "CALM", "FOCUS", "EAT"]

SHORTCUT_TARGETS_PLAIN = [
    "habits", "settings", "explore_move", "explore_eat", "explore_mind",
    "top3", "focus_timer", "health", "journeys",
]

# Estimated speaking speed for guided sessions (words per second) at the app's calm speech rate.
WORDS_PER_SECOND = 2.2
DEFAULT_PAUSE_SECONDS = 1.5

BANNED_PHRASES = [r"\bcure[sd]?\b", r"\bdetox\w*\b", r"\bmiracle\b", r"\bguarantee[ds]?\b", r"\bclinically proven\b"]
