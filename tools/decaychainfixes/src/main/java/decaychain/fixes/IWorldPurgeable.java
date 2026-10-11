package decaychain.fixes;

import net.minecraft.world.World;

/** Implemented by mixins on objects that cache per-world data and must forget an unloaded world. */
public interface IWorldPurgeable {
    void decaychain$purgeWorld(World world);
}
