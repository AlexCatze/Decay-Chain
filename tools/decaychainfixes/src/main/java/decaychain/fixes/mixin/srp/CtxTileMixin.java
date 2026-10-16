package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityDod;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.tileentity.TileEntity;

/** Decay Chain hives: SRP tile entities (canisters, dispatchers, relay controller) work with the hive they stand in. */
@Mixin(value = {TileEntityCanister.class, TileEntityDod.class, TileEntityRelayController.class}, remap = false)
public abstract class CtxTileMixin {
    @WrapMethod(method = "func_73660_a")
    private void decaychain$ctxUpdate(Operation<Void> original) {
        HiveContext.push((TileEntity) (Object) this);
        try {
            original.call();
        } finally {
            HiveContext.pop();
        }
    }
}
