package decaychain.fixes.mixin;

import com.hbm.handler.HbmKeybinds;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import decaychain.fixes.GunLocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * HBM NTM guns fire (and reload) from keybind packets, never through the click events Reskillable's item locks listen
 * to, so a gun locked behind Attack levels fired anyway. Every trigger, reload and inspect key of the current guns goes
 * through handleKeybind; check the lock there, on both sides (the client skips its firing animation too).
 */
@Mixin(value = ItemGunBaseNT.class, remap = false)
public abstract class HbmGunLockMixin {
    @Inject(method = "handleKeybind(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lcom/hbm/handler/HbmKeybinds$EnumKeybind;Z)V",
            at = @At("HEAD"), cancellable = true)
    private void decaychain$skillLock(EntityPlayer player, ItemStack stack, HbmKeybinds.EnumKeybind key, boolean pressed, CallbackInfo ci) {
        if (GunLocks.blocked(player, stack, pressed)) ci.cancel();
    }
}
