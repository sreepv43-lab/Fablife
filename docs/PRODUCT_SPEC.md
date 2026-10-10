# Fab Life — Product Specification

**Version:** 0.1 (draft) · **Platform:** Android (phone + tablet) · **Stack:** Kotlin, Jetpack Compose, Material 3

---

## 1. Vision

Fab Life helps people build a healthier lifestyle **one tiny habit at a time**. Instead of a to-do list of good intentions, users follow guided **journeys**: a short coaching letter explains *why* a habit matters, the user practises that one habit until it sticks, and only then is the next habit added. Habits are grouped into **rituals** (morning, afternoon, evening) so each new behaviour is anchored to an existing routine.

### 1.1 Positioning

Fab Life is inspired by the coaching-journey model popularised by apps like Fabulous, and deliberately fixes their most common complaints:

| Common complaint in the category | Fab Life answer |
|---|---|
| Aggressive paywall, hard to cancel | Core journey and habit tracking free forever; premium is optional extra content |
| Pacing too slow and rigid | Adaptive pacing: advance early when consistent, slow down when struggling, manual "skip ahead" |
| Too many notifications | Max one reminder per ritual per day; user controls every notification |
| Generic coaching | Letters adapt to the user's goal and progress; missed-day letters are supportive, not guilt-based |
| Account required, data in the cloud | Works fully offline; data stays on device; no account in MVP |

### 1.2 Non-goals (MVP)

- Calorie counting, diet plans, or medical advice
- Social feed or community features
- Wearable / Health Connect integration (Phase 2)
- iOS or web

---

## 2. Target Users

### Persona A — "Tired Priya", 29, office worker
Wakes up groggy, relies on coffee, skips breakfast, wants more energy but has failed at big "new year" resolutions. Needs: very small first steps, encouragement, visible progress.

### Persona B — "Busy Arjun", 41, parent of two
Mornings are chaotic. Wants better sleep and a little exercise but has no time for 1-hour workouts. Needs: 2–5 minute habits, flexible reminder times, no guilt when life gets in the way.

### Persona C — "Restarting Meera", 55
Wants to rebuild healthy routines after a stressful period. Prefers calm design, large text, clear instructions. Needs: accessibility, gentle pacing, reading-friendly letters.

---

## 3. Success Metrics

| Metric | Target (first 3 months after launch) |
|---|---|
| Onboarding completion | ≥ 80% of installs |
| Day-1 retention | ≥ 50% |
| Day-7 retention | ≥ 30% |
| Day-30 retention | ≥ 15% |
| Daily habit completion rate (active users) | ≥ 60% of scheduled habits |
| Journey 1 completion (of users who start it) | ≥ 25% within 6 weeks |
| Notification opt-out rate | ≤ 20% |

All metrics are computed from **on-device, anonymous, opt-in** analytics events (see §11).

---

## 4. Core Concepts (Glossary)

| Term | Definition |
|---|---|
| **Journey** | A themed coaching programme (e.g. *Feel More Energetic*) made of ordered steps. One journey is active at a time. |
| **Step** | One unit of a journey. Has a **letter** and one **goal**: a *habit challenge*, a *one-time action*, or a *ritual challenge*. |
| **Letter** | A short coaching text (250–450 words) that explains the science and motivation behind the step. |
| **Habit** | A small repeatable action (e.g. "Drink a glass of water"). Belongs to exactly one ritual. |
| **Habit challenge** | A step goal: complete a habit on N days (e.g. 3 days) to unlock the next step. |
| **Ritual challenge** | A step goal: complete **all** habits of a ritual on N days (e.g. 5 of 7) to unlock the next step. |
| **One-time action** | A step goal done once (e.g. "Put a glass of water next to your bed tonight"). |
| **Ritual** | An ordered group of habits done together at a time of day: Morning, Afternoon, Evening. |
| **Check-in** | Marking a habit as done (or skipped) for a given day. |
| **Streak** | Consecutive days on which the user completed *all* habits in a ritual. |
| **Ritual player** | A focused full-screen mode that walks the user through each habit in a ritual, one at a time. |

---

## 5. Information Architecture

```
Onboarding (first launch only)
  └─ Welcome → Goal quiz → Wake/sleep time → Commitment → Journey intro letter

Main (bottom navigation)
  ├─ Today        — rituals for today, check-ins, current step card
  ├─ Journey      — step map, letters, progress
  ├─ Progress     — calendar heatmap, streaks, stats
  └─ Settings     — reminders, ritual times, theme, data export/reset

Overlays / full-screen
  ├─ Letter reader
  ├─ Ritual player
  ├─ Celebration
  ├─ Habit editor
  └─ Step complete / Journey complete
```

---

## 6. Feature Specifications (MVP)

Each feature lists user stories and acceptance criteria (AC).

### 6.1 Onboarding

**User stories**
- As a new user, I want to tell the app what I want to improve so my plan feels personal.
- As a new user, I want to set when I wake up so reminders fit my day.

**Flow**
1. **Welcome screen** — app name, one-line promise ("Small habits. Big change."), *Get started* button.
2. **Goal quiz** (single choice, required):
   - Have more energy → *Feel More Energetic*
   - Sleep better → *Sleep Better* (Phase 1.1)
   - Focus and get things done → *Focus & Achieve* (Phase 1.1)
   - Eat healthier → *Eat Well* (Phase 2)
   - Feel calmer, less stressed → *Calm Mind* (Phase 2)

   In MVP only *Feel More Energetic* is fully written. Other goals show "Coming soon — we'll start with energy, the foundation for everything else" and still start Journey 1, with the chosen goal saved for later personalisation.
3. **Obstacles** (multi-choice, optional): "I'm too busy", "I lose motivation", "I forget", "I start too big". Used to pick tips shown in letters.
4. **Wake-up time** and **bed time** pickers (defaults 07:00 / 23:00). Used to schedule ritual reminder defaults: Morning = wake + 10 min, Afternoon = 14:00, Evening = bed − 60 min.
5. **Name** (optional, used in letters: "Dear Priya"). Fallback: "Dear friend".
6. **Commitment screen** — "I commit to taking care of myself, one small step at a time." User **presses and holds** a button for 1.5 s to sign (haptic feedback + animation).
7. **Notification permission** (Android 13+), explained first: "We'll send one gentle reminder for your morning ritual. You can change this any time."
8. Opens **Journey intro letter** → then Step 1 letter → lands on **Today**.

**AC**
- Onboarding can be completed in under 2 minutes.
- Back navigation works on every onboarding screen; state is preserved.
- If the app is killed mid-onboarding, it resumes at the same screen.
- Denying notification permission does not block onboarding.

### 6.2 Today Screen

**User stories**
- As a user, I want to see what to do today at a glance.
- As a user, I want to check off a habit with one tap.

**Layout (top to bottom)**
1. Greeting by time of day: "Good morning, Priya".
2. **Current step card** — step title, progress ("2 of 3 days"), *Read letter* button if unread.
3. **Ritual cards** — Morning, Afternoon, Evening. Each shows: name, reminder time, habit list with checkboxes, *Start ritual* button (opens ritual player), streak flame + count.
4. Empty rituals are shown collapsed with "No habits yet — your journey will add some soon."

**Interactions**
- Tap checkbox → habit marked done for today, light haptic, check animation. Tap again → undo.
- Long-press habit → menu: *Skip today*, *Edit*, *Move to another ritual*, *Remove*.
- Completing **all** habits in a ritual → celebration overlay (confetti + message), streak increments.
- A day boundary is the user's **local midnight + 3 h** (so 01:30 still counts as "yesterday" for night owls).

**AC**
- Check-in state persists across app restarts.
- Checking in a habit that is the current habit challenge updates the step progress card immediately.
- Users can check in for **yesterday** (via the Progress calendar) but not for future days.

### 6.3 Ritual Player

**User story:** As a user, I want a focused, guided mode that walks me through my morning ritual.

- Full-screen, one habit per page: icon, name, short instruction, optional timer (e.g. 5:00 for exercise).
- Buttons: *Done* (advance), *Skip*, *Back*.
- Progress dots at top.
- Final page → celebration → returns to Today.
- Screen stays awake while the player is open.

**AC**
- Timer continues if the screen is locked; vibrates when finished.
- Exiting early saves completed check-ins.

### 6.4 Journey Screen

**User stories**
- As a user, I want to see where I am in my journey and what's coming next.
- As a user, I want to re-read letters I've already unlocked.

**Layout**
- Journey header: title, illustration, overall progress bar ("Step 3 of 8").
- Vertical **step map**: completed steps (check icon), current step (highlighted, progress ring), locked future steps (lock icon, title visible, letter hidden).
- Tapping a completed or current step opens its letter.

**AC**
- Locked step letters cannot be opened.
- "Skip ahead" option on the current step (with a confirmation: "We recommend practising a habit for a few days before adding the next. Skip anyway?").

### 6.5 Letters

- Rendered from Markdown content (headings, bold, italics, bullet lists, a single hero illustration).
- Personalised placeholders: `{name}`, `{wakeTime}`, `{habit}`.
- Reading view: comfortable line length (max ~65 characters), 18 sp body text, supports system font scaling to 200%.
- Bottom button depends on step goal:
  - Habit challenge → *Add "Drink water" to my morning* (adds habit to the ritual and starts the challenge).
  - One-time action → *I'll do it* (shows action card on Today with *Mark as done*).
  - Ritual challenge → *Start the final challenge* (shows the challenge progress on the ritual card).
- Unread letters show a dot badge on the Journey tab.

### 6.6 Journey Engine (pacing rules)

This is the core logic. It must be deterministic and fully unit-tested.

**Step states:** `LOCKED → UNLOCKED (letter unread) → ACTIVE → COMPLETED`

**Habit challenge completion**
- Each habit-challenge step defines `targetDays` (e.g. 3) and `windowDays` (default `targetDays + 2`).
- The step completes when the habit has been checked in on `targetDays` distinct days **within any rolling window** of `windowDays` days since the step became active.
- Example: target 3, window 5 → done on Mon, Tue, Thu completes the step on Thursday.

**Ritual challenge completion**
- Same rolling-window rule as habit challenges, but a day counts only if **every** non-archived habit in the target ritual was checked in as DONE that day.

**One-time action completion**
- Completes when the user taps *Mark as done*.

**Unlocking**
- When a step completes: show *Step complete* screen → next step becomes `UNLOCKED` → its letter is shown immediately (or on next app open if completed from a notification).
- Habits added by earlier steps **stay in the ritual** permanently (that is the point — the ritual grows).

**Struggle handling (adaptive pacing)**
- If the active habit challenge has **0 check-ins for 3 consecutive days**, show a supportive *"Missed a few days?"* card (see journey content) with options:
  - *Keep going* (no change)
  - *Make it smaller* (shows a smaller version of the habit, e.g. "Drink a few sips of water")
  - *Change reminder time*
- If inactive for **7+ days**, on next open show a "Welcome back" letter; the active step's progress resets to 0 but completed steps are never lost.

**Journey completion**
- After the last step: *Journey complete* screen with summary (days active, habits built, longest streak) and the journey's final letter. Next journey is offered (Phase 1.1+).

### 6.7 Habits

- Each habit: `id`, `title`, `description`, `icon`, `ritual`, `order`, optional `durationSeconds`, `source` (journey step or user-created).
- Users may create **custom habits** (title, icon, ritual) from Settings → *My habits* or from a ritual card's *+ Add habit*.
- Users may reorder habits within a ritual (drag handle).
- Removing a journey habit asks for confirmation and does not affect journey progress if the step is already completed.

### 6.8 Reminders & Notifications

**Policy:** at most **one notification per ritual per day**, plus at most one journey notification per day. Never more than 3 per day total.

| Notification | When | Example copy |
|---|---|---|
| Ritual reminder | Ritual time, only if ritual has habits and is not yet complete | "☀️ Your morning ritual is ready — 3 small steps." |
| New letter | When a step unlocks and the app is not open | "You unlocked a new letter: *A Great Breakfast*." |
| Gentle nudge | 3 days without check-ins on the active challenge, at the morning ritual time (replaces that day's ritual reminder) | "No pressure — even one sip of water counts today." |

- Tapping a ritual reminder opens the **ritual player** directly.
- Notification actions: *Done* (checks in all habits of a 1-habit ritual), *Snooze 30 min*.
- Each ritual's reminder can be toggled and its time changed in Settings.
- Reminders must survive device reboot and time-zone changes.

### 6.9 Progress Screen

- **Calendar heatmap** (month view): each day shaded by % of scheduled habits completed (0 / 1–49 / 50–99 / 100%).
- Tap a day → list of check-ins for that day; can edit **today and yesterday** only.
- Stats cards: current streak (per ritual), longest streak, total check-ins, days active, journey progress.
- Per-habit view: completion rate over last 30 days.

### 6.10 Settings

- Profile: name, goal.
- Ritual times and reminder toggles.
- My habits (create, edit, reorder, remove).
- Theme: System / Light / Dark.
- Day start time (default 03:00).
- Data: *Export my data* (JSON file via Storage Access Framework), *Reset all progress* (typed confirmation "RESET").
- About, privacy policy, open-source licences.
- Health disclaimer: "Fab Life provides general wellness information, not medical advice."

---

## 7. Content Model

Journeys are shipped as JSON assets in `app/src/main/assets/journeys/` so new journeys can be added without code changes. Letter bodies are Markdown strings.

```json
{
  "id": "energy",
  "version": 1,
  "title": "Feel More Energetic",
  "subtitle": "Build a morning ritual that powers your day",
  "introLetter": { "title": "...", "body": "markdown..." },
  "outroLetter": { "title": "...", "body": "markdown..." },
  "steps": [
    {
      "id": "energy.water",
      "title": "Drink Water",
      "letter": { "title": "...", "body": "markdown..." },
      "goal": {
        "type": "habit",
        "habit": {
          "id": "drink_water",
          "title": "Drink a glass of water",
          "description": "Right after you wake up",
          "icon": "water_drop",
          "ritual": "MORNING",
          "durationSeconds": null,
          "smallerVersion": "Take three sips of water"
        },
        "targetDays": 3,
        "windowDays": 5
      },
      "tips": ["..."],
      "completionMessage": "..."
    },
    {
      "id": "energy.prepare",
      "title": "Prepare Your Water",
      "letter": { "title": "...", "body": "markdown..." },
      "goal": {
        "type": "action",
        "action": { "title": "Put a glass of water next to your bed tonight" }
      }
    },
    {
      "id": "energy.full_ritual",
      "title": "The Morning You",
      "letter": { "title": "...", "body": "markdown..." },
      "goal": { "type": "ritual", "ritual": "MORNING", "targetDays": 5, "windowDays": 7 }
    }
  ],
  "missedDaysLetter": { "title": "...", "body": "markdown..." },
  "welcomeBackLetter": { "title": "...", "body": "markdown..." }
}
```

---

## 8. Data Model (on-device)

Persisted with **Room** (SQLite). User preferences in **DataStore**.

```kotlin
enum class Ritual { MORNING, AFTERNOON, EVENING }
enum class StepState { LOCKED, UNLOCKED, ACTIVE, COMPLETED }
enum class CheckInStatus { DONE, SKIPPED }

@Entity data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val icon: String,
    val ritual: Ritual,
    val order: Int,
    val durationSeconds: Int?,
    val sourceStepId: String?,      // null = user-created
    val archived: Boolean = false,
    val createdAt: Instant,
)

@Entity(primaryKeys = ["habitId", "day"])
data class CheckInEntity(
    val habitId: String,
    val day: LocalDate,             // logical day (after day-start offset)
    val status: CheckInStatus,
    val timestamp: Instant,
)

@Entity data class StepProgressEntity(
    @PrimaryKey val stepId: String,
    val journeyId: String,
    val state: StepState,
    val letterReadAt: Instant?,
    val activatedAt: Instant?,
    val completedAt: Instant?,
)

@Entity data class RitualSettingsEntity(
    @PrimaryKey val ritual: Ritual,
    val reminderTime: LocalTime,
    val reminderEnabled: Boolean,
)

// DataStore
data class UserPrefs(
    val name: String?,
    val goal: String,
    val obstacles: Set<String>,
    val wakeTime: LocalTime,
    val bedTime: LocalTime,
    val dayStartOffsetHours: Int = 3,
    val activeJourneyId: String = "energy",
    val onboardingCompleted: Boolean,
    val theme: Theme,
)
```

---

## 9. Technical Architecture

| Concern | Choice |
|---|---|
| Language / UI | Kotlin 2.x, Jetpack Compose, Material 3 |
| Navigation | Navigation Compose (type-safe routes) |
| Persistence | Room (habits, check-ins, progress), DataStore (prefs) |
| Content parsing | kotlinx.serialization (JSON), a lightweight Markdown renderer for Compose |
| Reminders | `AlarmManager.setExactAndAllowWhileIdle` when exact-alarm permission is granted, otherwise `setWindow`; reschedule on `BOOT_COMPLETED`, `TIMEZONE_CHANGED`, `TIME_SET` |
| DI | Manual container (`AppContainer`) — no framework in MVP |
| Architecture | Single-activity, MVVM (ViewModel + StateFlow), unidirectional data flow |
| Module layout | Single `:app` module; packages `content/`, `data/`, `engine/`, `notifications/`, `ui/` |
| Pure logic | `engine/` (journey rules, streaks, day boundary) has **no Android imports** → JVM unit tests |
| Min / target SDK | minSdk 26, targetSdk 35 |
| CI | GitHub Actions: unit tests + debug APK artifact on every push |

---

## 10. Non-Functional Requirements

- **Offline-first:** every feature works without network.
- **Performance:** cold start < 1.5 s on a mid-range device; Today screen check-in feedback < 100 ms.
- **Accessibility:** TalkBack labels for all controls, minimum 48 dp touch targets, contrast ratio ≥ 4.5:1, supports font scale 200%, animations respect "Remove animations" system setting.
- **Localisation-ready:** all UI strings in `strings.xml`; journey content keyed by locale (`energy.en.json`). MVP ships English only.
- **Reliability:** no data loss on process death; Room migrations tested.
- **Battery:** no background work except scheduled alarms.

---

## 11. Privacy & Safety

- No account, no server, no ads in MVP.
- Optional anonymous analytics (off by default in MVP; Phase 2 decision).
- Data export (JSON) and full reset available to the user.
- Wellness disclaimer shown in onboarding footer and Settings. Letters avoid medical claims and recommend consulting a doctor for medical conditions (e.g. exercise step).

---

## 12. Monetisation (post-MVP, for planning)

- **Free forever:** Journey 1, unlimited custom habits, rituals, reminders, progress.
- **Fab Life Plus** (optional subscription or one-time purchase): additional journeys, guided audio (meditations, workouts), advanced stats, themes.
- Clear pricing, cancel in one tap via Google Play, no dark patterns.

---

## 13. Roadmap

| Phase | Scope |
|---|---|
| **MVP (v0.1)** | Onboarding, Today, Ritual player, Journey engine, Journey 1 *Feel More Energetic*, reminders, Progress, Settings |
| **v0.2** | Journeys *Sleep Better* and *Focus & Achieve*; custom habit schedules (weekdays only, etc.) |
| **v0.3** | Guided audio and timers library; home-screen widget; journaling/mood check-in |
| **v1.0** | Health Connect (steps, sleep), Fab Life Plus, Play Store release |
| **Later** | AI-personalised letters, cloud backup, Wear OS tile, community challenges |

---

## 14. Open Questions

1. Should the ritual streak require *all* habits, or ≥ 80% to be more forgiving?
2. Should users be allowed to run two journeys at once after completing Journey 1?
3. Illustrations: commission custom art, or use a consistent open-licence illustration set?
4. Final app name and icon — "Fab Life" is a working title; check trademark conflicts before release.

---

## 15. Related Documents

- [Journey 1 — Feel More Energetic (full content)](journeys/01-feel-more-energetic.md)
