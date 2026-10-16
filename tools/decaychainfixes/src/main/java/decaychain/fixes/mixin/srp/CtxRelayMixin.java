package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.EntityPlayerMP;

/** Decay Chain hives: the relay controller's phase report describes the hive at the player. */
@Mixin(value = TileEntityRelayController.class, remap = false)
public abstract class CtxRelayMixin {
    @WrapMethod(method = "givePhasePaper")
    private void decaychain$ctxPaper(EntityPlayerMP player, Operation<Void> original) {
        HiveContext.push(player);
        try {
            original.call(player);
        } finally {
            HiveContext.pop();
        }
    }
}
