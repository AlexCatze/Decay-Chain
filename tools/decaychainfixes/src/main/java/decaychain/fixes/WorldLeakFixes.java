package decaychain.fixes;

import CoroUtil.difficulty.DynamicDifficulty;
import CoroUtil.entity.data.AttackData;
import CoroUtil.pathfinding.PFQueue;
import com.hbm.tileentity.network.RTTYSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * World memory leaks: every world loaded in a game session stayed in memory after quitting to the menu (or after a
 * dimension unloaded), ~300 MB each, until the heap filled and garbage collection froze the game every few seconds.
 * Found with heap dumps (paths from GC roots to old WorldServer / WorldServerMulti / WorldClient objects). Each holder
 * below forgets a world when Forge unloads it (the event fires on the thread that owns the world):
 * - HBM RTTYSystem: static broadcast maps keyed by (World, channel), refilled every tick for every server world.
 * - HBM loot wand: static FakePlayer; HBM item renderers (ItemRenderShim) keep the last client world.
 * - CoroUtil PFQueue: the static pathfinding queue keeps the first world it was given.
 * - CoroUtil DynamicDifficulty: static damage logs per entity, only removed when the entity dies.
 * - Lycanites spawners: trigger counters keyed by player.
 * - Epic Siege Mod ChunkCacheFixed: static empty-chunk cache per dimension id.
 * - Advancement triggers (Corail Tombstone and others): listeners of players that never log out, e.g. FakePlayers.
 * - Athenaeum TileDataService (Pyrotech tiles): see AthenaeumTileDataServiceMixin (registered via register()).
 * - Immersive Vehicles EntityManager: see MtsEntityManagerMixin.
 * Mod internals are reached by reflection; every fix is isolated so a changed or missing mod cannot break unloading.
 */
@Mod.EventBusSubscriber(modid = "decaychainfixes")
public final class WorldLeakFixes {
    private static final Logger LOG = LogManager.getLogger("decaychainfixes");
    private static final Set<IWorldPurgeable> PURGEABLE = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<String> FAILED = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<Class<?>, Field> WORLD_FIELDS = new ConcurrentHashMap<>();

    private WorldLeakFixes() {
    }

    /** Called by mixins (e.g. every Athenaeum TileDataService) so their caches are purged on world unload. */
    public static void register(IWorldPurgeable purgeable) {
        synchronized (PURGEABLE) {
            PURGEABLE.add(purgeable);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        run("hbm-rtty", () -> {
            RTTYSystem.broadcast.keySet().removeIf(key -> key.getKey() == world);
            RTTYSystem.newMessages.keySet().removeIf(key -> key.getKey() == world);
        });
        run("hbm-wand-fakeplayer", () -> {
            Field field = declaredField(Class.forName("com.hbm.blocks.generic.BlockWandLoot$TileEntityWandLoot"), "fakePlayer");
            Object fakePlayer = field.get(null);
            if (fakePlayer != null && worldOf(fakePlayer) == world) field.set(null, null);
        });
        run("item-renderer-world", () -> clearItemRendererWorlds(world));
        run("coroutil-pfqueue", () -> {
            PFQueue queue = PFQueue.instance;
            if (queue != null && (Object) queue.worldMap == world) queue.worldMap = null;
        });
        run("coroutil-damage-log", () -> DynamicDifficulty.lookupEntToDamageLog.values().removeIf(log -> {
            Object ent = ((AttackData) log).getEnt();
            return ent == null || worldOf(ent) == world;
        }));
        run("lycanites-spawners", () -> purgeLycanitesTriggerCounts(world));
        run("esm-empty-chunks", () -> {
            Map<?, ?> cache = (Map<?, ?>) declaredField(Class.forName("funwayguy.epicsiegemod.ai.hooks.ChunkCacheFixed"), "emptyCache").get(null);
            synchronized (cache) {
                cache.values().removeIf(chunks -> ((Map<?, ?>) chunks).values().stream().anyMatch(chunk -> worldOf(chunk) == world));
            }
        });
        run("advancement-listeners", () -> purgeAdvancementListeners(world));
        run("purgeable", () -> {
            IWorldPurgeable[] all;
            synchronized (PURGEABLE) {
                all = PURGEABLE.toArray(new IWorldPurgeable[0]);
            }
            for (IWorldPurgeable purgeable : all) purgeable.decaychain$purgeWorld(world);
        });
    }

    /** Lycanites: every spawner counts trigger hits per player in a map that is never cleaned. */
    private static void purgeLycanitesTriggerCounts(World world) throws Exception {
        Class<?> listenerClass = Class.forName("com.lycanitesmobs.core.spawner.SpawnerEventListener");
        Object listener = listenerClass.getMethod("getInstance").invoke(null);
        Field countsField = declaredField(Class.forName("com.lycanitesmobs.core.spawner.Spawner"), "triggerCounts");
        Set<Object> seen = Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        for (Field listField : listenerClass.getFields()) {
            if (!List.class.isAssignableFrom(listField.getType())) continue;
            for (Object trigger : new ArrayList<>((List<?>) listField.get(listener))) {
                Object spawner = trigger.getClass().getField("spawner").get(trigger);
                if (spawner == null || !seen.add(spawner)) continue;
                Map<?, ?> counts = (Map<?, ?>) countsField.get(spawner);
                if (counts != null) counts.keySet().removeIf(player -> worldOf(player) == world);
            }
        }
    }

    /**
     * Advancement triggers keep listeners per PlayerAdvancements until the player logs out. FakePlayers never do, so
     * their advancements (and through the player, the world) stay registered in every trigger forever.
     */
    private static void purgeAdvancementListeners(World world) throws Exception {
        Class<?> triggersClass = Class.forName("net.minecraft.advancements.CriteriaTriggers");
        Class<?> triggerInterface = Class.forName("net.minecraft.advancements.ICriterionTrigger");
        Class<?> advancementsClass = Class.forName("net.minecraft.advancements.PlayerAdvancements");
        Method removeAll = null;
        for (Method m : triggerInterface.getMethods()) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == advancementsClass) removeAll = m;
        }
        if (removeAll == null) throw new IllegalStateException("ICriterionTrigger.removeAllListeners not found");
        Map<?, ?> registry = null;
        for (Field f : triggersClass.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && Map.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                registry = (Map<?, ?>) f.get(null);
            }
        }
        if (registry == null) throw new IllegalStateException("CriteriaTriggers registry not found");
        Field playerField = fieldOfType(advancementsClass, "net.minecraft.entity.player.EntityPlayerMP");
        for (Object trigger : registry.values()) {
            List<Object> stale = new ArrayList<>();
            for (Class<?> c = trigger.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) || !Map.class.isAssignableFrom(f.getType())) continue;
                    f.setAccessible(true);
                    Map<?, ?> map = (Map<?, ?>) f.get(trigger);
                    if (map == null) continue;
                    for (Object key : new ArrayList<>(map.keySet())) {
                        if (advancementsClass.isInstance(key) && worldOf(playerField.get(key)) == world) stale.add(key);
                    }
                }
            }
            for (Object advancements : stale) removeAll.invoke(trigger, advancements);
        }
    }

    /** Item stack renderers (HBM ItemRenderShim) remember the last client world they rendered in. */
    private static void clearItemRendererWorlds(World world) throws Exception {
        Class<?> itemClass = Class.forName("net.minecraft.item.Item");
        Field teisrField;
        try {
            teisrField = declaredField(itemClass, "teisr");   // Forge field, client only
        } catch (NoSuchFieldException dedicatedServer) {
            return;
        }
        Iterable<?> registry = null;
        for (Field f : itemClass.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && Iterable.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                registry = (Iterable<?>) f.get(null);
                break;
            }
        }
        if (registry == null) return;
        for (Object item : registry) {
            Object renderer = teisrField.get(item);
            if (renderer == null) continue;
            Field worldField = worldField(renderer.getClass());
            if (worldField != null && worldField.get(renderer) == world) worldField.set(renderer, null);
        }
    }

    /** The first instance field of type World in the object's class hierarchy (cached per class). */
    static World worldOf(Object o) {
        if (o == null) return null;
        try {
            Field f = worldField(o.getClass());
            return f == null ? null : (World) f.get(o);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static Field worldField(Class<?> type) {
        return WORLD_FIELDS.computeIfAbsent(type, t -> {
            for (Class<?> c = t; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (!Modifier.isStatic(f.getModifiers()) && World.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        return f;
                    }
                }
            }
            return null;
        });
    }

    private static Field fieldOfType(Class<?> owner, String typeName) {
        for (Class<?> c = owner; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType().getName().equals(typeName)) {
                    f.setAccessible(true);
                    return f;
                }
            }
        }
        throw new IllegalStateException(owner.getName() + " has no field of type " + typeName);
    }

    private static Field declaredField(Class<?> owner, String name) throws NoSuchFieldException {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private interface Fix {
        void run() throws Exception;
    }

    private static void run(String name, Fix fix) {
        try {
            fix.run();
        } catch (Throwable t) {   // a missing or changed mod must not break world unloading
            if (FAILED.add(name)) LOG.error("World leak fix '{}' failed (further errors suppressed)", name, t);
        }
    }
}
