package decaychain.fixes.torch;

import com.codetaylor.mc.pyrotech.modules.ignition.ModuleIgnition;
import com.codetaylor.mc.pyrotech.modules.ignition.ModuleIgnitionConfig;
import com.codetaylor.mc.pyrotech.modules.ignition.block.spi.BlockTorchBase;
import com.codetaylor.mc.pyrotech.modules.ignition.tile.spi.TileTorchBase;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Decay Chain: Pyrotech torches you can carry lit (Hardcore Darkness makes the night pitch black).
 * - A torch item with damage value 1 is lit (its model is the lit torch, see LitTorchModels); its NBT keeps the burn
 *   time left. It lights the area while held in either hand (Moving Light Source, MovingLightSourceMixins).
 * - Light it like a placed torch: use a Pyrotech igniter (PyrotechIgniterMixin) or flint and steel with the torch in
 *   the other hand. Lighting a stack lights one torch.
 * - It burns as long as a placed torch (the Pyrotech config: duration, burns up or not) and is doused by water, and by
 *   rain if that torch type is. A doused or partly burnt torch keeps its remaining time.
 * - Placing a lit torch places it lit with the time left; placing a partly burnt one keeps its time too.
 */
@Mod.EventBusSubscriber(modid = "decaychainfixes")
public final class LitTorches {
    public static final int LIT_META = 1;
    private static final String BURN_TAG = "decaychain:burn";          // ticks left
    private static final String STAMP_TAG = "decaychain:burnStamp";    // world time the burn time was last counted
    private static final int UPDATE_INTERVAL = 10;
    private static final Random RNG = new Random();

    private LitTorches() {
    }

    // ---- what a torch is --------------------------------------------------------------------------------------------

    public static boolean isTorch(ItemStack stack) {
        if (stack == null || stack.func_190926_b()) return false;
        Item item = stack.func_77973_b();
        return item == Item.func_150898_a(ModuleIgnition.Blocks.TORCH_FIBER) || item == Item.func_150898_a(ModuleIgnition.Blocks.TORCH_STONE);
    }

    public static boolean isLit(ItemStack stack) {
        return isTorch(stack) && stack.func_77952_i() == LIT_META;
    }

    public static boolean isUnlit(ItemStack stack) {
        return isTorch(stack) && stack.func_77952_i() != LIT_META;
    }

    private static boolean isFiber(ItemStack stack) {
        return stack.func_77973_b() == Item.func_150898_a(ModuleIgnition.Blocks.TORCH_FIBER);
    }

    /** Light level of a held torch: the placed lit torch's, 0 when unlit. */
    public static int lightLevel(ItemStack stack) {
        if (!isLit(stack)) return 0;
        int value = isFiber(stack) ? ModuleIgnitionConfig.FIBER_TORCH.LIGHT_VALUE : ModuleIgnitionConfig.STONE_TORCH.LIGHT_VALUE;
        return Math.max(0, Math.min(15, value));
    }

    static boolean burnsUp(ItemStack stack) {
        return isFiber(stack) ? ModuleIgnitionConfig.FIBER_TORCH.BURNS_UP : ModuleIgnitionConfig.STONE_TORCH.BURNS_UP;
    }

    private static boolean dousedByRain(ItemStack stack) {
        return isFiber(stack) ? ModuleIgnitionConfig.FIBER_TORCH.EXTINGUISHED_BY_RAIN : ModuleIgnitionConfig.STONE_TORCH.EXTINGUISHED_BY_RAIN;
    }

    /** A fresh torch's burn time, rolled like a placed torch's (duration +- variant). */
    private static int freshDuration(ItemStack stack) {
        int duration = isFiber(stack) ? ModuleIgnitionConfig.FIBER_TORCH.DURATION : ModuleIgnitionConfig.STONE_TORCH.DURATION;
        int variant = isFiber(stack) ? ModuleIgnitionConfig.FIBER_TORCH.DURATION_VARIANT : ModuleIgnitionConfig.STONE_TORCH.DURATION_VARIANT;
        return (int) (duration + (RNG.nextDouble() * 2.0 - 1.0) * variant);
    }

    /** Ticks of burning left (counting the time since the last update if it is lit), or -1 for a fresh torch. */
    public static int burnLeft(ItemStack stack, World world) {
        NBTTagCompound tag = stack.func_77978_p();
        if (tag == null || !tag.func_74764_b(BURN_TAG)) return -1;
        int left = tag.func_74762_e(BURN_TAG);
        if (isLit(stack) && burnsUp(stack) && world != null) {
            left -= (int) Math.max(0, world.func_82737_E() - tag.func_74763_f(STAMP_TAG));
        }
        return left;
    }

    private static void setBurn(ItemStack stack, int left, World world) {
        NBTTagCompound tag = stack.func_77942_o() ? stack.func_77978_p() : new NBTTagCompound();
        tag.func_74768_a(BURN_TAG, left);
        tag.func_74772_a(STAMP_TAG, world.func_82737_E());
        stack.func_77982_d(tag);
    }

    // ---- lighting and dousing ----------------------------------------------------------------------------------------

    /** Lights the unlit torch in the player's hand (one of a stack). Server side; false if it can't burn here. */
    public static boolean light(EntityPlayer player, EnumHand hand) {
        World world = player.field_70170_p;
        ItemStack held = player.func_184586_b(hand);
        if (!isUnlit(held) || world.field_72995_K) return false;
        if (dousedByRain(held) && world.func_175727_C(player.func_180425_c().func_177984_a())) return false;   // as placed
        ItemStack torch = held.func_77979_a(1);   // split one off; the rest goes back to the inventory
        int left = burnLeft(torch, world);
        torch.func_77964_b(LIT_META);
        setBurn(torch, left < 0 ? freshDuration(torch) : left, world);
        ItemStack rest = player.func_184586_b(hand);
        player.func_184611_a(hand, torch);
        if (!rest.func_190926_b() && !player.field_71071_by.func_70441_a(rest)) player.func_71019_a(rest, false);
        world.func_184133_a(null, player.func_180425_c(), SoundEvents.field_187649_bu, SoundCategory.PLAYERS, 1.0F, RNG.nextFloat() * 0.4F + 0.8F);
        return true;
    }

    private static void douse(ItemStack stack, World world) {
        int left = burnLeft(stack, world);
        stack.func_77964_b(0);
        setBurn(stack, left, world);
    }

    // ---- events ----------------------------------------------------------------------------------------------------

    /** Lit torches in a player's inventory burn down; water douses the ones in hand, and so does rain for some. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.field_70170_p;
        if (event.phase != TickEvent.Phase.END || world.field_72995_K || player.field_70173_aa % UPDATE_INTERVAL != 0) return;
        boolean wet = player.func_70090_H();
        boolean rain = world.func_175727_C(player.func_180425_c().func_177984_a());
        burn(world, player.field_71071_by.field_70462_a, player, wet, rain, true);
        burn(world, player.field_71071_by.field_184439_c, player, wet, rain, false);
    }

    private static void burn(World world, NonNullList<ItemStack> slots, EntityPlayer player, boolean wet, boolean rain, boolean main) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            if (!isLit(stack)) continue;
            boolean inHand = !main || i == player.field_71071_by.field_70461_c;
            if (inHand && (wet || rain && dousedByRain(stack))) {
                douse(stack, world);
                world.func_184133_a(null, player.func_180425_c(), SoundEvents.field_187646_bt, SoundCategory.PLAYERS, 0.5F, 2.6F);
                continue;
            }
            if (!burnsUp(stack)) continue;
            int left = burnLeft(stack, world);
            if (left <= 0) {
                slots.set(i, ItemStack.field_190927_a);   // burnt out, like a placed torch
                world.func_184133_a(null, player.func_180425_c(), SoundEvents.field_187646_bt, SoundCategory.PLAYERS, 0.5F, 2.6F);
            } else {
                setBurn(stack, left, world);
            }
        }
    }

    /** Flint and steel lights the torch in the other hand when used in the air, as it lights a placed torch. */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        EnumHand other = event.getHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        ItemStack igniter = event.getItemStack();
        if (igniter.func_77973_b() != Items.field_151033_d || !isUnlit(player.func_184586_b(other))) return;
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.util.EnumActionResult.SUCCESS);
        if (light(player, other)) igniter.func_77972_a(1, player);
    }

    /** Placing a lit torch places it lit, with the burn time it had left. */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlace(BlockEvent.PlaceEvent event) {
        if (event.isCanceled() || event.getPlayer() == null) return;
        IBlockState placed = event.getPlacedBlock();
        Block block = placed.func_177230_c();
        if (block != ModuleIgnition.Blocks.TORCH_FIBER && block != ModuleIgnition.Blocks.TORCH_STONE) return;
        World world = event.getWorld();
        ItemStack stack = event.getPlayer().func_184586_b(event.getHand());
        if (!isTorch(stack)) return;
        int left = burnLeft(stack, world);
        TileEntity tile = world.func_175625_s(event.getPos());
        BlockPos pos = event.getPos();
        if (isLit(stack)) {
            world.func_175656_a(pos, placed.func_177226_a(BlockTorchBase.TYPE, BlockTorchBase.EnumType.LIT));
            tile = world.func_175625_s(pos);
        }
        if (left >= 0 && tile instanceof TileTorchBase) {
            ITorchTile torch = (ITorchTile) tile;
            torch.decaychain$setDuration(left);
            torch.decaychain$setLastTimeStamp(isLit(stack) ? world.func_82737_E() : 0L);
            tile.func_70296_d();
        }
    }
}
