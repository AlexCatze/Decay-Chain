"""Age I - Primitive: from empty pockets to iron (Pyrotech), plus the survival basics.

Two chapters:
  10 - Path to Iron   (main progression)
  11 - Staying Alive  (water / food / shelter / health / hints)
Layout (xy = top-left pixel, size) is copied from the in-game quest editor - see diff_save.py.
Quests added in the in-game editor keep the ids BQ gave them, so existing progress carries over.
"""
from bq import item, ore, retrieve, checkbox

PYRO_BOOK = item('patchouli:guide_book', nbt={'patchouli:book:8': 'pyrotech:book'})

def pyro(name, meta=0, count=1):
    return item('pyrotech:' + name, count, meta)

def mat(meta, count=1):
    """pyrotech:material sub-items: 2 straw, 5 refractory brick, 10 flint shard, 11 bone shard,
    12 plant fibers, 13 dried plant fibers, 14 twine, 17 clay lump"""
    return item('pyrotech:material', count, meta)


# ids of quests created in the in-game editor
FIRE_STARTER = 830666229
DRIED_FIBERS = 1349163978
FIRST_NIGHT = 409861656
BOTTLE = 1226231121
CANTEEN = 1942058366
SOMETHING_TO_EAT = 1586696649
HUNTING = 1665027210


PATH = [
    dict(id=0, xy=(-12, -12), size=36, main=True, name='Empty Pockets', icon=PYRO_BOOK, tasks=[checkbox()],
         desc="The world ended a long time ago. You have nothing - no tools, no food, no clean water.\n\n"
              "These quests are §eonly a guide§r: there are no rewards, and nothing here is required. Follow them "
              "to learn how this pack works, or ignore them and explore.\n\n"
              "§eTips§r\n- Look up any item with §eR§r (recipe) / §eU§r (uses) in the item list.\n"
              "- You start with Pyrotech's guide book, §ePyrotechnic Esoterica§r. Its pages explain every primitive "
              "machine in detail - read it alongside these quests.\n"
              "- The §eStaying Alive§r chapter runs in parallel: water, food, shelter and dangers."),
    dict(id=1, xy=(48, -24), requires=[0], name='Rocks', icon=pyro('rock'),
         tasks=[retrieve(item('pyrotech:rock', 8, 32767))],
         desc="Small rocks lie on the ground almost everywhere - pick them up.\n\n"
              "Rocks are your first building material and your first weapon: you can §ethrow§r them at things that "
              "come too close. Mining stone with weak tools also gives rocks instead of cobblestone."),
    dict(id=2, xy=(48, 36), requires=[0], name='Sticks and Fibers', icon=mat(12),
         tasks=[retrieve(item('minecraft:stick', 6), mat(12, 6))],
         desc="§eSticks§r drop from leaves (any tree, including the Biomes O' Plenty ones) - break them by hand.\n"
              "§ePlant Fibers§r come from breaking tall grass and other grassy plants.\n\n"
              "You cannot punch trees in this world: logs only come off with an axe. Make one first."),
    dict(id=DRIED_FIBERS, xy=(-48, 36), requires=[0], name='Dried Plant Fibers', icon=mat(13),
         tasks=[retrieve(mat(13, 3))],
         desc="Fresh plant fibers are too green to burn or twist. §eDried Plant Fibers§r are the base of twine, "
              "tinder, fiber torches and fire starters.\n\n"
              "- Dry grass - §edead grass, desert grass, dune grass§r in dry biomes - drops them directly.\n"
              "- Any tall grass drops one now and then.\n"
              "- A §eCrude Drying Rack§r (2 sticks over 2 plant fibers) dries fresh fibers. It works faster in the "
              "sun, in warm dry biomes and next to a campfire.\n"
              "- One straw splits into two dried fibers."),
    dict(id=3, xy=(96, 36), requires=[2, 1], name='Crude Tools', icon=pyro('crude_pickaxe'),
         tasks=[retrieve(pyro('crude_axe'), pyro('crude_pickaxe'), pyro('crude_shovel'), any_of=True)],
         desc="Rocks + a stick + plant fibers make crude tools. They are weak and break fast, but you will want "
              "all three:\n\n"
              "- §eCrude Axe§r: the only way to get your first logs.\n"
              "- §eCrude Pickaxe§r: starts you on stone.\n"
              "- §eCrude Shovel§r: digging gravel with a shovel finds §eflint shards§r."),
    dict(id=4, xy=(144, 36), requires=[3], name='Timber!', icon=item('minecraft:log'),
         tasks=[retrieve(ore('logWood', 8, 'minecraft:log'))],
         desc="Chop a tree with your axe. §cTrees fall when you cut them§r - logs come crashing down, so step "
              "aside or you may get hit.\n\nLogs fuel everything in the primitive age: campfires, kilns, charcoal."),
    dict(id=7, xy=(192, 36), requires=[4], name='Chopping Block', icon=pyro('chopping_block'),
         tasks=[retrieve(pyro('chopping_block'))],
         desc="Your inventory crafting grid is all you have yet. Put §eany log + your axe§r into it to make a "
              "§eChopping Block§r (the axe takes a little damage and comes back).\n\n"
              "Place the chopping block on the ground - it is your first workstation."),
    dict(id=5, xy=(144, -24), requires=[2, 1], name='Crude Hammer', icon=pyro('crude_hammer'),
         tasks=[retrieve(pyro('crude_hammer'))],
         desc="Rocks + a stick + plant fibers. Hammers are how you craft on a §eWorktable§r and later an "
              "§eAnvil§r: place the ingredients on top, then hit them with the hammer."),
    dict(id=6, xy=(240, 0), size=36, requires=[7, 5], main=True, name='The Worktable', icon=pyro('worktable'),
         tasks=[retrieve(ore('plankWood', 1, 'minecraft:planks'), optional=True),
                retrieve(ore('slabWood', 1, 'minecraft:wooden_slab'), optional=True),
                retrieve(pyro('worktable'))],
         desc="The Worktable is §ea wooden slab on top of a log§r (inventory grid). Planks and slabs cannot be "
              "crafted by hand - you split them on the chopping block:\n\n"
              "1. §ePlank:§r right-click the chopping block with a §elog§r to put it on top, then hold your axe and "
              "§ehit (left-click)§r the block until the log splits.\n"
              "2. §eSlab:§r put one of those §eplanks§r on the block and hit it again.\n"
              "3. §eWorktable:§r the slab on top of a log.\n\n"
              "Chopping is hard work: §eif nothing happens, you are too hungry§r - eat something.\n\n"
              "There is no vanilla crafting table at the start - the Worktable replaces it for every 3x3 recipe. "
              "Right-click it to lay out a recipe, then §ehit it with your hammer§r until the item is done. "
              "A real crafting table needs iron (see §eA Real Crafting Table§r)."),
    dict(id=8, xy=(0, 84), requires=[0], name='Shards', icon=mat(10),
         tasks=[retrieve(mat(10), mat(11), any_of=True)],
         desc="Sharper tools need §eflint shards§r or §ebone shards§r (either one works).\n\n"
              "- Flint shards: dig gravel with a shovel.\n- Bone shards: break bones on an anvil, or find them in fossils.\n"
              "Skeletons drop bones - but at night you have bigger problems than skeletons."),
    dict(id=9, xy=(-48, 132), requires=[DRIED_FIBERS], name='Twine', icon=mat(14),
         tasks=[retrieve(mat(14))],
         desc="Twist §e3 dried plant fibers§r into §e3 twine§r (inventory grid). Twine ties tools together, "
              "builds drying racks and knives, and lashes the better tools."),
    dict(id=FIRE_STARTER, xy=(48, 84), requires=[1, 2, 8], name='Fire Starter', icon=pyro('bow_drill'),
         tasks=[retrieve(pyro('flint_and_tinder'), pyro('bow_drill'), any_of=True)],
         desc="Nothing burns in this world unless you light it yourself. You need a fire starter:\n\n"
              "- §eFlint and Tinder§r: a flint shard + dried plant fibers + a rock. Cheap, wears out quickly.\n"
              "- §eBow Drill§r: a bow + a stick. Lasts much longer, but a bow needs string.\n\n"
              "Use it on tinder, a kiln or a log pile to light it."),
    dict(id=10, xy=(144, 84), size=36, requires=[4, FIRE_STARTER], main=True, name='Fire', icon=pyro('campfire'),
         tasks=[retrieve(pyro('tinder')), retrieve(ore('logWood', 8, 'minecraft:log'))],
         desc="A campfire in four steps:\n\n"
              "1. Craft §eTinder§r: 2 dried plant fibers + 2 sticks.\n"
              "2. Place the tinder on the ground.\n"
              "3. Right-click it with §elogs§r to add fuel.\n"
              "4. Light it with your §efire starter§r.\n\n"
              "The campfire cooks food (roasted food is far better than raw), §eboils dirty water§r into drinkable "
              "water, keeps you warm at night and speeds up nearby drying racks. Resting next to it at night "
              "slowly heals you. Rain or a bucket of water puts it out; clear the ash with a shovel."),
    dict(id=11, xy=(240, 84), size=36, requires=[8, 9, 6], task_logic='OR', name='Bone and Flint Tools',
         icon=pyro('flint_pickaxe'),
         tasks=[retrieve(pyro('flint_pickaxe')), retrieve(pyro('bone_pickaxe'))],
         desc="Shards + sticks + twine, crafted on the §eWorktable§r. Flint or bone tools last much longer than "
              "crude ones and can mine iron ore. Make a pickaxe, an axe and a shovel.\n\n"
              "§7Note: there are no hoes until you have steel - farming is not a primitive-age option."),
    dict(id=12, xy=(0, 132), requires=[9, 2], name='Drying Rack', icon=pyro('drying_rack', 1),
         tasks=[retrieve(item('pyrotech:drying_rack', 1, 32767))],
         desc="Drying racks dry plant fibers, wheat and food, and can store items.\n\n"
              "- §eCrude Drying Rack§r: 2 sticks over 2 plant fibers, holds one item.\n"
              "- §eDrying Rack§r: sticks lashed with twine around a ladder, holds four.\n\n"
              "They work faster under open sky, in warm dry biomes and next to a campfire - and slower in rain."),
    dict(id=13, xy=(48, 132), requires=[12], name='Straw', icon=pyro('thatch'),
         tasks=[retrieve(mat(2, 4), pyro('thatch'))],
         desc="Dried plant fibers tie together into §eStraw§r; straw packs into a §eStraw Bale§r (thatch).\n\n"
              "Straw is the fuel bed of a pit kiln, and a straw bed is the cheapest place to sleep."),
    dict(id=14, xy=(96, 84), requires=[3], name='Clay', icon=mat(17),
         tasks=[retrieve(mat(17, 8))],
         desc="Dig clay in rivers, swamps and lakes. Lumps of clay become bricks, buckets and - most importantly - "
              "§erefractory bricks§r for the bloomery."),
    dict(id=15, xy=(144, 156), size=36, requires=[13, 14, 10], main=True, name='Pit Kiln', icon=pyro('kiln_pit'),
         tasks=[retrieve(pyro('kiln_pit'))],
         desc="Your first furnace. Place the kiln, put the item inside, cover it with straw and a straw bale, stack "
              "three logs and light it. Wait - and hope nothing cracks.\n\n"
              "The pit kiln fires clay into bricks and can even smelt §ecopper ore§r into ingots."),
    dict(id=16, xy=(300, 36), requires=[11], name='Granite Anvil', icon=pyro('anvil_granite'),
         tasks=[retrieve(pyro('anvil_granite'))],
         desc="A stone anvil: place things on it and hit them with a pickaxe or hammer to split rocks, shard bones "
              "and flint, and later to hammer iron blooms into metal."),
    dict(id=17, xy=(348, 36), requires=[16], name='Stone Tools', icon=item('minecraft:stone_pickaxe'),
         tasks=[retrieve(item('minecraft:stone_pickaxe'))],
         desc="§7Optional.§r Stone tools are a solid upgrade over flint and bone, and the stone hammer works the "
              "anvil faster."),
    dict(id=18, xy=(144, 240), requires=[15], task_logic='OR', name='Stone Machines', icon=pyro('stone_kiln'),
         tasks=[retrieve(pyro('stone_kiln')), retrieve(pyro('stone_oven'))],
         desc="§7Optional.§r The Stone Kiln does the pit kiln's job faster and with fewer failures; the Stone Oven "
              "cooks and dries without burning your food. Both run on fuel."),
    dict(id=19, xy=(240, 240), requires=[15], name='Charcoal', icon=item('minecraft:coal', 1, 1),
         tasks=[retrieve(item('minecraft:coal', 8, 1))],
         desc="Stack §elog piles§r, seal them under dirt or stone and light them - a §ePit Burn§r turns wood into "
              "charcoal (and tar).\n\nCharcoal is the fuel that gets a bloomery hot enough for iron. It also makes "
              "water filters."),
    dict(id=20, xy=(240, 192), requires=[15], name='Refractory Bricks', icon=mat(5),
         tasks=[retrieve(mat(5, 16))],
         desc="Mix clay into §erefractory clay§r, shape unfired refractory bricks and fire them in a kiln.\n\n"
              "Refractory bricks survive the heat of iron smelting - the bloomery is built from them."),
    dict(id=21, xy=(300, 84), requires=[11], name='Iron Ore', icon=item('minecraft:iron_ore'),
         tasks=[retrieve(ore('oreIron', 8, 'minecraft:iron_ore'))],
         desc="Iron ore needs at least a flint, bone or stone pickaxe.\n\n"
              "§eScavenging tip:§r iron found in ruins is mostly ore too - nobody left neat ingots lying around."),
    dict(id=22, xy=(240, 144), requires=[11], task_logic='OR', name='Tongs', icon=pyro('tongs_flint'),
         tasks=[retrieve(pyro('tongs_flint')), retrieve(pyro('tongs_bone'))],
         desc="A fresh bloom is glowing-hot iron. §cHolding it with bare hands burns you.§r Carry tongs."),
    dict(id=23, xy=(300, 192), requires=[19, 20, 21, 22], main=True, name='The Bloomery', icon=pyro('bloomery'),
         tasks=[retrieve(pyro('bloomery'))],
         desc="Load iron ore and fuel into the top of the bloomery and light it. The more (and better) fuel, the "
              "faster it works; a bellows helps.\n\nThe result is a §eBloom§r: a lump of iron and slag."),
    dict(id=24, xy=(372, 144), requires=[23, 16], main=True, name='Iron!', icon=item('minecraft:iron_ingot'),
         tasks=[retrieve(ore('ingotIron', 4, 'minecraft:iron_ingot'))],
         desc="Put the bloom on an anvil and hammer it: iron comes out, slag stays behind (slag with ore left in "
              "it can go back into the bloomery).\n\nYou made metal from nothing. The primitive age is almost over."),
    dict(id=25, xy=(456, 144), requires=[24], main=True, name='The First Anvil', icon=item('hbm:anvil_iron'),
         tasks=[retrieve(item('hbm:anvil_iron'))],
         desc="An §eIron Anvil§r from HBM's Nuclear Tech - the first real machine of the old world. With it you can "
              "build the next generation of tools and begin to salvage the ruins for steel.\n\n"
              "§eThis is the end of the Primitive age.§r Next: the Scavenger age."),
    dict(id=28, xy=(384, 0), requires=[24, 6], name='A Real Crafting Table', icon=item('minecraft:crafting_table'),
         tasks=[retrieve(item('minecraft:crafting_table'))],
         desc="With iron you can finally build a vanilla crafting table - no more hammering every recipe.\n\n"
              "1. On the Worktable: §e8 iron ingots§r in a ring make a §eCrafting Table Template§r.\n"
              "2. Then: the template surrounded by §e8 planks§r makes the §eCrafting Table§r."),
]

ALIVE = [
    # --- Water ---
    dict(id=100, xy=(-192, 48), requires=[0], name='Thirst', icon=item('simpledifficulty:purified_water_bottle'),
         tasks=[checkbox()],
         desc="You get thirsty as you move, work and fight. In an emergency you can §edrink straight from a water "
              "source§r: look at the water with an §eempty hand§r and right-click.\n\n"
              "- Water from normal biomes is §edirty§r: drinkable, but it can give you parasites. Boil or filter it.\n"
              "- §eSea water§r is salty: it makes you thirstier and sick to your stomach.\n"
              "- Water in §cwastelands, deserts and nuke craters is toxic§r: poison and radiation with every sip. "
              "You cannot even fill a canteen there.\n"
              "- Standing in the rain and looking up lets you drink rain water.\n\n"
              "If you start in the desert, head for green land and a river - then make a canteen."),
    dict(id=BOTTLE, xy=(-144, 48), requires=[100], name='Mehrwegflasche', icon=item('minecraft:glass_bottle'),
         tasks=[retrieve(item('minecraft:glass_bottle'))],
         desc="§7(German: \"reusable bottle\".)§r\n\n"
              "You cannot make glass yet, but the old world left plenty of bottles behind: look in kitchens, houses "
              "and chests in ruined towns. Some are still full.\n\n"
              "A bottle holds one drink. Fill it at any water, §eboil it on a campfire§r to make it safe, drink, and "
              "keep the empty bottle - it is reusable forever."),
    dict(id=CANTEEN, xy=(-144, 0), requires=[100], name='Not a Tin Can', icon=item('simpledifficulty:canteen'),
         tasks=[retrieve(item('simpledifficulty:canteen'), item('simpledifficulty:iron_canteen'), any_of=True)],
         desc="A §eCanteen§r holds several drinks. Animals drop carcasses, not leather - make a §erawhide canteen§r:\n\n"
              "1. Process a carcass with a §eButcher's Knife§r to get §epelts§r (see §eHunting and Butchering§r).\n"
              "2. Scrape a pelt with a §eHunter's Knife§r (crafting grid) into a §escraped hide§r.\n"
              "3. Wash it: craft it with a bucket of water, or leave it lying in water for a while.\n"
              "4. §e3 washed hides + twine§r make the canteen.\n\n"
              "Tanned leather (6 for a canteen) works too, but comes much later. With iron you can upgrade it to an "
              "§eIron Canteen§r that holds more."),
    dict(id=101, xy=(-84, 24), requires=[BOTTLE, CANTEEN], requires_logic='OR', name='Something to Drink From',
         icon=item('simpledifficulty:canteen'), tasks=[checkbox()],
         desc="Bottles and canteens are how you carry water:\n\n"
              "- §eFill§r: right-click water with an empty bottle or canteen. Water from normal biomes is §edirty§r.\n"
              "- §eDrink§r: hold right-click. A canteen holds several drinks, a bottle one.\n"
              "- §ePurify§r: boil bottles on a campfire; canteens cannot go on a fire - craft them with a "
              "charcoal filter.\n\n"
              "§cWasteland water fills bottles with Toxic Water, and canteens refuse it.§r"),
    dict(id=102, xy=(-108, 96), requires=[101, 10], name='Boil It', icon=item('simpledifficulty:purified_water_bottle'),
         tasks=[retrieve(item('simpledifficulty:purified_water_bottle'))],
         desc="Put a bottle of dirty water on a §ecampfire§r (or in a stone oven) and it comes back purified and safe."),
    dict(id=103, xy=(-60, 96), requires=[101, 19], name='Charcoal Filter', icon=item('simpledifficulty:charcoal_filter'),
         tasks=[retrieve(item('simpledifficulty:charcoal_filter'))],
         desc="Canteens cannot go on a fire. Craft a filled canteen with a §echarcoal filter§r to purify it."),
    dict(id=104, xy=(-192, 0), requires=[100], name='Never Drink the Wasteland', icon=item('decaychain:toxic_water_bottle'),
         tasks=[checkbox()],
         desc="A bottle filled in the wasteland comes out as §cToxic Water§r: poison, nausea and radiation.\n\n"
              "No campfire or filter can clean it - that needs a chemical plant, far in the future. "
              "§eWhen you travel into the wastes, carry your water with you.§r"),
    # --- Food ---
    dict(id=110, xy=(72, 96), requires=[0], name='Forage', icon=pyro('strange_tuber'),
         tasks=[retrieve(pyro('strange_tuber'), pyro('freckleberries'), item('hbmspace:strawberry'),
                         item('biomesoplenty:berries'), any_of=True)],
         desc="Dig grassy dirt by hand for §eStrange Tubers§r, and pick berries - freckleberries, wild "
              "strawberries and Biomes O' Plenty berry bushes grow in plains and forests.\n\n"
              "Food is less filling here than you are used to, and you only heal when you are well fed. Keep an "
              "eye on the saturation shown on your hunger bar."),
    dict(id=HUNTING, xy=(24, -48), requires=[8, 9, 6], name='Hunting and Butchering', icon=pyro('carcass'),
         tasks=[retrieve(pyro('bone_butchers_knife'), pyro('flint_butchers_knife'), pyro('stone_butchers_knife'),
                         pyro('iron_butchers_knife'), any_of=True),
                retrieve(pyro('carcass'))],
         desc="Animals do not drop meat and leather here. When they die they leave a §eCarcass§r - pick it up and "
              "place it on the ground.\n\n"
              "- §eButcher's Knife§r (shards + sticks + twine): use it on the carcass for §emeat§r, §epelts§r, "
              "bone shards and lard.\n"
              "- §eHunter's Knife§r: gets more pelts out of a carcass, and scrapes pelts into hides.\n"
              "- A §eButcher's Block§r gives better yields.\n\n"
              "Butchering is hard work: §eif nothing happens, you are too hungry§r."),
    dict(id=111, xy=(24, -96), requires=[6], name='Hunting Spear', icon=pyro('crude_spear'),
         tasks=[retrieve(pyro('crude_spear'))],
         desc="A crude spear can be thrown - it sticks in the target, so go and pick it up. Animals are the best "
              "early source of meat, pelts (canteens, later leather) and bones."),
    dict(id=112, xy=(72, -48), requires=[10, HUNTING], task_logic='OR', name='Cooked Meal',
         icon=item('minecraft:cooked_porkchop'),
         tasks=[retrieve(item('minecraft:cooked_porkchop')), retrieve(item('minecraft:cooked_beef')),
                retrieve(item('minecraft:cooked_mutton')), retrieve(item('minecraft:cooked_chicken')),
                retrieve(item('minecraft:cooked_rabbit')), retrieve(item('minecraft:baked_potato'))],
         desc="Cook meat from your carcasses on the campfire. Roasted food restores much more than raw food - and "
              "raw meat can make you sick. §eFood left cooking unattended burns.§r"),
    dict(id=113, xy=(72, 0), requires=[0], name='Canned Goods', icon=item('hbm:canned_conserve'),
         tasks=[retrieve(item('hbm:canned_conserve', 1, 32767))],
         desc="Ruined cities still hide pre-war food: canned beef, tuna, mystery meat... Cans keep forever. "
              "City chests, rail tunnels and old dungeons are your pantry."),
    dict(id=114, xy=(72, 48), requires=[0], task_logic='OR', name='Wild Gardens', icon=item('harvestcraft:windygarden'),
         tasks=[retrieve(item('harvestcraft:' + g)) for g in
                ('aridgarden', 'frostgarden', 'shadedgarden', 'soggygarden', 'tropicalgarden', 'windygarden')],
         desc="Wild gardens grow in many biomes. Break them for vegetables, fruits and seeds.\n\n"
              "§7Real farming needs a steel hoe - that comes in the Scavenger age. Until then, gather and scavenge.§r"),
    dict(id=SOMETHING_TO_EAT, xy=(24, 24), requires=[114, 112, 113, 110], requires_logic='OR', name='Something to Eat',
         icon=item('minecraft:cake'), tasks=[checkbox()],
         desc="There are four ways to eat in the primitive age: §eforage§r, §ehunt and cook§r, §escavenge cans§r "
              "and §epick wild gardens§r. You have found one - none of them is enough on its own, so use them all.\n\n"
              "- Food is less filling than you are used to - cooked food is worth far more than raw.\n"
              "- You only heal while you are well fed.\n"
              "- Eating next to a campfire at night makes you §eComfortable§r: food goes further, and a big meal "
              "gives you §eWell Fed§r (more stamina)."),
    # --- Shelter ---
    dict(id=120, xy=(-12, 96), requires=[2], name='Light in the Dark', icon=pyro('torch_fiber'),
         tasks=[retrieve(pyro('torch_fiber'), pyro('torch_stone'), any_of=True)],
         desc="Nights are §cpitch black§r here - no moonlight to see by. Darkness is also where things spawn: "
              "light up your camp.\n\n"
              "- §eFiber Torch§r: dried plant fibers on a stick. Cheap, but it §eburns up§r over time and the rain "
              "puts it out.\n"
              "- §eStone Torch§r: coal pieces on a stone rod. §eNever burns up§r, but rain can still put it out."),
    dict(id=121, xy=(144, -96), requires=[1], name='Four Walls', icon=item('minecraft:cobblestone'),
         tasks=[retrieve(ore('cobblestone', 32, 'minecraft:cobblestone'))],
         desc="Rocks craft into cobblestone. Build something you can close behind you before your first night.\n\n"
              "Mud and straw make §eCob§r - another cheap early building block."),
    dict(id=122, xy=(144, -48), requires=[121], task_logic='OR', name='A Place to Sleep', icon=item('comforts:sleeping_bag'),
         tasks=[retrieve(pyro('straw_bed')), retrieve(item('comforts:sleeping_bag', 1, 32767))],
         desc="A §estraw bed§r is cheap; a §esleeping bag§r (wool) can be carried. Sleeping skips the night - "
              "except when the horde is coming."),
    dict(id=123, xy=(144, 0), requires=[6], task_logic='OR', name='Storage', icon=pyro('crate'),
         tasks=[retrieve(pyro('crate')), retrieve(pyro('stash')), retrieve(item('storagedrawers:basicdrawers', 1, 32767))],
         desc="Crates, stashes and drawers keep your loot safe. A §eRock Bag§r collects rocks automatically."),
    dict(id=124, xy=(144, 48), requires=[121], name='The Horde', icon=item('minecraft:rotten_flesh'), tasks=[checkbox()],
         desc="§cEvery tenth night a horde comes for you§r - and it grows each time. You cannot sleep through it.\n\n"
              "Zombies carrying pickaxes §edig through walls§r. HBM concrete stops them; until you have it, build "
              "thick walls, light everything and have an escape route.\n\n"
              "Bitten survivors can catch the zombie infection. Watch your status effects."),
    dict(id=FIRST_NIGHT, xy=(-36, 0), size=36, requires=[FIRE_STARTER, 101, 120, SOMETHING_TO_EAT],
         name='The First Night', icon=item('minecraft:iron_sword'), tasks=[checkbox()],
         desc="Night falls fast, and it is darker than you remember. Before the sun goes down:\n\n"
              "- §eFire§r: a campfire keeps you warm, cooks, and lets you rest and heal.\n"
              "- §eWater§r: a full bottle or canteen - you cannot go looking for a river in the dark.\n"
              "- §eLight§r: torches around your camp; monsters spawn in darkness.\n"
              "- §eFood§r: something cooked, something stored.\n"
              "- §eWalls§r: anything you can close behind you - dig into a hillside if you must.\n\n"
              "Do not wander at night. Zombies hear noise and follow light, and worse things hunt in the dark. "
              "Survive until morning - then do it again."),
    # --- Health ---
    dict(id=130, xy=(216, -96), requires=[0], task_logic='OR', name='First Aid', icon=item('firstaid:plaster'),
         tasks=[retrieve(item('firstaid:plaster')), retrieve(item('firstaid:bandage'))],
         desc="Damage hits §especific body parts§r: a hurt leg slows you down, a damaged head blurs your sight. "
              "Plasters and bandages heal individual parts - look for them in ruins.\n\n"
              "Health only regenerates while you are well fed."),
    dict(id=131, xy=(216, -48), requires=[0], name='Hot and Cold', icon=item('simpledifficulty:wool_helmet'),
         tasks=[checkbox()],
         desc="Your body temperature matters. Deserts and wastelands are scorching by day and freezing at night; "
              "rain and water chill you.\n\nStay near a campfire at night, wear cloth armour in the cold, and keep "
              "an eye on the thermometer on your HUD."),
    dict(id=132, xy=(216, 0), requires=[0], name='Death Is Not the End', icon=item('tombstone:decorative_grave_simple'),
         tasks=[checkbox()],
         desc="When you die your belongings stay in a §egrave§r where you fell - you respawn with a key to it. "
              "Go back carefully: whatever killed you is probably still there.\n\n"
              "You lose most of your experience on death, and experience is what buys skill levels."),
    # --- Hints ---
    dict(id=140, xy=(288, -96), requires=[0], name='Know Your Enemies', icon=item('minecraft:skull', 1, 2),
         tasks=[checkbox()],
         desc="- §eParasites§r start weak, but they evolve as they spread and assimilate other creatures. "
              "The longer they go unchecked, the worse they get.\n"
              "- §eMutated wildlife§r roams the wastes - giant worms, insects and worse.\n"
              "- Some monsters are §eelite§r: a coloured health bar and special powers. Avoid them early.\n"
              "- Zombies hear noise, see light and follow blood."),
    dict(id=141, xy=(288, -48), requires=[0], name='Ruins', icon=item('hbm:brick_concrete_cracked'), tasks=[checkbox()],
         desc="Cities stand in ruins - the worst in the wastelands. They are full of loot and full of danger.\n\n"
              "- Some buildings are §ehaunted§r: their chests stay locked until you clear the building.\n"
              "- Concrete and steel walls §ecannot be dug through§r without at least an iron pickaxe - use the doors.\n"
              "- §cSmashing glass cuts you§r: breaking windows makes you bleed. A §eGlass Cutter§r removes glass safely (and keeps it).\n"
              "- Watch for radiation, landmines and asbestos dust near old industrial sites.\n"
              "- Underground rail tunnels hold the best early caches."),
    dict(id=142, xy=(288, 0), requires=[0], name='Skills', icon=item('minecraft:experience_bottle'), tasks=[checkbox()],
         desc="Experience buys skill levels (open the skills tab in your inventory). Tools and machines are never "
              "locked by skills - but §eweapons and armour are§r: better guns, swords and armour need Attack, "
              "Defense or Agility levels."),
    dict(id=143, xy=(288, 48), requires=[3], task_logic='OR', name='Something to Fight With',
         icon=item('spartanweaponry:spear_wood'),
         tasks=[retrieve(item('spartanweaponry:spear_wood')), retrieve(item('spartanweaponry:spear_stone')),
                retrieve(item('spartanweaponry:dagger_stone')), retrieve(pyro('crude_spear')), retrieve(item('minecraft:bow'))],
         desc="Spears, daggers and clubs of wood and stone are your first real weapons; a bow keeps danger at a "
              "distance. Firearms come with steel."),
    dict(id=144, xy=(288, 96), requires=[6], name='Backpack', icon=item('wearablebackpacks:backpack'),
         tasks=[retrieve(item('wearablebackpacks:backpack'))],
         desc="A backpack worn on your back roughly doubles what you can carry home from a scavenging run.\n\n"
              "It is made of §etanned leather§r (7), a gold ingot and wool - a goal for once your tanning rack "
              "is running."),
]

CHAPTERS = [
    dict(id=10, name='I. Primitive - Path to Iron', icon=pyro('flint_pickaxe'), quests=PATH,
         desc='From empty pockets to your first iron and the first HBM anvil.'),
    dict(id=11, name='I. Primitive - Staying Alive', icon=item('simpledifficulty:canteen'), quests=ALIVE,
         desc='Water, food, shelter, health and the dangers of this world.'),
]
