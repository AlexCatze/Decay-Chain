package decaychain.fixes.mixin;

import ZombieAwareness.CommandZA;
import ZombieAwareness.ZombieAwareness;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Zombie Awareness 1.11.16 registers its command on FMLCommonHandler's "current" server. Chunk Pregenerator's world
 * preview (on the Create World screen) starts its own server that the client doesn't know about, so that lookup is
 * null and the preview crashes the game. Register the command on the server that is actually starting.
 */
@Mixin(value = ZombieAwareness.class, remap = false)
public abstract class ZombieAwarenessCommandMixin {
    @Inject(method = "serverStarting", at = @At("HEAD"), cancellable = true)
    private void decaychain$registerCommand(FMLServerStartingEvent event, CallbackInfo ci) {
        event.registerServerCommand(new CommandZA());
        ci.cancel();
    }
}
