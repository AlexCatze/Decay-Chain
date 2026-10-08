// Decay Chain: OpenComputers tier 1-2 is Industrial (plastic/silicon); tier 3 is Space (vacuum-soldered circuits).
// Every OC component flows through transistors, microchips and printed circuit boards, so gating those gates OC.

val transistor = <opencomputers:material:6>;

// Transistors need HBM steel and silicon
recipes.remove(transistor);
recipes.addShaped("decay_oc_transistor", transistor * 8, [
    [<ore:plateSteel>, <ore:plateSteel>, <ore:plateSteel>],
    [<ore:nuggetGold>, <ore:itemSilicon>, <ore:nuggetGold>],
    [null, <minecraft:redstone>, null]]);

// Printed circuit boards come from HBM printed circuit boards instead of gold/clay/dye
recipes.remove(<opencomputers:material:2>);
recipes.addShapeless("decay_oc_pcb", <opencomputers:material:4>, [<hbm:circuit:3>]);

// Tier 3 microchips need a vacuum-soldered circuit (Space age)
recipes.remove(<opencomputers:material:9>);
recipes.addShaped("decay_oc_chip3", <opencomputers:material:9> * 2, [
    [<ore:chipDiamond>, <ore:chipDiamond>, <ore:chipDiamond>],
    [<hbmspace:circuit:4>, transistor, <minecraft:redstone>],
    [<ore:chipDiamond>, <ore:chipDiamond>, <ore:chipDiamond>]]);
