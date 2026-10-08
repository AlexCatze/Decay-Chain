// Decay Chain: SRP meteor on a schedule and at a safe distance.
// SRP's own meteor rolls a random chance every few minutes, picks its start and impact points independently (so it
// can fly right over the player) and kills everything within "Meteor Damage" blocks of the impact - more than the
// distance it lands from the player. Here the meteor falls at sunset: first at the very first sunset, later ones only
// at a sunset after the infection origin it planted has been destroyed. It lands in the western (sunset) sky, as far
// from the player as the loaded chunks allow, and descends beside its own impact point - never over the player.

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon
import com.dhanantry.scapeandrunparasites.world.SRPWorldData
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DecayMeteor {
    static final int LETHAL_RADIUS = 32                    // SRP applies "Meteor Damage" as the radius of the blast
    static final int MIN_DISTANCE = 48                     // blast radius + margin for fragments
    static final int MAX_DISTANCE = 140
    static final long SUNSET = 12000L
    static final long SUNSET_END = 13000L
    static final double LATER_METEOR_CHANCE = 0.5          // per sunset, once the infection origin is gone
    static final String DAY_RULE = 'decaychainMeteorDay'   // game rule (saved in level.dat): last day a meteor was handled
    static final Random RNG = new Random()

    // SRP's per-world settings can override the config, so enforce these at runtime: no random meteors, small blast
    static void enforceSrpSettings() {
        SRPConfigWorld.meteorChance = 0.0d
        SRPConfigWorld.meteorDamage = LETHAL_RADIUS
    }

    static long lastMeteorDay(World world) {
        def rules = world.getGameRules()
        return rules.hasRule(DAY_RULE) ? Long.parseLong(rules.getString(DAY_RULE)) : -1L
    }

    static void setLastMeteorDay(World world, long day) {
        world.getGameRules().setOrCreateGameRule(DAY_RULE, String.valueOf(day))
    }

    static boolean infectionActive(World world) {
        return !SRPWorldData.get(world).getorigins('x').isEmpty()
    }

    // Spawns a meteor that lands west of center; returns the impact point, or null if the area is not loaded yet.
    static BlockPos spawnSafe(World world, BlockPos center) {
        double angle = Math.PI + (RNG.nextDouble() - 0.5d) * Math.PI / 2.0d   // west, +-45 degrees
        double dx = Math.cos(angle), dz = Math.sin(angle)
        int side = RNG.nextBoolean() ? 1 : -1
        // as far out as loaded chunks allow: entities only spawn in loaded chunks and only tick 32+ blocks inside them
        for (int dist = MAX_DISTANCE; dist >= MIN_DISTANCE; dist -= 8) {
            int tx = center.getX() + (int) (dx * dist), tz = center.getZ() + (int) (dz * dist)
            double offset = dist * 0.4d   // start point sits sideways from the impact, perpendicular to the player
            int ox = tx - (int) (dz * offset * side), oz = tz + (int) (dx * offset * side)
            if (!world.isAreaLoaded(new BlockPos(tx, 64, tz), 40) || !world.isAreaLoaded(new BlockPos(ox, 64, oz), 40)) continue
            BlockPos target = world.getHeight(new BlockPos(tx, 0, tz))   // only after the check: would load the chunk
            ParasiteSummon.spawnMeteor(ox, world.getHeight(), oz, tx, target.getY(), tz, world)
            return target
        }
        return null
    }
}
