// Decay Chain: biomes treated as wasteland (toxic water, hostile to crops).
// Keep in sync with config/lostcities/profile_decay.cfg (cityBiomeFactors) and config/lostcities/userassets.json.

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.ForgeRegistries

class DecayBiomes {
    static final Set<String> WASTELAND = [
        'biomesoplenty:wasteland', 'biomesoplenty:dead_forest', 'biomesoplenty:dead_swamp',
        'biomesoplenty:outback', 'biomesoplenty:xeric_shrubland', 'biomesoplenty:steppe',
        'biomesoplenty:quagmire', 'biomesoplenty:cold_desert', 'biomesoplenty:volcanic_island',
        'minecraft:desert', 'minecraft:desert_hills', 'minecraft:mutated_desert',
        'minecraft:mesa', 'minecraft:mesa_rock', 'minecraft:mesa_clear_rock',
        'minecraft:mutated_mesa', 'minecraft:mutated_mesa_rock', 'minecraft:mutated_mesa_clear_rock',
        'hbm:crater', 'hbm:crater_inner', 'hbm:crater_outer'
    ] as Set

    static boolean isWasteland(World world, BlockPos pos) {
        // registry lookup instead of biome.getRegistryName(): avoids a Groovy metaclass for mod biome classes
        return WASTELAND.contains(ForgeRegistries.BIOMES.getKey(world.getBiome(pos)).toString())
    }
}
