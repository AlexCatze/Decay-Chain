package decaychain.fixes.torch;

/** Implemented on Pyrotech's TileTorchBase by PyrotechTorchTileMixin: lets a placed torch take the item's burn time. */
public interface ITorchTile {
    void decaychain$setDuration(int ticks);

    void decaychain$setLastTimeStamp(long worldTime);
}
