package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.potion.PotionCOTH;
import com.dhanantry.scapeandrunparasites.potion.PotionPrey;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: infection (COTH) and Prey effects act with the hive where the affected creature is. */
@Mixin(value = {PotionCOTH.class, PotionPrey.class}, remap = false)
public abstract class CtxPotionMixin {
    @WrapMethod(method = "func_76394_a")
    private void decaychain$ctxEffect(EntityLivingBase entity, int amplifier, Operation<Void> original) {
        HiveContext.push(entity);
        try {
            original.call(entity, amplifier);
        } finally {
            HiveContext.pop();
        }
    }
}
