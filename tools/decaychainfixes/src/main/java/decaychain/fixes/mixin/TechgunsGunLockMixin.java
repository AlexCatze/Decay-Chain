package decaychain.fixes.mixin;

import decaychain.fixes.GunLocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.items.guns.GenericGun;

/**
 * Techguns guns fire from the PacketShootGun/PacketShootGunTarget packets (left click and held triggers), which skip
 * the click events Reskillable's item locks listen to. Both end in shootGunPrimary (charge guns call super into it).
 */
@Mixin(value = GenericGun.class, remap = false)
public abstract class TechgunsGunLockMixin {
    @Inject(method = "shootGunPrimary", at = @At("HEAD"), cancellable = true)
    private void decaychain$skillLock(ItemStack stack, World world, EntityPlayer player, boolean zooming, EnumHand hand, Entity target, CallbackInfo ci) {
        if (GunLocks.blocked(player, stack, true)) ci.cancel();
    }
}
