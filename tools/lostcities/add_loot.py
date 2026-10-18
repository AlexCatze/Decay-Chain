"""Adds chests and spawners to the Lost Cities buildings that have none.

Usage:  python tools/lostcities/add_loot.py [path to lostcities-1.12-*.jar]
Rewrites the generated 'part' entries in config/lostcities/userassets.json (loaded after the stock assets, so a part
with the same name replaces the stock one); everything else in that file is kept.

In stock Lost Cities the 'building5' tower, the shopping malls, the city centres and some libraries are built without a
single chest or spawner - in the ruined (HBM concrete) cities they stood out as empty shells. This copies their floors
and adds loot the way the stock buildings do it: a spawner ('1', easy mobs) set into the floor slab under every floor,
and a chest ('C', city loot) standing against a wall on every other floor. Roof parts are left alone.
"""
import glob
import json
import os
import random
import sys
import zipfile

REPO = os.path.join(os.path.dirname(__file__), '..', '..')
USERASSETS = os.path.join(REPO, 'config', 'lostcities', 'userassets.json')
DEFAULT_JAR = glob.glob(os.path.expandvars(r'%APPDATA%\PrismLauncher\instances\Decay Chain-1.0.0\minecraft\mods'
                                           r'\lostcities-1.12-*.jar'))
LOOT = ('C', '1', '4')
SEED = 1986


def load_stock(jar):
    with zipfile.ZipFile(jar) as z:
        entries = []
        for name in ('library.json', 'buildingparts.json'):
            entries += json.loads(z.read('assets/lostcities/citydata/' + name).decode('utf-8'))
    return entries


def has_loot(part):
    return any(ch in row for sl in part['slices'] for row in sl for ch in LOOT)


def add_loot(part, rng, with_chest):
    """Copy of the part with a spawner in its floor slab and (optionally) a chest against a wall."""
    slices = [[list(row) for row in sl] for sl in part['slices']]
    floor, room = slices[0], slices[1]
    above = slices[2] if len(slices) > 2 else None
    zs, xs = len(room), len(room[0])
    counts = {}
    for row in floor:
        for ch in row:
            counts[ch] = counts.get(ch, 0) + 1
    slab = max((c for c in counts if c != ' '), key=counts.get, default=None)

    def open_cell(z, x):
        return room[z][x] == ' ' and (above is None or above[z][x] == ' ')

    cells = [(z, x) for z in range(zs) for x in range(xs)]
    chest = None
    if with_chest:
        walls = [(z, x) for z, x in cells if open_cell(z, x) and floor[z][x] != ' '
                 and any(0 <= z + dz < zs and 0 <= x + dx < xs and room[z + dz][x + dx] != ' '
                         for dz, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)))]
        if walls:
            chest = rng.choice(walls)
            room[chest[0]][chest[1]] = 'C'
    spawner = [(z, x) for z, x in cells if floor[z][x] == slab and room[z][x] == ' ' and 2 <= z < zs - 2
               and 2 <= x < xs - 2 and (chest is None or abs(z - chest[0]) + abs(x - chest[1]) > 3)]
    if spawner:
        z, x = rng.choice(spawner)
        floor[z][x] = '1'
    out = dict(part)
    out['slices'] = [[''.join(row) for row in sl] for sl in slices]
    return out, chest is not None, bool(spawner)


def main():
    jar = sys.argv[1] if len(sys.argv) > 1 else (DEFAULT_JAR[0] if DEFAULT_JAR else None)
    if not jar:
        sys.exit('Lost Cities jar not found - pass its path')
    stock = load_stock(jar)
    parts = {e['name']: e for e in stock if e['type'] == 'part'}
    rng = random.Random(SEED)
    generated = []
    for b in (e for e in stock if e['type'] == 'building'):
        names = []
        for key in ('parts', 'parts2'):
            for ref in b.get(key, []):
                if ref['part'] not in names:
                    names.append(ref['part'])
        if any(has_loot(parts[n]) for n in names if n in parts):
            continue                                          # the building already has its own loot
        floors = [n for n in names if n in parts and not n.startswith('top') and len(parts[n]['slices']) > 2]
        added = [0, 0]
        for i, name in enumerate(floors):
            if any(g['name'] == name for g in generated):
                continue                                      # a part shared with an earlier building
            part, chest, spawner = add_loot(parts[name], rng, with_chest=i % 2 == 0)
            generated.append(part)
            added[0] += chest
            added[1] += spawner
        print(f"{b['name']}: {len(floors)} floor parts, +{added[0]} chests, +{added[1]} spawners")

    with open(USERASSETS, encoding='utf-8') as f:
        assets = json.load(f)
    names = {p['name'] for p in generated}
    assets = [e for e in assets if not (e['type'] == 'part' and e['name'] in names)] + generated
    with open(USERASSETS, 'w', encoding='utf-8') as f:
        json.dump(assets, f, indent=2, ensure_ascii=False)
    print(f'wrote {len(generated)} parts -> {os.path.normpath(USERASSETS)}')


if __name__ == '__main__':
    main()
