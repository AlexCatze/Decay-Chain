package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Decay Chain hives: biome infestation spreading earns points for the hive where it happens. */
@Mixin(value = BlockParasiteSpreading.class, remap = false)
public abstract class CtxSpreadMixin {
    @WrapMethod(method = "spreadBiomeBlockStain")
    private static void decaychain$ctxStain(World world, BlockPos pos, Random rand, Operation<Void> original) {
        HiveContext.push(world, pos);
        try {
            original.call(world, pos, rand);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "spreadBiomeBlockTrunk")
    private static void decaychain$ctxTrunk(World world, BlockPos pos, Random rand, Operation<Void> original) {
        HiveContext.push(world, pos);
        try {
            original.call(world, pos, rand);
        } finally {
            HiveContext.pop();
        }
    }
}
