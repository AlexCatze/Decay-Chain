package decaychain.fixes.torch;

import com.codetaylor.mc.pyrotech.modules.ignition.ModuleIgnition;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;

/**
 * Client side of LitTorches: a lit torch item (damage value LitTorches.LIT_META) shows Pyrotech's lit torch model (the
 * unlit one keeps Pyrotech's facing=up,type=unlit), and torch tooltips show the burn time. The client model classes
 * are called by reflection - the fix jar is compiled against the server classes only.
 */
@Mod.EventBusSubscriber(modid = "decaychainfixes", value = Side.CLIENT)
public final class LitTorchClient {
    private LitTorchClient() {
    }

    @SubscribeEvent
    public static void onModels(ModelRegistryEvent event) {
        try {
            Class<?> location = Class.forName("net.minecraft.client.renderer.block.model.ModelResourceLocation");
            Constructor<?> create = location.getConstructor(ResourceLocation.class, String.class);
            Method register = Class.forName("net.minecraftforge.client.model.ModelLoader")
                    .getMethod("setCustomModelResourceLocation", Item.class, int.class, location);
            for (Block torch : new Block[]{ModuleIgnition.Blocks.TORCH_FIBER, ModuleIgnition.Blocks.TORCH_STONE}) {
                register.invoke(null, Item.func_150898_a(torch), LitTorches.LIT_META, create.newInstance(torch.getRegistryName(), "facing=up,type=lit"));
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            LogManager.getLogger("decaychainfixes").error("Could not register the lit torch item models", e);
        }
    }

    /** Shows whether a torch is lit and how long it will still burn. */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!LitTorches.isTorch(stack)) return;
        List<String> tooltip = event.getToolTip();
        World world = event.getEntityPlayer() != null ? event.getEntityPlayer().field_70170_p : null;
        int left = LitTorches.burnLeft(stack, world);
        String minutes = left < 0 ? "" : String.valueOf(Math.max(1, (left + 1199) / 1200));
        if (LitTorches.isLit(stack)) {
            tooltip.add(1, LitTorches.burnsUp(stack) ? I18n.func_74837_a("decaychain.torch.lit", minutes.isEmpty() ? "?" : minutes)
                                          : I18n.func_74838_a("decaychain.torch.lit_forever"));
        } else {
            if (left >= 0 && LitTorches.burnsUp(stack)) tooltip.add(1, I18n.func_74837_a("decaychain.torch.used", minutes));
            tooltip.add(I18n.func_74838_a("decaychain.torch.how_to_light"));
        }
    }
}
