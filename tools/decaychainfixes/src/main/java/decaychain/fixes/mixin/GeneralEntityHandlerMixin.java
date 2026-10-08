package decaychain.fixes.mixin;

import funwayguy.epicsiegemod.handlers.entities.GeneralEntityHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Epic Siege Mod 13.169: the world load/unload handlers dereference FMLCommonHandler's server, which is null while
 * Chunk Pregenerator's world-creation preview runs its own private server -> NPE "Exception in server tick loop".
 * Skip ESM's boss-modifier bookkeeping when there is no registered server.
 */
@Mixin(value = GeneralEntityHandler.class, remap = false)
public abstract class GeneralEntityHandlerMixin {
    @Inject(method = "onWorldLoad", at = @At("HEAD"), cancellable = true)
    private void decaychain$skipLoadWithoutServer(WorldEvent.Load event, CallbackInfo ci) {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) ci.cancel();
    }

    @Inject(method = "onWorldUnload", at = @At("HEAD"), cancellable = true)
    private void decaychain$skipUnloadWithoutServer(WorldEvent.Unload event, CallbackInfo ci) {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) ci.cancel();
    }
}
