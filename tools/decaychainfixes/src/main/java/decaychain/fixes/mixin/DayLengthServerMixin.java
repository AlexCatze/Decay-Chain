package decaychain.fixes.mixin;

import decaychain.fixes.DayLength;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Longer days (see DayLength): WorldServer.tick advances the clock only while the doDaylightCycle rule is on, so the
 * rule reads as off on the ticks where the clock should stand still. The total world time still counts every tick.
 */
@Mixin(value = WorldServer.class, remap = false)
public abstract class DayLengthServerMixin {
    @Redirect(method = "func_72835_b", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/world/GameRules;func_82766_b(Ljava/lang/String;)Z"))
    private boolean decaychain$slowClock(GameRules rules, String name) {
        boolean value = rules.func_82766_b(name);
        return value && "doDaylightCycle".equals(name) ? DayLength.advances(((World) (Object) this).func_82737_E()) : value;
    }
}
