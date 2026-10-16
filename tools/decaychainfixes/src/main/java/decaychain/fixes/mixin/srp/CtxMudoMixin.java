package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** Decay Chain hives: the Rupter's own spawn bonuses use its hive's phase. */
@Mixin(value = EntityMudo.class, remap = false)
public abstract class CtxMudoMixin {
    @WrapMethod(method = "applyBonuses")
    private void decaychain$ctxBonuses(SRPSaveData save, World world, Operation<Void> original) {
        HiveContext.push((Entity) (Object) this);
        try {
            original.call(save, world);
        } finally {
            HiveContext.pop();
        }
    }
}
