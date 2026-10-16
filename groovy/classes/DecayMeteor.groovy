// Decay Chain: SRP meteors per area, on a schedule and at a safe distance.
// SRP's own meteor rolls a random chance every few minutes, picks its start and impact points independently (so it
// can fly right over the player) and kills everything within "Meteor Damage" blocks of the impact - more than the
// distance it lands from the player. Here a meteor falls at sunset for each player who has no meteorite hive nearby:
// first at that player's very first sunset, later 3-5 days after the hive near them was destroyed (the fix jar's
// decaychain.fixes.hive.Hives sets the day). Each meteor plants a hive with its own evolution, so a player starting
// somewhere new meets young parasites. The meteor lands in the western (sunset) sky, as far from the player as the
// loaded chunks allow, and descends beside its own impact point - never over the player.

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon
import com.dhanantry.scapeandrunparasites.world.SRPWorldData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DecayMeteor {
    static final int LETHAL_RADIUS = 32                    // SRP applies "Meteor Damage" as the radius of the blast
    static final int MIN_DISTANCE = 48                     // blast radius + margin for fragments
    static final int MAX_DISTANCE = 140
    static final long SUNSET = 12000L
    static final long SUNSET_END = 13000L
    static final String DAY_TAG = 'decaychain:meteorDay'   // persisted per player: earliest day of their next meteor
    static final long NEVER = Long.MAX_VALUE               // a meteor landed near them: wait until its hive is gone
    static final int COVERED = 400                         // a hive covers players this far beyond its zone radius
    static final int STRAYED = 800                         // ... and lets them go once they are this far beyond it
    static final int SHARED = 600                          // players this close to an impact share its meteor
    static final Random RNG = new Random()

    // SRP's per-world settings can override the config, so enforce these at runtime: no random meteors, small blast
    static void enforceSrpSettings() {
        SRPConfigWorld.meteorChance = 0.0d
        SRPConfigWorld.meteorDamage = LETHAL_RADIUS
    }

    static long meteorDay(EntityPlayer player) {
        NBTTagCompound persisted = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
        return persisted.hasKey(DAY_TAG) ? persisted.getLong(DAY_TAG) : 0L
    }

    static void setMeteorDay(EntityPlayer player, long day) {
        NBTTagCompound persisted = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
        persisted.setLong(DAY_TAG, day)
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted)
    }

    // Whether an infection origin (hive zone) reaches within extra blocks of x/z
    static boolean hiveNear(World world, double x, double z, int extra) {
        def data = SRPWorldData.get(world)
        def xs = data.getorigins('x'), zs = data.getorigins('z'), radii = data.getorigins('a')
        for (int i = 0; i < xs.size(); i++) {
            double dx = xs.get(i) - x, dz = zs.get(i) - z, reach = radii.get(i) + extra
            if (dx * dx + dz * dz <= reach * reach) return true
        }
        return false
    }

    // Whether this player gets a meteor at today's sunset
    static boolean isDue(World world, EntityPlayer player, long day) {
        long due = meteorDay(player)
        if (due == NEVER) {
            // their hive died while they were away, or they left it far behind: a new meteor in 3-5 days
            if (!hiveNear(world, player.posX, player.posZ, STRAYED)) setMeteorDay(player, day + 3 + RNG.nextInt(3))
            return false
        }
        if (day < due) return false
        if (hiveNear(world, player.posX, player.posZ, COVERED)) {
            setMeteorDay(player, NEVER)   // someone else's hive is already here
            return false
        }
        return true
    }

    // Everyone near the impact shares this meteor and its hive
    static void claim(World world, BlockPos target, def message) {
        for (def player : world.playerEntities) {
            if (player.getDistanceSq(target) > SHARED * SHARED) continue
            setMeteorDay(player, NEVER)
            player.sendMessage(message)
        }
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
