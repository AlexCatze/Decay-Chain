package decaychain.fixes.mixin.srp;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Longer days: the sky's per-night celestial rolls use the world clock, like SrpNightIndexMixin. */
@Mixin(targets = "com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry", remap = false)
public abstract class SrpCelestialRollMixin {
    @Redirect(method = "rollsThisNight", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_82737_E()J"))
    private static long decaychain$clockDay(World world) {
        return world.func_72820_D();
    }
}
