// Decay Chain: smashing glass cuts you. Breaking any glass block without a Glass Cutter (Glassential) causes
// bleeding - climbing through a dungeon window is not free. The Glass Cutter also picks glass up intact.

import classes.DecayRules
import net.minecraft.block.material.Material
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries

BLEED = ForgeRegistries.POTIONS.getValue(new ResourceLocation('lycanitesmobs:bleed'))

eventManager.listen { BlockEvent.BreakEvent event ->
    DecayRules.guard('glass_bleed') {
        def player = event.getPlayer()
        if (BLEED == null || player == null || player.capabilities.isCreativeMode) return
        if (event.getState().getMaterial() != Material.GLASS) return
        if (DecayRules.itemId(player.getHeldItemMainhand()) == 'glassential:glass_cutter_iron') return
        player.addPotionEffect(new PotionEffect(BLEED, 120, 0))
    }
}
