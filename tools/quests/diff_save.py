"""Compare a world's Better Questing database (edited with the in-game editor) against the generated questbook.

Usage:  python tools/quests/diff_save.py "<instance>/saves/<world>/betterquesting/QuestDatabase.json"

Prints every quest whose name, prerequisites, logic, icon, tasks or layout differ, plus quests that exist on only one
side, so in-game edits can be ported back into the chapter modules (primitive.py, ...). BQ omits default values when
it saves, so missing keys are compared as their defaults. Descriptions are reported as changed/unchanged only.
"""
import json
import os
import sys

REPO_BOOK = os.path.join(os.path.dirname(__file__), '..', '..', 'config', 'betterquesting', 'DefaultQuests.json')
DEFAULTS = {'taskLogic:8': 'AND', 'questLogic:8': 'AND', 'isMain:1': 0, 'visibility:8': 'NORMAL'}


def load(path):
    d = json.load(open(path, encoding='utf-8'))
    quests = {q['questID:3']: q for q in d['questDatabase:9'].values()}
    layout = {}
    for line in d['questLines:9'].values():
        for e in line['quests:9'].values():
            layout[e['id:3']] = (line['lineID:3'], e['x:3'], e['y:3'], e.get('sizeX:3', 24))
    return quests, layout


def props(q):
    p = q['properties:10']['betterquesting:10']
    return {k: p.get(k, v) for k, v in DEFAULTS.items()} | {'name:8': p.get('name:8'), 'desc:8': p.get('desc:8'),
                                                            'icon': (p['icon:10'].get('id:8'), p['icon:10'].get('Damage:2', 0))}


def tasks(q):
    out = []
    for t in sorted(q.get('tasks:9', {}).values(), key=lambda t: t.get('index:3', 0)):
        items = [f"{e['id:8']}:{e.get('Damage:2', 0)}x{e.get('Count:3', 1)}" + (f"[{e['OreDict:8']}]" if e.get('OreDict:8') else '')
                 for e in t.get('requiredItems:9', {}).values()]
        out.append((t['taskID:8'], t.get('entryLogic:8', 'AND'), t.get('optional:1', 0), items))
    return out


def main(save_path):
    sq, sl = load(save_path)
    rq, rl = load(REPO_BOOK)
    for i in sorted(set(sq) - set(rq)):
        p = props(sq[i])
        print(f'ONLY IN SAVE {i} {p["name:8"]!r} prereq={sq[i].get("preRequisites:11", [])} icon={p["icon"]} layout={sl.get(i)}')
        for t in tasks(sq[i]):
            print('    task', t)
    for i in sorted(set(rq) - set(sq)):
        print(f'ONLY IN REPO {i} {props(rq[i])["name:8"]!r}')
    for i in sorted(set(sq) & set(rq)):
        a, b = props(sq[i]), props(rq[i])
        diffs = [(k, b[k], a[k]) for k in a if k != 'desc:8' and a[k] != b[k]]
        if a['desc:8'] != b['desc:8']:
            diffs.append(('desc', 'changed', ''))
        if sorted(sq[i].get('preRequisites:11', [])) != sorted(rq[i].get('preRequisites:11', [])):
            diffs.append(('prereq', rq[i].get('preRequisites:11', []), sq[i].get('preRequisites:11', [])))
        if tasks(sq[i]) != tasks(rq[i]):
            diffs.append(('tasks', tasks(rq[i]), tasks(sq[i])))
        if sl.get(i) != rl.get(i):
            diffs.append(('layout (line, x, y, size)', rl.get(i), sl.get(i)))
        if diffs:
            print(f'\nDIFF {i} {b["name:8"]!r}  (repo -> save)')
            for d in diffs:
                print('   ', d)


if __name__ == '__main__':
    main(sys.argv[1])
