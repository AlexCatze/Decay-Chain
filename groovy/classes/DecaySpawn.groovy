// Decay Chain: spread spawns around the world spawn instead of dropping everyone on the same spot.
// A player without a valid bed (first join or respawn) lands on a random safe surface spot 48-500 blocks
// from the world spawn: dry natural ground, open sky (no rooftops, caves or tree canopies), no oceans/rivers/beaches,
// normal biomes preferred over wasteland.

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.BiomeDictionary

class DecaySpawn {
    static final int RADIUS = 500
    static final int MIN_DISTANCE = 48
    static final int ATTEMPTS = 64        // first half: normal biomes only, second half: wasteland allowed
    static final Set<Material> GROUND = [Material.GRASS, Material.GROUND, Material.SAND, Material.SNOW,
                                         Material.CRAFTED_SNOW, Material.CLAY] as Set
    static final List<BiomeDictionary.Type> AVOID_TYPES = [BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER,
                                                          BiomeDictionary.Type.BEACH]
    static final String SPAWNED_TAG = 'decaychain:spawned'
    static final Random RNG = new Random()

    static boolean hasValidBed(EntityPlayer player, World world) {
        int dim = world.provider.getDimension()
        BlockPos bed = player.getBedLocation(dim)
        return bed != null && EntityPlayer.getBedSpawnLocation(world, bed, player.isSpawnForced(dim)) != null
    }

    static BlockPos findSpot(World world) {
        BlockPos center = world.getSpawnPoint()
        for (int i = 0; i < ATTEMPTS; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2
            double dist = MIN_DISTANCE + Math.sqrt(RNG.nextDouble()) * (RADIUS - MIN_DISTANCE)  // even spread by area
            BlockPos column = center.add((int) (Math.cos(angle) * dist), 0, (int) (Math.sin(angle) * dist))
            if (!world.getWorldBorder().contains(column)) continue
            // biome check first: answered by the biome provider without generating the chunk
            Biome biome = world.getBiome(column)
            if (AVOID_TYPES.any { BiomeDictionary.hasType(biome, it) }) continue
            if (i < ATTEMPTS / 2 && DecayBiomes.isWasteland(world, column)) continue
            BlockPos feet = world.getTopSolidOrLiquidBlock(column)
            if (isSafe(world, feet)) return feet
        }
        return null
    }

    static boolean isSafe(World world, BlockPos feet) {
        if (feet.getY() < world.getSeaLevel() || feet.getY() > 200) return false
        // natural ground only: city streets/roofs are stone, concrete, glass or metal
        if (!GROUND.contains(world.getBlockState(feet.down()).getMaterial())) return false
        for (BlockPos p : [feet, feet.up()]) {
            Material m = world.getBlockState(p).getMaterial()
            if (m.isLiquid() || m.blocksMovement()) return false
        }
        return world.canSeeSky(feet)
    }

    // returns false when no safe spot was found (the player keeps the vanilla spawn position)
    static boolean spread(EntityPlayerMP player) {
        BlockPos feet = findSpot(player.world)
        if (feet == null) return false
        player.setPositionAndUpdate(feet.getX() + 0.5d, feet.getY(), feet.getZ() + 0.5d)
        return true
    }
}
