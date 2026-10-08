// Decay Chain: random spawn within ~500 blocks of the world spawn, on first join and on every respawn
// without a valid bed, so a bad world spawn (SRP colony, crater, ocean) is not a death loop.
// Logic lives in classes/DecaySpawn.groovy.

import classes.DecayRules
import classes.DecaySpawn
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.stats.StatList
import net.minecraftforge.fml.common.gameevent.PlayerEvent

eventManager.listen { PlayerEvent.PlayerRespawnEvent event ->
    DecayRules.guard('spawn_spread') {
        def player = event.player
        if (event.isEndConquered() || player.world.provider.getDimension() != 0) return
        if (DecaySpawn.hasValidBed(player, player.world)) return
        DecaySpawn.spread(player)
    }
}

eventManager.listen { PlayerEvent.PlayerLoggedInEvent event ->
    DecayRules.guard('spawn_spread') {
        def player = event.player
        NBTTagCompound persisted = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
        if (persisted.getBoolean(DecaySpawn.SPAWNED_TAG)) return
        persisted.setBoolean(DecaySpawn.SPAWNED_TAG, true)
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted)
        // brand-new players only; players from before this script existed have already left the game once
        if (player.getStatFile().readStat(StatList.LEAVE_GAME) > 0) return
        if (player.world.provider.getDimension() == 0 && !DecaySpawn.hasValidBed(player, player.world)) DecaySpawn.spread(player)
    }
}
