package decaychain.fixes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/** Bug fixes for third-party mods in Decay Chain. The work is done by the mixins (META-INF manifest: MixinConfigs). */
@Mod(modid = "decaychainfixes", name = "Decay Chain Fixes", version = "1.0.0", acceptableRemoteVersions = "*")
public class DecayChainFixes {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) decaychain.fixes.client.PanoramaCapture.register();   // client-only classes
    }
}
