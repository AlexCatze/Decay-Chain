package decaychain.fixes.mixin.srp;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: SRP's phase rules for other mobs' spawns are local. */
@Mixin(targets = "com.dhanantry.scapeandrunparasites.init.SRPSpawning$DimensionHandler", remap = false)
public abstract class CtxSpawnCheckMixin {
    @WrapMethod(method = "onSpawn")
    private static void decaychain$ctxCheckSpawn(LivingSpawnEvent.CheckSpawn event, Operation<Void> original) {
        HiveContext.push(event.getWorld(), new BlockPos(event.getX(), event.getY(), event.getZ()));
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }
}
