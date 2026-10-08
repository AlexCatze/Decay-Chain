// Decay Chain: firearms follow the five ages by recipe (Reskillable adds the skill gate on top).
//   Scavenger  - steel parts (pistols, revolvers, shotguns)
//   Industrial - plastic (HBM polymer) stocks and grips (rifles, automatic rifles, SMGs)
//   Space      - stainless steel (nickel is off-world only) and vacuum-soldered circuits (minigun, auto-shotgun)
//   Nuclear    - ferrouranium (uranium is Mars-only) for energy weapons
// HBM guns already climb their own material ladder (steel -> gunmetal -> weapon steel -> desh -> saturnite/alloys).

import mods.jei.JEI;

// --- Techguns: Scavenger parts use HBM steel ---
for part in [<techguns:itemshared:33>, <techguns:itemshared:38>, <techguns:itemshared:57>, <techguns:itemshared:68>, <techguns:itemshared:70>] as crafttweaker.item.IItemStack[] {
    recipes.replaceAllOccurences(<ore:ingotIron>, <ore:plateSteel>, part);
}

// --- Techguns: plastic means HBM polymer (Industrial) ---
<ore:sheetPlastic>.remove(<techguns:itemshared:55>);
<ore:sheetPlastic>.add(<hbm:plate_polymer>);
JEI.hide(<techguns:itemshared:55>);

// --- Techguns: obsidian steel is a Space alloy (its blast furnace is removed in techguns.zs) ---
recipes.addShapeless("decay_tg_obsidian_steel", <techguns:itemshared:84> * 2,
    [<ore:ingotSteel>, <ore:ingotSteel>, <minecraft:obsidian>, <ore:ingotStainlessSteel>]);

// --- Techguns: energy weapon components need ferrouranium (Nuclear) ---
recipes.replaceAllOccurences(<ore:ingotGold>, <ore:ingotFerrouranium>, <techguns:itemshared:41>);   // laser barrel
recipes.replaceAllOccurences(<ore:plateLead>, <ore:ingotFerrouranium>, <techguns:itemshared:131>);  // plasma generator
recipes.replaceAllOccurences(<ore:plateTitanium>, <ore:ingotFerrouranium>, <techguns:itemshared:128>); // gauss barrel

// --- HBM: Space-age heavy weapons ---
recipes.addShaped("decay_hbm_autoshotgun_space", <hbm:gun_autoshotgun>, [
    [<ore:barrelHeavyAnyResistantAlloy>, <ore:receiverHeavyAnyResistantAlloy>, <ore:gunMechanismWeaponSteel>],
    [<ore:gripAnyPlastic>, <hbmspace:circuit:4>, <ore:gripAnyPlastic>]]);
recipes.replaceAllOccurences(<ore:gripAnyPlastic>, <hbmspace:circuit:4>, <hbm:gun_minigun>);

// --- HBM: laser weapons need top-tier vacuum circuits ---
for laser in [<hbm:gun_lasrifle>, <hbm:gun_tau>] as crafttweaker.item.IItemStack[] {
    recipes.replaceAllOccurences(<hbm:circuit:11>, <hbmspace:circuit:6>, laser);
}
