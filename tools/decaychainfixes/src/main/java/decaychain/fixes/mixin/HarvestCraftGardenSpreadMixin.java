package decaychain.fixes.mixin;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.pam.harvestcraft.HarvestCraft;
import com.pam.harvestcraft.blocks.blocks.BlockBaseGarden;

/**
 * Pam's HarvestCraft 1.12.2zg: gardens spread into ever larger patches. The built-in cap (gardenspreadMax) only counts
 * gardens of the same kind within 4 blocks, and a new garden lands up to 4 blocks away - so a garden on the edge of a
 * patch sees only part of it and the patch keeps creeping outward; different kinds never block each other.
 * Here a garden does not spread while MAX_NEIGHBOURS or more gardens of any kind grow within RADIUS blocks. The check
 * runs only when the garden has already rolled a spread (gardenSpreadRate), so it costs a small block scan a few times
 * per garden per hour of game time. Unloaded neighbours count as crowded, so it never loads chunks.
 */
@Mixin(value = BlockBaseGarden.class, remap = false)
public abstract class HarvestCraftGardenSpreadMixin {
    @Unique private static final int RADIUS = 6;
    @Unique private static final int MAX_NEIGHBOURS = 3;

    @Redirect(method = "func_180650_b", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
    private int decaychain$spreadRoll(Random rand, int bound, World world, BlockPos pos, IBlockState state, Random random) {
        int roll = rand.nextInt(bound);
        if (roll >= HarvestCraft.config.gardenspreadRate) return roll;      // not spreading this tick anyway
        return decaychain$crowded(world, pos) ? bound : roll;                // bound = always above the spread rate
    }

    @Unique
    private static boolean decaychain$crowded(World world, BlockPos pos) {
        int found = 0;
        for (BlockPos p : BlockPos.func_177980_a(pos.func_177982_a(-RADIUS, -2, -RADIUS), pos.func_177982_a(RADIUS, 2, RADIUS))) {
            if (!world.func_175667_e(p)) return true;
            if (world.func_180495_p(p).func_177230_c() instanceof BlockBaseGarden && !p.equals(pos) && ++found >= MAX_NEIGHBOURS) {
                return true;
            }
        }
        return false;
    }
}
