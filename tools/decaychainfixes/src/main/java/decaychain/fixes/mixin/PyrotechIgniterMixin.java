package decaychain.fixes.mixin;

import com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableWithIgniterItem;
import com.codetaylor.mc.pyrotech.modules.ignition.item.ItemIgniterBase;
import decaychain.fixes.torch.LitTorches;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Decay Chain lit torches: a Pyrotech igniter (bow drill, flint and tinder, matchstick) lights the unlit torch in the
 * other hand, the same way it lights a placed torch - unless it is aimed at something it lights anyway (a placed torch,
 * a campfire, ...). Igniters normally only work when aimed at a block; with a torch in hand they work anywhere, and they
 * no longer set fire to the ground the player happens to look at.
 */
@Mixin(value = ItemIgniterBase.class, remap = false)
public abstract class PyrotechIgniterMixin extends Item {
    @Shadow protected abstract void damageItem(ItemStack stack, EntityLivingBase entity);

    @Shadow protected abstract int getCooldownTicks();

    @Unique
    private boolean decaychain$lightsHeldTorch(World world, EntityPlayer player, EnumHand igniterHand) {
        EnumHand other = igniterHand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        if (!LitTorches.isUnlit(player.func_184586_b(other))) return false;
        RayTraceResult hit = func_77621_a(world, player, false);
        return hit == null || hit.field_72313_a != RayTraceResult.Type.BLOCK
                || !(world.func_180495_p(hit.func_178782_a()).func_177230_c() instanceof IBlockIgnitableWithIgniterItem);
    }

    @Inject(method = "func_77659_a", at = @At("HEAD"), cancellable = true)
    private void decaychain$startOnTorch(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        if (!decaychain$lightsHeldTorch(world, player, hand)) return;
        player.func_184598_c(hand);
        cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, player.func_184586_b(hand)));
    }

    @Inject(method = "onUsingTick", at = @At("HEAD"), cancellable = true)
    private void decaychain$keepUsingOnTorch(ItemStack stack, EntityLivingBase entity, int count, CallbackInfo ci) {
        if (entity instanceof EntityPlayer && decaychain$lightsHeldTorch(entity.field_70170_p, (EntityPlayer) entity, entity.func_184600_cs())) ci.cancel();
    }

    @Inject(method = "func_77654_b", at = @At("HEAD"), cancellable = true)
    private void decaychain$finishOnTorch(ItemStack stack, World world, EntityLivingBase entity, CallbackInfoReturnable<ItemStack> cir) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        EnumHand hand = player.func_184600_cs();
        if (!decaychain$lightsHeldTorch(world, player, hand)) return;
        if (!world.field_72995_K && LitTorches.light(player, hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND)) {
            damageItem(stack, player);
            player.func_184811_cZ().func_185145_a(this, getCooldownTicks());
        }
        cir.setReturnValue(stack);
    }
}
