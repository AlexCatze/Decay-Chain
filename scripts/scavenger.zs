// Decay Chain: Scavenger age - steel, first energy, farming.

import mods.jei.JEI;
import mods.pyrotech.GraniteAnvil;
import mods.pyrotech.PitKiln;
import mods.pyrotech.StoneKiln;

// Farming becomes a thing with steel: only HBM steel-and-better hoes can till.
for hoe in [<minecraft:wooden_hoe>, <minecraft:stone_hoe>, <minecraft:iron_hoe>, <minecraft:golden_hoe>, <minecraft:diamond_hoe>,
            <pyrotech:crude_hoe>, <pyrotech:flint_hoe>, <pyrotech:flint_hoe_durable>, <pyrotech:bone_hoe>, <pyrotech:bone_hoe_durable>,
            <pyrotech:obsidian_hoe>, <pyrotech:quartz_hoe>, <pyrotech:redstone_hoe>] as crafttweaker.item.IItemStack[] {
    JEI.removeAndHide(hoe);
}

// First energy is expensive: the wood burner needs more steel and a motor, and burns in a Pyrotech stone kiln instead of
// a vanilla furnace (furnace core). Looted diesel generators are the better start.
recipes.remove(<hbm:machine_wood_burner>);
recipes.addShaped("decay_wood_burner", <hbm:machine_wood_burner>, [
    [<ore:plateSteel>, <ore:plateSteel>, <ore:plateSteel>],
    [<hbm:coil_copper>, <pyrotech:stone_kiln>, <hbm:coil_copper>],
    [<ore:plateSteel>, <hbm:motor>, <ore:plateSteel>]]);

// Firebricks from the primitive kilns: fireclay is clay tempered with Pyrotech's broken pottery (grog) instead of
// aluminium (off-world here) or limestone, and it fires in the pit kiln or stone kiln - the vanilla furnace needs a
// furnace core that is out of reach this early. A cracked firing leaves pottery fragments for the next batch.
recipes.remove(<hbm:ball_fireclay>);
recipes.addShapeless("decay_fireclay", <hbm:ball_fireclay> * 4,
    [<minecraft:clay_ball>, <minecraft:clay_ball>, <minecraft:clay_ball>, <pyrotech:material:6> | <pyrotech:material:7>]);
PitKiln.addRecipe("decay_firebrick", <hbm:ingot_firebrick>, <hbm:ball_fireclay>, 6000, 0.2, [<pyrotech:material:6>]);
StoneKiln.addRecipe("decay_firebrick", <hbm:ingot_firebrick>, <hbm:ball_fireclay>, 3600, 0.05, [<pyrotech:material:6>]);
// A steady grog supply: hammer old clay bricks into pottery fragments on the granite anvil (and the better anvils).
GraniteAnvil.addRecipe("decay_pottery_fragments_from_brick", <pyrotech:material:6>, <minecraft:brick>, 2, "hammer", true);
GraniteAnvil.addRecipe("decay_pottery_fragments_from_brick_block", <pyrotech:material:6> * 4, <minecraft:brick_block>, 6, "hammer", true);

// Burner press without a furnace (furnace core) or a piston (redstone): an iron frame and ram, a Pyrotech cog for the
// drive and a masonry firebox underneath.
recipes.remove(<hbm:machine_press>);
recipes.addShaped("decay_burner_press", <hbm:machine_press>, [
    [<ore:ingotIron>, <minecraft:iron_block>, <ore:ingotIron>],
    [<ore:ingotIron>, <pyrotech:cog_stone> | <pyrotech:cog_flint> | <pyrotech:cog_bone> | <pyrotech:cog_iron>, <ore:ingotIron>],
    [<pyrotech:masonry_brick_block>, <pyrotech:masonry_brick_block>, <pyrotech:masonry_brick_block>]]);

// No furnace or piston gates in this age (the vanilla furnace needs a furnace core, the piston redstone):
// - HBM's iron furnace sits on a Pyrotech stone kiln (its stone bricks are already refractory bricks, see pyrotech.zs);
// - the firebox is an anvil recipe: config/hbmRecipes/hbmAnvil.json (stone kiln instead of furnace);
// - the ammo press and the compacting drawer get a Pyrotech cog instead of a piston.
val cog = <pyrotech:cog_stone> | <pyrotech:cog_flint> | <pyrotech:cog_bone> | <pyrotech:cog_iron>;
recipes.replaceAllOccurences(<minecraft:furnace>, <pyrotech:stone_kiln>, <hbm:furnace_iron>);
recipes.replaceAllOccurences(<minecraft:piston>, cog, <hbm:machine_ammo_press>);
recipes.replaceAllOccurences(<minecraft:piston>, cog, <storagedrawers:compdrawers>);

// Zinc ore (NTM Space generates it on Earth in place of HBM's gneiss lithium) fires into zinc in the primitive kilns,
// like copper ore - zinc + copper make gunmetal in the rotary furnace.
PitKiln.addRecipe("decay_zinc_ore", <hbmspace:ingot_zinc>, <hbmspace:ore_zinc:*>, 8000, 0.25, [<minecraft:gravel>]);
StoneKiln.addRecipe("decay_zinc_ore", <hbmspace:ingot_zinc>, <hbmspace:ore_zinc:*>, 4800, 0.1, [<minecraft:gravel>]);
