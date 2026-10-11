package decaychain.fixes.mixin;

import com.codetaylor.mc.athenaeum.network.tile.TileDataService;
import com.codetaylor.mc.athenaeum.network.tile.TileDataTracker;
import com.codetaylor.mc.athenaeum.network.tile.spi.TileEntityDataContainerBase;
import decaychain.fixes.IWorldPurgeable;
import decaychain.fixes.WorldLeakFixes;
import java.util.List;
import java.util.Map;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Athenaeum 1.19.6 (Pyrotech tile sync): the per-thread tracker list/map only drop a tile once it is invalidated, which
 * never happens when the whole world unloads - and the client thread never prunes at all. The tracked tiles keep their
 * World (and its chunks) alive after quitting to the menu. Forget the tiles of a world when it unloads.
 */
@Mixin(value = TileDataService.class, remap = false)
public abstract class AthenaeumTileDataServiceMixin implements IWorldPurgeable {
    @Shadow @Final private ThreadLocal<List<TileDataTracker>> dataTrackerList;
    @Shadow @Final private ThreadLocal<Map<TileEntityDataContainerBase, TileDataTracker>> dataTrackerMap;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void decaychain$register(CallbackInfo ci) {
        WorldLeakFixes.register(this);
    }

    @Override
    public void decaychain$purgeWorld(World world) {
        // the unload event fires on the thread that owns the world, so these are that thread's collections
        dataTrackerList.get().removeIf(tracker -> tracker.getTile().func_145831_w() == world);
        dataTrackerMap.get().keySet().removeIf(tile -> tile.func_145831_w() == world);
    }
}
