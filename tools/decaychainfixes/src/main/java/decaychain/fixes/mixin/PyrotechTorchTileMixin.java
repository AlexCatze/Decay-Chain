package decaychain.fixes.mixin;

import com.codetaylor.mc.pyrotech.modules.ignition.tile.spi.TileTorchBase;
import decaychain.fixes.torch.ITorchTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Decay Chain lit torches (decaychain.fixes.torch.LitTorches): a placed torch continues the item's burn time. */
@Mixin(value = TileTorchBase.class, remap = false)
public abstract class PyrotechTorchTileMixin implements ITorchTile {
    @Shadow private int duration;
    @Shadow private long lastTimeStamp;

    @Override
    public void decaychain$setDuration(int ticks) {
        duration = ticks;
    }

    @Override
    public void decaychain$setLastTimeStamp(long worldTime) {
        lastTimeStamp = worldTime;
    }
}
