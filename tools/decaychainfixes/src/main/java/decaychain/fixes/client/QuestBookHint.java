package decaychain.fixes.client;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;

/**
 * Decay Chain: players start without a quest book item, so a tutorial toast (like vanilla's "Open your inventory")
 * tells them which key opens Better Questing. It stays on screen until the quest book is opened for the first time;
 * after that a marker file (config/decaychain/questbook_opened) keeps it from coming back.
 */
@Mod.EventBusSubscriber(modid = "decaychainfixes", value = Side.CLIENT)
public final class QuestBookHint {
    private static final String KEY = "key.betterquesting.quests";
    private static final int DELAY_TICKS = 60;   // let the world appear first

    private static Boolean opened;
    private static TutorialToast toast;
    private static int inWorldTicks;

    private QuestBookHint() {
    }

    private static File marker() {
        return new File(Minecraft.func_71410_x().field_71412_D, "config/decaychain/questbook_opened");
    }

    private static boolean opened() {
        if (opened == null) opened = marker().exists();
        return opened;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || opened()) return;
        Minecraft mc = Minecraft.func_71410_x();
        if (mc.field_71441_e == null || mc.field_71439_g == null) {
            inWorldTicks = 0;
            if (toast != null) {
                toast.func_193670_a();   // left the world: hide it, show a fresh one next time
                toast = null;
            }
            return;
        }
        if (toast != null || ++inWorldTicks < DELAY_TICKS) return;
        toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK,
                new TextComponentTranslation("decaychain.questbook_hint.title"),
                new TextComponentTranslation("decaychain.questbook_hint.text", keyName(mc)), false);
        mc.func_193033_an().func_192988_a(toast);
    }

    private static String keyName(Minecraft mc) {
        for (KeyBinding binding : mc.field_71474_y.field_74324_K) {
            if (KEY.equals(binding.func_151464_g())) return GameSettings.func_74298_c(binding.func_151463_i());
        }
        return "`";
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() == null || opened() || !event.getGui().getClass().getName().startsWith("betterquesting.")) return;
        opened = true;
        if (toast != null) {
            toast.func_193670_a();   // hide
            toast = null;
        }
        File marker = marker();
        try {
            marker.getParentFile().mkdirs();
            marker.createNewFile();
        } catch (IOException e) {
            LogManager.getLogger("decaychainfixes").warn("Could not save the quest book hint marker {}", marker, e);
        }
    }
}
