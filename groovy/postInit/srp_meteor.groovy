// Decay Chain: SRP meteors fall at sunset at a safe distance, one per area (logic in classes/DecayMeteor.groovy).
// A player's first day is their one safe day: their first meteor - and with it the parasites - arrives at their first
// sunset, unless another player's meteorite hive is already nearby.

import classes.DecayMeteor
import classes.DecayRules
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.gameevent.TickEvent

eventManager.listen { TickEvent.WorldTickEvent event ->
    def world = event.world
    if (world.isRemote || world.provider.getDimension() != 0 || world.getTotalWorldTime() % 20 != 0) return
    DecayRules.guard('srp_meteor') {
        DecayMeteor.enforceSrpSettings()
        long time = world.getWorldTime()
        long day = time.intdiv(24000L), timeOfDay = time % 24000L
        if (timeOfDay < DecayMeteor.SUNSET || timeOfDay >= DecayMeteor.SUNSET_END) return
        for (def player : new ArrayList(world.playerEntities)) {
            if (!DecayMeteor.isDue(world, player, day)) continue
            def target = DecayMeteor.spawnSafe(world, player.getPosition())
            if (target == null) continue   // area not loaded yet: retry next second
            DecayMeteor.claim(world, target, new TextComponentTranslation('decaychain.meteor.incoming'))
        }
    }
}
