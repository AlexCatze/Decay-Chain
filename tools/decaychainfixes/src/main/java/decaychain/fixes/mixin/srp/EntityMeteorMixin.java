package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.projectile.EntityMeteor;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.Hives;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * SRP meteor impact:
 * - It gives the infection (COTH) to every living thing whose distance factor against 800 blocks is above 0.5, i.e.
 *   within ~566 blocks. The mass conversion pushed a fresh world to phase 1 on the first night and filled the area
 *   with infected animals. 160 limits it to ~113 blocks around the crater.
 * - The origin it plants is a meteor hive: flag the impact so the hive records its living core.
 */
@Mixin(value = EntityMeteor.class, remap = false)
public abstract class EntityMeteorMixin {
    @ModifyConstant(method = "onImpact", constant = @Constant(intValue = 800))
    private int decaychain$infectionRange(int range) {
        return 160;
    }

    @WrapMethod(method = "onImpact")
    private void decaychain$impact(Operation<Void> original) {
        boolean outer = Hives.meteorImpact;
        Hives.meteorImpact = true;
        try {
            original.call();
        } finally {
            Hives.meteorImpact = outer;
        }
    }
}
