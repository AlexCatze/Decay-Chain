package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import decaychain.fixes.hive.Hives;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Decay Chain hives: phase alerts go to the hive's surroundings; conversions and reinforcements use the local phase. */
@Mixin(value = ParasiteEventEntity.class, remap = false)
public abstract class ParasiteEventEntityMixin {
    @Inject(method = "alertAllPlayerDim", at = @At("HEAD"), cancellable = true)
    private static void decaychain$hiveAlert(World world, String message, int warning, CallbackInfo ci) {
        if (Hives.alertHive(world, message, warning)) ci.cancel();
    }

    @WrapMethod(method = "convertEntity")
    private static void decaychain$ctxConvert(EntityLivingBase entity, NBTTagCompound tags, boolean flag, String[] list, Operation<Void> original) {
        HiveContext.push(entity);
        try {
            original.call(entity, tags, flag, list);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "spawnBeckonE")
    private static void decaychain$ctxBeckon(SRPWorldData data, World world, EntityParasiteBase parasite, Operation<Void> original) {
        HiveContext.push(parasite);
        try {
            original.call(data, world, parasite);
        } finally {
            HiveContext.pop();
        }
    }
}
