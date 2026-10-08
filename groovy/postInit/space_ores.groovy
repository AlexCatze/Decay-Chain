// Decay Chain: the Nuclear age starts on Mars (Duna).
// NTM Space generates uranium/thorium on every celestial body; on the Space-age bodies (Mun, Minmus) those ores
// are replaced with plain stone by a world generator that runs after the others (high weight).
// (Earth/Nether nuclear ores are disabled in config/hbm/hbm_dimensions.cfg and hbm_bedrock_ores.json.)

import classes.DecayRules
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.registry.GameRegistry

GameRegistry.registerWorldGenerator(new IWorldGenerator() {
    void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        DecayRules.guard('space_ores') { DecayRules.stripNuclearOres(world, chunkX, chunkZ) }
    }
}, 10000)
