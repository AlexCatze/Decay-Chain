package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.network.SRPProgressSnapshot;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: SRP's progress screen shows the hive the player is in. */
@Mixin(value = SRPProgressSnapshot.class, remap = false)
public abstract class CtxPlayerInfoMixin {
    @WrapMethod(method = "collect")
    private static SRPProgressSnapshot decaychain$ctxCollect(World world, EntityPlayer player, Operation<SRPProgressSnapshot> original) {
        HiveContext.push(player);
        try {
            return original.call(world, player);
        } finally {
            HiveContext.pop();
        }
    }
}
