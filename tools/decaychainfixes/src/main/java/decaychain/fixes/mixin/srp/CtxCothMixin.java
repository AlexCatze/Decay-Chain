package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.potion.PotionCOTH;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: COTH progress (points when an infection advances) goes to the hive where the creature is. */
@Mixin(value = PotionCOTH.class, remap = false)
public abstract class CtxCothMixin {
    @WrapMethod(method = "effectCOTHextendDuration")
    private void decaychain$ctxExtend(int a, int b, EntityLivingBase entity, int c, boolean d, boolean e, boolean f, Operation<Void> original) {
        HiveContext.push(entity);
        try {
            original.call(a, b, entity, c, d, e, f);
        } finally {
            HiveContext.pop();
        }
    }
}
