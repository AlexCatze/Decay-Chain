package decaychain.fixes.mixin;

import com.blogspot.michaelsebero.movinglightsource.TerrainGenEventHandler;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Moving Light Source 1.0.x still carries a leftover handler from the mod template it was built on: it denies every
 * SaplingGrowTreeEvent, so no sapling in the game could ever grow. Disable it.
 */
@Mixin(value = TerrainGenEventHandler.class, remap = false)
public abstract class MovingLightSourceSaplingMixin {
    @Inject(method = "onEvent(Lnet/minecraftforge/event/terraingen/SaplingGrowTreeEvent;)V", at = @At("HEAD"), cancellable = true)
    private void decaychain$letSaplingsGrow(SaplingGrowTreeEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
