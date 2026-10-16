package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;

/** Decay Chain hives: Moving Flesh merges earn points for the hive it is in. */
@Mixin(value = EntityLesh.class, remap = false)
public abstract class CtxLeshMixin {
    @WrapMethod(method = "func_70071_h_")
    private void decaychain$ctxUpdate(Operation<Void> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            original.call();
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "merge")
    private boolean decaychain$ctxMerge(EntityParasiteBase other, int value, String name, Operation<Boolean> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            return original.call(other, value, name);
        } finally {
            HiveContext.pop();
        }
    }
}
