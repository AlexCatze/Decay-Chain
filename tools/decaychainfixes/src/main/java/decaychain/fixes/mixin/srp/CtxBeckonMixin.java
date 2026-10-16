package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Decay Chain hives: beckon block infestation earns points for the hive where it happens. */
@Mixin(value = BeckonBlockInfestation.class, remap = false)
public abstract class CtxBeckonMixin {
    @WrapMethod(method = "beckonInfestation")
    private static void decaychain$ctxInfest(World world, BlockPos pos, Random rand, int pivot, boolean flag, Operation<Void> original) {
        HiveContext.push(world, pos);
        try {
            original.call(world, pos, rand, pivot, flag);
        } finally {
            HiveContext.pop();
        }
    }
}
