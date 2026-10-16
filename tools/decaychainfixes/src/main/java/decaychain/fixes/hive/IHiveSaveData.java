package decaychain.fixes.hive;

import java.util.function.Supplier;
import net.minecraft.world.World;

/** Implemented on SRPSaveData by SRPSaveDataMixin. */
public interface IHiveSaveData {
    /**
     * Runs SRP code for one hive: the hive's points, phase, cooldown and generation are swapped into SRP's record of
     * the hive dimension, op runs (SRP's own rules apply unchanged), and the results are stored back into the hive.
     */
    <T> T decaychain$runForHive(Hive hive, World world, Supplier<T> op);

    /** Sets SRP's record of a dimension directly (the strongest hive), without SRP's phase-change side effects. */
    void decaychain$setDimRecord(int dim, byte phase, int points);
}
