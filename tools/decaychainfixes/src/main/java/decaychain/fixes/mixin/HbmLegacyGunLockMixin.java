package decaychain.fixes.mixin;

import com.hbm.items.weapon.ItemGunBase;
import decaychain.fixes.GunLocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** HBM's older guns (ItemGunBase) start firing from the gun button packet; enforce Reskillable's item lock there. */
@Mixin(value = ItemGunBase.class, remap = false)
public abstract class HbmLegacyGunLockMixin {
    @Inject(method = "startAction", at = @At("HEAD"), cancellable = true)
    private void decaychain$skillLock(ItemStack stack, World world, EntityPlayer player, boolean main, EnumHand hand, CallbackInfo ci) {
        if (GunLocks.blocked(player, stack, true)) ci.cancel();
    }

    @Inject(method = "startActionClient", at = @At("HEAD"), cancellable = true)
    private void decaychain$skillLockClient(ItemStack stack, World world, EntityPlayer player, boolean main, EnumHand hand, CallbackInfo ci) {
        if (GunLocks.blocked(player, stack, false)) ci.cancel();
    }
}
