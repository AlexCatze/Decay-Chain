package decaychain.fixes;

import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Reskillable's item locks for guns. Reskillable enforces locks on the vanilla click events, but HBM and Techguns guns
 * fire from their own keybind/shoot packets, so a locked gun fired anyway. The gun mixins ask here before firing.
 */
public final class GunLocks {
    private static final Map<UUID, Long> lastTold = new HashMap<>();   // automatic triggers ask every tick

    private GunLocks() {
    }

    /** True if the player may not use this gun; tells them why when tell is set (server side only). */
    public static boolean blocked(EntityPlayer player, ItemStack stack, boolean tell) {
        if (player == null || stack == null || stack.func_190926_b()) return false;
        if (player.func_184812_l_() && !ConfigHandler.enforceOnCreative) return false;
        if (LevelLockHandler.canPlayerUseItem(player, stack)) return false;
        if (tell && !player.field_70170_p.field_72995_K) {
            long now = player.field_70170_p.func_82737_E();
            Long last = lastTold.get(player.func_110124_au());
            if (last == null || now - last >= 20 || now < last) {
                lastTold.put(player.func_110124_au(), now);
                LevelLockHandler.tellPlayer(player, stack, "reskillable.misc.locked.item");
            }
        }
        return true;
    }
}
