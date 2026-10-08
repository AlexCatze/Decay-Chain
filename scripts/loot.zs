// Decay Chain: scavenger economy. No diamonds, emeralds, golden apples or enchanted books in early loot;
// instead scrap, canned food, water, medicine, simple ammo and gun parts.
// Lost Cities chooses which table a city chest uses in config/lostcities/userassets.json (condition "chestloot").

import loottweaker.LootTweaker;
import loottweaker.LootPool;
import loottweaker.Functions;

val dirtyWater = <minecraft:potion>.withTag({Potion: "minecraft:water"});

// Common scavenger loot: city ruins, vanilla dungeons
function addScavenge(pool as LootPool, dirtyWater as crafttweaker.item.IItemStack) {
    pool.addItemEntry(<hbm:scrap>, 20, 0, [Functions.setCount(1, 6)], [], "decay_scrap");
    pool.addItemEntry(<minecraft:gunpowder>, 6, 0, [Functions.setCount(1, 4)], [], "decay_gunpowder");
    pool.addItemEntry(<minecraft:iron_ingot>, 6, 0, [Functions.setCount(1, 3)], [], "decay_iron");
    pool.addItemEntry(<minecraft:string>, 4, 0, [Functions.setCount(1, 4)], [], "decay_string");
    pool.addItemEntry(<minecraft:leather>, 3, 0, [Functions.setCount(1, 2)], [], "decay_leather");
    pool.addItemEntry(<techguns:itemshared:60>, 3, 0, [Functions.setCount(1, 2)], [], "decay_heavy_cloth");
    // Food and water
    for meta in [0, 1, 2, 3, 13, 14, 16, 20] as int[] {
        pool.addItemEntry(<hbm:canned_conserve>.definition.makeStack(meta), 4, 0, [Functions.setCount(1, 2)], [], "decay_canned_" ~ meta);
    }
    pool.addItemEntry(<hbm:bottle_nuka>, 6, 0, [], [], "decay_nuka");
    pool.addItemEntry(<hbm:bottle_cherry>, 2, 0, [], [], "decay_nuka_cherry");
    pool.addItemEntry(<hbm:cap_nuka>, 6, 0, [Functions.setCount(1, 8)], [], "decay_caps");
    pool.addItemEntry(dirtyWater, 8, 0, [], [], "decay_dirty_water");
    pool.addItemEntry(<decaychain:toxic_water_bottle>, 3, 0, [], [], "decay_toxic_water");
    // Medicine
    pool.addItemEntry(<firstaid:plaster>, 8, 0, [Functions.setCount(1, 3)], [], "decay_plaster");
    pool.addItemEntry(<firstaid:bandage>, 6, 0, [Functions.setCount(1, 2)], [], "decay_bandage");
    pool.addItemEntry(<hbm:pill_iodine>, 3, 0, [], [], "decay_iodine");
    pool.addItemEntry(<hbm:radx>, 2, 0, [], [], "decay_radx");
    pool.addItemEntry(<hbm:radaway>, 2, 0, [], [], "decay_radaway");
    // Simple ammo and gun parts
    pool.addItemEntry(<techguns:itemshared:0>, 4, 0, [Functions.setCount(4, 12)], [], "decay_stone_bullets");
    pool.addItemEntry(<techguns:itemshared:1>, 6, 0, [Functions.setCount(2, 8)], [], "decay_pistol_rounds");
    pool.addItemEntry(<techguns:itemshared:2>, 4, 0, [Functions.setCount(2, 6)], [], "decay_shotgun_rounds");
    pool.addItemEntry(<techguns:itemshared:57>, 3, 0, [], [], "decay_mech_parts");
    pool.addItemEntry(<techguns:itemshared:33>, 2, 0, [], [], "decay_iron_receiver");
    pool.addItemEntry(<techguns:itemshared:38>, 2, 0, [], [], "decay_iron_barrel");
    pool.addItemEntry(<hbm:cigarette>, 2, 0, [], [], "decay_cigarette");
    pool.addEmptyEntry(10, "decay_empty");
}

// --- Lost Cities: ordinary city chests ---
val cityChest = LootTweaker.getTable("lostcities:chests/lostcitychest");
cityChest.removePool("lostcities:lostcitychest");
addScavenge(cityChest.addPool("decay_scavenge", 2, 5, 0, 0), dirtyWater);

// --- Lost Cities: rail dungeons (guarded by spawners) - better gear ---
val railChest = LootTweaker.getTable("lostcities:chests/raildungeonchest");
railChest.removePool("lostcities:raildungeonchest");
addScavenge(railChest.addPool("decay_scavenge", 2, 4, 0, 0), dirtyWater);
val railBonus = railChest.addPool("decay_cache", 1, 3, 0, 0);
railBonus.addItemEntry(<hbm:ingot_steel>, 8, 0, [Functions.setCount(1, 3)], [], "decay_steel");
railBonus.addItemEntry(<hbm:plate_steel>, 6, 0, [Functions.setCount(1, 2)], [], "decay_steel_plate");
railBonus.addItemEntry(<techguns:itemshared:3>, 6, 0, [Functions.setCount(4, 12)], [], "decay_rifle_rounds");
railBonus.addItemEntry(<techguns:itemshared:1>, 6, 0, [Functions.setCount(8, 16)], [], "decay_pistol_rounds_bulk");
railBonus.addItemEntry(<firstaid:morphine>, 3, 0, [], [], "decay_morphine");
railBonus.addItemEntry(<hbm:med_bag>, 1, 0, [], [], "decay_med_bag");
railBonus.addItemEntry(<minecraft:gold_ingot>, 4, 0, [Functions.setCount(1, 2)], [], "decay_gold");
railBonus.addItemEntry(<hbm:motor>, 2, 0, [], [], "decay_motor");
// First energy: looted diesel generators and fuel
railBonus.addItemEntry(<hbm:machine_diesel>, 2, 0, [], [], "decay_diesel_generator");
railBonus.addItemEntry(<hbm:canister_fuel:20>, 5, 0, [Functions.setCount(1, 3)], [], "decay_diesel_fuel");

// --- Vanilla dungeons (spawner rooms) ---
val dungeon = LootTweaker.getTable("minecraft:chests/simple_dungeon");
dungeon.clear();
addScavenge(dungeon.addPool("decay_scavenge", 3, 5, 0, 0), dirtyWater);

// --- Abandoned mineshafts (also used by YUNG's Better Mineshafts) ---
val mineshaft = LootTweaker.getTable("minecraft:chests/abandoned_mineshaft");
val mineMain = mineshaft.getPool("main");
mineMain.clearEntries();
mineMain.addItemEntry(<minecraft:iron_pickaxe>, 5, 0, [], [], "decay_pickaxe");
mineMain.addItemEntry(<hbm:canned_conserve>.definition.makeStack(0), 10, 0, [Functions.setCount(1, 2)], [], "decay_canned_beef");
mineMain.addItemEntry(<firstaid:plaster>, 10, 0, [Functions.setCount(1, 3)], [], "decay_plaster");
mineMain.addItemEntry(<minecraft:torch>, 15, 0, [Functions.setCount(4, 12)], [], "decay_torches");
mineMain.addItemEntry(<hbm:scrap>, 15, 0, [Functions.setCount(1, 4)], [], "decay_scrap");
mineMain.addEmptyEntry(10, "decay_empty");
mineshaft.getPool("pool1").removeEntry("minecraft:diamond");
