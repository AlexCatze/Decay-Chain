// Decay Chain: crops barely grow in wasteland biomes (poisoned soil).
// 90% of growth ticks are denied; Hunger Overhaul already slows crops further (config/hungeroverhaul/HungerOverhaul.cfg).

import classes.DecayRules
import classes.DecayBiomes
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.Event

eventManager.listen { BlockEvent.CropGrowEvent.Pre event ->
    DecayRules.guard('wasteland_crops') {
        if (DecayBiomes.isWasteland(event.getWorld(), event.getPos()) && event.getWorld().rand.nextFloat() < 0.9f) {
            event.setResult(Event.Result.DENY)
        }
    }
}
