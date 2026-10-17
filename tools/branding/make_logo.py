"""Draws the Decay Chain title logo (main menu): Minecraft's own title lettering, about 40% rusted, overgrown by
the parasites and broken.

Usage:  python tools/branding/make_logo.py [path to the Minecraft 1.12.2 client jar]
Writes groovy/assets/decaychain/textures/gui/title_logo.png (shown by Custom Main Menu, config/CustomMainMenu).

The letters are built like the vanilla title (textures/gui/title/minecraft.png, read from the client jar): 27 x 32 px
faces with 12 px strokes and 6-7 px bars, textured with the vanilla letters' own stone pixels, light lower edges,
black walls in the gaps and a 10 px dark underside leaning towards the middle. One logo pixel is SCALE image pixels.
"""
import math
import os
import random
import sys
import zipfile
from io import BytesIO

from PIL import Image

OUT = os.path.join(os.path.dirname(__file__), '..', '..', 'groovy', 'assets', 'decaychain', 'textures', 'gui',
                   'title_logo.png')
CLIENT_JAR = os.path.expandvars(r'%APPDATA%\PrismLauncher\libraries\com\mojang\minecraft\1.12.2'
                                r'\minecraft-1.12.2-client.jar')
SCALE = 4
SEED = 1986
WORDS = ['DECAY', 'CHAIN']
FACE_H = 32
UNDERSIDE = 10
LETTER_GAP = 3
WORD_GAP = 12
DAMAGE = 0.40                   # share of the letter faces that is rusted, overgrown or broken off

# 5 x 5 glyph grid: column widths 6 6 4 6 6 and row heights 6 6 7 6 7 px, the vanilla proportions (a little wider gap)
COLS, ROWS = [6, 6, 4, 6, 6], [6, 6, 7, 6, 7]
GLYPHS = {
    'D': ["####.", "##.##", "##.##", "##.##", "####."],
    'E': ["#####", "##...", "#####", "##...", "#####"],
    'C': ["#####", "##...", "##...", "##...", "#####"],
    'A': ["#####", "##.##", "#####", "##.##", "##.##"],
    'Y': ["##.##", "##.##", "#####", ".###.", ".###."],
    'H': ["##.##", "##.##", "#####", "##.##", "##.##"],
    'I': ["##", "##", "##", "##", "##"],
}

# vanilla title palette
BLACK = (2, 1, 1)
UNDER = (30, 28, 31)
STONE = (168, 159, 156)
EDGE = (200, 195, 191)
CRACK = (131, 124, 124)
DARK_CRACK = (83, 80, 83)
# decay
RUST = [(84, 38, 18), (112, 52, 22), (140, 68, 28), (166, 86, 36), (190, 108, 48), (208, 132, 66)]
RUST_UNDER = [(58, 30, 18), (74, 38, 20)]
FLESH = [(58, 10, 16), (88, 18, 26), (118, 30, 36), (148, 46, 48), (176, 72, 66)]
VEIN = (36, 5, 9)
PUS = (210, 192, 112)
RUBBLE = [(44, 41, 42), (58, 55, 56)]


# ---------------------------------------------------------------- vanilla letters

def load_vanilla(jar):
    with zipfile.ZipFile(jar) as z:
        return Image.open(BytesIO(z.read('assets/minecraft/textures/gui/title/minecraft.png'))).convert('RGBA')


def vanilla_faces(im):
    """Stone face pixels of the nine vanilla letters, straightened (the title leans in perspective): list of
    {(x, y): rgb} with y 0..31."""
    w, h = im.size

    def is_face(x, y):
        p = im.getpixel((x, y))
        return p[3] > 128 and p[0] >= 75
    seeds = [(5, 20), (50, 10), (70, 10), (105, 3), (135, 3), (5, 50), (35, 48), (65, 48), (100, 48)]   # MINEC RAFT
    faces = []
    for seed in seeds:
        y0, y1 = (1, 32) if seed[1] < 40 else (46, 77)
        todo, seen = [seed], {seed}
        while todo:
            x, y = todo.pop()
            for q in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                if y0 <= q[1] <= y1 and 0 <= q[0] < w and q not in seen and is_face(*q):
                    seen.add(q)
                    todo.append(q)
        left = {}
        for x, y in seen:                                     # lean from the left edge below the top bar
            if y >= y0 + 7:
                left[y] = min(left.get(y, w), x)
        ys = sorted(left)
        my, mx = sum(ys) / len(ys), sum(left[y] for y in ys) / len(ys)
        slope = sum((y - my) * (left[y] - mx) for y in ys) / sum((y - my) ** 2 for y in ys)
        face = {}
        for x, y in seen:
            r, g, b, _ = im.getpixel((x, y))
            if r >= 190:
                r, g, b = STONE                               # highlights are redrawn on the new letter edges
            face[(x - round(slope * (y - y1)), y - y0)] = (r, g, b)
        x_min = min(x for x, _ in face)
        faces.append({(x - x_min, y): c for (x, y), c in face.items()})
    return faces


# ---------------------------------------------------------------- letter masks

def glyph_mask(ch):
    if ch == 'N':                                             # vanilla N: 31 px, the diagonal is not on the grid
        mask = set()
        for y in range(FACE_H):
            for x in range(31):
                centre = 8 + (x - 12) * 1.8
                if x < 12 or x >= 19 or abs(y - centre) <= 5:
                    mask.add((x, y))
        return mask, 31
    rows, mask = GLYPHS[ch], set()
    cols = COLS if len(rows[0]) == 5 else [6, 6]
    y0 = 0
    for gy, row in enumerate(rows):
        x0 = 0
        for gx, c in enumerate(row):
            if c == '#':
                for x in range(x0, x0 + cols[gx]):
                    for y in range(y0, y0 + ROWS[gy]):
                        mask.add((x, y))
            x0 += cols[gx]
        y0 += ROWS[gy]
    return mask, sum(cols)


# ---------------------------------------------------------------- helpers

def value_noise(rng, w, h, scale):
    cols, rows = int(w / scale) + 3, int(h / scale) + 3
    grid = [[rng.random() for _ in range(cols)] for _ in range(rows)]

    def at(x, y):
        gx, gy = max(0.0, x / scale), max(0.0, y / scale)
        x0, y0 = int(gx), int(gy)
        fx, fy = gx - x0, gy - y0
        fx, fy = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy)
        top = grid[y0][x0] * (1 - fx) + grid[y0][x0 + 1] * fx
        bottom = grid[y0 + 1][x0] * (1 - fx) + grid[y0 + 1][x0 + 1] * fx
        return top * (1 - fy) + bottom * fy
    return at


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def n4(p):
    x, y = p
    return (x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)


# ---------------------------------------------------------------- drawing

def main():
    rng = random.Random(SEED)
    sources = vanilla_faces(load_vanilla(sys.argv[1] if len(sys.argv) > 1 else CLIENT_JAR))
    pad_x, top = 8, 6
    # lay out the faces
    face, letter_of, letters = {}, {}, []
    x0 = pad_x
    for wi, word in enumerate(WORDS):
        for ch in word:
            mask, width = glyph_mask(ch)
            index = len(letters)
            order = sources[index % len(sources):] + sources[:index % len(sources)]
            for (x, y) in mask:
                color = next((s[(x, y)] for s in order if (x, y) in s), STONE)
                face[(x0 + x, top + y)] = color
                letter_of[(x0 + x, top + y)] = index
            letters.append((x0, width))
            x0 += width + LETTER_GAP
        x0 += WORD_GAP - LETTER_GAP
    W = x0 - WORD_GAP + pad_x
    H = top + FACE_H + UNDERSIDE + 22
    base = top + FACE_H - 1                                  # last face row
    centre = W / 2
    area = len(face)

    # ---- broken: one letter split with its piece slipped, bites out of edges and corners
    broken = set()
    lx, lw = letters[3]                                       # the A of DECAY cracks through and its right half slips
    cut = lambda x, y: x - lx > 13 + (y - top) * 0.25
    moved = {(x, y): c for (x, y), c in face.items() if letter_of[(x, y)] == 3 and cut(x, y)}
    for p in moved:
        del face[p]
    for (x, y), c in moved.items():
        face[(x + 2, y + 3)] = c
        letter_of[(x + 2, y + 3)] = 3
    bite_noise = value_noise(rng, W, H, 2.5)
    edge = [p for p in face if p[1] < base - 4 and any(q not in face for q in ((p[0], p[1] - 1),))]
    corners = [p for p in face if sum(q not in face for q in n4(p)) >= 2]
    for sx, sy in rng.sample(edge, 8) + rng.sample(corners, 6):
        r = rng.uniform(2.5, 5.5)
        for (x, y) in list(face):
            if (x - sx) ** 2 + ((y - sy) * 1.2) ** 2 <= r * r * (0.6 + bite_noise(x, y)):
                broken.add((x, y))
    for p in broken:
        face.pop(p, None)
    chips = set()
    for _ in range(4):                                        # chips in the face expose the dark core
        sx, sy = rng.choice([p for p in face if all(q in face for q in n4(p))])
        for (x, y) in list(face):
            if abs(x - sx) + abs(y - sy) <= rng.choice((1, 2)) and all(q in face for q in n4((x, y))):
                chips.add((x, y))

    img = {}
    # ---- walls and underside: every face pixel is extruded 13 px down and towards the middle
    for (x, y) in face:
        lean = (centre - x) / centre * 0.35
        for d in range(1, 14):
            q = (round(x + lean * d), y + d)
            if q in face or q in img:
                continue
            if q[1] > base:
                if q[1] - base <= UNDERSIDE:
                    img[q] = UNDER
            else:
                img[q] = BLACK
    for y in range(top, base + 1):                            # narrow gaps inside letters are black
        row = sorted(x for (x, yy) in face if yy == y)
        for a, b in zip(row, row[1:]):
            if 1 < b - a <= 6 and letter_of[(a, y)] == letter_of[(b, y)]:
                for x in range(a + 1, b):
                    img[(x, y)] = BLACK
    for p in list(img):                                       # specks in the underside, like the vanilla one
        if img[p] == UNDER and rng.random() < 0.03:
            img[p] = DARK_CRACK
    # ---- stone faces, light lower edges, cracks
    for p, c in face.items():
        img[p] = c
    for (x, y) in face:
        if (x, y + 1) not in face:
            ends = (x - 1, y + 1) in face or (x + 1, y + 1) in face or (x - 1, y) not in face or (x + 1, y) not in face
            img[(x, y)] = CRACK if ends else EDGE
    face_list = sorted(face)
    for _ in range(18):
        x, y = rng.choice(face_list)
        dx = rng.choice((-1, 1))
        for _ in range(rng.randint(4, 10)):
            if (x, y) in face:
                img[(x, y)] = DARK_CRACK if rng.random() < 0.7 else BLACK
            x += dx if rng.random() < 0.65 else 0
            y += 1 if rng.random() < 0.45 else 0
    for x, y in moved:                                        # the split line
        for q in ((x, y), (x + 2, y + 3)):
            if q in face and (q[0] - 1, q[1]) not in face:
                img[q] = DARK_CRACK
    for p in chips:
        img[p] = RUBBLE[(p[0] * 7 + p[1] * 3) % 2]

    # ---- rust: stains from the top, streaks running down
    rust_big, rust_fine = value_noise(rng, W, H, 8), value_noise(rng, W, H, 2)
    alive = [p for p in face if p not in chips]
    score = {p: rust_big(*p) * 0.8 + rust_fine(*p) * 0.2 - (p[1] - top) / FACE_H * 0.15 for p in alive}
    ranked = sorted(alive, key=score.get, reverse=True)
    rust = set(ranked[:int(area * DAMAGE * 0.42)])
    for p in rust:                                            # patches: dark in the middle, flaky light rims
        depth = score[p] - score[ranked[len(rust) - 1]]
        tone = 1 if depth > 0.16 else 2 if depth > 0.08 else 3 if depth > 0.03 else 4
        if rust_fine(*p) > 0.72:
            tone = min(5, tone + 1)
        elif rust_fine(*p) < 0.2:
            tone = max(0, tone - 1)
        img[p] = RUST[tone]
    for p in ranked[len(rust):len(rust) + int(area * 0.06)]:  # stained stone around the rust
        img[p] = mix(img[p], RUST[3], 0.45)
    for (x, y) in rust:
        if rng.random() < 0.05:
            for d in range(1, rng.randint(3, 9)):
                q = (x, y + d)
                if q in face:
                    img[q] = mix(img[q], RUST[2], 0.6)
                elif q in img and q[1] > base:
                    img[q] = RUST_UNDER[d % 2]
                else:
                    break
    # ---- parasite biomass: creeps up from below, veins, tendrils hanging off the letters
    flesh_n = value_noise(rng, W, H, 6)
    score = {p: flesh_n(*p) + (p[1] - top) / FACE_H * 0.55 for p in alive if p not in rust}
    ranked = sorted(score, key=score.get, reverse=True)
    flesh = set(ranked[:int(area * DAMAGE * 0.42)])
    flesh_fine = value_noise(rng, W, H, 1.5)
    for (x, y) in flesh:                                      # wet lumps: lit from above, dark below
        tone = 2 if flesh_fine(x, y) > 0.45 else 1
        if (x, y - 1) not in flesh:
            tone = 4
        elif (x, y - 2) not in flesh:
            tone = 3
        elif (x, y + 1) not in flesh:
            tone = 1
        img[(x, y)] = FLESH[tone]
    for p in flesh:
        if any(q in face and q not in flesh for q in n4(p)):
            img[p] = VEIN if rng.random() < 0.75 else FLESH[0]
    flesh_list = sorted(flesh)
    for _ in range(int(len(flesh) / 18)):                     # veins out into the stone
        x, y = rng.choice(flesh_list)
        for _ in range(rng.randint(3, 9)):
            x += rng.choice((-1, 0, 1))
            y += rng.choice((-1, -1, 0, 1))
            if (x, y) in face and (x, y) not in flesh:
                img[(x, y)] = VEIN if rng.random() < 0.6 else FLESH[1]
    for p in rng.sample(flesh_list, max(2, len(flesh) // 60)):
        img[p] = PUS
    hang = [p for p in flesh if (p[0], p[1] + 1) not in face]
    for x, y in rng.sample(hang, min(len(hang), 9)):          # tendrils
        length = rng.randint(4, UNDERSIDE + 9)
        for d in range(1, length + 1):
            x += 1 if rng.random() < 0.12 else -1 if rng.random() < 0.12 else 0
            img[(x, y + d)] = FLESH[2] if d % 3 else FLESH[1]
        img[(x, y + length + 1)] = FLESH[3]
        img[(x + 1, y + length + 1)] = FLESH[2]
        img[(x, y + length + 2)] = FLESH[1]
    tops = [p for p in flesh | rust if (p[0], p[1] - 1) not in face and p[1] > top]
    for x, y in rng.sample(tops, min(len(tops), 5)):          # biomass lumps on the upper edges
        for d in range(1, rng.randint(2, 3)):
            for dx in (-1, 0, 1):
                if (x + dx, y - d) not in face:
                    img[(x + dx, y - d)] = FLESH[2 if dx else 3]

    # ---- black outline
    for p in list(img):
        for q in n4(p):
            if q not in img:
                img[q] = BLACK

    damaged = len(rust) + len(flesh) + len(broken) + len(chips)
    print('damage %.0f%% (rust %d, biomass %d, broken %d, chips %d of %d face px)'
          % (100.0 * damaged / area, len(rust), len(flesh), len(broken), len(chips), area))
    out = Image.new('RGBA', (W + 4, H + 4), (0, 0, 0, 0))
    for (x, y), c in img.items():
        if 0 <= x < out.size[0] and 0 <= y < out.size[1]:
            out.putpixel((x, y), c + (255,))
    out = out.crop(out.getbbox())
    out = out.resize((out.size[0] * SCALE, out.size[1] * SCALE), Image.NEAREST)
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    out.save(OUT)
    print('wrote', os.path.normpath(OUT), out.size, '= %d x %d logo px' % (out.size[0] // SCALE,
                                                                       out.size[1] // SCALE))


if __name__ == '__main__':
    main()
