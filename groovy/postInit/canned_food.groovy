// Decay Chain: HBM cans are exempt from Hunger Overhaul's food nerf.
// Cans are only found as loot, never made - but Hunger Overhaul divides every food's hunger by 4, so most cans filled a
// single drumstick. Each kind of can gets its own value here instead: 4 to 8 hunger, with real food filling more and
// keeping you full longer than the fuels and the jokes. Saturation added = hunger x modifier x 2.
// Canned Recursion stays small on purpose: nine times in ten it hands you another can.
// The Spice of Life still applies its diminishing returns per player afterwards (see meat_saturation.groovy).

import classes.DecayRules
import net.minecraftforge.fml.common.registry.ForgeRegistries
import squeek.applecore.api.food.FoodEvent
import squeek.applecore.api.food.FoodValues

// metadata (HBM ItemConserve.EnumFoodType order) -> [hunger, saturation modifier]
CANS = [
    0: [8, 0.75f],    // beef
    1: [6, 0.75f],    // tuna
    2: [6, 0.6f],     // mystery meat
    3: [5, 0.6f],     // pashtet
    4: [4, 0.6f],     // melted cheese
    5: [6, 0.6f],     // stallion milk
    6: [4, 0.6f],     // evaporated milk
    7: [6, 0.6f],     // ass
    8: [8, 0.75f],    // pepperoni pizza
    9: [4, 0.6f],     // astronaut food tube
    10: [5, 0.6f],    // tomato soup
    11: [4, 0.5f],    // asbestos
    12: [4, 0.5f],    // black hole
    13: [6, 0.75f],   // hotdogs
    14: [4, 0.5f],    // leftovers
    15: [4, 0.6f],    // yogurt
    16: [5, 0.6f],    // "mushroom" stew
    17: [6, 0.6f],    // chinese food
    18: [4, 0.5f],    // engine oil
    19: [5, 0.6f],    // fist
    20: [8, 0.75f],   // spam
    21: [8, 0.75f],   // fried chicken
    22: [4, 0.5f],    // napalm
    23: [4, 0.5f],    // diesel
    24: [4, 0.5f],    // kerosene
    25: [2, 0.5f],    // recursion
    26: [4, 0.5f],    // pine bark jerky
]
CAN_ID = 'hbm:canned_conserve'

eventManager.listen { FoodEvent.GetFoodValues event ->
    DecayRules.guard('canned_food') {
        def food = event.food
        if (food == null || food.isEmpty()) return
        if (ForgeRegistries.ITEMS.getKey(food.getItem()).toString() != CAN_ID) return
        def value = CANS[food.getMetadata()]
        if (value != null) event.foodValues = new FoodValues(value[0], value[1])
    }
}
