package decaychain.fixes.mixin;

import java.util.List;
import java.util.Set;
import minecrafttransportsimulator.baseclasses.EntityManager;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Immersive Vehicles 24.0.0: every world wrapper (one per loaded World, server and client) is added to the static
 * EntityManager.managers list and registered on the Forge event bus, and neither is undone when the world unloads.
 * The wrappers keep every World that was ever loaded in the session alive. Release them in onUnload().
 */
@Mixin(value = EntityManager.class, remap = false)
public abstract class MtsEntityManagerMixin {
    @Shadow @Final private static List<EntityManager> managers;
    @Shadow @Final private static Set<EntityManager> managersToHotload;

    @Inject(method = "onUnload", at = @At("TAIL"))
    private void decaychain$release(CallbackInfo ci) {
        managers.remove((Object) this);
        managersToHotload.remove((Object) this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
