package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import decaychain.fixes.hive.Hives;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Decay Chain hives: every new infection origin becomes a hive, and origins grow by their own hive's phase. SRP grows
 * the radius by 135% per day, so after a week one origin covers the whole world; a hive's zone is 200 blocks plus 40
 * per phase, up to 600 (Hives.radiusFor).
 */
@Mixin(value = SRPWorldData.class, remap = false)
public abstract class SRPWorldDataMixin {
    @Inject(method = "setOrigin", at = @At("HEAD"))
    private void decaychain$originStart(World world, int x, int y, int z, int health, int radius, CallbackInfoReturnable<Integer> cir) {
        Hives.beginOrigin();
    }

    @Inject(method = "setOrigin", at = @At("RETURN"))
    private void decaychain$originCreated(World world, int x, int y, int z, int health, int radius, CallbackInfoReturnable<Integer> cir) {
        Hives.endOrigin(world, x, y, z, cir.getReturnValue());
    }

    @Inject(method = "updateOriginValues", at = @At("HEAD"), cancellable = true)
    private void decaychain$hiveGrowth(World world, int updates, SRPSaveData data, CallbackInfo ci) {
        if (Hives.growOrigins(world, (SRPWorldData) (Object) this, updates, data)) ci.cancel();
    }
}
