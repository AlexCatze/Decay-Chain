// Decay Chain: biome-dependent water quality.
// - Drinking straight from water blocks is disabled in Simple Difficulty (ThirstDrinkBlocks=false),
//   so every drink goes through a bottle or canteen.
// - Normal biomes: vanilla water bottle / Simple Difficulty canteen (CanteenType 0) = dirty water
//   (parasite chance, see config/simpledifficulty.cfg). Boil it or use a charcoal filter.
// - Wasteland and nuke-crater biomes: bottles fill with Toxic Water, canteens and buckets refuse.

import classes.DecayBiomes
import classes.DecayRules
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

CANTEENS = ['simpledifficulty:canteen', 'simpledifficulty:iron_canteen'] as Set

// Returns the water block the player is looking at, or null.
BlockPos targetedWater(World world, EntityPlayer player) {
    Vec3d eyes = player.getPositionEyes(1.0f)
    double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()
    Vec3d end = eyes.add(player.getLookVec().scale(reach))
    RayTraceResult hit = world.rayTraceBlocks(eyes, end, true)
    if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) return null
    BlockPos pos = hit.getBlockPos()
    return world.getBlockState(pos).getMaterial() == Material.WATER ? pos : null
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
