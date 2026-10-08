// Decay Chain: the SRP meteor falls at sunset at a safe distance (logic in classes/DecayMeteor.groovy).
// Day 0 is the one safe day: the first meteor - and with it the parasites - arrives at the first sunset.

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
        long last = DecayMeteor.lastMeteorDay(world)
        if (last >= day || world.playerEntities.isEmpty()) return
        if (last >= 0) {
            // later meteors: only once the infection origin is gone, and not every sunset
            if (DecayMeteor.infectionActive(world)) return
            if (DecayMeteor.RNG.nextDouble() >= DecayMeteor.LATER_METEOR_CHANCE) {
                DecayMeteor.setLastMeteorDay(world, day)
                return
            }
        }
        def players = world.playerEntities
        def player = players.get(DecayMeteor.RNG.nextInt(players.size()))
        if (DecayMeteor.spawnSafe(world, player.getPosition()) == null) return   // area not loaded: retry next second
        DecayMeteor.setLastMeteorDay(world, day)
        for (def p : players) p.sendMessage(new TextComponentTranslation('decaychain.meteor.incoming'))
    }
}
