package decaychain.fixes.mixin.srp;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Longer days (see decaychain.fixes.DayLength): SRP numbers its celestial nights by total world time / 24000, which
 * no longer matches the day/night cycle - the night number would change in the middle of a night and re-roll the
 * celestial events. Number nights by the world clock instead, the same one the sky follows.
 */
@Mixin(targets = {"com.dhanantry.scapeandrunparasites.network.CelestialNightJoinSync",
                  "com.dhanantry.scapeandrunparasites.network.CelestialPhaseSyncHandler"}, remap = false)
public abstract class SrpNightIndexMixin {
    @Redirect(method = "getNightIndex", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_82737_E()J"))
    private static long decaychain$clockDay(World world) {
        return world.func_72820_D();
    }
}
