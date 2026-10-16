package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

/** Decay Chain hives: a parasite's ticks (AI, attacks, merges, despawn), spawn setup, kills and death use its hive. */
@Mixin(value = EntityParasiteBase.class, remap = false)
public abstract class CtxParasiteMixin {
    @WrapMethod(method = "func_70636_d")
    private void decaychain$ctxLivingUpdate(Operation<Void> original) {
        HiveContext.push((EntityParasiteBase) (Object) this);
        try {
            original.call();
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "func_180482_a")
    private IEntityLivingData decaychain$ctxInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data, Operation<IEntityLivingData> original) {
        HiveContext.push((EntityParasiteBase) (Object) this);
        try {
            return original.call(difficulty, data);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "applyBonuses")
    private void decaychain$ctxBonuses(SRPSaveData save, World world, Operation<Void> original) {
        HiveContext.push((EntityParasiteBase) (Object) this);
        try {
            original.call(save, world);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "func_70074_a")
    private void decaychain$ctxKill(EntityLivingBase victim, Operation<Void> original) {
        HiveContext.push((EntityParasiteBase) (Object) this);
        try {
            original.call(victim);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "func_70645_a")
    private void decaychain$ctxDeath(DamageSource cause, Operation<Void> original) {
        HiveContext.push((EntityParasiteBase) (Object) this);
        try {
            original.call(cause);
        } finally {
            HiveContext.pop();
        }
    }
}
