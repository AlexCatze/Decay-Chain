package decaychain.fixes.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodValues;
import techguns.events.TGTickHandler;

/**
 * Techguns 2.2.0.1: food eaten from the Techguns food slots filled the hunger bar with the item's raw values. The tick
 * handler runs the normal ItemFood.onItemUseFinish (effects, The Spice of Life's food history), restores the food stats
 * it saved before, and then adds getHealAmount / getSaturationModifier itself - the item's own numbers, which Hunger
 * Overhaul has divided by 4 and which skip every AppleCore food event (Decay Chain's meat and can values, Spice of
 * Life's diminishing returns). A cooked porkchop filled 2 instead of 8.
 * Fix: right before the meal, ask AppleCore what this food is worth to this player (the same values eating it by hand
 * gives) and hand those to Techguns in place of the raw numbers. Client-side prediction keeps the raw values; the server
 * syncs the real ones.
 */
@Mixin(value = TGTickHandler.class, remap = false)
public abstract class TechgunsFoodSlotMixin {
    @Unique private static final ThreadLocal<FoodValues> decaychain$meal = new ThreadLocal<>();

    @Redirect(method = "onPlayerTick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/item/ItemFood;func_77654_b(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack decaychain$valueThenEat(ItemFood food, ItemStack stack, World world, EntityLivingBase eater) {
        decaychain$meal.set(eater instanceof EntityPlayer
                ? AppleCoreAPI.accessor.getFoodValuesForPlayer(stack, (EntityPlayer) eater) : null);
        return food.func_77654_b(stack, world, eater);
    }

    @Redirect(method = "onPlayerTick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/item/ItemFood;func_150905_g(Lnet/minecraft/item/ItemStack;)I"))
    private static int decaychain$hunger(ItemFood food, ItemStack stack) {
        FoodValues meal = decaychain$meal.get();
        return meal != null ? meal.hunger : food.func_150905_g(stack);
    }

    @Redirect(method = "onPlayerTick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/item/ItemFood;func_150906_h(Lnet/minecraft/item/ItemStack;)F"))
    private static float decaychain$saturation(ItemFood food, ItemStack stack) {
        FoodValues meal = decaychain$meal.get();
        return meal != null ? meal.saturationModifier : food.func_150906_h(stack);
    }
}
