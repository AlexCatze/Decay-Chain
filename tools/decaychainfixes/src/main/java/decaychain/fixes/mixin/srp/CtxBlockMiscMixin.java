package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Decay Chain hives: infested remains tick with the hive they are in. */
@Mixin(value = BlockInfestedRemain.class, remap = false)
public abstract class CtxBlockMiscMixin {
    @WrapMethod(method = "func_180650_b")
    private void decaychain$ctxTick(World world, BlockPos pos, IBlockState state, Random rand, Operation<Void> original) {
        HiveContext.push(world, pos);
        try {
            original.call(world, pos, state, rand);
        } finally {
            HiveContext.pop();
        }
    }
}
