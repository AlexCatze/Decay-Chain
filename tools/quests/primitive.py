"""Age I - Primitive: from empty pockets to iron (Pyrotech), plus the survival basics.

Two chapters:
  10 - Path to Iron   (main progression, quest ids 0-29)
  11 - Staying Alive  (water / food / shelter / health / hints, quest ids 100-149)
Positions are grid cells (40 px).
"""
from bq import item, ore, retrieve, checkbox

PYRO_BOOK = item('patchouli:guide_book', nbt={'patchouli:book:8': 'pyrotech:book'})

def pyro(name, meta=0, count=1):
    return item('pyrotech:' + name, count, meta)

def mat(meta, count=1):
    """pyrotech:material sub-items: 2 straw, 5 refractory brick, 10 flint shard, 11 bone shard,
    12 plant fibers, 13 dried plant fibers, 14 twine, 17 clay lump"""
    return item('pyrotech:material', count, meta)


PATH = [
    dict(id=0, pos=(0, 2), main=True, name='Empty Pockets', icon=PYRO_BOOK, tasks=[checkbox()],
         desc="The world ended a long time ago. You have nothing - no tools, no food, no clean water.\n\n"
              "These quests are §eonly a guide§r: there are no rewards, and nothing here is required. Follow them "
              "to learn how this pack works, or ignore them and explore.\n\n"
              "§eTips§r\n- Look up any item with §eR§r (recipe) / §eU§r (uses) in the item list.\n"
              "- You start with Pyrotech's guide book, §ePyrotechnic Esoterica§r. Its pages explain every primitive "
              "machine in detail - read it alongside these quests.\n"
              "- The §eStaying Alive§r chapter runs in parallel: water, food, shelter and dangers."),
    dict(id=1, pos=(1, 2), requires=[0], name='Rocks', icon=pyro('rock'),
         tasks=[retrieve(item('pyrotech:rock', 8, 32767))],
         desc="Small rocks lie on the ground almost everywhere - pick them up.\n\n"
              "Rocks are your first building material and your first weapon: you can §ethrow§r them at things that "
              "come too close. Mining stone with weak tools also gives rocks instead of cobblestone."),
    dict(id=2, pos=(2, 2), requires=[1], name='Sticks and Fibers', icon=mat(12),
         tasks=[retrieve(item('minecraft:stick', 6), mat(12, 6))],
         desc="§eSticks§r drop from leaves (any tree, including the Biomes O' Plenty ones) - break them by hand.\n"
              "§ePlant Fibers§r come from breaking tall grass.\n\n"
              "You cannot punch trees in this world: logs only come off with an axe. Make one first."),
    dict(id=3, pos=(3, 2), requires=[2], name='Crude Tools', icon=pyro('crude_pickaxe'),
         tasks=[retrieve(pyro('crude_axe'), pyro('crude_pickaxe'))],
         desc="Rocks + sticks make crude tools. They are weak and break fast, but a §ecrude axe§r is the only way "
              "to get your first logs, and a §ecrude pickaxe§r starts you on stone.\n\n"
              "A crude shovel is worth it too: digging gravel with a shovel finds §eflint shards§r."),
    dict(id=4, pos=(4, 2), requires=[3], name='Timber!', icon=item('minecraft:log'),
         tasks=[retrieve(ore('logWood', 8, 'minecraft:log'))],
         desc="Chop a tree with your axe. §cTrees fall when you cut them§r - logs come crashing down, so step "
              "aside or you may get hit.\n\nLogs fuel everything in the primitive age: campfires, kilns, charcoal."),
    dict(id=5, pos=(5, 2), requires=[4], name='Crude Hammer', icon=pyro('crude_hammer'),
         tasks=[retrieve(pyro('crude_hammer'))],
         desc="Hammers are how you craft on a Worktable and an Anvil: place the ingredients on top, then hit them."),
    dict(id=6, pos=(6, 2), requires=[5], name='The Worktable', icon=pyro('worktable'),
         tasks=[retrieve(pyro('worktable'))],
         desc="There is no vanilla crafting table at the start. The §eWorktable§r replaces it: place the recipe "
              "ingredients on top and strike it with your hammer.\n\nA proper crafting table comes much later."),
    dict(id=7, pos=(7, 2), requires=[6], name='Chopping Block', icon=pyro('chopping_block'),
         tasks=[retrieve(pyro('chopping_block'))],
         desc="Put a log on the Chopping Block and hit it with an axe to split it into planks and firewood.\n\n"
              "Chopping is hard work: §eif nothing happens, you are too hungry§r."),
    dict(id=8, pos=(8, 2), requires=[3], task_logic='OR', name='Shards', icon=mat(10),
         tasks=[retrieve(mat(10)), retrieve(mat(11))],
         desc="Sharper tools need §eflint shards§r or §ebone shards§r (either one works).\n\n"
              "- Flint shards: dig gravel with a shovel.\n- Bone shards: break bones on an anvil, or find them in fossils.\n"
              "Skeletons drop bones - but at night you have bigger problems than skeletons."),
    dict(id=9, pos=(9, 2), requires=[2], name='Twine', icon=mat(14),
         tasks=[retrieve(mat(14, 4))],
         desc="Twist plant fibers into §etwine§r. Twine ties tools together, builds drying racks and makes "
              "fire-starting kits."),
    dict(id=10, pos=(10, 2), requires=[4, 8, 9], main=True, name='Fire', icon=pyro('campfire'),
         tasks=[retrieve(pyro('flint_and_tinder'), pyro('campfire'))],
         desc="Craft §eTinder§r, place it, add logs and light it with a §eFlint and Tinder§r.\n\n"
              "The campfire cooks food (roasted food is far better than raw), §eboils dirty water§r into drinkable "
              "water, keeps you warm at night and speeds up nearby drying racks."),
    dict(id=11, pos=(10, 3), requires=[8, 9], task_logic='OR', name='Bone and Flint Tools', icon=pyro('flint_pickaxe'),
         tasks=[retrieve(pyro('flint_pickaxe')), retrieve(pyro('bone_pickaxe'))],
         desc="Flint or bone tools last much longer than crude ones and can mine iron ore. Make a pickaxe, an axe "
              "and a shovel.\n\n§7Note: there are no hoes until you have steel - farming is not a primitive-age option."),
    dict(id=12, pos=(9, 3), requires=[9], name='Drying Rack', icon=pyro('drying_rack', 1),
         tasks=[retrieve(item('pyrotech:drying_rack', 1, 32767))],
         desc="A crude drying rack (sticks tied with fibers) dries plant fibers, wheat and food. It works faster "
              "under open sky, in warm dry biomes and next to a campfire - and slower in rain."),
    dict(id=13, pos=(8, 3), requires=[12], name='Straw', icon=pyro('thatch'),
         tasks=[retrieve(mat(2, 4), pyro('thatch'))],
         desc="Dried plant fibers tie together into §eStraw§r; straw packs into a §eStraw Bale§r (thatch).\n\n"
              "Straw is the fuel bed of a pit kiln, and a straw bed is the cheapest place to sleep."),
    dict(id=14, pos=(7, 3), requires=[3], name='Clay', icon=mat(17),
         tasks=[retrieve(mat(17, 8))],
         desc="Dig clay in rivers, swamps and lakes. Lumps of clay become bricks, buckets and - most importantly - "
              "§erefractory bricks§r for the bloomery."),
    dict(id=15, pos=(6, 3), requires=[13, 14, 10], main=True, name='Pit Kiln', icon=pyro('kiln_pit'),
         tasks=[retrieve(pyro('kiln_pit'))],
         desc="Your first furnace. Place the kiln, put the item inside, cover it with straw and a straw bale, stack "
              "three logs and light it. Wait - and hope nothing cracks.\n\n"
              "The pit kiln fires clay into bricks and can even smelt §ecopper ore§r into ingots."),
    dict(id=16, pos=(5, 3), requires=[11], name='Granite Anvil', icon=pyro('anvil_granite'),
         tasks=[retrieve(pyro('anvil_granite'))],
         desc="A stone anvil: place things on it and hit them with a pickaxe or hammer to split rocks, shard bones "
              "and flint, and later to hammer iron blooms into metal."),
    dict(id=17, pos=(5, 4), requires=[16], name='Stone Tools', icon=item('minecraft:stone_pickaxe'),
         tasks=[retrieve(item('minecraft:stone_pickaxe'))],
         desc="§7Optional.§r Stone tools are a solid upgrade over flint and bone, and the stone hammer works the "
              "anvil faster."),
    dict(id=18, pos=(6, 4), requires=[15], task_logic='OR', name='Stone Machines', icon=pyro('stone_kiln'),
         tasks=[retrieve(pyro('stone_kiln')), retrieve(pyro('stone_oven'))],
         desc="§7Optional.§r The Stone Kiln does the pit kiln's job faster and with fewer failures; the Stone Oven "
              "cooks and dries without burning your food. Both run on fuel."),
    dict(id=19, pos=(4, 3), requires=[15], name='Charcoal', icon=item('minecraft:coal', 1, 1),
         tasks=[retrieve(item('minecraft:coal', 8, 1))],
         desc="Stack §elog piles§r, seal them under dirt or stone and light them - a §ePit Burn§r turns wood into "
              "charcoal (and tar).\n\nCharcoal is the fuel that gets a bloomery hot enough for iron. It also makes "
              "water filters."),
    dict(id=20, pos=(3, 3), requires=[15], name='Refractory Bricks', icon=mat(5),
         tasks=[retrieve(mat(5, 16))],
         desc="Mix clay into §erefractory clay§r, shape unfired refractory bricks and fire them in a kiln.\n\n"
              "Refractory bricks survive the heat of iron smelting - the bloomery is built from them."),
    dict(id=21, pos=(2, 3), requires=[11], name='Iron Ore', icon=item('minecraft:iron_ore'),
         tasks=[retrieve(ore('oreIron', 8, 'minecraft:iron_ore'))],
         desc="Iron ore needs at least a flint, bone or stone pickaxe.\n\n"
              "§eScavenging tip:§r iron found in ruins is mostly ore too - nobody left neat ingots lying around."),
    dict(id=22, pos=(2, 4), requires=[11], task_logic='OR', name='Tongs', icon=pyro('tongs_flint'),
         tasks=[retrieve(pyro('tongs_flint')), retrieve(pyro('tongs_bone'))],
         desc="A fresh bloom is glowing-hot iron. §cHolding it with bare hands burns you.§r Carry tongs."),
    dict(id=23, pos=(3, 4), requires=[19, 20, 21, 22], main=True, name='The Bloomery', icon=pyro('bloomery'),
         tasks=[retrieve(pyro('bloomery'))],
         desc="Load iron ore and fuel into the top of the bloomery and light it. The more (and better) fuel, the "
              "faster it works; a bellows helps.\n\nThe result is a §eBloom§r: a lump of iron and slag."),
    dict(id=24, pos=(4, 4), requires=[23, 16], main=True, name='Iron!', icon=item('minecraft:iron_ingot'),
         tasks=[retrieve(ore('ingotIron', 4, 'minecraft:iron_ingot'))],
         desc="Put the bloom on an anvil and hammer it: iron comes out, slag stays behind (slag with ore left in "
              "it can go back into the bloomery).\n\nYou made metal from nothing. The primitive age is almost over."),
    dict(id=25, pos=(4, 5), requires=[24], main=True, name='The First Anvil', icon=item('hbm:anvil_iron'),
         tasks=[retrieve(item('hbm:anvil_iron'))],
         desc="An §eIron Anvil§r from HBM's Nuclear Tech - the first real machine of the old world. With it you can "
              "build the next generation of tools and begin to salvage the ruins for steel.\n\n"
              "§eThis is the end of the Primitive age.§r Next: the Scavenger age."),
]

ALIVE = [
    # --- Water (column 0) ---
    dict(id=100, pos=(0, 0), requires=[0], name='Thirst', icon=item('simpledifficulty:purified_water_bottle'), tasks=[checkbox()],
         desc="You get thirsty as you move, work and fight. §eYou cannot drink straight from rivers or lakes§r - "
              "water has to go into a bottle or a canteen first.\n\n"
              "- Water from normal biomes is §edirty§r: drinkable, but it can give you parasites. Boil or filter it.\n"
              "- Water in §cwastelands, deserts and nuke craters is toxic§r. You cannot even fill a canteen there.\n"
              "- Standing in the rain and looking up lets you drink rain water."),
    dict(id=101, pos=(0, 1), requires=[100], task_logic='OR', name='Something to Drink From',
         icon=item('simpledifficulty:canteen'),
         tasks=[retrieve(item('simpledifficulty:canteen')), retrieve(item('minecraft:glass_bottle'))],
         desc="A §eCanteen§r (leather) holds several drinks. Glass bottles work too - you will find empty and "
              "full ones in ruined buildings long before you can make glass."),
    dict(id=102, pos=(0, 2), requires=[101, 10], name='Boil It', icon=item('simpledifficulty:purified_water_bottle'),
         tasks=[retrieve(item('simpledifficulty:purified_water_bottle'))],
         desc="Put a bottle of dirty water on a §ecampfire§r (or in a stone oven) and it comes back purified and safe."),
    dict(id=103, pos=(0, 3), requires=[101, 19], name='Charcoal Filter', icon=item('simpledifficulty:charcoal_filter'),
         tasks=[retrieve(item('simpledifficulty:charcoal_filter'))],
         desc="Canteens cannot go on a fire. Craft a filled canteen with a §echarcoal filter§r to purify it."),
    dict(id=104, pos=(0, 4), requires=[100], name='Never Drink the Wasteland', icon=item('decaychain:toxic_water_bottle'),
         tasks=[checkbox()],
         desc="A bottle filled in the wasteland comes out as §cToxic Water§r: poison, nausea and radiation.\n\n"
              "No campfire or filter can clean it - that needs a chemical plant, far in the future. "
              "§eWhen you travel into the wastes, carry your water with you.§r"),
    # --- Food (column 2) ---
    dict(id=110, pos=(2, 0), requires=[0], task_logic='OR', name='Forage', icon=pyro('strange_tuber'),
         tasks=[retrieve(pyro('strange_tuber')), retrieve(pyro('freckleberries'))],
         desc="Dig grassy dirt by hand for §eStrange Tubers§r, and pick berries from bushes in plains and forests.\n\n"
              "Food is less filling here than you are used to, and you only heal when you are well fed. Keep an "
              "eye on the saturation shown on your hunger bar."),
    dict(id=111, pos=(2, 1), requires=[3], name='Hunting Spear', icon=pyro('crude_spear'),
         tasks=[retrieve(pyro('crude_spear'))],
         desc="A crude spear can be thrown - it sticks in the target, so go and pick it up. Animals are the best "
              "early source of meat, leather (canteens, backpacks) and bones."),
    dict(id=112, pos=(2, 2), requires=[10], task_logic='OR', name='Cooked Meal', icon=item('minecraft:cooked_porkchop'),
         tasks=[retrieve(item('minecraft:cooked_porkchop')), retrieve(item('minecraft:cooked_beef')),
                retrieve(item('minecraft:cooked_mutton')), retrieve(item('minecraft:cooked_chicken')),
                retrieve(item('minecraft:cooked_rabbit')), retrieve(item('minecraft:baked_potato'))],
         desc="Cook food on the campfire. Roasted food restores much more than raw food - and raw meat can make you sick."),
    dict(id=113, pos=(2, 3), requires=[110], name='Canned Goods', icon=item('hbm:canned_conserve'),
         tasks=[retrieve(item('hbm:canned_conserve', 1, 32767))],
         desc="Ruined cities still hide pre-war food: canned beef, tuna, mystery meat... Cans keep forever. "
              "City chests, rail tunnels and old dungeons are your pantry."),
    dict(id=114, pos=(2, 4), requires=[110], task_logic='OR', name='Wild Gardens', icon=item('harvestcraft:windygarden'),
         tasks=[retrieve(item('harvestcraft:' + g)) for g in
                ('aridgarden', 'frostgarden', 'shadedgarden', 'soggygarden', 'tropicalgarden', 'windygarden')],
         desc="Wild gardens grow in many biomes. Break them for vegetables, fruits and seeds.\n\n"
              "§7Real farming needs a steel hoe - that comes in the Scavenger age. Until then, gather and scavenge.§r"),
    # --- Shelter (column 4) ---
    dict(id=120, pos=(4, 0), requires=[2], name='Light in the Dark', icon=pyro('torch_fiber'),
         tasks=[retrieve(pyro('torch_fiber', count=4))],
         desc="Nights are §cpitch black§r here - no moonlight to see by. Fiber torches burn out, so make plenty.\n\n"
              "Darkness is also where things spawn. Light up your camp."),
    dict(id=121, pos=(4, 1), requires=[1], name='Four Walls', icon=item('minecraft:cobblestone'),
         tasks=[retrieve(ore('cobblestone', 32, 'minecraft:cobblestone'))],
         desc="Rocks craft into cobblestone. Build something you can close behind you before your first night.\n\n"
              "Mud and straw make §eCob§r - another cheap early building block."),
    dict(id=122, pos=(4, 2), requires=[121], task_logic='OR', name='A Place to Sleep', icon=item('comforts:sleeping_bag'),
         tasks=[retrieve(pyro('straw_bed')), retrieve(item('comforts:sleeping_bag', 1, 32767))],
         desc="A §estraw bed§r is cheap; a §esleeping bag§r (wool) can be carried. Sleeping skips the night - "
              "except when the horde is coming."),
    dict(id=123, pos=(4, 3), requires=[7], task_logic='OR', name='Storage', icon=pyro('crate'),
         tasks=[retrieve(pyro('crate')), retrieve(pyro('stash')), retrieve(item('storagedrawers:basicdrawers', 1, 32767))],
         desc="Crates, stashes and drawers keep your loot safe. A §eRock Bag§r collects rocks automatically."),
    dict(id=124, pos=(4, 4), requires=[121], name='The Horde', icon=item('minecraft:rotten_flesh'), tasks=[checkbox()],
         desc="§cEvery tenth night a horde comes for you§r - and it grows each time. You cannot sleep through it.\n\n"
              "Zombies carrying pickaxes §edig through walls§r. HBM concrete stops them; until you have it, build "
              "thick walls, light everything and have an escape route.\n\n"
              "Bitten survivors can catch the zombie infection. Watch your status effects."),
    # --- Health (column 6) ---
    dict(id=130, pos=(6, 0), requires=[0], task_logic='OR', name='First Aid', icon=item('firstaid:plaster'),
         tasks=[retrieve(item('firstaid:plaster')), retrieve(item('firstaid:bandage'))],
         desc="Damage hits §especific body parts§r: a hurt leg slows you down, a damaged head blurs your sight. "
              "Plasters and bandages heal individual parts - look for them in ruins.\n\n"
              "Health only regenerates while you are well fed."),
    dict(id=131, pos=(6, 1), requires=[0], name='Hot and Cold', icon=item('simpledifficulty:wool_helmet'), tasks=[checkbox()],
         desc="Your body temperature matters. Deserts and wastelands are scorching by day and freezing at night; "
              "rain and water chill you.\n\nStay near a campfire at night, wear cloth armour in the cold, and keep "
              "an eye on the thermometer on your HUD."),
    dict(id=132, pos=(6, 2), requires=[0], name='Death Is Not the End', icon=item('tombstone:decorative_grave_simple'), tasks=[checkbox()],
         desc="When you die your belongings stay in a §egrave§r where you fell - you respawn with a key to it. "
              "Go back carefully: whatever killed you is probably still there.\n\n"
              "You lose most of your experience on death, and experience is what buys skill levels."),
    # --- Hints (column 8) ---
    dict(id=140, pos=(8, 0), requires=[0], name='Know Your Enemies', icon=item('minecraft:skull', 1, 2), tasks=[checkbox()],
         desc="- §eParasites§r start weak, but they evolve as they spread and assimilate other creatures. "
              "The longer they go unchecked, the worse they get.\n"
              "- §eMutated wildlife§r roams the wastes - giant worms, insects and worse.\n"
              "- Some monsters are §eelite§r: a coloured health bar and special powers. Avoid them early.\n"
              "- Zombies hear noise, see light and follow blood."),
    dict(id=141, pos=(8, 1), requires=[0], name='Ruins', icon=item('hbm:brick_concrete_cracked'), tasks=[checkbox()],
         desc="Cities stand in ruins - the worst in the wastelands. They are full of loot and full of danger.\n\n"
              "- Some buildings are §ehaunted§r: their chests stay locked until you clear the building.\n"
              "- Concrete and steel walls §ecannot be dug through§r without at least an iron pickaxe - use the doors.\n"
              "- §cSmashing glass cuts you§r: breaking windows makes you bleed. A §eGlass Cutter§r removes glass safely (and keeps it).\n"
              "- Watch for radiation, landmines and asbestos dust near old industrial sites.\n"
              "- Underground rail tunnels hold the best early caches."),
    dict(id=142, pos=(8, 2), requires=[0], name='Skills', icon=item('minecraft:experience_bottle'), tasks=[checkbox()],
         desc="Experience buys skill levels (open the skills tab in your inventory). Tools and machines are never "
              "locked by skills - but §eweapons and armour are§r: better guns, swords and armour need Attack, "
              "Defense or Agility levels."),
    dict(id=143, pos=(8, 3), requires=[3], task_logic='OR', name='Something to Fight With', icon=item('spartanweaponry:spear_wood'),
         tasks=[retrieve(item('spartanweaponry:spear_wood')), retrieve(item('spartanweaponry:spear_stone')),
                retrieve(item('spartanweaponry:dagger_stone')), retrieve(pyro('crude_spear')), retrieve(item('minecraft:bow'))],
         desc="Spears, daggers and clubs of wood and stone are your first real weapons; a bow keeps danger at a "
              "distance. Firearms come with steel."),
    dict(id=144, pos=(8, 4), requires=[111], name='Backpack', icon=item('wearablebackpacks:backpack'),
         tasks=[retrieve(item('wearablebackpacks:backpack'))],
         desc="A leather backpack worn on your back roughly doubles what you can carry home from a scavenging run."),
]

CHAPTERS = [
    dict(id=10, name='I. Primitive - Path to Iron', icon=pyro('flint_pickaxe'), quests=PATH,
         desc='From empty pockets to your first iron and the first HBM anvil.'),
    dict(id=11, name='I. Primitive - Staying Alive', icon=item('simpledifficulty:canteen'), quests=ALIVE,
         desc='Water, food, shelter, health and the dangers of this world.'),
]
