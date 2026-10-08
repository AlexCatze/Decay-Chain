package decaychain.fixes.mixin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.pam.harvestcraft.blocks.blocks.BlockBaseGarden;

/**
 * Pam's HarvestCraft 1.12.2zg: gardens only accept the exact vanilla soil blocks (grass/dirt, sand for arid gardens).
 * Most Biomes O' Plenty biomes are surfaced with biomesoplenty:grass/dirt, so gardens almost never generated (or
 * spread) there. Also accept the BoP equivalents.
 */
@Mixin(value = BlockBaseGarden.class, remap = false)
public abstract class HarvestCraftGardenSoilMixin {
    @Unique private static final Set<String> PLAINS_SOIL = new HashSet<>(Arrays.asList("biomesoplenty:grass", "biomesoplenty:dirt"));
    @Unique private static final Set<String> DESERT_SOIL = new HashSet<>(Arrays.asList("biomesoplenty:white_sand"));

    @Shadow @Final private BlockBaseGarden.Region region;

    @Inject(method = "checkSoilBlock", at = @At("RETURN"), cancellable = true)
    private void decaychain$bopSoil(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        Block soil = world.func_180495_p(pos.func_177977_b()).func_177230_c();
        if (soil.getRegistryName() == null) return;
        Set<String> accepted = region == BlockBaseGarden.Region.DESERT ? DESERT_SOIL : PLAINS_SOIL;
        if (accepted.contains(soil.getRegistryName().toString())) cir.setReturnValue(true);
    }
}
