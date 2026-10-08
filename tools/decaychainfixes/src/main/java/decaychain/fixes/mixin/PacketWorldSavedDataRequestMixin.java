package decaychain.fixes.mixin;

import minecrafttransportsimulator.packets.instances.PacketWorldSavedDataRequest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Immersive Vehicles 24.0.0: the client sends this packet when it joins a world, and the server handled it on the
 * Netty thread. Its player lookup iterates World.loadedEntityList while the server thread adds entities (fresh chunks
 * on a new world) -> ConcurrentModificationException -> "A fatal error has occurred" disconnect to the main menu.
 * Handle it on the server thread like every other MTS player packet.
 */
@Mixin(value = PacketWorldSavedDataRequest.class, remap = false)
public abstract class PacketWorldSavedDataRequestMixin {
    @Inject(method = "runOnMainThread", at = @At("HEAD"), cancellable = true)
    private void decaychain$runOnMainThread(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
