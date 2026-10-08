// Decay Chain: helpers for event scripts.
// NEVER call a method directly on a mod Item/Block/Biome object from Groovy: Groovy then builds a metaclass for
// the mod class, and many HBM classes reference client-only types - on a dedicated server that throws, and an
// exception inside an event can crash the server. Instead:
//   - registry names via ForgeRegistries.*.getKey(obj) (the mod object is only an argument),
//   - behaviour via vanilla ItemStack/IBlockState methods, which call into the mod from Java.
// (@CompileStatic and java.lang.reflect are not usable inside GroovyScript.)

import net.minecraft.block.Block
import net.minecraft.block.BlockLog
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.registry.ForgeRegistries

class DecayRules {
    static final Set<String> STRUCTURE_IRON = new HashSet<>()
    static final Set<String> STRUCTURE_STEEL = new HashSet<>()
    // Pickaxe tiers are read from mining speed on plain stone (efficiency grows with tier):
    // measured: flint 3.8, iron 6, HBM desh 7.5, steel/diamond 8, titanium 9. Gold (12) is soft: excluded by name.
    static final float IRON_TIER_SPEED = 6.0f
    static final float STEEL_TIER_SPEED = 7.5f
    static final Set<String> SOFT_PICKS = ['minecraft:golden_pickaxe'] as Set

    static String itemId(ItemStack stack) {
        return stack.isEmpty() ? '' : ForgeRegistries.ITEMS.getKey(stack.getItem()).toString()
    }

    static String blockId(IBlockState state) {
        return ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString()
    }

    static boolean isLog(IBlockState state) {
        return state.getBlock() instanceof BlockLog
    }

    // Any axe-class tool cuts wood faster than by hand
    static boolean isAxe(ItemStack stack) {
        return !stack.isEmpty() && stack.getDestroySpeed(Blocks.LOG.getDefaultState()) > 1.0f
    }

    // 0 = cannot break structure blocks, 1 = iron tier, 2 = steel tier
    static int pickTier(ItemStack stack) {
        if (stack.isEmpty() || SOFT_PICKS.contains(itemId(stack))) return 0
        float speed = stack.getDestroySpeed(Blocks.STONE.getDefaultState())
        return speed >= STEEL_TIER_SPEED ? 2 : (speed >= IRON_TIER_SPEED ? 1 : 0)
    }

    // HBM concrete/steel structure blocks: iron tier; reinforced/ducrete/combine blocks: steel tier
    static void initStructureBlocks() {
        for (key in ForgeRegistries.BLOCKS.getKeys()) {
            if (key.getNamespace() != 'hbm') continue
            String path = key.getPath()
            if (path ==~ /(reinforced_.*|ducrete.*|cmb_brick.*|brick_compound.*|brick_obsidian.*)/) {
                STRUCTURE_STEEL.add(key.toString())
            } else if (path ==~ /(concrete.*|brick_concrete.*|brick_asbestos.*|brick_light.*|deco_(steel|rusty_steel|titanium|aluminium|lead|tungsten|beryllium|red_copper|pipe.*)|steel_(beam|grate.*|wall|roof|scaffold|corner|poles)|tile_lab.*)/) {
                STRUCTURE_IRON.add(key.toString())
            }
        }
    }

    static int requiredTier(IBlockState state) {
        String id = blockId(state)
        if (STRUCTURE_STEEL.contains(id)) return 2
        if (STRUCTURE_IRON.contains(id)) return 1
        return 0
    }

    static boolean canBreak(EntityPlayer player, IBlockState state) {
        int required = requiredTier(state)
        return required == 0 || pickTier(player.getHeldItemMainhand()) >= required
    }

    // --- Nuclear age is on Mars: strip uranium/thorium from the Space-age bodies (see postInit/space_ores.groovy) ---
    static final Set<String> NUCLEAR_ORES = ['hbmspace:ore_uranium', 'hbmspace:ore_thorium'] as Set
    static final Map<Integer, String> NUCLEAR_FREE_DIMS = [15: 'hbmspace:moon_rock', 21: 'hbmspace:minmus_stone']

    // block state from a registry name through vanilla statics (no method call on the mod block object)
    static IBlockState defaultState(String blockName) {
        return Block.getStateById(Block.getIdFromBlock(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName))))
    }

    static void stripNuclearOres(World world, int chunkX, int chunkZ) {
        // match by world identity: world.provider is a mod class on these planets (no method calls on it)
        String stoneName = NUCLEAR_FREE_DIMS.find { world.is(DimensionManager.getWorld(it.key)) }?.value
        if (stoneName == null) return
        IBlockState stone = defaultState(stoneName)
        int x0 = chunkX * 16, z0 = chunkZ * 16
        // the chunk itself plus the +8 population overlap (those neighbours are loaded during population)
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) for (int y = 1; y < 128; y++) {
            BlockPos pos = new BlockPos(x0 + x, y, z0 + z)
            if (NUCLEAR_ORES.contains(blockId(world.getBlockState(pos)))) world.setBlockState(pos, stone, 2)
        }
    }

    // Run an event handler body without ever letting an exception escape into the game
    static void guard(String name, Closure body) {
        try {
            body.call()
        } catch (Throwable t) {
            if (FAILED.add(name)) org.apache.logging.log4j.LogManager.getLogger('decaychain').error('Event handler ' + name + ' failed (further errors suppressed)', t)
        }
    }
    static final Set<String> FAILED = new HashSet<>()
}
