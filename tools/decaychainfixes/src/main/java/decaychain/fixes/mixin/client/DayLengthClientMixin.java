package decaychain.fixes.mixin.client;

import decaychain.fixes.DayLength;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Longer days (see DayLength), client side: WorldClient.tick advances the clock between the server's time updates the
 * same way the server does, so the sun does not run ahead and snap back every second.
 */
@Mixin(value = WorldClient.class, remap = false)
public abstract class DayLengthClientMixin {
    @Redirect(method = "func_72835_b", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/world/GameRules;func_82766_b(Ljava/lang/String;)Z"))
    private boolean decaychain$slowClock(GameRules rules, String name) {
        boolean value = rules.func_82766_b(name);
        return value && "doDaylightCycle".equals(name) ? DayLength.advances(((World) (Object) this).func_82737_E()) : value;
    }
}
