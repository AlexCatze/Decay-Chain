// Decay Chain: meat is exempt from Hunger Overhaul's food nerf.
// Hunger Overhaul divides every food's hunger by 4 (and its saturation with it). With Pyrotech carcasses, meat already
// costs a hunt, a knife and butchering - so raw and cooked meat (HarvestCraft's listAllmeatraw / listAllmeatcooked:
// vanilla, HarvestCraft and Lycanites meats) restore their original hunger and saturation again: a cooked steak is
// 8 hunger / 12.8 saturation like in vanilla. Restoring saturation alone was not enough - saturation is capped at the
// current hunger level, so a steak that fills one drumstick could never keep you full.
// The Spice of Life applies its diminishing returns later (per player), so eating only meat still fills less and less.
// Hunger Overhaul edits food values at HIGHEST priority; this listener runs later at the default priority.

import classes.DecayRules
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.OreDictionary
import squeek.applecore.api.food.FoodEvent
import squeek.applecore.api.food.FoodValues

MEAT_ORE_IDS = ['listAllmeatraw', 'listAllmeatcooked'].collect { OreDictionary.getOreID(it) } as Set
// Hunger Overhaul rewrites the vanilla items themselves, so their original values are not available at runtime
VANILLA_MEAT = [
    'minecraft:beef': [3, 0.3f], 'minecraft:cooked_beef': [8, 0.8f],
    'minecraft:porkchop': [3, 0.3f], 'minecraft:cooked_porkchop': [8, 0.8f],
    'minecraft:chicken': [2, 0.3f], 'minecraft:cooked_chicken': [6, 0.6f],
    'minecraft:mutton': [2, 0.3f], 'minecraft:cooked_mutton': [6, 0.8f],
    'minecraft:rabbit': [3, 0.3f], 'minecraft:cooked_rabbit': [5, 0.6f],
]
IS_MEAT = [:]   // "item@meta" -> Boolean; food values are queried every frame for tooltips and the HUD

eventManager.listen { FoodEvent.GetFoodValues event ->
    DecayRules.guard('meat_saturation') {
        def food = event.food
        if (food == null || food.isEmpty()) return
        String id = ForgeRegistries.ITEMS.getKey(food.getItem()).toString()
        String key = id + '@' + food.getMetadata()
        Boolean meat = IS_MEAT[key]
        if (meat == null) {
            meat = OreDictionary.getOreIDs(food).any { MEAT_ORE_IDS.contains(it) }
            IS_MEAT[key] = meat
        }
        if (!meat) return
        def original = VANILLA_MEAT[id]
        int origHunger = original ? original[0] : event.unmodifiedFoodValues.hunger
        float origSaturation = original ? original[1] : event.unmodifiedFoodValues.saturationModifier
        event.foodValues = new FoodValues(origHunger, origSaturation)
    }
}
