package decaychain.fixes.mixin;

import com.blogspot.michaelsebero.movinglightsource.tileentities.TileEntityMovingLightSource;
import decaychain.fixes.torch.LitTorches;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Moving Light Source: a dropped lit Pyrotech torch keeps its light (the light block checks the item by type only). */
@Mixin(value = TileEntityMovingLightSource.class, remap = false)
public abstract class MovingLightSourceTileMixin {
    @Inject(method = "isItemEmittingLight", at = @At("HEAD"), cancellable = true)
    private void decaychain$droppedTorch(EntityItem item, CallbackInfoReturnable<Boolean> cir) {
        if (item != null && LitTorches.isTorch(item.func_92059_d())) cir.setReturnValue(LitTorches.isLit(item.func_92059_d()));
    }
}
