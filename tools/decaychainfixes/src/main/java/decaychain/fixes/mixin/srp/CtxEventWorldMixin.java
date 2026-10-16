package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: nodes, colonies, origins and dislodgment events check the phase where they happen. */
@Mixin(value = ParasiteEventWorld.class, remap = false)
public abstract class CtxEventWorldMixin {
    @WrapMethod(method = "placeHeartInWorld")
    private static int decaychain$ctxHeart(World world, BlockPos pos, int type, Operation<Integer> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(world, pos, type);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "placeColonyInWorld")
    private static int decaychain$ctxColony(World world, BlockPos pos, Operation<Integer> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(world, pos);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "placeOriginInWorld")
    private static int decaychain$ctxOrigin(World world, BlockPos pos, int health, int radius, Operation<Integer> original) {
        HiveContext.push(world, pos);
        try {
            return original.call(world, pos, health, radius);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "setDisloWorldPhase")
    private static void decaychain$ctxDislo(World world, ArrayList<Byte> events, double chance, int value, BlockPos pos, Operation<Void> original) {
        HiveContext.push(world, pos);
        try {
            original.call(world, events, chance, value, pos);
        } finally {
            HiveContext.pop();
        }
    }
}
