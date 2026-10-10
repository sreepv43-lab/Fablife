#!/usr/bin/env python3
"""Validates journey and guided-session documents against docs/DESIGN_PHASE2.md.

Usage:
  python3 tools/validate_content.py                 # every journey doc 02-05 and every docs/audio/sessions_*.md
  python3 tools/validate_content.py FILE [FILE...]  # specific files

Exit status is 1 if any error is found. Warnings never fail the run.
"""
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import catalog as C  # noqa: E402
from journey_md import ParseError, parse_journey, parse_sessions  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
SNAKE = re.compile(r"^[a-z][a-z0-9_]*$")
PLACEHOLDER = re.compile(r"\{(\w+)\}")
SIGN_OFF = "Your Fab Life coach"


def words(text: str) -> int:
    return len(re.findall(r"[A-Za-z0-9’'\-]+", re.sub(r"[|*>_#`]", " ", text)))


class Report:
    def __init__(self, name):
        self.name, self.errors, self.warnings = name, [], []

    def err(self, where, msg):
        self.errors.append(f"{where}: {msg}")

    def warn(self, where, msg):
        self.warnings.append(f"{where}: {msg}")


def check_letter(r: Report, where, letter, lo, hi, challenge=None, action=False):
    body = letter.get("body", "")
    if not letter.get("title"):
        r.err(where, "letter has no title")
    n = words(body)
    if not lo <= n <= hi:
        r.err(where, f"{n} words, expected {lo}-{hi}")
    if not body.startswith("Dear {name},"):
        r.err(where, "must start with 'Dear {name},'")
    if not body.rstrip().endswith(SIGN_OFF):
        r.err(where, f"must end with the sign-off '{SIGN_OFF}'")
    for p in PLACEHOLDER.findall(body):
        if p not in C.PLACEHOLDERS:
            r.err(where, f"unknown placeholder {{{p}}}")
    for pat in C.BANNED_PHRASES:
        m = re.search(pat, body, re.I)
        if m:
            r.err(where, f"banned phrase '{m.group(0)}'")
    if challenge and not re.search(r"^Your challenge:", body.replace("**", ""), re.M | re.I):
        r.err(where, "habit/ritual letters need a line starting 'Your challenge:'")
    if action and "Mark as done" not in body:
        r.err(where, "action letters must tell the user to tap **Mark as done**")
    if "**" not in body:
        r.warn(where, "no bold text; the habit or action should be in bold")


def check_tool(r, where, tool):
    t = tool.get("type")
    ref = tool.get("ref")
    if t not in C.TOOL_TYPES:
        r.err(where, f"unknown tool type {t!r}")
        return
    valid = {
        "ROUTINE": C.ROUTINE_GOALS, "BREATHING": list(C.BREATHING), "GUIDED": list(C.SESSIONS),
        "SOUND": list(C.SOUNDS), "JOURNAL": C.JOURNAL_KINDS,
    }
    if t in valid:
        if ref not in valid[t]:
            r.err(where, f"tool {t} needs ref in {valid[t]}, got {ref!r}")
    elif ref is not None:
        r.err(where, f"tool {t} takes no ref")


def check_shortcut(r, where, sc):
    if not sc.get("label"):
        r.err(where, "shortcut needs a label")
    target = sc.get("target", "")
    ok = target in C.SHORTCUT_TARGETS_PLAIN
    if not ok and ":" in target:
        kind, ref = target.split(":", 1)
        ok = (
            (kind == "journal" and ref in C.JOURNAL_KINDS)
            or (kind == "sound" and ref in C.SOUNDS)
            or (kind == "guided" and ref in C.SESSIONS)
            or (kind == "breathing" and ref in C.BREATHING)
        )
    if not ok:
        r.err(where, f"invalid shortcut target {target!r}")


def validate_journey(path, seen_habits):
    r = Report(str(path))
    try:
        j = parse_journey(path)
    except (ParseError, StopIteration) as e:
        r.err("parse", str(e))
        return r, []
    jid = j.get("id")
    info = C.JOURNEYS.get(jid)
    if not info:
        r.err("meta", f"id {jid!r} must be one of {list(C.JOURNEYS)}")
        return r, []
    if j.get("goalKey") != info["goalKey"]:
        r.err("meta", f"goalKey must be {info['goalKey']!r}")
    if j.get("order") != info["order"]:
        r.err("meta", f"order must be {info['order']}")
    if j.get("title") != info["title"]:
        r.err("meta", f"title must be {info['title']!r}")
    if j.get("emoji") != info["emoji"]:
        r.err("meta", f"emoji must be {info['emoji']}")
    if j.get("ritualFocus") not in C.RITUALS:
        r.err("meta", f"ritualFocus must be one of {C.RITUALS}")
    if not j.get("subtitle"):
        r.err("meta", "subtitle is required")
    for key, lo, hi in (("introLetter", 250, 560), ("outroLetter", 200, 500),
                        ("missedDaysLetter", 120, 300), ("welcomeBackLetter", 100, 260)):
        if key not in j:
            r.err(key, "missing")
        else:
            check_letter(r, key, j[key], lo, hi)

    steps = j["steps"]
    if not 7 <= len(steps) <= 9:
        r.err("steps", f"{len(steps)} steps, expected 7-9")
    ids, habits_by_ritual, food_steps = set(), {}, 0
    for n, s in enumerate(steps, 1):
        where = f"step {n} ({s.get('id')})"
        sid = s.get("id", "")
        if not sid.startswith(jid + ".") or not SNAKE.match(sid.split(".", 1)[-1] or "?"):
            r.err(where, f"step id must be '{jid}.<snake_case>'")
        if sid in ids:
            r.err(where, "duplicate step id")
        ids.add(sid)
        for f in ("buttonLabel", "completionTitle", "completionMessage"):
            if not s.get(f):
                r.err(where, f"missing {f}")
        goal = s.get("goal", {})
        gt = goal.get("type")
        if gt == "habit":
            h = goal.get("habit", {})
            hid = h.get("id", "")
            if not SNAKE.match(hid):
                r.err(where, f"habit id {hid!r} must be snake_case")
            if hid in seen_habits or hid in C.JOURNEY1_HABIT_IDS:
                r.err(where, f"habit id {hid!r} is already used")
            seen_habits.add(hid)
            for f in ("title", "description", "smallerVersion"):
                if not h.get(f):
                    r.err(where, f"habit missing {f}")
            if h.get("icon") not in C.ICONS:
                r.err(where, f"habit icon {h.get('icon')!r} not in catalog")
            if h.get("ritual") not in C.RITUALS:
                r.err(where, f"habit ritual must be one of {C.RITUALS}")
            if not isinstance(h.get("order"), int):
                r.err(where, "habit order must be an integer")
            if h.get("tool"):
                check_tool(r, where, h["tool"])
            t, w = goal.get("targetDays"), goal.get("windowDays")
            if not (isinstance(t, int) and isinstance(w, int) and 2 <= t <= 5 and t <= w <= t + 3):
                r.err(where, f"targetDays {t} / windowDays {w} out of range")
            if not 2 <= len(s.get("tips", [])) <= 4:
                r.err(where, "habit steps need 2-4 tips")
            habits_by_ritual.setdefault(h.get("ritual"), []).append(hid)
            check_letter(r, where + " letter", s["letter"], 200, 560, challenge=True)
        elif gt == "action":
            for f in ("title", "description"):
                if not goal.get(f):
                    r.err(where, f"action missing {f}")
            for k, sc in enumerate(goal.get("shortcuts", [])):
                check_shortcut(r, f"{where} shortcut {k + 1}", sc)
            check_letter(r, where + " letter", s["letter"], 200, 560, action=True)
        elif gt == "ritual":
            if n != len(steps):
                r.err(where, "a ritual challenge must be the last step")
            if goal.get("ritual") not in C.RITUALS:
                r.err(where, "ritual challenge needs a valid ritual")
            elif len(habits_by_ritual.get(goal["ritual"], [])) < 2:
                r.err(where, f"ritual {goal['ritual']} needs at least 2 habits from earlier steps of this journey")
            t, w = goal.get("targetDays"), goal.get("windowDays")
            if not (isinstance(t, int) and isinstance(w, int) and 3 <= t <= 7 and t <= w <= t + 3):
                r.err(where, f"targetDays {t} / windowDays {w} out of range")
            check_letter(r, where + " letter", s["letter"], 200, 560, challenge=True)
        else:
            r.err(where, f"goal type must be habit, action or ritual, got {gt!r}")
        if s.get("gentleNote"):
            food_steps += 1
    if jid == "eat" and food_steps < 5:
        r.err("gentleNote", f"the eat journey needs a gentleNote on every food step ({food_steps} found, at least 5 required)")
    if not any(s.get("goal", {}).get("type") == "ritual" for s in steps):
        r.warn("steps", "no final ritual challenge step")
    return r, sorted(seen_habits)


def validate_sessions(path):
    r = Report(str(path))
    try:
        sessions = parse_sessions(path)
    except (ParseError, StopIteration) as e:
        r.err("parse", str(e))
        return r, []
    if not sessions:
        r.err("sessions", "no '## Session' sections found")
    ids = []
    for s in sessions:
        sid = s.get("id")
        where = f"session {sid}"
        ids.append(sid)
        info = C.SESSIONS.get(sid)
        if not info:
            r.err(where, f"id must be one of {list(C.SESSIONS)}")
            continue
        if s.get("minutes") != info["minutes"]:
            r.err(where, f"minutes must be {info['minutes']}")
        if s.get("category") != info["category"]:
            r.err(where, f"category must be {info['category']}")
        for f in ("title", "description", "bestFor"):
            if not s.get(f):
                r.err(where, f"missing {f}")
        if s.get("ambient") is not None and s["ambient"] not in C.SOUNDS:
            r.err(where, f"ambient {s['ambient']!r} not in sound catalog")
        spoken = [x for x in s["steps"] if x.get("say")]
        if len(spoken) < 8:
            r.err(where, f"only {len(spoken)} spoken lines, expected at least 8")
        text = " ".join(x["say"] for x in spoken)
        speech = words(text) / C.WORDS_PER_SECOND
        pauses = sum(x["pause"] for x in s["steps"])
        total = (speech + pauses) / 60
        target = info["minutes"]
        if not 0.8 * target <= total <= 1.2 * target:
            r.err(where, f"estimated {total:.1f} min (speech {speech / 60:.1f} + pauses {pauses / 60:.1f}), expected {target} min +/-20%")
        for pat in C.BANNED_PHRASES:
            m = re.search(pat, text, re.I)
            if m:
                r.err(where, f"banned phrase '{m.group(0)}'")
        if re.search(r"[*_#`>]|\bhttps?://|[\U0001F300-\U0001FAFF]", text):
            r.err(where, "spoken lines must be plain text: no markdown, links or emoji")
        if any(len(x["say"].split()) > 40 for x in spoken):
            r.err(where, "a spoken line is longer than 40 words; split it")
    return r, ids


def main(argv):
    paths = [Path(a) for a in argv]
    if not paths:
        paths = sorted((ROOT / "docs" / "journeys").glob("0[2-5]-*.md")) + sorted((ROOT / "docs" / "audio").glob("sessions_*.md"))
    reports, seen_habits, seen_sessions = [], set(), []
    for p in paths:
        if "audio" in p.parts or p.name.startswith("sessions_"):
            r, ids = validate_sessions(p)
            for i in ids:
                if i in seen_sessions:
                    r.err("sessions", f"session id {i} is defined more than once")
            seen_sessions += ids
        else:
            r, _ = validate_journey(p, seen_habits)
        reports.append(r)
    bad = False
    for r in reports:
        status = "FAIL" if r.errors else "OK"
        print(f"[{status}] {r.name}")
        for e in r.errors:
            print(f"   error: {e}")
            bad = True
        for w in r.warnings:
            print(f"   warning: {w}")
    if not paths:
        print("no content documents found")
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
