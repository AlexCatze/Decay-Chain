// Decay Chain: Scavenger age - steel, first energy, farming.

import mods.jei.JEI;

// Farming becomes a thing with steel: only HBM steel-and-better hoes can till.
for hoe in [<minecraft:wooden_hoe>, <minecraft:stone_hoe>, <minecraft:iron_hoe>, <minecraft:golden_hoe>, <minecraft:diamond_hoe>,
            <pyrotech:crude_hoe>, <pyrotech:flint_hoe>, <pyrotech:flint_hoe_durable>, <pyrotech:bone_hoe>, <pyrotech:bone_hoe_durable>,
            <pyrotech:obsidian_hoe>, <pyrotech:quartz_hoe>, <pyrotech:redstone_hoe>] as crafttweaker.item.IItemStack[] {
    JEI.removeAndHide(hoe);
}

// First energy is expensive: the wood burner needs more steel and a motor. Looted diesel generators are the better start.
recipes.remove(<hbm:machine_wood_burner>);
recipes.addShaped("decay_wood_burner", <hbm:machine_wood_burner>, [
    [<ore:plateSteel>, <ore:plateSteel>, <ore:plateSteel>],
    [<hbm:coil_copper>, <minecraft:furnace>, <hbm:coil_copper>],
    [<ore:plateSteel>, <hbm:motor>, <ore:plateSteel>]]);
