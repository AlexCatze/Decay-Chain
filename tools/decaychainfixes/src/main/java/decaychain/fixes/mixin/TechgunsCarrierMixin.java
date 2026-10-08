package decaychain.fixes.mixin;

import java.util.Random;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import techguns.world.StructureLandType;
import techguns.world.StructureSize;
import techguns.world.TGStructureSpawnRegister;
import techguns.world.WorldGenTGStructureSpawn;
import techguns.world.structures.WorldgenStructure;

/**
 * Techguns 2.2.0.1: every BIG structure slot (chunk grid, spawnWeightTGStructureBigOverworld) that lands in an ocean
 * spawns the Aircraft Carrier - it is the only BIG water structure, so there is no roll. Oceans were full of them.
 * Keep only 1 in CARRIER_CHANCE ocean slots; land structures (military base, castle, train) are unaffected.
 */
@Mixin(value = WorldGenTGStructureSpawn.class, remap = false)
public abstract class TechgunsCarrierMixin {
    @Unique private static final int CARRIER_CHANCE = 4;

    @Redirect(method = "generateSurface", at = @At(value = "INVOKE",
              target = "Ltechguns/world/TGStructureSpawnRegister;choseStructure(Ljava/util/Random;Lnet/minecraft/world/biome/Biome;Ltechguns/world/StructureSize;Ltechguns/world/StructureLandType;I)Ltechguns/world/structures/WorldgenStructure;"))
    private WorldgenStructure decaychain$rarerCarrier(Random rnd, Biome biome, StructureSize size, StructureLandType type, int dimension) {
        if (size == StructureSize.BIG && type == StructureLandType.WATER && rnd.nextInt(CARRIER_CHANCE) != 0) return null;
        return TGStructureSpawnRegister.choseStructure(rnd, biome, size, type, dimension);
    }
}
