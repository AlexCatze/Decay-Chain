package decaychain.fixes.mixin;

import com.blogspot.michaelsebero.movinglightsource.blocks.BlockMovingLightSource;
import decaychain.fixes.torch.LitTorches;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Moving Light Source (held items light the area around their holder): it derives an item's light from the block it
 * places, but a Pyrotech torch item always places an unlit torch. A lit torch (LitTorches) gives the light of a placed
 * lit torch; an unlit one none.
 */
@Mixin(value = BlockMovingLightSource.class, remap = false)
public abstract class MovingLightSourceMixins {
    @Inject(method = "getItemLightLevel", at = @At("HEAD"), cancellable = true)
    private static void decaychain$torchLight(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (LitTorches.isTorch(stack)) cir.setReturnValue(LitTorches.lightLevel(stack));
    }
}
