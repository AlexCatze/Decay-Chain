package net.minecraftforge.event.world;

import net.minecraft.world.World;

/**
 * Compile-time stub only (not packaged). The server Forge jar references Minecraft classes by their obfuscated names,
 * so getWorld() would not type-check against the World stub; at runtime the real class returns the deobfuscated World.
 */
public class WorldEvent {
    public World getWorld() {
        return null;
    }

    public static class Load extends WorldEvent {
    }

    public static class Unload extends WorldEvent {
    }
}
