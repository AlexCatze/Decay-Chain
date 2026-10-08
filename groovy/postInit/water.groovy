// Decay Chain: biome-dependent water quality.
// - Drinking straight from water blocks (empty hand, Simple Difficulty ThirstDrinkBlocks=true) works like a dirty
//   canteen in normal biomes, so a player who spawns in the desert can reach water before dying of thirst.
//   Ocean/beach water is salty (Thirsty effect, nausea); wasteland water is toxic (poison, nausea, radiation).
// - Normal biomes: vanilla water bottle / Simple Difficulty canteen (CanteenType 0) = dirty water
//   (parasite chance, see config/simpledifficulty.cfg). Boil it or use a charcoal filter.
// - Wasteland and nuke-crater biomes: bottles fill with Toxic Water, canteens and buckets refuse.

import classes.DecayBiomes
import classes.DecayRules
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import com.hbm.capability.HbmLivingProps
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

CANTEENS = ['simpledifficulty:canteen', 'simpledifficulty:iron_canteen'] as Set
THIRSTY = ForgeRegistries.POTIONS.getValue(new ResourceLocation('simpledifficulty:thirsty'))

// Returns the water block the player is looking at, or null.
// Walks the look ray in small steps instead of World.rayTraceBlocks: Groovy cannot touch Vec3d on a dedicated server
// (building its metaclass fails on client-only members), and getPositionEyes() is client-only in 1.12.
BlockPos targetedWater(World world, EntityPlayer player) {
    double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()
    double yaw = Math.toRadians(player.rotationYaw), pitch = Math.toRadians(player.rotationPitch)
    double dx = -Math.sin(yaw) * Math.cos(pitch), dy = -Math.sin(pitch), dz = Math.cos(yaw) * Math.cos(pitch)
    double ex = player.posX, ey = player.posY + player.getEyeHeight(), ez = player.posZ
    for (double d = 0.0d; d <= reach; d += 0.1d) {
        BlockPos pos = new BlockPos(ex + dx * d, ey + dy * d, ez + dz * d)
        Material material = world.getBlockState(pos).getMaterial()
        if (material == Material.WATER) return pos
        if (material.blocksMovement()) return null
    }
    return null
}

boolean isToxic(World world, BlockPos pos) {
    return DecayBiomes.isWasteland(world, pos)
}

eventManager.listen { PlayerInteractEvent.RightClickItem event ->
    DecayRules.guard('water_fill') {
        ItemStack stack = event.getItemStack()
        if (stack.isEmpty()) return
        String id = DecayRules.itemId(stack)
        boolean bottle = id == 'minecraft:glass_bottle'
        if (!bottle && !CANTEENS.contains(id)) return

        World world = event.getWorld()
        EntityPlayer player = event.getEntityPlayer()
        BlockPos pos = targetedWater(world, player)
        if (pos == null || !isToxic(world, pos)) return

        event.setCanceled(true)
        event.setCancellationResult(EnumActionResult.SUCCESS)
        if (world.isRemote) return

        if (bottle) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1)
            ItemStack toxic = item('decaychain:toxic_water_bottle')
            if (!player.inventory.addItemStackToInventory(toxic)) player.dropItem(toxic, false)
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 0.8f)
        } else {
            player.sendStatusMessage(new TextComponentTranslation('decaychain.water.toxic_canteen'), true)
        }
    }
}

eventManager.listen { FillBucketEvent event ->
    DecayRules.guard('water_bucket') {
        RayTraceResult target = event.getTarget()
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) return
        World world = event.getWorld()
        BlockPos pos = target.getBlockPos()
        if (world.getBlockState(pos).getMaterial() != Material.WATER || !isToxic(world, pos)) return
        event.setCanceled(true)
        if (!world.isRemote) {
            event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation('decaychain.water.toxic_bucket'), true)
        }
    }
}

boolean isSalty(World world, BlockPos pos) {
    def biome = world.getBiome(pos)
    return BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
}

// Simple Difficulty handles block drinking from a client packet with no event of its own; after a successful drink it
// plays the drink sound through the player (server side), which fires PlaySoundAtEntityEvent. Drinking an item plays
// the same sound but with the hand in use, and the rain drink needs the player looking straight up (no water in sight).
eventManager.listen { PlaySoundAtEntityEvent event ->
    DecayRules.guard('water_sip') {
        if (!(event.getEntity() instanceof EntityPlayer) || event.getSound() == null) return
        EntityPlayer player = (EntityPlayer) event.getEntity()
        World world = player.world
        if (world.isRemote || player.isHandActive()) return
        if (ForgeRegistries.SOUND_EVENTS.getKey(event.getSound()).toString() != 'minecraft:entity.generic.drink') return
        BlockPos pos = targetedWater(world, player)
        if (pos == null) return
        if (isToxic(world, pos)) {
            player.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 0))
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0))
            HbmLivingProps.incrementRadiation(player, 20.0d)
            player.sendStatusMessage(new TextComponentTranslation('decaychain.water.toxic_sip'), true)
        } else if (isSalty(world, pos)) {
            if (THIRSTY != null) player.addPotionEffect(new PotionEffect(THIRSTY, 600, 0))
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0))
            player.sendStatusMessage(new TextComponentTranslation('decaychain.water.salty_sip'), true)
        }
    }
}
