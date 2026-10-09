package decaychain.fixes.mixin;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lost Cities 2.0.22: the "Lost Cities + BoP" world type (and the adapter used for other mods' world types) caches
 * the wrapped biome provider in a field of the WorldType - a global singleton - and never resets it. Every world
 * created later in the same game session (including Chunk Pregenerator's world preview) reuses the first world's
 * biome provider and therefore its seed: the terrain repeats while seed-based structures move around. Drop the cache
 * so each world builds its own provider, like vanilla. It is only requested when a world is created.
 */
@Mixin(targets = {"mcjty.lostcities.dimensions.world.LostWorldTypeBOP", "mcjty.lostcities.dimensions.world.LostWorldTypeAdapter"},
       remap = false)
public abstract class LostCitiesBiomeProviderMixin {
    @Shadow(remap = false) private BiomeProvider biomeProvider;   // class-level remap=false is not applied to shadows with multiple targets

    @Inject(method = "getInternalBiomeProvider", at = @At("HEAD"), remap = false)
    private void decaychain$freshProviderPerWorld(World world, CallbackInfoReturnable<BiomeProvider> cir) {
        biomeProvider = null;
    }
}
