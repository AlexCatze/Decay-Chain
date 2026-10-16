package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.item.ItemClockDevelopment;
import com.dhanantry.scapeandrunparasites.item.ItemClockEvolution;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: SRP's evolution clocks show the phase of the hive their holder is in. */
@Mixin(value = {ItemClockEvolution.class, ItemClockDevelopment.class}, remap = false)
public abstract class CtxInfoMixin {
    @WrapMethod(method = "func_77663_a")
    private void decaychain$ctxHeld(ItemStack stack, World world, Entity holder, int slot, boolean selected, Operation<Void> original) {
        HiveContext.push(holder);
        try {
            original.call(stack, world, holder, slot, selected);
        } finally {
            HiveContext.pop();
        }
    }
}
