package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.block.BlockFallingInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Decay Chain hives: infested blocks spread and lose points for the hive they are in. */
@Mixin(value = {BlockParasiteSpreading.class, BlockFallingInfestedStain.class, BlockInfestedTrunk.class, BlockEvolutionLure.class}, remap = false)
public abstract class CtxBlockMixin {
    @WrapMethod(method = "removedByPlayer")
    private boolean decaychain$ctxRemoved(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest, Operation<Boolean> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(state, world, pos, player, harvest);
        } finally {
            HiveContext.pop();
        }
    }

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
