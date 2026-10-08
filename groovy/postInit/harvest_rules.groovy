// Decay Chain: block hardness tiers for the Primitive -> Scavenger -> Industrial ages.
// - HBM concrete/steel structure blocks (ruined cities, NTM structures) need an iron-or-better pickaxe.
// - Reinforced/ducrete/combine blocks need a steel-tier pickaxe (HBM steel and up).
// Players cannot dig through dungeon walls early; they have to use doorways and fight.
// Logic lives in classes/DecayRules.groovy.

import classes.DecayRules
import net.minecraftforge.event.entity.player.PlayerEvent

DecayRules.initStructureBlocks()

eventManager.listen { PlayerEvent.BreakSpeed event ->
    DecayRules.guard('harvest_rules') {
        def player = event.getEntityPlayer()
        if (!player.capabilities.isCreativeMode && !DecayRules.canBreak(player, event.getState())) event.setCanceled(true)
    }
}
