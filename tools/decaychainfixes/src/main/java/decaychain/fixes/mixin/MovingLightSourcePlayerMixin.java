package decaychain.fixes.mixin;

import com.blogspot.michaelsebero.movinglightsource.EventHandler;
import com.blogspot.michaelsebero.movinglightsource.blocks.BlockMovingLightSource;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Moving Light Source only places a player's light into an air block at their feet, so standing in tall grass, a
 * crop or a snow layer gives no light at all. Fall back to the block at head height.
 */
@Mixin(value = EventHandler.class, remap = false)
public abstract class MovingLightSourcePlayerMixin {
    @Redirect(method = "handlePlayerLightPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;func_177984_a()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos decaychain$lightPos(BlockPos below, EntityPlayer player) {
        BlockPos feet = below.func_177984_a();
        if (canHoldLight(player, feet)) return feet;
        BlockPos head = feet.func_177984_a();
        return canHoldLight(player, head) ? head : feet;
    }

    @Unique
    private static boolean canHoldLight(EntityPlayer player, BlockPos pos) {
        Block block = player.field_70170_p.func_180495_p(pos).func_177230_c();
        return block == Blocks.field_150350_a || block instanceof BlockMovingLightSource;
    }
}
