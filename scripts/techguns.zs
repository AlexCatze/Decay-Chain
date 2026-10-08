// Decay Chain: Techguns rides on HBM's tech tree.
// - Techguns circuits are replaced by HBM circuits: integrated board (T3) = circuitBasic, military board (T4) = circuitElite.
// - Metals that HBM also has are unified to HBM (Techguns ore gen is off, see config/techguns.cfg).

import mods.jei.JEI;
import crafttweaker.item.IItemStack;

<ore:circuitBasic>.add(<hbm:circuit:8>);
<ore:circuitElite>.add(<hbm:circuit:9>);
JEI.removeAndHide(<techguns:itemshared:65>);
JEI.removeAndHide(<techguns:itemshared:66>);

// Techguns ingot/plate/nugget -> HBM equivalent; Techguns copies are hidden
val duplicates = {
    79: <hbm:ingot_copper>,
    82: <hbm:ingot_lead>,
    83: <hbm:ingot_steel>,
    85: <hbm:ingot_titanium>,
    46: <hbm:plate_iron>,
    47: <hbm:plate_copper>,
    50: <hbm:plate_steel>,
    52: <hbm:plate_lead>,
    54: <hbm:plate_titanium>,
    87: <hbm:nugget_lead>
} as IItemStack[int];

for meta, hbmItem in duplicates {
    val tgItem = <techguns:itemshared>.definition.makeStack(meta);
    recipes.addShapeless("decay_tg_unify_" ~ meta, hbmItem, [tgItem]);
    JEI.hide(tgItem);
}

// The Techguns blast furnace would bypass HBM's steel progression
JEI.removeAndHide(<techguns:simplemachine:11>);
