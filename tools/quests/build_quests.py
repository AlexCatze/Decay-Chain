"""Decay Chain questbook generator.

Writes config/betterquesting/DefaultQuests.json (Better Questing Unofficial 4.x, NBT-as-JSON format).
Quests are a guide only: no rewards, they auto-complete when their tasks are done.

Usage:  python tools/quests/build_quests.py
Bump PACK_VERSION whenever the questbook changes so existing worlds pick up the new defaults
("Load DefaultQuests when an Update is Available" is on in betterquesting.cfg).

Note: edits made with the in-game editor are overwritten the next time this script runs.
"""
import json
import os

from bq import build
from primitive import CHAPTERS as PRIMITIVE

PACK_VERSION = 12
OUT = os.path.join(os.path.dirname(__file__), '..', '..', 'config', 'betterquesting', 'DefaultQuests.json')


if __name__ == '__main__':
    data = build(PRIMITIVE, PACK_VERSION)
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    print(f"wrote {len(data['questDatabase:9'])} quests in {len(data['questLines:9'])} chapters -> {os.path.normpath(OUT)}")
