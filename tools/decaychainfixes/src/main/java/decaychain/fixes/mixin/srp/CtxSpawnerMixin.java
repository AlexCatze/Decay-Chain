package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.SRPWorldParasiteSpawner;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: which parasites spawn is decided by the phase of the hive at the spawn position. */
@Mixin(value = {SRPWorldEntitySpawner.class, SRPWorldParasiteSpawner.class}, remap = false)
public abstract class CtxSpawnerMixin {
    @WrapMethod(method = "getSpawnListEntryForTypeAt")
    private static Biome.SpawnListEntry decaychain$ctxSpawn(WorldServer world, BlockPos pos, Operation<Biome.SpawnListEntry> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(world, pos);
        } finally {
            HiveContext.pop();
        }
    }
}
