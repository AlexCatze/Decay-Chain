package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLeer;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/** Decay Chain hives: Rupter and Airscrew override their tick and kill handlers (both earn evolution points). */
@Mixin(value = {EntityMudo.class, EntityLeer.class}, remap = false)
public abstract class CtxParasiteSubclassMixin {
    @WrapMethod(method = "func_70071_h_")
    private void decaychain$ctxUpdate(Operation<Void> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            original.call();
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "func_70074_a")
    private void decaychain$ctxKill(EntityLivingBase victim, Operation<Void> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            original.call(victim);
        } finally {
            HiveContext.pop();
        }
    }
}
