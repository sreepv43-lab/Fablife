"""Parsers for the Markdown content documents described in docs/DESIGN_PHASE2.md (sections 2 and 3)."""
import json
import re
from pathlib import Path

from catalog import DEFAULT_PAUSE_SECONDS


class ParseError(Exception):
    pass


FENCE = re.compile(r"^```(\w+)\s+(\w+)\s*$")


def _blockquote(lines, start):
    """Returns (text, next_index) for the first blockquote at or after `start`, skipping blank lines."""
    i = start
    while i < len(lines) and not lines[i].strip():
        i += 1
    body = []
    while i < len(lines) and lines[i].startswith(">"):
        body.append(lines[i][2:] if lines[i].startswith("> ") else lines[i][1:])
        i += 1
    if not body:
        raise ParseError(f"expected a blockquote after line {start}")
    return "\n".join(body).strip(), i


def _fenced_json(lines, start, kind):
    """Finds a ```json <kind> block starting at or after `start`. Returns (obj, next_index)."""
    i = start
    while i < len(lines):
        m = FENCE.match(lines[i])
        if m and m.group(1) == "json" and m.group(2) == kind:
            j = i + 1
            block = []
            while j < len(lines) and not lines[j].startswith("```"):
                block.append(lines[j])
                j += 1
            try:
                return json.loads("\n".join(block)), j + 1
            except json.JSONDecodeError as e:
                raise ParseError(f"line {i + 1}: invalid JSON in ```json {kind} block: {e}")
        i += 1
    raise ParseError(f"missing ```json {kind} block after line {start}")


def _title(heading: str) -> str:
    parts = re.split(r"\s+—\s+", heading, maxsplit=1)
    return (parts[1] if len(parts) > 1 else parts[0]).strip()


def parse_journey(path):
    """Parses a journey document into the app's Journey JSON (see content/Models.kt)."""
    lines = Path(path).read_text().splitlines()
    meta, _ = _fenced_json(lines, 0, "meta")

    # Split into level-2 sections.
    sections = []
    for i, line in enumerate(lines):
        if line.startswith("## "):
            sections.append((i, line[3:].strip()))
    sections.append((len(lines), None))

    journey = dict(meta)
    journey["steps"] = []
    for (start, heading), (end, _) in zip(sections, sections[1:]):
        body = lines[start + 1:end]
        if heading.startswith("Intro Letter"):
            text, _ = _blockquote(body, 0)
            journey["introLetter"] = {"title": _title(heading), "body": text}
        elif heading.startswith("Outro Letter"):
            text, _ = _blockquote(body, 0)
            journey["outroLetter"] = {"title": _title(heading), "body": text}
        elif heading.startswith("Supporting Letter") and "Missed" in heading:
            text, _ = _blockquote(body, 0)
            journey["missedDaysLetter"] = {"title": _title(heading), "body": text}
        elif heading.startswith("Supporting Letter") and "Welcome" in heading:
            text, _ = _blockquote(body, 0)
            journey["welcomeBackLetter"] = {"title": _title(heading), "body": text}
        elif re.match(r"Step \d+", heading):
            step, idx = _fenced_json(body, 0, "step")
            letter_heading = next((l for l in body[idx:] if l.startswith("### Letter")), None)
            if letter_heading is None:
                raise ParseError(f"line {start + 1}: step '{heading}' has no '### Letter — ...' heading")
            li = body.index(letter_heading)
            text, _ = _blockquote(body, li + 1)
            step["title"] = _title(heading)
            step["letter"] = {"title": _title(letter_heading[4:]), "body": text}
            step.setdefault("tips", [])
            step.setdefault("gentleNote", None)
            journey["steps"].append(step)
    return journey


def parse_sessions(path):
    """Parses a guided-session document into a list of GuidedSession dicts."""
    lines = Path(path).read_text().splitlines()
    starts = [i for i, l in enumerate(lines) if l.startswith("## Session")]
    starts.append(len(lines))
    sessions = []
    for a, b in zip(starts, starts[1:]):
        body = lines[a + 1:b]
        meta, idx = _fenced_json(body, 0, "session")
        steps = []
        for raw in body[idx:]:
            line = raw.strip()
            if not line or line.startswith("#"):
                continue
            m = re.match(r"^(.*?)\s*\[pause\s+(\d+)\]\s*$", line)
            if m:
                text, pause = m.group(1).strip(), float(m.group(2))
            else:
                text, pause = line, DEFAULT_PAUSE_SECONDS
            step = {"pause": pause}
            if text:
                step["say"] = text
            steps.append(step)
        session = dict(meta)
        session["steps"] = steps
        sessions.append(session)
    return sessions
