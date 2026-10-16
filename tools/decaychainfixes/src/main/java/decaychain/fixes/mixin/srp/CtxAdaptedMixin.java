package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;

/** Decay Chain hives: despawning adapted parasites refund points to their hive. */
@Mixin(value = EntityPAdapted.class, remap = false)
public abstract class CtxAdaptedMixin {
    @WrapMethod(method = "func_70623_bb")
    private void decaychain$ctxDespawn(Operation<Void> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            original.call();
        } finally {
            HiveContext.pop();
        }
    }
}
