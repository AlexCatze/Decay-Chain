// Decay Chain: Pyrotech is the early-game (T0) backbone.

import mods.pyrotech.Campfire;
import mods.pyrotech.StoneOven;
import mods.pyrotech.BrickOven;
import mods.jei.JEI;

// Boiling dirty water (vanilla water bottle) makes it safe to drink.
val dirtyWater = <minecraft:potion>.withTag({Potion: "minecraft:water"});
Campfire.addRecipe("decay_boil_water", <simpledifficulty:purified_water_bottle>, dirtyWater, 1200);
StoneOven.addRecipe("decay_boil_water", <simpledifficulty:purified_water_bottle>, dirtyWater);
BrickOven.addRecipe("decay_boil_water", <simpledifficulty:purified_water_bottle>, dirtyWater);

// One campfire system: Pyrotech's. Canteens are purified with the Simple Difficulty charcoal filter.
JEI.removeAndHide(<simpledifficulty:campfire>);
JEI.removeAndHide(<simpledifficulty:spit>);

// HBM sits on top of Pyrotech: the vanilla furnace already needs a Pyrotech furnace core,
// and HBM's iron furnace is built on Pyrotech refractory bricks.
recipes.replaceAllOccurences(<minecraft:stonebrick:*>, <pyrotech:refractory_brick_block>, <hbm:furnace_iron>);
