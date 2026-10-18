package decaychain.fixes.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
 * Choosing the meal: Techguns ate the first food in its three slots as soon as the bar was not full and banked whatever
 * did not fit. Now a food is eaten only when all of it fits into the empty part of the bar, foods worth nothing to the
 * player (The Spice of Life has worn them down to 0 hunger) are skipped, and of the foods that qualify the one The Spice
 * of Life reduces the least (value for this player / normal value) is eaten - the bigger meal on a tie.
 */
@Mixin(value = TGTickHandler.class, remap = false)
public abstract class TechgunsFoodSlotMixin {
    @Unique private static final ThreadLocal<FoodValues> decaychain$meal = new ThreadLocal<>();

    @Redirect(method = "onPlayerTick", at = @At(value = "INVOKE",
              target = "Ltechguns/util/InventoryUtil;consumeFood(Lnet/minecraft/util/NonNullList;II)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack decaychain$chooseMeal(NonNullList<ItemStack> slots, int start, int end, TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        int missing = 20 - player.func_71024_bL().func_75116_a();
        int best = -1, bestHunger = 0;
        float bestShare = -1.0f;
        for (int i = start; i < end; i++) {
            ItemStack stack = slots.get(i);
            if (stack.func_190926_b() || !(stack.func_77973_b() instanceof ItemFood)) continue;
            FoodValues forPlayer = AppleCoreAPI.accessor.getFoodValuesForPlayer(stack, player);
            if (forPlayer.hunger <= 0 || forPlayer.hunger > missing) continue;     // worthless now, or would not all fit
            FoodValues normal = AppleCoreAPI.accessor.getFoodValues(stack);
            float share = normal.hunger > 0 ? (float) forPlayer.hunger / normal.hunger : 1.0f;
            if (share > bestShare || share == bestShare && forPlayer.hunger > bestHunger) {
                best = i;
                bestShare = share;
                bestHunger = forPlayer.hunger;
            }
        }
        if (best < 0) return ItemStack.field_190927_a;
        ItemStack meal = slots.get(best).func_77946_l();
        meal.func_190920_e(1);
        slots.get(best).func_190918_g(1);
        if (slots.get(best).func_190926_b()) slots.set(best, ItemStack.field_190927_a);
        return meal;
    }

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
