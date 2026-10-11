# Fab Life — Phase 2 Design Contract

Status: **working contract for the phase 2 build.** Every author and implementer follows this document. If an implementation has to deviate, it updates this file in the same change.

Phase 2 adds what the MVP left out:

1. Four more journeys with full coaching letters: **Sleep Better**, **Focus & Achieve**, **Eat Well**, **Calm Mind**.
2. **Guided audio**: breathing pacer, guided sessions read aloud, ambient sounds with a sleep timer, a focus timer.
3. A small **journal** (gratitude, reflection, mood) and a daily **Top 3** planner, which several journeys use.
4. **Health Connect**: steps and sleep, read-only.
5. **AI-personalised letters**: always-on, private, on-device adaptive coach notes, plus optional Claude-written notes using the user's own API key.

Principles carried over from the MVP: offline-first, private by default, supportive tone, no medical claims, everything has an easier version, and low-impact / seated / gentle (low-fibre) options stay first-class.

---

## 1. Catalogs (shared ids)

Journey authors, audio authors, the validator and the app all use these exact ids.

### 1.1 Journeys

| id | goalKey | order | title | emoji | typical ritual focus |
|---|---|---|---|---|---|
| `energy` | `energy` | 1 | Feel More Energetic | ☀️ | MORNING |
| `sleep` | `sleep` | 2 | Sleep Better | 🌙 | EVENING |
| `focus` | `focus` | 3 | Focus & Achieve | 🎯 | MORNING + AFTERNOON |
| `eat` | `eat` | 4 | Eat Well | 🥗 | AFTERNOON + EVENING |
| `calm` | `calm` | 5 | Calm Mind | 🧘 | all three |

Onboarding goal keys map 1:1 to journey ids. The chosen goal starts that journey. Any journey can be started or switched to later; progress per journey is kept.

### 1.2 Rituals

`MORNING`, `AFTERNOON`, `EVENING` (as in the MVP).

### 1.3 Habit icons

`water`, `breakfast`, `movement`, `light`, `celebrate` (MVP) plus: `moon`, `bed`, `book`, `journal`, `breathe`, `meditate`, `focus`, `timer`, `plan`, `phone_off`, `plate`, `apple`, `walk`, `stretch`, `heart`, `sound`, `check`. Anything else falls back to a check icon, but the validator rejects unknown ids.

### 1.4 Habit tools

A habit may carry a `tool`: a button on Today and in the ritual player opens an in-app tool.

| `tool.type` | `tool.ref` | Opens |
|---|---|---|
| `ROUTINE` | a `RoutineGoal`: `WAKE_UP`, `STRENGTH`, `CARDIO`, `FLEXIBILITY`, `WIND_DOWN` | the recommended exercise routine for the user's level and needs |
| `BREATHING` | a breathing pattern id (1.5) | the breathing pacer |
| `GUIDED` | a guided session id (1.7) | the guided session player |
| `SOUND` | a sound id (1.6) | the sound player with sleep timer |
| `FOCUS_TIMER` | none | the focus timer |
| `JOURNAL` | `GRATITUDE`, `REFLECTION`, `MOOD` | the journal composer for that kind |
| `TOP3` | none | the Top 3 planner |

Tool screens accept an optional `habit` argument. When a session finishes they offer *Mark "<habit>" as done*.

Button labels in the app, which letters should use when pointing users at a tool: `ROUTINE` → **Start**, `BREATHING` → **Breathe**, `GUIDED` → **Listen**, `SOUND` → **Play**, `FOCUS_TIMER` → **Focus**, `JOURNAL` → **Write**, `TOP3` → **Plan**. Tools also live in the **Explore → Mind** tab (and **Explore → Move** / **Eat** for routines and meals). Letters may mention them, but must never promise features beyond this list (no streak freezes, social features, wearables, or reminders that don't exist).

### 1.5 Breathing patterns

Seconds per phase: inhale / hold / exhale / hold.

| id | name | pattern | default minutes | what it is for |
|---|---|---|---|---|
| `box` | Box breathing | 4 / 4 / 4 / 4 | 3 | steadying focus under pressure |
| `relax_478` | 4-7-8 breathing | 4 / 7 / 8 / 0 | 3 | winding down; beginners start with 3 rounds |
| `coherent` | Coherent breathing | 5 / 0 / 5 / 0 | 5 | calm, steady rhythm (about 6 breaths a minute) |
| `calm_exhale` | Long exhale | 4 / 0 / 6 / 0 | 3 | easing tension, an easy first pattern |

### 1.6 Ambient sounds (synthesised in the app, no audio files)

`white`, `pink`, `brown`, `rain`, `ocean`, `fan`, `focus_pad`. They are generated sound textures, not recordings or compositions. The app says so honestly ("generated ambient sound").

### 1.7 Guided sessions (read aloud with Android text-to-speech, with on-screen captions)

| id | title | minutes | category |
|---|---|---|---|
| `body_scan` | Body scan | 10 | SLEEP |
| `sleep_winddown` | Sleep wind-down | 10 | SLEEP |
| `worry_unload` | Unload your worries | 6 | SLEEP |
| `morning_intention` | Morning intention | 3 | MORNING |
| `stress_reset` | Two-minute stress reset | 3 | CALM |
| `grounding_54321` | 5-4-3-2-1 grounding | 3 | CALM |
| `gratitude` | Gratitude pause | 4 | CALM |
| `loving_kindness` | Kindness practice | 5 | CALM |
| `focus_reset` | Focus reset | 3 | FOCUS |
| `mindful_eating` | Mindful eating | 5 | EAT |

Categories: `SLEEP`, `MORNING`, `CALM`, `FOCUS`, `EAT`.

### 1.8 Journal kinds

`GRATITUDE`, `REFLECTION`, `MOOD`.

### 1.9 Action shortcut targets

An action step may list buttons (`shortcuts`: `{label, target}`). Valid targets:
`habits`, `settings`, `explore_move`, `explore_eat`, `explore_mind`, `journal:GRATITUDE`, `journal:REFLECTION`, `journal:MOOD`, `top3`, `focus_timer`, `sound:<id>`, `guided:<id>`, `breathing:<id>`, `health`, `journeys`.

### 1.10 Letter placeholders

`{name}` (first name or "friend"), `{wakeTime}`, `{bedTime}` (formatted like `7:00 AM`).

---

## 2. Journey document format (Markdown is the source of truth)

Journeys 2–5 are authored as `docs/journeys/0N-<slug>.md`. `tools/journey_md.py` parses them; `tools/validate_content.py` checks them; `tools/build_content.py` turns them into `app/src/main/assets/journeys/<id>.json`. Journey 1 keeps its existing document and generator code, and its output moves to `assets/journeys/energy.json`.

Free prose (an overview table, notes) may appear anywhere **outside** the blocks below.

````markdown
# Journey 2 — Sleep Better

```json meta
{"id": "sleep", "goalKey": "sleep", "order": 2, "title": "Sleep Better",
 "subtitle": "Wind down well and wake up rested", "emoji": "🌙", "ritualFocus": "EVENING"}
```

## Intro Letter — Title of the intro letter
> Dear {name},
>
> Paragraphs, **bold**, *italic*, "- " bullets, "1. " lists, and "|" tables, all inside the blockquote.
>
> Your Fab Life coach

## Step 1 — Step title
```json step
{
  "id": "sleep.bedtime",
  "goal": {"type": "habit",
           "habit": {"id": "fixed_bedtime", "title": "...", "description": "...", "icon": "bed",
                     "ritual": "EVENING", "order": 1, "durationSeconds": null,
                     "smallerVersion": "...", "tool": {"type": "GUIDED", "ref": "sleep_winddown"}},
           "targetDays": 3, "windowDays": 5},
  "buttonLabel": "Add \"Fixed bedtime\" to my evening",
  "tips": ["...", "...", "..."],
  "completionTitle": "...", "completionMessage": "...",
  "gentleNote": null
}
```
### Letter — Title of this step's letter
> Dear {name},
> ...

## Outro Letter — Title
> ...
## Supporting Letter — Missed a Few Days?
> ...
## Supporting Letter — Welcome Back
> ...
````

Rules the validator enforces:

- 7–9 steps. Step ids are `<journeyId>.<slug>`. Habit ids are `snake_case` and **unique across all journeys**. Journey 1 already uses `drink_water`, `great_breakfast`, `morning_movement`, `morning_light`, `celebrate`.
- Goal types: `habit` (`habit`, `targetDays` 2–5, `windowDays` between `targetDays` and `targetDays + 3`), `action` (`title`, `description`, optional `shortcuts`), `ritual` (`ritual`, `targetDays`, `windowDays`). A `ritual` step is the last step, and its ritual has at least two habits added earlier in the same journey.
- Every habit has a `smallerVersion`, an icon from 1.3, a ritual, an `order`, and a valid `tool` (or none).
- Habit steps have 2–4 `tips`, a `completionTitle` and a `completionMessage`.
- Every letter starts with `Dear {name},`, ends with the sign-off `Your Fab Life coach`, and uses only the placeholders in 1.10.
- Word counts: step letters 200–560, intro 250–560, outro 200–500, missed-days 120–300, welcome-back 100–260.
- Habit and ritual step letters contain a line starting `Your challenge:`. Action step letters tell the user to tap **Mark as done**.
- No blunt medical claims (`cure`, `detox`, `miracle`, `guarantee`). Science is stated carefully ("research suggests"). Letters that touch physical health (exercise, food, sleep, breathing) include a short note to talk to a doctor where it matters.
- The `eat` journey must set `gentleNote` (the low-fibre alternative) on every step that involves food choices.

Voice, structure and length follow `docs/journeys/01-feel-more-energetic.md`: warm, a relatable hook, plain-language science, the habit in bold, 3–5 practical tips, a "Stack it" line, a challenge statement, a sign-off. Never guilt or shame. Offer examples across cultures, budgets and abilities, including low-impact and seated options.

---

## 3. Guided session document format

`docs/audio/sessions_*.md` hold the scripts. One section per session:

````markdown
## Session — Body scan
```json session
{"id": "body_scan", "title": "Body scan", "minutes": 10, "category": "SLEEP",
 "description": "Slowly relax each part of your body.", "bestFor": "Falling asleep or releasing tension",
 "ambient": "brown"}
```
Welcome. Make yourself comfortable, lying down or sitting. [pause 5]
Let your eyes close, or rest them on one spot. [pause 4]
[pause 10]
````

Script rules:

- One spoken line per script line. A trailing `[pause N]` (seconds, integer) adds silence after the line. A line that is only `[pause N]` is a silent gap. Lines without a pause get 1.5 seconds.
- Plain spoken language, second person, present tense, short sentences, no markdown, no emoji, no stage directions other than pauses.
- Estimated length = words ÷ 2.2 per second of speech, plus the pauses. It must be within ±20% of the declared `minutes`.
- Start by inviting a comfortable position, and offer choices (seated is fine, eyes open is fine). Never push the body ("force yourself to relax"). No medical or therapeutic claims. `CALM`, `MORNING`, `FOCUS` and `EAT` sessions close by bringing attention back ("take a breath, and open your eyes when you're ready"). `SLEEP` sessions end by inviting rest and say that if the listener is still awake, resting quietly is fine; they never tell the listener to wake up. Include a one-line note that this is for relaxation and that anyone feeling unwell or distressed should reach out to a professional, at the start or end of the first session in each file.
- `ambient` is an optional sound id (1.6) suggested as a quiet background.

---

## 4. Content model (Kotlin, `content/Models.kt`)

```kotlin
@Serializable data class HabitTool(val type: ToolType, val ref: String? = null)
enum class ToolType { ROUTINE, BREATHING, GUIDED, SOUND, FOCUS_TIMER, JOURNAL, TOP3 }

HabitTemplate(+ tool: HabitTool? = null)
Goal.Action(+ shortcuts: List<Shortcut> = emptyList())      // Shortcut(label, target)
Journey(+ goalKey: String, order: Int, emoji: String, ritualFocus: Ritual)

@Serializable data class GuidedStep(val say: String? = null, val pause: Float = 1.5f)   // seconds of silence after the line
@Serializable data class GuidedSession(id, title, minutes, category: SessionCategory, description, bestFor,
                                       ambient: String? = null, steps: List<GuidedStep>)
@Serializable data class BreathingPattern(id, name, description, inhale: Float, hold1: Float, exhale: Float, hold2: Float, defaultMinutes: Int)
@Serializable data class SoundInfo(id, name, description)
@Serializable data class AudioLibrary(sessions, breathing, sounds)
```

Assets: `assets/journeys/{energy,sleep,focus,eat,calm}.json`, `assets/audio.json`, plus the existing `exercise.json` and `food.json`. `ContentRepository` exposes `journeys` (ordered), `journey(id)`, `audio`.

---

## 5. Engine behaviour (multi-journey)

- `UserPrefs.activeJourneyId` (default: the onboarding goal). The Today card, Journey tab, reminders nudge, and welcome-back logic use the **active** journey. Switching journeys never deletes progress.
- `StepProgressEntity` rows are created when a journey is started (not all at onboarding). `startJourney(id)` creates its rows (first step `UNLOCKED`) and makes it active. A journey is complete when all its rows are `COMPLETED`.
- `evaluate` only looks at the active journey's `ACTIVE` step.
- A `ritual` challenge counts the habits **in that ritual that this journey added** (`sourceStepId` starts with `<journeyId>.`), not user-created habits or other journeys' habits.
- Habits keep the `tool` from their template (`HabitEntity.toolType`, `toolRef`). On app open, tools are re-synced from content for journey habits whose tool is missing.
- Journey completion offers the next journey by `order` (wrapping), plus a picker listing all five with progress.
- Letter ids: `intro`, `outro`, `missed`, `welcome` refer to the active journey. `intro:<journeyId>` and `outro:<journeyId>` refer to a specific one. Step letters use the step id.
- Weekly movement minutes count `ROUTINE`-tool habits (5 minutes each unless a routine log exists that day) as before.

## 6. Data model v2 (Room AutoMigration 1 → 2)

New or changed:

- `habits`: `toolType: String?`, `toolRef: String?`.
- `journal_entries(id PK auto, day, kind: JournalKind, mood: Int?, text, createdAtMillis)`.
- `top_tasks(day, slot 0..2, text, done)` primary key `(day, slot)`.
- `session_logs(id PK auto, kind: SessionKind {FOCUS, GUIDED, BREATHING, SOUND}, ref, day, minutes)`.
- `coach_notes(key PK, text, source: String {LOCAL, AI}, createdAtMillis)`.

`resetAll` clears all of them. Export includes journal entries, session logs and Top 3.

## 7. Navigation

New routes (all tool routes accept an optional `habit` query argument):

`breathing/{id}`, `guided/{id}`, `sound/{id}`, `sounds`, `focus`, `journal?kind={kind}`, `top3`, `health`, `journeys`.

Explore has three tabs: **Move**, **Eat**, **Mind**. Mind lists: Breathe, Guided sessions, Sounds, Focus timer, Journal, Top 3.

---

## 8. Audio module (`audio/`)

Pure Kotlin (no Android imports, JVM-tested):

- `SoundId`, `SoundGenerator`, `Soundscapes.create(id, sampleRate, seed)`: continuous mono float PCM in [-1, 1]. White, pink (Voss–McCartney or filtered), brown (leaky integration with a high-pass), rain (pink/white noise with a slow, quiet drop texture), ocean (filtered noise with a slow swell, about 8–12 s period), fan (low band-limited noise plus a quiet mains hum at about 100 Hz), `focus_pad` (a soft slowly detuned drone with a few harmonics). RMS levels are within about 3 dB of each other so volumes feel comparable. No clipping. Deterministic for a seed.
- `Fades.gain(...)`: fade in/out envelope used for start and sleep-timer fade-out.
- `BreathingPacer`: expands a pattern into timed phases and gives the pacer's state (phase, progress 0..1, cycle count) for any elapsed time.
- `SessionTimeline`: cumulative start times for each guided step and the total estimated duration; finds the step for any elapsed time.

Android side:

- `AudioEngine`: streams generated PCM to `AudioTrack` on its own thread. Volume, pause, resume, stop, fade-in/out.
- `SpeechPlayer`: Android `TextToSpeech`, speech rate about 0.9, language from the device with a fallback, an `available` flag. If TTS is missing or fails, sessions still run as **timed on-screen captions** with a gentle chime at the start and end.
- `AudioController` (in `AppContainer`): the single entry point for UI. A foreground **`AudioService`** (`mediaPlayback` type, notification with Pause/Stop) hosts playback so sounds continue with the screen off. Handles audio focus (pause on loss, duck briefly), `ACTION_AUDIO_BECOMING_NOISY` (pause on headphone unplug), a sleep timer with a gentle fade-out, and clean shutdown.
- Manifest: `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, the service declaration with `foregroundServiceType="mediaPlayback"`.
- UI: breathing pacer screen (animated circle, large phase label, optional soft haptic tick, reduced-motion friendly), guided session player (captions, progress, ambient toggle, pause, stop), sound player and library (volume, sleep timer 15/30/60 min or off), focus timer.
- The app tells the truth about sounds: they are generated textures, not music.

## 9. Mind tab features

- **Focus timer**: Pomodoro-style, default 25 min focus / 5 min break, long break every 4 rounds, editable lengths, optional focus sound (`brown`, `pink`, `focus_pad`), vibrates and chimes at the end, keeps the screen on, logs finished focus minutes in `session_logs`.
- **Journal**: three composers. Gratitude (three short lines), Reflection (one prompt with a free text box, prompts rotate), Mood (1–5 faces plus an optional note). A list of past entries grouped by day. Entries stay on the device and can be deleted.
- **Top 3**: three text slots per day with check-offs. Yesterday's unfinished tasks are offered to carry over.

## 10. Health Connect module (`health/`)

- Dependency `androidx.health.connect:connect-client` (latest stable). Read-only permissions `READ_STEPS` and `READ_SLEEP`. `<queries>` for the Health Connect package, a privacy rationale activity handling `androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE`, and the Android 14 `VIEW_PERMISSION_USAGE` activity alias.
- `HealthRepository` interface with `availability`, `hasPermissions()`, `requiredPermissions`, and `summary(days)`; `HealthConnectRepository` implements it. Tests use a fake.
- `SleepMath` (pure, JVM-tested): merges overlapping sessions, ignores awake stages when stages exist, assigns a night to the day it ends on, handles naps and sessions that cross midnight.
- UI: a Health card on Progress and on the `health` screen showing today's steps against a goal (default 7,000, editable) and last night's sleep, plus a 7-day view. Clear states for: not available on this device, Health Connect not installed (with a link), permission not granted (with a button), and no data yet. Nothing is shown to the user as medical advice. The Sleep journey may mention it as optional.
- Health data **never leaves the device** and is never included in AI prompts.

## 11. Coach notes (`coach/`)

Two layers, both optional to the user's attention and neither blocking anything:

1. **Adaptive notes (always on, on-device, free, private).** `AdaptiveNotes.note(ctx)` picks a short (under about 70 words), warm note from a large set of templates using the user's progress: step just unlocked, a streak milestone (3, 7, 14, 21), a gentle comeback after missed days, a struggling step (offer the smaller version), the user's chosen obstacles (I'm too busy / I lose motivation / I forget / I start too big), time of day, low-impact or seated movement needs, gentle (low-fibre) eating for food steps, journey completed. Seeded by the date so it is stable within a day. Tests cover every rule and check there are no medical claims.
2. **Claude-written notes (optional, bring your own key).** Off by default. The user turns it on in Settings, reads exactly what will be shared, accepts, and pastes their own Anthropic API key. There is no app backend.
   - Official **Anthropic Java SDK** (`com.anthropic:anthropic-java`), model `claude-opus-5-5`, no `thinking` field (thinking can't be disabled on this model), `OutputConfig` effort `LOW`, `maxTokens` large enough that thinking doesn't truncate the note (at least 2048), timeout about 20 s, one retry.
   - Handles `refusal` stop reasons and typed errors (401 key rejected, 429, 5xx, network) by silently showing the adaptive note instead, and marks the key status so Settings can say so.
   - Shared with the API when enabled: journey and step title, the step goal in words, challenge progress numbers, streak lengths, selected obstacle categories, time of day, and the first name **only if** the user turned that on. **Never** shared: journal text, Top 3 text, health data, diet type, allergies, the gentle-stomach flag, movement needs, or any identifier. Settings shows a live preview of the exact payload.
   - The key is stored encrypted with an Android Keystore key (AES-GCM) in a private file excluded from backup. It is never logged. *Remove key* wipes it. The cipher takes an injected key provider so it can be unit-tested on the JVM.
   - Notes are cached per day and step (`coach_notes`) and capped (default 10 AI notes a day) so costs stay predictable. The AI note replaces the adaptive note only after it arrives.
   - Adds the `INTERNET` permission. Manifest and README privacy text are updated: "No account, no servers of ours; the only network use is the optional Claude notes you switch on yourself."
   - Safety prompt: warm, practical, at most 90 words, plain text, no diagnosis, no medical or dietary prescriptions, no shame, tell the user to see a professional for medical concerns, never claim to be a human coach.
- UI: `CoachNoteCard` on Today and under each step letter. Shows the adaptive note immediately. A small label says whether it came from the on-device coach or was written by Claude.

## 12. Testing expectations

- `AppContainer` is `open`, and each module's entry point (`audio`, `health`, `coach`) is an `open val`, so Robolectric tests can supply a `FabLifeApp` subclass (`@Config(application = ...)`) that overrides `createContainer()` with fakes. Nothing in the UI constructs a platform service (audio, Health Connect, the network client) directly.

- Pure logic gets JVM unit tests: DSP properties, fades, pacer, session timeline, sleep math, adaptive notes, cipher, prompt builder, journal/top-3 repositories, multi-journey rules.
- The Claude client is tested against `MockWebServer`: request shape and headers, no sensitive fields in the body, refusal and error handling, the daily cap.
- Robolectric UI flow tests: onboarding to each journey's first step, switching journeys, a tool route opening and offering to mark its habit done, the Mind tab, the Health states with a fake repository, the Settings AI section with consent.
- Content assets are parsed and validated by the real parser in a unit test, so docs and app cannot drift.
- Everything stays green: `./gradlew testDebugUnitTest assembleDebug assembleRelease lintDebug`.
- Things that can only be proven on a real device (audio output, TTS voices, foreground service behaviour, Health Connect data, Keystore) are called out honestly in the PR and README.

---

## 13. Implementation status

Built as specified: sections 1 to 10 and the adaptive layer of section 11 (on-device coach notes on the Today tab).

Differences from this contract:
- Health Connect uses `connect-client:1.1.0-beta01`, the newest release that builds with `compileSdk 35`. The stable `1.1.0` needs `compileSdk 36` and a newer Android Gradle Plugin.
- Coach notes under each step letter were not added; they appear on Today.
- **Section 11, layer 2 (Claude-written notes with the user's own API key) is not built.** The app therefore does not request the INTERNET permission. Everything in the section stays the plan for that feature (consent screen, payload preview, Keystore-encrypted key, daily cap, refusal handling).
- The focus timer counts against a background-safe clock but alerts only while the app is open.

