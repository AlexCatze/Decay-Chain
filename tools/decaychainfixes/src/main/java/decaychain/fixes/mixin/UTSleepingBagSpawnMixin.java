package decaychain.fixes.mixin;

import c4.comforts.common.blocks.BlockSleepingBag;
import mod.acgaming.universaltweaks.tweaks.entities.sleeping.UTSleepingTweak;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Decay Chain: sleeping is disabled and right-clicking a bed sets the spawn point (Universal Tweaks "ANYTIME"), so the
 * home point is a bed you have to defend. A Comforts sleeping bag would make that home portable - it no longer sets
 * the spawn point; the player is told so instead of "spawn point set".
 */
@Mixin(value = UTSleepingTweak.class, remap = false)
public abstract class UTSleepingBagSpawnMixin {
    @Redirect(method = "utSleepTweak", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/entity/player/EntityPlayer;func_180473_a(Lnet/minecraft/util/math/BlockPos;Z)V"))
    private static void decaychain$noBagSpawn(EntityPlayer player, BlockPos pos, boolean forced, PlayerSleepInBedEvent event) {
        if (!decaychain$isSleepingBag(event)) player.func_180473_a(pos, forced);
    }

    @Redirect(method = "utSleepTweak", at = @At(value = "INVOKE", ordinal = 0,
              target = "Lnet/minecraft/entity/player/EntityPlayer;func_146105_b(Lnet/minecraft/util/text/ITextComponent;Z)V"))
    private static void decaychain$bagMessage(EntityPlayer player, ITextComponent message, boolean actionBar, PlayerSleepInBedEvent event) {
        player.func_146105_b(decaychain$isSleepingBag(event) ? new TextComponentTranslation("decaychain.sleeping_bag.no_spawn") : message, actionBar);
    }

    @Unique
    private static boolean decaychain$isSleepingBag(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        return event.getPos() != null && player.field_70170_p.func_180495_p(event.getPos()).func_177230_c() instanceof BlockSleepingBag;
    }
}
