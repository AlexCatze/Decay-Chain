package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Decay Chain hives: the evolution lure feeds the hive it stands in. */
@Mixin(value = BlockEvolutionLure.class, remap = false)
public abstract class CtxLureMixin {
    @WrapMethod(method = "func_180639_a")
    private boolean decaychain$ctxActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                            float hitX, float hitY, float hitZ, Operation<Boolean> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        } finally {
            HiveContext.pop();
        }
    }
}
