// Decay Chain: Primitive age - Pyrotech flint/bone/stone tools -> iron (bloomery). Copper is an HBM crafting metal.

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

// Lignite must not be a shortcut to permanent vanilla torches: drop HBM's lignite + stick -> 3 torches recipe.
// Mined with a pre-iron pickaxe, lignite gives Pyrotech coal pieces instead (config/dropt/decaychain_lignite.json) -
// those make stone torches. Coal (iron pickaxe and up) still makes vanilla torches.
recipes.removeByRecipeName("hbm:torch");
