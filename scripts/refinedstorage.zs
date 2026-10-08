// Decay Chain: Refined Storage is Industrial (plastic age). Basic storage needs the integrated circuit board,
// autocrafting/wireless need the military circuit (late Industrial - "autocraft of space-grade components").

import mods.jei.JEI;

val steelPlate = <ore:plateSteel>;
val qei = <refinedstorage:quartz_enriched_iron>;
val silicon = <ore:itemSilicon>;

// Silicon must come from HBM processing, not from smelting quartz
furnace.remove(<refinedstorage:silicon>);
JEI.removeAndHide(<refinedstorage:silicon>);

// Quartz-enriched iron: steel plates instead of iron ingots
recipes.remove(qei);
recipes.addShaped("decay_rs_qei", qei * 4, [
    [steelPlate, steelPlate],
    [steelPlate, <ore:gemQuartz>]]);
recipes.addShapeless("decay_rs_qei_from_block", qei * 9, [<refinedstorage:quartz_enriched_iron_block>]);

// Machine casing: built around an integrated circuit board
recipes.remove(<refinedstorage:machine_casing>);
recipes.addShaped("decay_rs_machine_casing", <refinedstorage:machine_casing>, [
    [qei, qei, qei],
    [qei, <hbm:circuit:8>, qei],
    [qei, qei, qei]]);

// Raw processors
recipes.remove(<refinedstorage:processor>);
recipes.remove(<refinedstorage:processor:1>);
recipes.remove(<refinedstorage:processor:2>);
recipes.addShapeless("decay_rs_raw_basic_processor", <refinedstorage:processor>,
    [<refinedstorage:processor_binding>, steelPlate, silicon, <hbm:circuit:5>]);
recipes.addShapeless("decay_rs_raw_improved_processor", <refinedstorage:processor:1>,
    [<refinedstorage:processor_binding>, <ore:plateGold>, silicon, <hbm:circuit:8>]);
recipes.addShapeless("decay_rs_raw_advanced_processor", <refinedstorage:processor:2>,
    [<refinedstorage:processor_binding>, <ore:gemDiamond>, silicon, <hbm:circuit:9>]);

// Controller: improved processor (T3) instead of advanced (T4)
recipes.remove(<refinedstorage:controller>);
recipes.addShaped("decay_rs_controller", <refinedstorage:controller>, [
    [qei, <refinedstorage:processor:4>, qei],
    [silicon, <refinedstorage:machine_casing>, silicon],
    [qei, <hbm:circuit:8>, qei]]);
