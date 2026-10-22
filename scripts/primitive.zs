// Decay Chain: Primitive age - Pyrotech flint/bone/stone tools -> iron (bloomery). Copper is an HBM crafting metal.

import mods.jei.JEI;
import mods.pyrotech.PitKiln;
import mods.pyrotech.StoneKiln;

// Copper ore can be fired in the earliest Pyrotech kilns (copper wire/coils for HBM's first machines)
PitKiln.addRecipe("decay_copper_ore", <hbm:ingot_copper>, <hbm:ore_copper>, 8000, 0.25, [<minecraft:gravel>]);
StoneKiln.addRecipe("decay_copper_ore", <hbm:ingot_copper>, <hbm:ore_copper>, 4800, 0.1, [<minecraft:gravel>]);

// BoP leaves are not in the treeLeaves ore dictionary, so Pyrotech's "sticks from leaves" skipped them
for i in 0 to 7 {
    val leaves = itemUtils.getItem("biomesoplenty:leaves_" ~ i, 32767);
    if (!isNull(leaves)) { <ore:treeLeaves>.add(leaves); }
}

// Glassential: keep the Glass Cutter and plain decorative glass; the phasing glasses are too magical for this world
for g in ["glass_ghostly", "glass_ethereal", "glass_ethereal_reverse"] as string[] {
    val glass = itemUtils.getItem("glassential:" ~ g);
    if (!isNull(glass)) { mods.jei.JEI.removeAndHide(glass); }
}

// Permanent vanilla torches are made from coke only (HBM's coke + stick -> 8 torches; coke comes from the Combination
// Oven). Coal drops from zombies, so Pyrotech's coal + stick recipe goes, and so does HBM's lignite + stick. Coal
// pieces (from lignite mined with a pre-iron pickaxe, config/dropt/decaychain_lignite.json) make stone torches.
recipes.removeByRecipeName("hbm:torch");
recipes.removeByRecipeName("pyrotech:torch_vanilla_coal");

// Wearable Backpacks: two Pyrotech leather straps and a leather sheet instead of 7 leather, twine (or string) in the
// corners, and iron instead of gold - a backpack is the reward for the first tanned leather and the first iron, not for
// a gold find. Durable straps and sheets work too.
val leatherStrap = <pyrotech:material:39> | <pyrotech:material:43>;
val leatherSheet = <pyrotech:material:38> | <pyrotech:material:42>;
recipes.removeByRecipeName("wearablebackpacks:backpack");
recipes.addShaped("decay_backpack", <wearablebackpacks:backpack>, [
    [<ore:twine>, <ore:ingotIron>, <ore:twine>],
    [leatherStrap, <minecraft:wool:*>, leatherStrap],
    [<ore:twine>, leatherSheet, <ore:twine>]]);

// Pam's HarvestCraft hardened leather armour is removed: leather armour comes from tanned Pyrotech leather only.
for item in [<harvestcraft:hardenedleatheritem>, <harvestcraft:hardenedleatherhelmitem>, <harvestcraft:hardenedleatherchestitem>,
             <harvestcraft:hardenedleatherleggingsitem>, <harvestcraft:hardenedleatherbootsitem>] as crafttweaker.item.IItemStack[] {
    recipes.remove(item);
    JEI.removeAndHide(item);
}

// Mechanical bellows without a piston (redstone is not a Primitive-age material): an iron ingot for the drive, and a
// heavy cloth (Techguns) under the bellows as the air bag.
recipes.remove(<pyrotech:mechanical_bellows>);
recipes.addShaped("decay_mechanical_bellows", <pyrotech:mechanical_bellows>, [
    [<pyrotech:material:23>, <ore:ingotIron>, <pyrotech:material:23>],
    [<ore:stickStone>, <pyrotech:bellows>, <ore:stickStone>],
    [<pyrotech:planks_tarred>, <techguns:itemshared:60>, <pyrotech:planks_tarred>]]);

// HBM iron anvil: an iron ingot in the middle instead of an iron block - 7 ingots instead of 15 for the first anvil.
recipes.replaceAllOccurences(<ore:blockIron>, <ore:ingotIron>, <hbm:anvil_iron>);
