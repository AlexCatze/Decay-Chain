// Decay Chain: cybernetics - surgery chamber is Industrial, the robosurgeon needs vacuum-soldered circuits (Space).
// Implants themselves come from CyberZombies and blueprints (see config/cyberware.cfg).

val steel = <ore:plateSteel>;

recipes.remove(<cyberware:surgery_chamber>);
recipes.addShaped("decay_cw_surgery_chamber", <cyberware:surgery_chamber>, [
    [steel, steel, steel],
    [steel, <hbm:circuit:8>, steel],
    [steel, <minecraft:iron_door>, steel]]);

// Robosurgeon recipe is enabled in config/cyberware.cfg and replaced here
recipes.remove(<cyberware:surgery>);
recipes.addShaped("decay_cw_robosurgeon", <cyberware:surgery>, [
    [steel, <hbmspace:circuit:4>, steel],
    [<hbm:motor>, <ore:ingotDuraSteel>, <hbm:motor>],
    [steel, <hbm:circuit:13>, steel]]);

recipes.remove(<cyberware:engineering_table>);
recipes.addShaped("decay_cw_engineering_table", <cyberware:engineering_table>, [
    [null, <minecraft:piston>, steel],
    [steel, steel, steel],
    [steel, <hbm:circuit:8>, steel]]);

recipes.remove(<cyberware:charger>);
recipes.addShaped("decay_cw_charger", <cyberware:charger>, [
    [steel, <minecraft:iron_bars>, steel],
    [steel, <ore:blockRedstone>, steel],
    [steel, <hbm:circuit:8>, steel]]);
