// Decay Chain: Storage Drawers are early storage (T1). Upgrades and the controller climb through HBM metals.

val stick = <ore:stickWood>;
val template = <storagedrawers:upgrade_template>;

// Storage upgrades: x2 iron plate, x4 steel plate, x8 titanium plate, x16 integrated circuit (T3), x32 desh (T4)
val upgrades = {
    0: <ore:plateIron>,
    1: <ore:plateSteel>,
    2: <ore:plateTitanium>,
    3: <hbm:circuit:8>,
    4: <ore:ingotWorkersAlloy>
} as crafttweaker.item.IIngredient[int];

for meta, mat in upgrades {
    val upgrade = <storagedrawers:upgrade_storage>.definition.makeStack(meta);
    recipes.remove(upgrade);
    recipes.addShaped("decay_sd_upgrade_storage_" ~ meta, upgrade, [
        [stick, stick, stick],
        [mat, template, mat],
        [stick, stick, stick]]);
}

// Drawer controller: HBM steel and a motor instead of a diamond
recipes.remove(<storagedrawers:controller>);
recipes.addShaped("decay_sd_controller", <storagedrawers:controller>, [
    [<ore:plateSteel>, <ore:plateSteel>, <ore:plateSteel>],
    [<minecraft:comparator>, <ore:drawerBasic>, <minecraft:comparator>],
    [<ore:plateSteel>, <hbm:motor>, <ore:plateSteel>]]);
