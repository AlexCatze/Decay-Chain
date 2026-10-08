// Decay Chain: primitive start (Pyrotech is the early-game backbone).
// Logs cannot be broken without an axe-class tool (Pyrotech crude/flint/bone axes and up). This also keeps
// Timberjack from felling a whole tree that was punched. Sticks come from leaves (Pyrotech + BoP leaves in
// scripts/primitive.zs), rocks and flint from the ground.

import classes.DecayRules
import net.minecraftforge.event.entity.player.PlayerEvent

eventManager.listen { PlayerEvent.BreakSpeed event ->
    DecayRules.guard('log_breaking') {
        def player = event.getEntityPlayer()
        if (!player.capabilities.isCreativeMode && DecayRules.isLog(event.getState()) && !DecayRules.isAxe(player.getHeldItemMainhand())) {
            event.setCanceled(true)
        }
    }
}
