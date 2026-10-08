// Decay Chain: custom water items.
// Toxic Water is what you get from wasteland water sources (see postInit/water.groovy).
// Drinking it poisons and irradiates you; purify it in an HBM Chemical Plant (config/hbmRecipes/hbmChemicalPlant.json).

import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.item.EnumAction
import net.minecraft.world.World
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.PotionEffect
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import com.hbm.capability.HbmLivingProps

content.registerItem('toxic_water_bottle', new ItemFood(0, 0.0f, false) {
    EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK
    }

    ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack result = super.onItemUseFinish(stack, world, entity)
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE)
            if (result.isEmpty()) return bottle
            if (!player.inventory.addItemStackToInventory(bottle)) player.dropItem(bottle, false)
        }
        return result
    }

    void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.POISON, 600, 1))
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 0))
            player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 900, 1))
            HbmLivingProps.incrementRadiation(player, 50.0d)
        }
    }
}.setAlwaysEdible().setMaxStackSize(16).setTranslationKey('decaychain.toxic_water_bottle'))
