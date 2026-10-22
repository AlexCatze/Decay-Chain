"""Age II - Scavenger: from the first HBM anvil to plastic, plus the weapons of the first two ages.

Two chapters:
  12 - Path to Plastic  (main progression: steel -> heat and power -> assembly machine -> oil -> plastic)
  13 - Arsenal          (good weapons of the Primitive and Scavenger ages and the way to them; hints only)
Recipes were checked against HBM's own recipe files (config/hbmRecipes, the anvil/assembly/press dumps) and the
crafting registry of a test server. Layout is a first draft - rearrange it in the in-game editor and port it back with
diff_save.py.
"""
from bq import item, ore, retrieve, checkbox


def hbm(name, meta=0, count=1):
    return item('hbm:' + name, count, meta)


def spartan(name):
    return item('spartanweaponry:' + name)


def pyro(name, meta=0, count=1):
    return item('pyrotech:' + name, count, meta)


# --- quests of the Primitive chapters this builds on ---
EMPTY_POCKETS, FIRST_ANVIL, IRON, MASONRY = 0, 25, 24, 30

# --- Path to Plastic ---
SCAVENGER, SALVAGED_STEEL, FIREBRICKS, BLAST_FURNACE, PRESS, COILS, MOTORS, STEEL_ANVIL = 200, 201, 202, 203, 204, 205, 206, 207
FIREBOX, POWER, CIRCUITS, ASSEMBLER, SHREDDER, FLUORITE, CRUCIBLE, TITANIUM_DRILL = 208, 209, 210, 211, 212, 213, 214, 215
DERRICK, INSULATOR, BOILER, REFINERY, CHEMPLANT, PLASTIC, RED_COPPER, STEEL = 216, 217, 218, 219, 220, 221, 222, 223
STEEL_TOOLS, FARMING, STORAGE, GAS_MASK, TANKS, BATTERIES = 230, 231, 232, 233, 234, 235
SOLDERING, CONTROLLER = 1478000950, 1974609047   # created in the in-game editor

# --- Arsenal ---
STICKS, FLINT_BONE, WOODEN, STONE_WEAPONS, BOWS, IRON_WEAPONS, PEPPERBOX, HAND_CANNON = (
    301, 302, 303, 304, 305, 306, 307, 308)
STEEL_WEAPONS, CROWBAR, REVOLVER_TG, AMMO_PRESS, GUN_FOUNDRY, REVOLVER, LEVER_ACTION, FLARE_GUN = (
    310, 311, 312, 313, 314, 315, 316, 317)
CLOTH, LEATHER_ARMOR, IRON_ARMOR, SHIELD, STEEL_ARMOR, SOLDIER_GEAR, HAZMAT, RIOT_SHIELD = (
    320, 321, 322, 323, 324, 325, 326, 327)


PLASTIC_PATH = [
    dict(id=SCAVENGER, xy=(0, 0), size=36, main=True, requires=[FIRST_ANVIL], name='The Scavenger Age', icon=hbm('scrap'),
         tasks=[checkbox()],
         desc="You can make iron. The old world made far more - and left it lying in the ruins.\n\n"
              "This age is about §esteel§r, the first §eelectricity§r and the old machines, and it ends with §eoil "
              "and plastic§r: everything after (computers, rifles, automation) is built on plastic.\n\n"
              "§eTips§r\n- Most HBM machines are built on the §eanvil§r: right-click it, pick the recipe, hammer.\n"
              "- An iron pickaxe breaks the concrete and steel of the ruins; reinforced blocks need steel.\n"
              "- Look up anything with §eR§r (recipe) and §eU§r (uses) - the machines below have long recipes."),
    dict(id=SALVAGED_STEEL, xy=(72, -48), requires=[SCAVENGER], name='Salvaged Steel', icon=hbm('deco_rusty_steel'),
         tasks=[retrieve(hbm('deco_rusty_steel', count=8), hbm('deco_steel', count=4), any_of=True)],
         desc="§eOne way to steel:§r take it from the ruins. They are full of steel - rusted, but steel.\n\n"
              "Break §erusty steel§r and §esteel deco blocks§r (iron pickaxe) and recycle them on the anvil: §e8 rusty "
              "steel or 4 steel blocks give 1 steel ingot§r. Old filing cabinets, tape recorders and satellite dishes "
              "give steel too.\n\n"
              "No furnace needed - just a lot of walking. The other way is the blast furnace."),
    dict(id=PRESS, xy=(72, 0), requires=[SCAVENGER], name='Burner Press', icon=hbm('machine_press'),
         tasks=[retrieve(hbm('machine_press')), retrieve(ore('plateCopper', 1, 'hbm:plate_copper'))],
         desc="§e4 iron ingots, an iron block, a Pyrotech cog§r (stone, flint, bone or iron) and §e3 masonry "
              "bricks blocks§r as its firebox. It burns solid fuel.\n\n"
              "§eStamps:§r 3 bricks + 3 stone (or iron, or steel) make a §eflat stamp§r; on the anvil you cut it "
              "into a §eplate§r, §ewire§r or §ecircuit§r stamp. Put the stamp in the press, fuel below, the metal in "
              "- out come §eplates§r (one per ingot) or §ewires§r (eight per ingot).\n\n"
              "Iron, copper and steel plates go into almost every machine."),
    dict(id=FIREBRICKS, xy=(72, 60), requires=[SCAVENGER], name='Firebricks', icon=hbm('ingot_firebrick'),
         tasks=[retrieve(hbm('ingot_firebrick', count=32))],
         desc="§eFireclay§r is clay tempered with broken pottery: §e3 clay balls + pottery fragments or a pottery "
              "shard§r make 4. Fire it in the §epit kiln or stone kiln§r into §eFirebricks§r.\n\n"
              "Pottery fragments are what a cracked kiln firing leaves behind - and a cracked fireclay firing leaves "
              "more of them for the next batch. For a steady supply, §ehammer clay bricks§r on the granite anvil: a "
              "brick gives 1 fragment, a brick block 4.\n\n"
              "The blast furnace takes §e32§r firebricks, the crucible 20, molds and foundry channels a few each."),
    dict(id=BLAST_FURNACE, xy=(120, 108), requires=[PRESS, FIREBRICKS], name='Blast Furnace',
         icon=hbm('machine_blast_furnace'),
         tasks=[retrieve(hbm('machine_blast_furnace'))],
         desc="§eThe other way to steel:§r make it. On the iron anvil: §e4 stone bricks, 32 firebricks and 8 copper "
              "plates§r.\n\n"
              "- §eSteel§r: 2 iron ingots (or 2 iron dust, or 1 iron ore) + sand. Flux instead of sand doubles the "
              "steel from ore.\n"
              "- §eRed copper§r: copper + redstone make 2 - the metal of wires and coils.\n\n"
              "Slag comes out as a by-product; keep the outputs clear."),
    dict(id=STEEL, xy=(120, 0), size=36, main=True, requires=[SALVAGED_STEEL, BLAST_FURNACE], requires_logic='OR',
         name='Steel', icon=hbm('ingot_steel'),
         tasks=[retrieve(ore('ingotSteel', 16, 'hbm:ingot_steel'))],
         desc="Salvaged or smelted - steel is steel. With §e16 ingots§r in hand the old world's machines are within "
              "reach: the steel anvil first.\n\n"
              "Salvage is quick to start but runs out where you live; a blast furnace keeps going as long as you "
              "have iron."),
    dict(id=RED_COPPER, xy=(288, 108), requires=[BLAST_FURNACE, CRUCIBLE], requires_logic='OR', name='Red Copper',
         icon=hbm('ingot_red_copper'),
         tasks=[retrieve(hbm('ingot_red_copper', count=4))],
         desc="Copper alloyed with §eredstone§r - the metal of every wire and coil.\n\n"
              "- §eBlast furnace:§r a copper ingot + redstone make 2.\n"
              "- §eCrucible:§r melt copper and redstone together, 1 to 1, and cast ingots."),
    dict(id=COILS, xy=(336, 108), requires=[RED_COPPER, PRESS], hidden=[PRESS], name='Wires and Coils', icon=hbm('coil_copper'),
         tasks=[retrieve(hbm('wire_fine', 31, 8)), retrieve(hbm('coil_copper'))],
         desc="Press a §ered copper ingot§r with the wire stamp: §e8 red copper wires§r.\n\n"
              "- §eCopper coil§r: 8 red copper wires around an iron (or steel) ingot.\n"
              "- §eCoil torus§r: 4 coils around an iron or steel plate make 2."),
    dict(id=MOTORS, xy=(336, 60), requires=[COILS], name='Motors', icon=hbm('motor'),
         tasks=[retrieve(hbm('motor', count=2))],
         desc="On the anvil: §e2 iron plates, a copper coil and a coil torus make 2 motors§r.\n\n"
              "Motors drive the assembly machine, the shredder, the derrick and the pack's wood-burning generator."),
    dict(id=STEEL_ANVIL, xy=(240, 0), requires=[STEEL], name='The Steel Anvil', icon=hbm('anvil_steel'),
         tasks=[retrieve(hbm('anvil_steel'))],
         desc="Put your iron anvil on the anvil's smithing slot with §e10 steel ingots§r: a §eSteel Anvil§r "
              "(tier 2).\n\n"
              "Tier 2 builds the real machines: firebox, crucible, Stirling engine, assembly machine, soldering "
              "station, rotary furnace, boiler."),
    dict(id=FIREBOX, xy=(240, 60), requires=[STEEL_ANVIL], name='Firebox', icon=hbm('heater_firebox'),
         tasks=[retrieve(hbm('heater_firebox'))],
         desc="Steel anvil: §ea Pyrotech stone kiln, 8 steel plates and 8 copper ingots§r.\n\n"
              "It burns solid fuel and §eheats the machine standing on top of it§r - the Stirling engine, the "
              "crucible, the boiler. HBM's heat machines do nothing without a heat source below."),
    dict(id=POWER, xy=(0, 72), size=36, main=True, requires=[SCAVENGER], task_logic='OR', name='First Power',
         icon=hbm('machine_stirling'),
         tasks=[retrieve(hbm('machine_stirling')), retrieve(hbm('machine_wood_burner')), retrieve(hbm('machine_diesel'))],
         desc="Three ways to the first electricity:\n\n"
              "- §eStirling Engine§r on a firebox (steel anvil: 16 planks, 6 steel plates, 8 copper, 4 coils, a "
              "large gear). Cheap, steady.\n"
              "- §eWood-Burning Generator§r: 5 steel plates, 2 coils, a stone kiln and a motor.\n"
              "- A §eDiesel Generator§r looted from the ruins - building one needs plastic. It burns liquid fuel.\n\n"
              "§eRed Copper Cable§r carries the power: 3 red copper wires + 2 insulators make 16."),
    dict(id=CIRCUITS, xy=(336, 0), requires=[STEEL_ANVIL, POWER], hidden=[POWER], name='Circuits', icon=hbm('circuit', 7),
         tasks=[retrieve(hbm('circuit', 0)), retrieve(hbm('circuit', 7))],
         desc="Machines need §evacuum tubes§r and §eanalog circuit boards§r.\n\n"
              "- §eSalvage:§r old computers, radios and satellite gear break down into circuits on the anvil.\n"
              "- §eSoldering Station:§r an analog board is 3 vacuum tubes and 2 capacitors soldered onto 4 printed "
              "circuit boards with fine lead wire."),
    dict(id=ASSEMBLER, xy=(384, 0), size=36, main=True, requires=[POWER, CIRCUITS, MOTORS], hidden=[POWER], name='Assembly Machine',
         icon=hbm('machine_assembly_machine'),
         tasks=[retrieve(hbm('machine_assembly_machine'))],
         desc="Steel anvil: §e8 steel ingots, 4 copper plates, 2 motors and 4 vacuum tubes§r.\n\n"
              "§eUse:§r choose a recipe in its menu, put the ingredients in and give it power (100 HE/t). It builds "
              "nearly every machine from here on - the shredder, tanks, the derrick, the refinery, the chemical "
              "plant."),
    dict(id=SHREDDER, xy=(336, -48), requires=[ASSEMBLER], name='Shredder', icon=hbm('machine_shredder'),
         tasks=[retrieve(hbm('machine_shredder')), retrieve(hbm('powder_coal', count=8))],
         desc="Assembly machine: §e8 steel plates, 4 copper plates, 2 motors§r. It needs §eshredder blades§r "
              "(4 steel plates + a steel ingot) and power.\n\n"
              "It grinds ores and items into dust - §ecoal dust§r and §efluorite dust§r for plastic, and doubles "
              "many ores."),
    dict(id=FLUORITE, xy=(540, 48), requires=[SCAVENGER], hidden=[SCAVENGER], name='Fluorite',
         icon=hbm('ore_fluorite'),
         tasks=[retrieve(hbm('fluorite', count=4))],
         desc="§eFluorite ore§r is found underground; mining it drops §eFluorite§r. One fluorite goes into every "
              "polymer ingot, together with 2 coal dust from the shredder."),
    dict(id=CRUCIBLE, xy=(288, 60), requires=[FIREBOX, FIREBRICKS], hidden=[FIREBRICKS], name='Crucible and Foundry',
         icon=hbm('machine_crucible'),
         tasks=[retrieve(hbm('machine_crucible')), retrieve(hbm('foundry_mold'))],
         desc="Steel anvil: §e20 firebricks, 8 copper ingots, 8 steel plates§r. Stand it §eon a firebox§r.\n\n"
              "It melts metals and mixes them into §ealloys§r. Molten metal pours through §efoundry channels§r "
              "(2 firebricks + a stone slab) into a §efoundry mold§r holding a §emold§r (blank mold: 4 firebricks + "
              "iron, shaped on the anvil) - ingots, plates, gun parts.\n\n"
              "§eHigh-speed steel§r (steel + tungsten + cobalt) is made here - the titanium drill needs it."),
    dict(id=TITANIUM_DRILL, xy=(396, 60), requires=[ASSEMBLER, CRUCIBLE], name='Titanium Drill',
         icon=hbm('drill_titanium'),
         tasks=[retrieve(hbm('drill_titanium'))],
         desc="Assembly machine: §ea triple high-speed steel plate and 8 titanium plates§r.\n\n"
              "§eTitanium ore§r is found underground and smelts like iron. High-speed steel comes from the "
              "crucible: steel, tungsten and cobalt."),
    dict(id=DERRICK, xy=(444, 0), size=24, main=True, requires=[TITANIUM_DRILL, ASSEMBLER], name='Black Gold',
         icon=hbm('machine_well'),
         tasks=[retrieve(hbm('oil_detector')), retrieve(hbm('machine_well'))],
         desc="Oil sits in deep pockets underground. An §eOil Detector§r (gold wire, copper, an analog board, "
              "steel plates) tells you when you stand above one.\n\n"
              "§eOil Derrick§r (assembly machine: steel plates, triple copper plates, steel pipes, a motor and the "
              "titanium drill): set it over the deposit and power it. It pumps §ecrude oil§r and §enatural gas§r - "
              "pipe them into tanks with universal fluid ducts."),
    dict(id=INSULATOR, xy=(12, -48), requires=[SCAVENGER], hidden=[SCAVENGER], name='Insulator',
         icon=hbm('plate_polymer'),
         tasks=[retrieve(hbm('plate_polymer', count=16))],
         desc="Not plastic - wrapped wool. §e1 wool + 2 twine (or string)§r make §e4 insulators§r.\n\n"
              "The ruins have plenty: insulators turn up in chests, and §eRolls of Insulation§r sit in the walls of "
              "ruined buildings - each one is 9.\n\n"
              "Cables need them (2 per 16 cables), and so do the big machines: the boiler and refinery take 8 each, "
              "the chemical plant 16."),
    dict(id=BOILER, xy=(492, 0), requires=[DERRICK, INSULATOR, FIREBOX], hidden=[FIREBOX, INSULATOR],
         name='Hot Crude Oil', icon=hbm('heat_boiler'),
         tasks=[retrieve(hbm('heat_boiler'))],
         desc="The refinery only takes §ehot§r crude oil. A §eLarge Boiler§r (steel anvil: 4 steel ingots, 16 "
              "copper plates, 8 insulators) on a §efirebox§r heats crude oil the way it boils water."),
    dict(id=REFINERY, xy=(540, 0), size=24, main=True, requires=[BOILER, INSULATOR], hidden=[INSULATOR], name='Oil Refinery',
         icon=hbm('machine_refinery'),
         tasks=[retrieve(hbm('machine_refinery'))],
         desc="Assembly machine: steel plates and shells, copper plates, 12 steel pipes, §e8 insulators and 3 "
              "analog boards§r.\n\n"
              "It splits hot crude oil into §e50% heavy oil, 25% naphtha, 15% light oil and 10% petroleum gas§r, "
              "plus sulfur. Petroleum gas is what plastic is made of; the oils become fuels later."),
    dict(id=CHEMPLANT, xy=(492, -48), requires=[ASSEMBLER, INSULATOR], hidden=[INSULATOR], name='Chemical Plant',
         icon=hbm('machine_chemical_plant'),
         tasks=[retrieve(hbm('machine_chemical_plant'))],
         desc="Assembly machine: 8 steel ingots, 2 copper pipes, §e16 insulators§r, 2 motors, 2 tungsten coils "
              "and an analog board.\n\n"
              "§eUse:§r choose a recipe, feed it items and fluids (ducts or canisters) and power. It also cleans "
              "toxic water, much later."),
    dict(id=PLASTIC, xy=(588, 0), size=36, main=True, requires=[REFINERY, CHEMPLANT, FLUORITE, SHREDDER], hidden=[SHREDDER],
         name='Plastic!', icon=hbm('ingot_polymer'),
         tasks=[retrieve(hbm('ingot_polymer', count=16))],
         desc="In the chemical plant: §e2 coal dust + 1 fluorite dust + 500 mB petroleum gas = 1 polymer ingot§r.\n\n"
              "You make plastic from oil now - the first truly modern material since the end.\n\n"
              "§eThis is the end of the Scavenger age.§r Next: the Industrial age - rifles, computers, storage, "
              "automation."),
    # --- side branches: what steel unlocks ---
    dict(id=STEEL_TOOLS, xy=(240, -48), requires=[STEEL], name='Steel Tools', icon=hbm('steel_pickaxe'),
         tasks=[retrieve(hbm('steel_pickaxe'))],
         desc="Steel tools and armour (vanilla shapes, steel ingots) outlast iron by far, and a steel pickaxe "
              "breaks the reinforced blocks of the ruins."),
    dict(id=FARMING, xy=(288, -48), requires=[STEEL_TOOLS], name='Farming', icon=hbm('steel_hoe'),
         tasks=[retrieve(hbm('steel_hoe'))],
         desc="Only §esteel hoes and better§r can till the soil in this world. With a steel hoe (2 steel ingots + "
              "2 sticks) you can finally plant the seeds from wild gardens - real food that grows back."),
    dict(id=STORAGE, xy=(120, -48), requires=[STEEL], name='Better Storage',
         icon=hbm('crate_steel'),
         tasks=[retrieve(hbm('crate_steel'))],
         desc="- §eSteel Crate§r: 3 steel plates + 5 steel ingots - lots of room, and it keeps its contents when "
              "you pick it up.\n"
              "- §eStorage drawer upgrades§r: x2 capacity with iron plates, x4 with steel plates."),
    dict(id=CONTROLLER, xy=(180, -48), requires=[STEEL, MOTORS], hidden=[MOTORS], name='Even Better Storage',
         icon=item('storagedrawers:controller'),
         tasks=[retrieve(item('storagedrawers:controller'))],
         desc="A §eDrawer Controller§r: 3 steel plates on top, comparators either side of a drawer, and steel plates "
              "around a §emotor§r below.\n\n"
              "Place it touching your wall of drawers: put items into the controller and they go to the right drawer, "
              "anywhere in the connected block."),
    dict(id=SOLDERING, xy=(288, 0), requires=[STEEL_ANVIL], name='Soldering Station',
         icon=hbm('machine_soldering_station'),
         tasks=[retrieve(hbm('machine_soldering_station'))],
         desc="Steel anvil: §e2 triple steel plates, 4 copper coils, 4 tungsten bolts and 2 vacuum tubes§r.\n\n"
              "§eUse:§r give it power, choose a circuit, put the parts on top, the circuit board in the middle and "
              "solder (fine lead wire) in its slot. It makes the §eanalog circuit boards§r that the assembly machine, "
              "the oil detector and the refinery need.\n\n"
              "Tungsten bolts: tungsten ore lies deep underground; 2 ingots make 16 bolts."),
    dict(id=GAS_MASK, xy=(192, 60), requires=[STEEL, PRESS], hidden=[PRESS], name='Gas Mask',
         icon=hbm('gas_mask'),
         tasks=[retrieve(hbm('gas_mask'))],
         desc="§e4 steel plates, 2 glass panes, an iron plate§r. With a filter (iron plate + coal filter) it keeps "
              "out coal dust, asbestos and the worst of the fallout dust around old industry."),
    dict(id=TANKS, xy=(396, -48), requires=[ASSEMBLER], name='Tanks and Barrels', icon=hbm('machine_fluidtank'),
         tasks=[retrieve(hbm('barrel_steel'), hbm('machine_fluidtank'), any_of=True)],
         desc="Oil needs somewhere to go.\n\n"
              "- §eSteel Barrel§r: 6 steel plates + 2 steel ingots, a portable tank.\n"
              "- §eTank§r (assembly machine: 8 steel plates, 4 steel shells): a big one.\n"
              "- §eUniversal Fluid Ducts§r connect them; set the fluid type with a fluid identifier."),
    dict(id=BATTERIES, xy=(72, 108), requires=[POWER], name='Battery Socket', icon=hbm('machine_battery_socket'),
         tasks=[retrieve(hbm('machine_battery_socket'))],
         desc="§e2 steel plates + a red copper ingot§r. Put a battery in it to store power for the night - a "
              "Stirling engine runs only while the firebox burns."),
]

ARSENAL_LINE = [
    # --- Age I ---
    dict(id=STICKS, xy=(60, 24), requires=[EMPTY_POCKETS], name='Sticks and Stones', icon=pyro('crude_spear'),
         tasks=[retrieve(pyro('rock', count=8), pyro('crude_spear'), any_of=True)],
         desc="- §eRocks§r can be thrown - right-click.\n"
              "- The §eCrude Spear§r is thrown too and sticks in what it hits; go and pick it up.\n\n"
              "Enough for a rabbit, not for a zombie."),
    dict(id=FLINT_BONE, xy=(120, 24), requires=[EMPTY_POCKETS], name='Flint and Bone', icon=pyro('flint_spear'),
         tasks=[retrieve(pyro('flint_spear'), pyro('bone_spear'), pyro('flint_sword'), pyro('bone_sword'), any_of=True)],
         desc="Shards, sticks and twine on the Worktable: §eflint or bone spears and swords§r. The spear still "
              "throws and hits much harder than the crude one."),
    dict(id=WOODEN, xy=(60, 96), requires=[EMPTY_POCKETS], name='Wooden Weapons', icon=spartan('spear_wood'),
         tasks=[retrieve(spartan('spear_wood'), spartan('club_wood'), spartan('dagger_wood'), any_of=True)],
         desc="Spartan Weaponry: §ehandles§r (a stick + twine or string) and planks make clubs, daggers, spears "
              "and more. The §espear§r is the pick - it reaches further than a sword."),
    dict(id=STONE_WEAPONS, xy=(120, 96), requires=[EMPTY_POCKETS], name='Stone Weapons',
         icon=spartan('warhammer_stone'),
         tasks=[retrieve(spartan('spear_stone'), spartan('warhammer_stone'), spartan('longsword_stone'),
                         spartan('javelin_stone'), spartan('throwing_knife_stone'), any_of=True)],
         desc="Every Spartan weapon also comes in stone - cut from §emasonry bricks§r. Good picks:\n\n"
              "- §eSpear / Pike§r: reach.\n- §eWarhammer§r: heavy hits, good against armour.\n"
              "- §eJavelins and throwing knives§r: damage from a distance."),
    dict(id=BOWS, xy=(60, 168), requires=[EMPTY_POCKETS], name='Bows and Arrows', icon=spartan('longbow_wood'),
         tasks=[retrieve(item('minecraft:bow'), spartan('longbow_wood'), spartan('crossbow_wood'), any_of=True)],
         desc="- §eBow§r (Attack 2): twine works as a bowstring.\n"
              "- §eWooden longbow and crossbow§r (Spartan): more power, no skill needed.\n"
              "- §eFlint and bone arrows§r (Pyrotech) hit harder than plain ones.\n\n"
              "A bow keeps the horde at arm's length - the safest weapon you will have for a long time."),
    dict(id=IRON_WEAPONS, xy=(180, 96), requires=[EMPTY_POCKETS], name='Iron Weapons',
         icon=spartan('spear_iron'),
         tasks=[retrieve(spartan('spear_iron'), spartan('longsword_iron'), spartan('warhammer_iron'),
                         spartan('crossbow_iron'), spartan('longbow_iron'), any_of=True)],
         desc="With bloomery iron: the whole Spartan arsenal in iron, crossbows and longbows included. Needs "
              "§eAttack 3§r."),
    dict(id=PEPPERBOX, xy=(180, 24), requires=[EMPTY_POCKETS], name='Pepperbox', icon=hbm('gun_pepperbox'),
         tasks=[retrieve(hbm('gun_pepperbox'))],
         desc="The first gun you can make: §e2 iron ingots, a plank and a copper ingot§r. Attack 4.\n\n"
              "It fires black powder loads - paper and gunpowder with §ea ball, flint, an iron ball or gravel "
              "shot§r (6 per craft). Gunpowder comes from the ruins."),
    dict(id=HAND_CANNON, xy=(180, 168), requires=[EMPTY_POCKETS], name='Hand Cannon',
         icon=item('techguns:handcannon'),
         tasks=[retrieve(item('techguns:handcannon'))],
         desc="Techguns: a §estone barrel§r (6 stone), §eflint and steel§r and a §ewooden stock§r (3 logs). "
              "Attack 4.\n\n"
              "§eStone Bullets§r: 3 masonry bricks + gunpowder make 16. Slow to reload, loud, and it hurts."),
    # --- Age II ---
    dict(id=STEEL_WEAPONS, xy=(264, 96), requires=[EMPTY_POCKETS],
         name='Steel Weapons', icon=spartan('longsword_steel'),
         tasks=[retrieve(spartan('longsword_steel'), spartan('spear_steel'), spartan('warhammer_steel'),
                         spartan('crossbow_steel'), spartan('longbow_steel'), any_of=True)],
         desc="HBM steel counts as steel for Spartan Weaponry: the best melee weapons and bows of this age. Needs "
              "§eAttack 5§r."),
    dict(id=CROWBAR, xy=(264, 24), requires=[EMPTY_POCKETS], name='Crowbar',
         icon=item('techguns:crowbar'),
         tasks=[retrieve(item('techguns:crowbar'))],
         desc="Techguns: §ea steel nugget and 3 steel plates§r. Attack 4. A solid melee weapon - and the classic "
              "tool of every scavenger."),
    dict(id=REVOLVER_TG, xy=(264, 168), main=True, requires=[EMPTY_POCKETS], task_logic='OR',
         name='Revolver', icon=item('techguns:revolver'),
         tasks=[retrieve(item('techguns:revolver')), retrieve(item('techguns:sawedoff'))],
         desc="The first real guns are simple ones (Techguns), built from steel parts:\n\n"
              "- §eRevolver§r (Attack 5): an §eiron barrel§r (6 iron plates), §eiron mechanical parts§r (4 steel "
              "plates + flint), §eflint and steel§r and a §ewooden stock§r (3 logs). Fires §ePistol Rounds§r.\n"
              "- §eSawed-Off§r (Attack 5): 2 iron barrels, 2 flint and steel and a wooden stock. Fires §eShotgun "
              "Rounds§r - devastating up close.\n\n"
              "§eAmmo:§r Pistol Rounds - 7 copper nuggets + a lead ingot + gunpowder make 8; Shotgun Rounds - 5 copper "
              "nuggets + 3 lead nuggets + gunpowder make 5. Lead ore is found underground; gunpowder in the ruins.\n\n"
              "§7The Techguns pistol needs space-age parts - until then, only what you find.§r"),
    dict(id=AMMO_PRESS, xy=(324, 168), requires=[EMPTY_POCKETS], name='Ammo Press', icon=hbm('machine_ammo_press'),
         tasks=[retrieve(hbm('machine_ammo_press'))],
         desc="HBM guns load HBM cartridges. The §eAmmo Press§r (2 iron, a Pyrotech cog, 2 copper, 3 stone) puts a "
              "bullet, powder and a casing together - e.g. §e.357 Magnum (black powder)§r: a lead ingot, gunpowder "
              "and a casing make 16.\n\n"
              "Casings are pressed from gunmetal plates with a casing stamp."),
    dict(id=GUN_FOUNDRY, xy=(324, 24), requires=[EMPTY_POCKETS], name='Gun Parts',
         icon=hbm('part_receiver_light', 30),
         tasks=[retrieve(hbm('part_barrel_light', 30), hbm('part_receiver_light', 30), any_of=True)],
         desc="HBM guns are put together from parts in a crafting grid. §eBarrels, receivers and mechanisms§r are "
              "§ecast§r: molten steel from the crucible into a foundry mold with the right mold. Grips and stocks are "
              "simple: 3 or 4 planks.\n\n"
              "Mechanisms are made of §egunmetal§r:\n"
              "- §eZinc ore§r lies deep underground (below y 37); fire it in the pit kiln or stone kiln into zinc.\n"
              "- The §eRotary Furnace§r (steel anvil: stone bricks, firebricks, iron, copper plates) melts §e3 copper "
              "+ 1 zinc§r into gunmetal. It runs on §esteam§r - water boiled in a boiler on a firebox."),
    dict(id=REVOLVER, xy=(384, 24), requires=[EMPTY_POCKETS], name='Break-Action Revolver',
         icon=hbm('gun_light_revolver'),
         tasks=[retrieve(hbm('gun_light_revolver'))],
         desc="A light steel barrel and receiver, a gunmetal mechanism and a wooden grip. Attack 5. It fires "
              "§e.357 Magnum§r from the ammo press."),
    dict(id=LEVER_ACTION, xy=(384, 96), requires=[EMPTY_POCKETS], task_logic='OR', name='Lever Actions',
         icon=hbm('gun_henry'),
         tasks=[retrieve(hbm('gun_maresleg')), retrieve(hbm('gun_henry'))],
         desc="- §eLever Action Shotgun§r (Attack 6): 2 light steel barrels, a steel receiver, a gunmetal "
              "mechanism, a steel bolt and a wooden stock.\n"
              "- §eLever Action Rifle§r (Attack 8): 2 light steel barrels, a gunmetal receiver and mechanism, a "
              "gunmetal plate and a wooden stock.\n\n"
              "Check what each one loads with §eU§r on the gun."),
    dict(id=FLARE_GUN, xy=(384, 168), requires=[EMPTY_POCKETS], name='Flare Gun',
         icon=hbm('gun_flaregun'),
         tasks=[retrieve(hbm('gun_flaregun'))],
         desc="A heavy steel barrel, a light steel receiver, a gunmetal mechanism and a steel grip. Attack 4.\n\n"
              "Not much of a weapon - but a flare lights up the pitch-black night around a horde before it reaches "
              "your walls."),
    # --- Armour, Age I ---
    dict(id=CLOTH, xy=(60, 240), requires=[EMPTY_POCKETS], name='Cloth', icon=item('simpledifficulty:wool_chestplate'),
         tasks=[retrieve(item('simpledifficulty:wool_helmet'), item('simpledifficulty:wool_chestplate'),
                         item('simpledifficulty:wool_leggings'), item('simpledifficulty:wool_boots'), any_of=True)],
         desc="Wool in the armour shapes: §eCloth Hood, Jacket, Trousers and Boots§r.\n\n"
              "Barely stops a bite - but it keeps you warm. Deserts and wastelands freeze at night, and rain chills "
              "you; a full cloth set is what lets you stay out after dark."),
    dict(id=LEATHER_ARMOR, xy=(120, 240), requires=[EMPTY_POCKETS], name='Leather and Cotton',
         icon=item('minecraft:leather_chestplate'),
         tasks=[retrieve(item('minecraft:leather_helmet'), item('minecraft:leather_chestplate'),
                         item('minecraft:leather_leggings'), item('minecraft:leather_boots'), any_of=True)],
         desc="Leather armour, two ways:\n\n"
              "- §eLeather§r: cut leather into §esheets and straps§r with a hunter's knife (a tunic is 6 sheets + 2 "
              "straps).\n"
              "- §eWoven Cotton§r (HarvestCraft): 2 twine or string make one; 8 make a tunic. No tanning needed.\n\n"
              "§7Soak it with 8 slaked lime around it and the leather becomes fireproof. Leather repair kits keep it "
              "going.§r"),
    dict(id=IRON_ARMOR, xy=(180, 240), requires=[EMPTY_POCKETS], name='Iron Armour', icon=item('minecraft:iron_chestplate'),
         tasks=[retrieve(item('minecraft:iron_helmet'), item('minecraft:iron_chestplate'),
                         item('minecraft:iron_leggings'), item('minecraft:iron_boots'), any_of=True)],
         desc="Bloomery iron in the vanilla shapes - 24 ingots for a full set. The first armour that really turns "
              "claws and teeth. No skill needed."),
    dict(id=SHIELD, xy=(60, 312), requires=[EMPTY_POCKETS], name='Shield', icon=item('minecraft:shield'),
         tasks=[retrieve(item('minecraft:shield'))],
         desc="§e6 planks around an iron ingot§r. Defense 3.\n\n"
              "Hold right-click to block - a zombie's swing or an arrow stops at the shield. In a doorway with a "
              "spear in the other hand, it buys you a night."),
    # --- Armour, Age II ---
    dict(id=STEEL_ARMOR, xy=(264, 240), requires=[EMPTY_POCKETS], name='Steel Armour', icon=hbm('steel_plate'),
         tasks=[retrieve(hbm('steel_helmet'), hbm('steel_plate'), hbm('steel_legs'), hbm('steel_boots'), any_of=True)],
         desc="HBM steel in the vanilla shapes: §e5, 8, 7 and 4 steel ingots§r. Defense 5.\n\n"
              "Salvaged steel works as well as smelted."),
    dict(id=SOLDIER_GEAR, xy=(324, 240), requires=[EMPTY_POCKETS], name="Soldier's Gear",
         icon=item('techguns:t1_combat_chestplate'),
         tasks=[retrieve(item('techguns:t1_combat_chestplate'), item('techguns:t1_scout_chestplate'),
                         item('techguns:t1_miner_chestplate'), item('techguns:t1_combat_helmet'), any_of=True)],
         desc="Techguns' first armour sets, made of §eHeavy Cloth§r (4 wool + 1 leather make 3 - the ruins have "
              "it too):\n\n"
              "- §eSoldier's§r: heavy cloth with iron plates sewn in (a vest is 5 cloth + 3 iron ingots).\n"
              "- §eBandit§r: all cloth (a vest is 8).\n"
              "- §eMiner's§r: cloth and some iron (a jacket is 6 cloth + 2 iron ingots).\n\n"
              "Defense 5."),
    dict(id=HAZMAT, xy=(384, 240), requires=[EMPTY_POCKETS], name='Hazmat Suit', icon=hbm('hazmat_helmet'),
         tasks=[retrieve(hbm('hazmat_helmet'), hbm('hazmat_plate'), hbm('hazmat_legs'), hbm('hazmat_boots'), any_of=True)],
         desc="Not armour against teeth - armour against the §ewasteland§r: radiation, fallout and poison.\n\n"
              "§eHazmat Cloth§r comes from the assembly machine: 4 lead dust + 8 string make 4. Helmet 5 cloth with a "
              "glass pane and an iron plate, suit 8, trousers 7, boots 4. Add a gas mask for the air."),
    dict(id=RIOT_SHIELD, xy=(264, 312), requires=[EMPTY_POCKETS], name='Riot Shield', icon=item('techguns:riot_shield'),
         tasks=[retrieve(item('techguns:riot_shield'))],
         desc="Techguns: a §eshield§r, §e6 steel ingots§r and §e2 glass panes§r. Defense 10.\n\n"
              "Big enough to hide behind, with a window to see what is coming. A goal for when your Defense skill "
              "catches up."),
]

CHAPTERS = [
    dict(id=12, name='II. Scavenger - Path to Plastic', icon=hbm('ingot_polymer'), quests=PLASTIC_PATH,
         desc='Steel, the first electricity and the old machines - from the HBM anvil to oil and plastic.'),
    dict(id=13, name='Arsenal (Ages I-II)', icon=item('minecraft:iron_sword'), quests=ARSENAL_LINE,
         desc='The good weapons of the Primitive and Scavenger ages, and the way to them. Weapons are locked behind '
              'Attack levels (bought with experience in the skills tab): wood and stone need none, iron Attack 3, '
              'steel Attack 5, the first guns Attack 4 to 8.'),
]
