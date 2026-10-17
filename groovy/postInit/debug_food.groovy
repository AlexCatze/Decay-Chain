// Decay Chain: TEMPORARY food diagnostics (remove once the meat hunger question is settled).
// Logs every meal to logs/groovy.log: the food, the values the game applied (after Hunger Overhaul, the meat boost and
// The Spice of Life) and the hunger actually added (capped by the full bar).

import classes.DecayRules
import net.minecraftforge.fml.common.registry.ForgeRegistries
import squeek.applecore.api.food.FoodEvent
import squeek.applecore.api.food.FoodValues

eventManager.listen { FoodEvent.FoodEaten event ->
    DecayRules.guard('debug_food') {
        def player = event.player
        if (player.world.isRemote) return
        def food = event.food
        def item = FoodValues.get(food)
        def stats = player.getFoodStats()
        log.info("FOOD ${ForgeRegistries.ITEMS.getKey(food.getItem())}@${food.getMetadata()}: item=${item.hunger}/${item.saturationModifier} " +
                 "applied=${event.foodValues.hunger}/${event.foodValues.saturationModifier} added=${event.hungerAdded} " +
                 "food now=${stats.getFoodLevel()} saturation now=${stats.getSaturationLevel()}")
    }
}
