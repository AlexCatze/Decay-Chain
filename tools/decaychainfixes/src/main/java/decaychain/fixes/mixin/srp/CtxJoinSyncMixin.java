package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.network.CelestialNightJoinSync;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: the phase sent to a joining player is the one of the hive they are in. */
@Mixin(value = CelestialNightJoinSync.class, remap = false)
public abstract class CtxJoinSyncMixin {
    @WrapMethod(method = "sync")
    private static void decaychain$ctxSync(EntityPlayerMP player, Operation<Void> original) {
        HiveContext.push(player);
        try {
            original.call(player);
        } finally {
            HiveContext.pop();
        }
    }
}
