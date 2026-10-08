"""Better Questing Unofficial 4.x JSON helpers for the Decay Chain questbook (see build_quests.py)."""

# ---------- item / task helpers (used by the chapter modules) ----------

def item(item_id, count=1, meta=0, nbt=None, ore=''):
    stack = {'id:8': item_id, 'Count:3': count, 'Damage:2': meta, 'OreDict:8': ore}
    if nbt:
        stack['tag:10'] = nbt
    return stack


def ore(name, count=1, icon='minecraft:stone'):
    """Any item in an ore dictionary entry (icon item is what the quest book shows)."""
    return item(icon, count, 32767, ore=name)


def _indexed(entries):
    return {f'{i}:10': e for i, e in enumerate(entries)}


def retrieve(*stacks, any_of=False, optional=False):
    """Have the items in your inventory (nothing is consumed). Optional tasks are shown as guidance steps but do not
    block the quest."""
    return {'taskID:8': 'bq_standard:retrieval', 'requiredItems:9': _indexed(stacks), 'consume:1': 0,
            'autoConsume:1': 0, 'groupDetect:1': 0, 'ignoreNBT:1': 1, 'partialMatch:1': 1,
            'entryLogic:8': 'OR' if any_of else 'AND', 'optional:1': 1 if optional else 0}


def checkbox():
    """Read and tick: for hint-only quests."""
    return {'taskID:8': 'bq_standard:checkbox'}


# ---------- assembly ----------

def _merge_any_of(tasks):
    """task_logic='OR' over single-item retrieval tasks -> one retrieval task with 'any of' entries.
    BQ shows separate tasks as separate checklists, which reads as 'collect all of these'."""
    if len(tasks) > 1 and all(t['taskID:8'] == 'bq_standard:retrieval' and len(t['requiredItems:9']) == 1 for t in tasks):
        return [retrieve(*(t['requiredItems:9']['0:10'] for t in tasks), any_of=True)]
    return tasks


def build_quest(q):
    props = {
        'name:8': q['name'], 'desc:8': q['desc'], 'icon:10': q['icon'],
        'isMain:1': 1 if q.get('main') else 0, 'isSilent:1': 0, 'autoClaim:1': 1,
        'globalShare:1': 0, 'lockedProgress:1': 0, 'simultaneous:1': 0, 'repeatTime:3': -1,
        'repeat_relative:1': 1, 'questLogic:8': 'AND', 'taskLogic:8': q.get('task_logic', 'AND'),
        'visibility:8': q.get('visibility', 'NORMAL'),
        'snd_complete:8': 'minecraft:entity.player.levelup', 'snd_update:8': 'minecraft:entity.player.levelup',
    }
    task_list = q['tasks']
    if q.get('task_logic') == 'OR':
        task_list = _merge_any_of(task_list)
    tasks = {}
    for i, t in enumerate(task_list):
        tasks[f'{i}:10'] = dict(t, **{'index:3': i})
    return {'questID:3': q['id'], 'preRequisites:11': q.get('requires', []),
            'properties:10': {'betterquesting:10': props}, 'tasks:9': tasks, 'rewards:9': {}}


def build(chapters, pack_version):
    quests, lines = {}, {}
    seen = set()
    for order, ch in enumerate(chapters):
        entries = {}
        for i, q in enumerate(ch['quests']):
            assert q['id'] not in seen, f"duplicate quest id {q['id']}"
            seen.add(q['id'])
            quests[f'{len(quests)}:10'] = build_quest(q)
            x, y = q['pos']
            size = 32 if q.get('main') else 24
            entries[f'{i}:10'] = {'id:3': q['id'], 'x:3': x * 40 - size // 2, 'y:3': y * 40 - size // 2,
                                  'sizeX:3': size, 'sizeY:3': size}
        lines[f'{order}:10'] = {
            'lineID:3': ch['id'], 'order:3': order,
            'properties:10': {'betterquesting:10': {
                'name:8': ch['name'], 'desc:8': ch['desc'], 'icon:10': ch['icon'],
                'visibility:8': 'NORMAL', 'bg_image:8': '', 'bg_size:3': 256}},
            'quests:9': entries}
    for ch in chapters:
        for q in ch['quests']:
            for r in q.get('requires', []):
                assert r in seen, f"quest {q['id']} requires unknown quest {r}"
    return {
        'format:8': '2.1.0',
        'build:8': '4.3.2',
        'questSettings:10': {'betterquesting:10': {
            'pack_name:8': 'Decay Chain', 'pack_version:3': pack_version, 'editMode:1': 0, 'hardcore:1': 0,
            'lockTray:1': 0, 'livesDef:3': 3, 'livesMax:3': 10, 'party_enable:1': 1,
            'home_image:8': 'betterquesting:textures/gui/default_title.png',
            'home_anchor_x:5': 0.5, 'home_anchor_y:5': 0.0, 'home_offset_x:3': -128, 'home_offset_y:3': 0}},
        'questDatabase:9': quests,
        'questLines:9': lines,
    }


