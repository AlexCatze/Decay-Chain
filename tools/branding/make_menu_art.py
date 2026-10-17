"""Draws the pixel-art scenes standing on the main menu buttons (Custom Main Menu, config/CustomMainMenu).

Usage:  python tools/branding/make_menu_art.py
Writes groovy/assets/decaychain/textures/gui/menu/*.png. One art pixel is one GUI pixel (like the title logo) and
SCALE image pixels. Every strip is as wide as its button; its bottom row sits on the button's top edge. The quit strip
reaches EMBED px further down: the button is drawn over that part, so the bombs look stuck in it.

Objects get the logo's 1 px black outline; thin things (lattices, wires, crops, smoke) are drawn without one.
"""
import math
import os
import random

from PIL import Image

OUT = os.path.join(os.path.dirname(__file__), '..', '..', 'groovy', 'assets', 'decaychain', 'textures', 'gui', 'menu')
SCALE = 4
STRIP_H = 20
EMBED = 8

BLACK = (2, 1, 1)
# shared palette (the logo's rust and biomass included)
RUST = [(84, 38, 18), (112, 52, 22), (140, 68, 28), (166, 86, 36), (190, 108, 48), (208, 132, 66)]
FLESH = [(58, 10, 16), (88, 18, 26), (118, 30, 36), (148, 46, 48), (176, 72, 66), (204, 110, 98)]
VEIN = (36, 5, 9)
PUS = (210, 192, 112)
BARK = [(72, 56, 34), (96, 74, 44), (110, 86, 52)]
PLANK = [(98, 76, 44), (132, 104, 62), (156, 126, 78), (176, 146, 94)]
COBBLE = [(78, 78, 80), (104, 104, 106), (128, 128, 130), (150, 150, 152)]
STEEL = [(36, 34, 36), (58, 56, 60), (86, 86, 92), (122, 124, 130), (160, 164, 170)]
LEAF = [(36, 82, 26), (50, 108, 34), (66, 132, 42), (92, 160, 56)]
FIRE = [(196, 56, 20), (244, 128, 30), (255, 196, 64), (255, 238, 150)]
OLIVE = [(56, 62, 34), (76, 84, 46), (98, 106, 60), (122, 130, 78)]
SAND = [(116, 102, 66), (150, 134, 92), (178, 162, 116), (198, 184, 138)]
SMOKE = [(150, 150, 150, 150), (186, 186, 186, 120), (214, 214, 214, 90)]


def n4(p):
    x, y = p
    return (x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)


class Strip:
    def __init__(self, w, h=STRIP_H, seed=1):
        self.w, self.h, self.px = w, h, {}
        self.rng = random.Random(seed)

    def add(self, layer, outline=True):
        """Draw one object over what is there; its outline goes over the objects behind it."""
        if outline:
            for p in layer:
                for q in n4(p):
                    if q not in layer:
                        self.px[q] = BLACK
        self.px.update(layer)

    def save(self, name):
        img = Image.new('RGBA', (self.w, self.h), (0, 0, 0, 0))
        for (x, y), c in self.px.items():
            if 0 <= x < self.w and 0 <= y < self.h:
                img.putpixel((x, y), c if len(c) == 4 else c + (255,))
        img = img.resize((self.w * SCALE, self.h * SCALE), Image.NEAREST)
        os.makedirs(OUT, exist_ok=True)
        img.save(os.path.join(OUT, name + '.png'))
        print('wrote', name, (self.w, self.h))


# ---------------------------------------------------------------- drawing helpers (on a layer dict)

def rect(layer, x0, y0, x1, y1, color):
    for x in range(x0, x1 + 1):
        for y in range(y0, y1 + 1):
            layer[(x, y)] = color(x, y) if callable(color) else color


def line(layer, x0, y0, x1, y1, color):
    steps = max(abs(x1 - x0), abs(y1 - y0), 1)
    for i in range(steps + 1):
        x, y = round(x0 + (x1 - x0) * i / steps), round(y0 + (y1 - y0) * i / steps)
        layer[(x, y)] = color(x, y) if callable(color) else color


def sprite(layer, x0, y0, rows, palette, mirror=False):
    w = max(len(r) for r in rows)
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != '.':
                layer[(x0 + (w - 1 - x if mirror else x), y0 + y)] = palette[ch]


def sag(layer, x0, y0, x1, y1, depth, color):
    """A hanging wire."""
    steps = abs(x1 - x0)
    points = [(round(x0 + (x1 - x0) * i / steps), round(y0 + (y1 - y0) * i / steps + depth * 4 * i / steps
                                                         * (1 - i / steps))) for i in range(steps + 1)]
    for (ax, ay), (bx, by) in zip(points, points[1:]):
        line(layer, ax, ay, bx, by, color)


# ---------------------------------------------------------------- singleplayer: shelter | oak and farm

def shelter(s, ox):
    rng = s.rng
    walls = {}
    hx, hw = ox + 10, 22
    for y in range(9, 20):
        for x in range(hx, hx + hw):
            if y >= 14:                                       # cobblestone base
                walls[(x, y)] = COBBLE[rng.choice((0, 1, 1, 2, 2, 3))]
            elif (y - 9) % 3 == 2:                            # plank seams
                walls[(x, y)] = PLANK[0]
            else:
                walls[(x, y)] = PLANK[2] if (x * 7 + y // 3 * 5) % 11 else PLANK[1]
    for x in range(hx, hx + hw):
        walls[(x, 9)] = PLANK[0]                              # shadow under the roof
    rect(walls, hx + 13, 12, hx + 17, 19, lambda x, y: PLANK[1] if x % 2 else PLANK[0])   # door
    walls[(hx + 16, 16)] = STEEL[3]
    rect(walls, hx + 3, 10, hx + 8, 14, PLANK[0])            # lit window
    rect(walls, hx + 4, 11, hx + 7, 13, lambda x, y: FIRE[3] if y == 12 and x in (hx + 5, hx + 6) else FIRE[2])
    walls[(hx + 5, 11)] = walls[(hx + 5, 13)] = PLANK[0]
    for x in range(hx + 18, hx + 22):                         # torch light on the wall
        for y in range(10, 17):
            if (x - hx - 19) ** 2 + (y - 13) ** 2 <= 6 and (x, y) in walls:
                r, g, b = walls[(x, y)]
                walls[(x, y)] = (min(255, r + 40), min(255, g + 26), b)
    roof = {}
    for x in range(hx - 3, hx + hw + 3):
        top = 5 + (x - hx + 3) // 9
        for y in (top, top + 1):
            roof[(x, y)] = RUST[4 if (x // 2) % 2 else 2] if y == top else RUST[3 if (x // 2) % 2 else 1]
    for x in rng.sample(range(hx - 2, hx + hw + 2), 4):
        top = 5 + (x - hx + 3) // 9
        roof[(x, top + 1)] = RUST[0]
    s.add(walls)
    s.add(roof)
    torch = {}
    line(torch, hx + 19, 14, hx + 19, 15, BARK[1])
    torch[(hx + 19, 13)], torch[(hx + 19, 12)] = FIRE[2], FIRE[1]
    s.add(torch, outline=False)
    palisade = {}                                             # sharpened logs: nights are long
    for i, height in enumerate((9, 11, 8, 10)):
        x = ox + i * 2
        for y in range(20 - height, 20):
            palisade[(x, y)] = BARK[1] if y % 3 else BARK[0]
            palisade[(x + 1, y)] = BARK[0]
        palisade[(x, 19 - height)] = PLANK[3]
        palisade[(x, 20 - height)] = palisade[(x + 1, 20 - height)] = PLANK[2]
    s.add(palisade)
    cx = hx + hw + 8                                          # campfire, boiling pot on a tripod
    fire = {}
    sprite(fire, cx - 3, 13, [
        "...a...",
        "..aba..",
        ".abcba.",
        ".bcdcb.",
        "abcdcba",
        "lLlLlLl",
    ], {'a': FIRE[0], 'b': FIRE[1], 'c': FIRE[2], 'd': FIRE[3], 'l': BARK[0], 'L': PLANK[1]})
    s.add(fire)
    tripod = {}
    line(tripod, cx - 5, 19, cx, 8, BARK[2])
    line(tripod, cx + 5, 19, cx, 8, BARK[1])
    s.add(tripod, outline=False)
    pot = {}
    pot[(cx, 9)] = STEEL[2]
    rect(pot, cx - 2, 10, cx + 2, 12, lambda x, y: STEEL[3] if y == 10 else STEEL[1] if x < cx + 1 else STEEL[0])
    s.add(pot)
    steam = {}
    for y in range(1, 9):
        x = cx + round(math.sin(y * 0.9) * 1.5)
        steam[(x, y)] = SMOKE[min(2, (8 - y) // 3)]
        if y % 3 == 1:
            steam[(x + 1, y)] = SMOKE[2]
    s.add(steam, outline=False)


def farm(s, x0, x1, tree_x):
    rng = s.rng
    crops = {}
    for x in range(x0 + 1, x1):
        if x in (x0 + 22, x0 + 23):
            continue
        if x < x0 + 22:                                       # wheat
            height = rng.randint(4, 6)
            for d in range(1, height + 1):
                crops[(x, 17 - d)] = (110, 150, 44) if d < height - 1 else (214, 184, 80) if x % 2 else (180, 148, 58)
        else:                                                 # carrots / potatoes
            height = rng.randint(2, 3)
            for d in range(1, height + 1):
                crops[(x, 17 - d)] = LEAF[2] if (x + d) % 2 else LEAF[1]
    s.add(crops, outline=False)
    land = {}
    rect(land, x0, 17, x1, 19, lambda x, y: (76, 48, 28) if y == 17 and x % 3 else (66, 40, 24) if y == 17
         else (118, 84, 56) if rng.random() < 0.6 else (98, 68, 44))
    rect(land, x0 + 22, 17, x0 + 23, 18, lambda x, y: (60, 96, 204) if y == 17 else (44, 72, 170))
    s.add(land)
    scarecrow = {}
    sx = x0 + 32
    rect(scarecrow, sx, 9, sx, 16, BARK[1])
    rect(scarecrow, sx - 4, 10, sx + 4, 10, BARK[1])
    sprite(scarecrow, sx - 2, 4, [
        ".gg..",
        "ooooo",
        "oyoyo",
        "ooooo",
        "oyyyo",
    ], {'g': LEAF[1], 'o': (220, 128, 32), 'y': (255, 214, 80)})
    rect(scarecrow, sx - 2, 11, sx + 2, 14, lambda x, y: (150, 52, 40) if (x + y) % 3 else (120, 40, 32))
    scarecrow[(sx - 2, 15)] = scarecrow[(sx + 1, 15)] = (190, 170, 90)   # straw
    s.add(scarecrow)
    fence = {}
    for fx in (x0 - 7, x0 - 2):
        rect(fence, fx, 12, fx + 1, 19, lambda x, y: PLANK[2] if x % 2 else PLANK[1])
    rect(fence, x0 - 6, 13, x0 - 3, 13, PLANK[2])
    rect(fence, x0 - 6, 16, x0 - 3, 16, PLANK[2])
    s.add(fence)
    tree = {}
    crown = [
        "......xxxxxx......",
        "...xxxxxxxxxxxx...",
        ".xxxxxxxxxxxxxxxx.",
        "xxxxxxxxxxxxxxxxxx",
        "xxxxxxxxxxxxxxxxxx",
        "xxxxxxxxxxxxxxxxxx",
        ".xxxxxxxxxxxxxxxx.",
        "..xxxxxxxxxxxxxx..",
        "....xxxx..xxxx....",
    ]
    rect(tree, tree_x - 1, 8, tree_x + 1, 19, lambda x, y: BARK[2] if x == tree_x - 1 else BARK[0] if x == tree_x + 1
         or y % 4 == 0 else BARK[1])
    tree[(tree_x - 2, 19)] = tree[(tree_x + 2, 19)] = BARK[0]
    line(tree, tree_x + 1, 12, tree_x + 4, 9, BARK[0])
    for y, row in enumerate(crown):
        for x, ch in enumerate(row):
            if ch == 'x':
                px, py = tree_x - 9 + x, 1 + y
                light = 0.55 - y * 0.09 - x * 0.015 + rng.random() * 0.5
                tree[(px, py)] = LEAF[3 if light > 0.62 else 2 if light > 0.38 else 1 if light > 0.12 else 0]
    for p in ((tree_x - 5, 5), (tree_x + 4, 7), (tree_x + 1, 3)):
        tree[p] = (196, 40, 36)                               # apples
    s.add(tree)


def singleplayer():
    s = Strip(200, seed=11)
    shelter(s, 2)
    farm(s, 140, 182, 191)
    s.save('singleplayer')


# ---------------------------------------------------------------- multiplayer: outpost | horde

def outpost(s, ox):
    rng = s.rng
    tent = {}
    for y in range(8, 20):
        half = 1 + (y - 8) * 0.8
        for x in range(round(ox + 10 - half), round(ox + 10 + half) + 1):
            c = OLIVE[2] if x < ox + 10 else OLIVE[1]
            if rng.random() < 0.18:
                c = OLIVE[0] if rng.random() < 0.5 else (92, 82, 54)   # camo blotches
            tent[(x, y)] = c
    for y in range(14, 20):
        half = (y - 14) * 0.6
        for x in range(round(ox + 10 - half), round(ox + 10 + half) + 1):
            tent[(x, y)] = OLIVE[0] if y > 14 else OLIVE[1]
    s.add(tent)
    pole = {}
    line(pole, ox + 10, 0, ox + 10, 7, STEEL[2])
    sprite(pole, ox + 11, 1, ["rrrrr.", "rRRrrr", "rrrr.r", "rr...."],
           {'r': (150, 54, 40), 'R': (196, 170, 80)})
    s.add(pole)
    crates = {}
    rect(crates, ox + 21, 15, ox + 27, 19, lambda x, y: OLIVE[0] if x in (ox + 21, ox + 27) or y in (15, 19)
         else OLIVE[1])
    crates[(ox + 24, 17)] = (220, 190, 60)
    s.add(crates)
    gx = ox + 33                                              # soldier, behind the sandbags, facing right
    soldier = {}
    sprite(soldier, gx, 2, [
        ".hHHh.",
        "hHHHHH",
        ".sSSe.",
        ".sSSS.",
        ".CcCC.",
        "CCcCCa",
        "CcCCaa",
        ".CCcC.",
        ".CcCC.",
        ".CCCc.",
    ], {'h': OLIVE[1], 'H': OLIVE[2], 's': (160, 114, 80), 'S': (196, 150, 110), 'e': (30, 26, 24),
        'C': OLIVE[2], 'c': OLIVE[0], 'a': (184, 140, 100)})
    s.add(soldier)
    bags = {}
    for row, (y, a, b) in enumerate(((12, ox + 30, ox + 44), (14, ox + 29, ox + 45), (16, ox + 28, ox + 46),
                                     (18, ox + 28, ox + 46))):
        for x in range(a, b + 1):
            seam = (x - a + row * 3) % 6 == 5
            bags[(x, y)] = SAND[0] if seam else SAND[3] if (x - a + row * 3) % 6 == 1 else SAND[2]
            bags[(x, y + 1)] = SAND[0] if seam else SAND[1]
    s.add(bags)
    gun = {}
    sprite(gun, gx + 3, 7, [
        "....kkkkk.........",
        "wwwkKKKKKjjjjjjbbbbb",
        "wwwkKKKKKjJjJjJ.....",
        "....kk.a.....l.l....",
        ".......a....l...l...",
    ], {'k': STEEL[1], 'K': STEEL[2], 'j': STEEL[1], 'J': STEEL[3], 'b': STEEL[2], 'w': PLANK[1],
        'a': (214, 172, 60), 'l': STEEL[1]})
    s.add(gun)
    flash = {}
    sprite(flash, gx + 23, 5, [
        "a...a",
        ".aba.",
        "abcba",
        ".aba.",
        "a...a",
    ], {'a': FIRE[1], 'b': FIRE[2], 'c': FIRE[3]})
    s.add(flash, outline=False)
    wire = {}
    for x in (ox + 52, ox + 66):
        line(wire, x, 12, x, 19, BARK[0])
    for cx in range(ox + 55, ox + 65, 4):
        for a in range(0, 360, 20):
            x, y = round(cx + 2.6 * math.cos(math.radians(a))), round(16 + 2.6 * math.sin(math.radians(a)))
            wire[(x, y)] = STEEL[3] if a < 180 else STEEL[2]
    s.add(wire, outline=False)


ZOMBIE = [
    "....HHHh.",
    "....EHHh.",
    "....HHHh.",
    "....MHHh.",
    "aaaSSTTt.",
    "aaaSSTTt.",
    ".....TTt.",
    ".....TtT.",
    ".....PPp.",
    "....PP.Pp",
    "....PP.Pp",
    "...ff..ff",
]
VILLAGER = [
    "....HHHh.",
    "....HHHh.",
    "....EHHh.",
    "...NNHHh.",
    "...NNHHh.",
    "aaaRRRRr.",
    "aaaRRRRr.",
    "....RRRr.",
    "....RrRr.",
    "....RRRr.",
    "....RR.Rr",
    "...ff..ff",
]
BABY = [
    "..HHHh",
    "..EHHh",
    "..MHHh",
    "aaSTT.",
    "...TT.",
    "...PP.",
    "..f..f",
]
BRUTE = [
    "......HHHHh.",
    "......HHHHh.",
    "......EEHHh.",
    "......HHHHh.",
    "......MMHHh.",
    "aaaaaaSSTTTt",
    "aaaaaaSSTBTt",
    "aaaaaaSSTTTt",
    "......TBTTTt",
    "......TTTTTt",
    "......tTTBTt",
    "......PPPPPp",
    ".....PPP.PPp",
    ".....PPP.PPp",
    ".....PPP.PPp",
    "....fff..fff",
]
CRAWLER = [
    ".....HHHh.........",
    ".....EHHhTT.......",
    "aaaa.HHHhTTTTTTtt.",
    "aaaaaMHHhTTTTtTTtB",
]


def zombie(s, x, rows, skin, shirt, pants, extra=None):
    pal = {'H': skin, 'h': tuple(int(c * 0.78) for c in skin), 'a': skin, 'E': (24, 20, 20), 'M': (40, 26, 24),
           'S': shirt, 'T': shirt, 't': tuple(int(c * 0.75) for c in shirt), 'P': pants,
           'p': tuple(int(c * 0.75) for c in pants), 'f': (58, 58, 62), 'R': shirt, 'r': tuple(int(c * 0.75) for c in shirt),
           'N': tuple(int(c * 0.85) for c in skin), 'B': FLESH[2]}
    pal.update(extra or {})
    layer = {}
    sprite(layer, x, STRIP_H - len(rows), rows, pal)
    s.add(layer)


def horde(s):
    green, husk, rotten = (92, 146, 70), (180, 152, 106), (84, 120, 66)
    zombie(s, 186, BRUTE, rotten, (90, 94, 104), (60, 58, 66))
    zombie(s, 178, ZOMBIE, husk, (120, 104, 76), (92, 78, 58))
    zombie(s, 168, ZOMBIE, green, (0, 136, 136), (60, 56, 146))
    zombie(s, 158, VILLAGER, green, (104, 74, 48), (104, 74, 48))
    zombie(s, 149, ZOMBIE, (108, 150, 140), (70, 110, 90), (58, 74, 92))   # drowned-looking
    zombie(s, 141, BABY, green, (0, 136, 136), (60, 56, 146))
    zombie(s, 121, CRAWLER, green, (120, 70, 60), (60, 56, 146))
    blood = {}
    for x in range(139, 146):
        if s.rng.random() < 0.6:
            blood[(x, 19)] = FLESH[1]
    s.add(blood, outline=False)


def multiplayer():
    s = Strip(200, seed=22)
    outpost(s, 0)
    horde(s)
    s.save('multiplayer')


# ---------------------------------------------------------------- mods: oil derrick | power grid

def lattice(layer, left, right, ys, color_a, color_b):
    """Two legs (lists of (x, y) from top to bottom) braced at the given rows, with X bracing between them."""
    def leg_x(leg, y):
        (x0, y0), (x1, y1) = leg
        return round(x0 + (x1 - x0) * (y - y0) / (y1 - y0))
    for leg, c in ((left, color_a), (right, color_b)):
        line(layer, leg[0][0], leg[0][1], leg[1][0], leg[1][1], c)
    for a, b in zip(ys, ys[1:]):
        line(layer, leg_x(left, a), a, leg_x(right, b), b, color_b)
        line(layer, leg_x(right, a), a, leg_x(left, b), b, color_b)
    for y in ys:
        line(layer, leg_x(left, y), y, leg_x(right, y), y, color_a)


def derrick(s, ox):
    rng = s.rng
    tower = {}
    lattice(tower, ((ox + 13, 2), (ox + 7, 19)), ((ox + 17, 2), (ox + 23, 19)), [2, 6, 10, 14, 19], RUST[3], RUST[1])
    line(tower, ox + 12, 2, ox + 6, 19, BLACK)                 # legs: dark outer edge
    line(tower, ox + 18, 2, ox + 24, 19, BLACK)
    for p in list(tower):
        if tower[p] != BLACK and rng.random() < 0.25:
            tower[p] = RUST[2]
    s.add(tower, outline=False)
    parts = {}
    rect(parts, ox + 11, 0, ox + 19, 1, lambda x, y: RUST[4] if y == 0 else RUST[2])                       # crown
    rect(parts, ox + 4, 14, ox + 26, 14, STEEL[3])                                                          # floor
    rect(parts, ox + 26, 15, ox + 31, 19, lambda x, y: RUST[4 if x % 2 else 2] if y == 15 else
         RUST[3 if x % 2 else 1])                                                                         # dog house
    parts[(ox + 28, 17)] = FIRE[2]
    s.add(parts)
    cable = {}
    line(cable, ox + 15, 2, ox + 15, 8, STEEL[4])
    rect(cable, ox + 14, 9, ox + 16, 10, (222, 182, 40))
    cable[(ox + 15, 11)] = STEEL[3]
    s.add(cable, outline=False)
    pipe = {}
    rect(pipe, ox + 32, 18, ox + 36, 18, STEEL[2])
    s.add(pipe)
    barrels = {}
    for bx in (ox + 37, ox + 42):
        rect(barrels, bx, 13, bx + 3, 19, lambda x, y: (222, 182, 40) if y in (14, 18) else STEEL[2] if x == bx
             else STEEL[1])
    sprite(barrels, ox + 47, 16, ["kyykkyk", "kkkkkkk", "KkkkkkK", ".......", ], {
        'k': STEEL[1], 'y': (222, 182, 40), 'K': STEEL[2]})
    s.add(barrels)
    oil = {}
    rect(oil, ox + 46, 19, ox + 58, 19, lambda x, y: (60, 46, 78) if x % 5 == 1 else (18, 14, 18))
    rect(oil, ox + 50, 18, ox + 54, 18, (18, 14, 18))
    s.add(oil, outline=False)


def pylon(layer, c):
    lattice(layer, ((c - 1, 2), (c - 5, 19)), ((c + 1, 2), (c + 5, 19)), [2, 4, 9, 14, 19], STEEL[3], STEEL[2])
    line(layer, c - 2, 2, c - 6, 19, BLACK)
    line(layer, c + 2, 2, c + 6, 19, BLACK)
    layer[(c, 0)] = layer[(c, 1)] = STEEL[3]
    line(layer, c - 8, 4, c + 8, 4, STEEL[3])
    line(layer, c - 7, 9, c + 7, 9, STEEL[3])
    line(layer, c - 8, 4, c - 2, 6, STEEL[2])
    line(layer, c + 8, 4, c + 2, 6, STEEL[2])
    for x, y in ((c - 8, 5), (c + 8, 5), (c - 7, 10), (c + 7, 10)):
        layer[(x, y)] = (150, 186, 196)
        layer[(x, y + 1)] = (110, 140, 150)


def power_grid(s, x0):
    rng = s.rng
    a, b = x0 + 30, x0 + 66
    box = {}
    rect(box, x0 + 2, 12, x0 + 13, 19, lambda x, y: STEEL[3] if y == 12 else STEEL[2] if x % 2 else STEEL[3])
    for x in (x0 + 4, x0 + 8, x0 + 12):
        box[(x, 11)] = box[(x, 10)] = (150, 186, 196)
    sprite(box, x0 + 5, 14, [".y.", "yky", "yyy"], {'y': (236, 196, 40), 'k': BLACK})
    s.add(box)
    towers = {}
    pylon(towers, a)
    pylon(towers, b)
    s.add(towers, outline=False)
    wires = {}
    for dy, dx in ((6, 8), (11, 7)):                          # wires between the arm ends facing each other
        sag(wires, a + dx, dy, b - dx, dy, 3, STEEL[0])
    sag(wires, x0 + 8, 10, a - 7, 11, 2, STEEL[0])
    sag(wires, a - 8, 6, x0 + 12, 10, 1, STEEL[0])
    sag(wires, b + 7, 11, x0 + 99, 13, 2, STEEL[0])
    line(wires, b + 8, 6, b + 10, 14, STEEL[0])             # a snapped line, sparking
    s.add(wires, outline=False)
    sparks = {}
    for p in ((b + 11, 15), (b + 9, 16), (b + 12, 13), (b + 10, 17), (b + 11, 17)):
        sparks[p] = (255, 250, 170) if rng.random() < 0.5 else (140, 220, 255)
    s.add(sparks, outline=False)


def mods():
    s = Strip(200, seed=33)
    derrick(s, 2)
    power_grid(s, 100)
    s.save('mods')


# ---------------------------------------------------------------- options: parasites and biomass

def options():
    s = Strip(98, seed=44)
    rng = s.rng
    mass = {}
    heights = []
    h = 2.0
    for x in range(0, 70):
        h = max(1.0, min(5.0, h + rng.uniform(-0.9, 0.9)))
        heights.append(round(h if x < 62 else h * (70 - x) / 8))
    for x, height in enumerate(heights):
        for d in range(height):
            y = 19 - d
            mass[(x, y)] = FLESH[4] if d == height - 1 else FLESH[3] if d == height - 2 else FLESH[2]
    for _ in range(14):                                       # veins and pus
        x = rng.randrange(2, 66)
        for d in range(rng.randint(2, 4)):
            if (x, 19 - d) in mass:
                mass[(x, 19 - d)] = VEIN
            x += rng.choice((-1, 0, 1))
    for _ in range(5):
        x = rng.randrange(2, 66)
        mass[(x, 19 - rng.randint(0, 1))] = PUS
    s.add(mass)
    stalk = {}                                                # a stalk with an eye, watching the player
    for y in range(4, 19):
        x = 9 + round(math.sin((19 - y) * 0.35) * 2)
        stalk[(x, y)] = FLESH[3]
        stalk[(x + 1, y)] = FLESH[1] if y % 4 else VEIN
    sprite(stalk, 7, 0, [
        ".WWWW.",
        "WWWrrW",
        "WWWrkW",
        ".WWWW.",
    ], {'W': (226, 214, 196), 'r': (190, 30, 30), 'k': BLACK})
    s.add(stalk)
    pods = {}
    for cx, cy, r in ((22, 15, 2.6), (28, 14, 3.2), (34, 16, 2.2)):
        for x in range(int(cx - r) - 1, int(cx + r) + 2):
            for y in range(int(cy - r) - 1, int(cy + r) + 2):
                d = math.hypot(x - cx, (y - cy) * 1.1)
                if d <= r:
                    pods[(x, y)] = FLESH[5] if (x - cx) + (y - cy) < -r * 0.6 else FLESH[4] if d < r * 0.6 else FLESH[3]
    pods[(28, 13)] = PUS
    pods[(27, 14)] = PUS
    s.add(pods)
    tendrils = {}
    for bx, height, phase in ((42, 9, 0.0), (50, 12, 1.4), (58, 7, 2.6)):
        for d in range(height):
            x = bx + round(math.sin(d * 0.6 + phase) * 1.4)
            tendrils[(x, 18 - d)] = FLESH[3] if d % 3 else FLESH[2]
        tendrils[(x + 1, 18 - height)] = FLESH[4]
    s.add(tendrils)
    buglin = {}
    sprite(buglin, 61, 12, [
        "m.....",
        "mHHbb.",
        "EHbbbb",
        ".l.l.l",
    ], {'m': (220, 200, 170), 'H': FLESH[3], 'b': FLESH[2], 'E': (250, 220, 60), 'l': FLESH[0]})
    s.add(buglin)
    beast = {}                                                # a Rupter-like parasite, claws towards the menu
    sprite(beast, 72, 2, [
        ".......bb.b......",
        "......bFFFFb.....",
        ".....FFFFFFFF....",
        "...EFFFFFFFFFFb..",
        ".TMMFFFfFFFFFFF..",
        ".TTMFFFFfFFFFFFb.",
        "..CCFFFFFfFFFFF..",
        ".CC...FFFFFfFFF..",
        "CC.....FFFFFFFF..",
        "C......fFF.fFFF..",
        "C......fF...FFF..",
        ".......fF...fFF..",
        "......fF....fF...",
        "......cc....cc...",
        "....................",
        "",
        "",
    ], {'b': (226, 214, 196), 'F': FLESH[3], 'f': FLESH[1], 'E': (250, 220, 60), 'M': BLACK, 'T': (226, 214, 196),
        'C': (226, 214, 196), 'c': (226, 214, 196)})
    for (x, y) in list(beast):
        if beast[(x, y)] == FLESH[3] and (x, y - 1) not in beast:
            beast[(x, y)] = FLESH[4]
    s.add(beast)
    spores = {}
    for _ in range(12):
        spores[(rng.randrange(14, 70), rng.randrange(1, 11))] = (220, 140, 160, 170)
    s.add(spores, outline=False)
    s.save('options')


# ---------------------------------------------------------------- quit: two bombs that did not go off

def bomb(s, cx, cy, angle, shape, reach):
    """Rasterises shape(u, v) (u along the axis towards the nose, v across) rotated by angle (degrees, 0 = right,
    90 = straight down) around (cx, cy)."""
    a = math.radians(angle)
    ca, sa = math.cos(a), math.sin(a)
    layer = {}
    for x in range(int(cx - reach), int(cx + reach) + 1):
        for y in range(int(cy - reach), int(cy + reach) + 1):
            dx, dy = x - cx, y - cy
            u, v = dx * ca + dy * sa, -dx * sa + dy * ca
            c = shape(u, v)
            if c:
                layer[(x, y)] = c
    s.add(layer)


def fat_man(u, v):
    body = (u / 9.0) ** 2 + (v / 7.0) ** 2
    if body <= 1:
        if abs(u + 1) < 0.5:
            return BLACK                                      # the seam between the two halves
        if v < -3.5:
            return (250, 226, 104)
        if v > 3.5:
            return (176, 142, 26)
        return (226, 192, 44)
    if -16 <= u <= -8 and abs(v) <= 6.5:                      # box tail
        if abs(v) <= 0.6 or u <= -15:
            return STEEL[1]
        return STEEL[3] if v < -3.5 else STEEL[2]
    return None


def little_boy(u, v):
    r = 3.6
    if -10 <= u <= 7 and abs(v) <= r or u > 7 and (u - 7) ** 2 + v ** 2 <= r * r:
        if abs(u - 3) < 0.5 or abs(u + 5) < 0.5:
            return STEEL[0]
        if -1.5 <= u <= 0.5:
            return (176, 40, 34)
        return (136, 150, 162) if v < -1.6 else (60, 70, 82) if v > 1.6 else (88, 102, 116)
    if -16 <= u <= -10 and abs(v) <= 5.2:
        if abs(v) <= 0.6 or u <= -15:
            return STEEL[0]
        return STEEL[2] if v < -2 else STEEL[1]
    return None


def quit_strip():
    s = Strip(98, STRIP_H + EMBED, seed=55)
    rng = s.rng
    bomb(s, 30, 16, 62, fat_man, 18)
    bomb(s, 68, 15, 112, little_boy, 18)
    debris = {}                                               # chunks knocked off the button, dust
    for ix in (35, 64):
        for _ in range(7):
            x, y = ix + rng.randint(-8, 8), STRIP_H - 1 - rng.choice((0, 0, 0, 1, 2, 4, 6))
            if not any(q in s.px for q in ((x, y),) + n4((x, y))):
                debris[(x, y)] = rng.choice(((110, 110, 110), (150, 150, 150), (70, 70, 70)))
    s.add(debris)
    dust = {}
    for ix in (35, 64):
        for _ in range(16):
            dust[(ix + rng.randint(-11, 11), STRIP_H - 1 - abs(round(rng.gauss(0, 2.5))))] = SMOKE[rng.randrange(3)]
    for p in list(dust):
        if p in s.px:
            del dust[p]
    s.add(dust, outline=False)
    s.save('quit')


if __name__ == '__main__':
    singleplayer()
    multiplayer()
    mods()
    options()
    quit_strip()
