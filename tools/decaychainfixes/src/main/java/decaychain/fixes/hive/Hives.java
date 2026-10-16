package decaychain.fixes.hive;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMusicTrackCancelUpdateClientEvoPhase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Decay Chain: parasite evolution per meteorite instead of per dimension.
 * Every SRP infection origin (EIV) in the overworld is a hive with its own points, phase and generation. SRP code
 * that runs at a position (see HiveContext) sees the phase of the hive there - 0 outside every zone - and the points
 * it earns go to that hive. SRP's own dimension record holds the strongest hive, for the few systems without a
 * position. A meteor's hive dies when most of the living core in its crater is destroyed (or when SRP drains its
 * health); nearby players then get their next meteor in 3-5 days (groovy/postInit/srp_meteor.groovy).
 */
@Mod.EventBusSubscriber(modid = "decaychainfixes")
public final class Hives {
    public static final int DIM = 0;
    public static final String METEOR_DAY_TAG = "decaychain:meteorDay";
    static final Logger LOG = LogManager.getLogger("decaychainfixes");

    private static final int CHECK_INTERVAL = 100;           // ticks between core checks / origin reconciliation
    private static final int RADIUS_PER_PHASE = 40;          // zone radius: SRP's default radius + 40 per phase ...
    private static final int MAX_RADIUS = 600;               // ... up to 600
    private static final double CORE_DEAD_FRACTION = 0.25;   // the hive dies when at most 25% of its core is left
    private static final int CORE_SCAN_XZ = 26, CORE_SCAN_Y = 40;
    private static final int MESSAGE_RANGE = 128;            // beyond the zone radius
    private static final int METEOR_DELAY_RANGE = 300;       // players this far beyond the zone get the 3-5 day delay
    private static final Random RNG = new Random();

    private static int bypass;                // > 0 while SRP's own code must see its plain dimension record
    private static Hive activeHive;           // the hive SRP code is running for (runForHive)
    private static World activeWorld;
    private static int creatingOrigin;        // > 0 inside SRPWorldData.setOrigin
    private static boolean resolving;         // inside hiveAt
    public static boolean meteorImpact;       // inside EntityMeteor.onImpact
    private static final Set<Integer> loggedSources = new HashSet<>();
    private static final Map<UUID, Byte> sentPhase = new HashMap<>();

    private Hives() {
    }

    // ---- hooks for SRPSaveData ----------------------------------------------------------------------------------

    /** Whether hive rules apply to this SRP query (hive dimension, not SRP's own nested code). */
    public static boolean handles(int dim) {
        return dim == DIM && bypass == 0;
    }

    /** The local phase for getEvolutionPhase, or null to use SRP's dimension record. */
    public static Byte contextPhase(int dim) {
        if (!handles(dim)) return null;
        HiveContext.Frame frame = HiveContext.current();
        if (frame == null) return null;
        Hive hive = hiveAt(frame.world, frame.pos);
        return hive == null ? 0 : hive.phase;
    }

    /** The local points for getTotalKills, or null to use SRP's dimension record. */
    public static Integer contextPoints(int dim) {
        if (!handles(dim)) return null;
        HiveContext.Frame frame = HiveContext.current();
        if (frame == null) return null;
        Hive hive = hiveAt(frame.world, frame.pos);
        return hive == null ? 0 : hive.points;
    }

    /** The hive at the current context position (null outside zones or without a context). */
    public static Hive contextHive(int dim) {
        if (!handles(dim)) return null;
        HiveContext.Frame frame = HiveContext.current();
        return frame == null ? null : hiveAt(frame.world, frame.pos);
    }

    public static World contextWorld() {
        HiveContext.Frame frame = HiveContext.current();
        return frame != null ? frame.world : DimensionManager.getWorld(DIM);
    }

    /** The hive that receives points from SRP source srcID; null drops them (no position, or outside every zone). */
    public static Hive pointsTarget(int srcID) {
        HiveContext.Frame frame = HiveContext.current();
        if (frame == null) {
            if (srcID != 1 && loggedSources.add(srcID)) {
                LOG.info("Hive evolution: SRP point source {} ran without a position; its points are dropped", srcID);
            }
            return null;
        }
        return hiveAt(frame.world, frame.pos);
    }

    public static void enter(Hive hive, World world) {
        bypass++;
        activeHive = hive;
        activeWorld = world;
    }

    public static void exit(Hive previous, World previousWorld) {
        bypass--;
        activeHive = previous;
        activeWorld = previousWorld;
    }

    public static Hive activeHive() {
        return activeHive;
    }

    public static World activeWorld() {
        return activeWorld;
    }

    public static <T> T bypassed(Supplier<T> op) {
        bypass++;
        try {
            return op.get();
        } finally {
            bypass--;
        }
    }

    // ---- lookups ------------------------------------------------------------------------------------------------

    /** The hive whose zone contains pos (the nearest one if zones overlap), or null. */
    public static Hive hiveAt(World world, BlockPos pos) {
        if (world == null || world.field_72995_K || world.field_73011_w.getDimension() != DIM || resolving) return null;
        resolving = true;   // SRPWorldData.get runs SRP's daily updates, which can query phases again
        try {
            BlockPos origin = SRPWorldData.get(world).nearestInfectionPosition(false, pos);
            return origin == null ? null : hiveFor(world, origin.func_177958_n(), origin.func_177956_o(), origin.func_177952_p());
        } finally {
            resolving = false;
        }
    }

    /** The hive of an existing origin; origins from before the hive system become hives at SRP's current phase. */
    static Hive hiveFor(World world, int x, int y, int z) {
        HiveData data = HiveData.get(world);
        Hive hive = data.get(x, y, z);
        if (hive != null || creatingOrigin > 0) return hive;
        hive = new Hive(x, y, z);
        SRPSaveData save = SRPSaveData.get(world, 0);
        hive.phase = (byte) Math.max(0, bypassed(() -> save.getEvolutionPhase(DIM)));
        hive.points = Math.max(0, bypassed(() -> save.getTotalKills(DIM)));
        hive.born = world.func_82737_E();
        hive.generationTime = (int) world.func_82737_E();
        hive.radius = radiusFor(hive.phase);
        data.put(hive);
        LOG.info("Hive registered for existing infection origin at {} {} {} (phase {}, {} points)", x, y, z, hive.phase, hive.points);
        return hive;
    }

    public static int radiusFor(int phase) {
        return Math.min(SRPConfigWorld.originRadius + RADIUS_PER_PHASE * Math.max(0, phase), MAX_RADIUS);
    }

    // ---- SRP origins --------------------------------------------------------------------------------------------

    public static void beginOrigin() {
        creatingOrigin++;
    }

    public static void endOrigin(World world, int x, int y, int z, int result) {
        creatingOrigin--;
        if ((result == 1 || result == 2) && world != null && !world.field_72995_K && world.field_73011_w.getDimension() == DIM) {
            onOriginCreated(world, x, y, z);
        }
    }

    /** A new origin: a meteor's hive starts at phase 0 with a core; an offshoot of a hive inherits its evolution. */
    static void onOriginCreated(World world, int x, int y, int z) {
        HiveData data = HiveData.get(world);
        Hive hive = new Hive(x, y, z);
        hive.born = world.func_82737_E();
        hive.generationTime = (int) world.func_82737_E();
        if (meteorImpact) {
            scanCore(world, hive);
        } else {
            Hive parent = null;
            double best = Double.MAX_VALUE;
            for (Hive other : data.all()) {
                if (other.key() == hive.key()) continue;
                double reach = other.radius * 1.5 + 64, distance = other.distanceSq(x, z);
                if (distance <= reach * reach && distance < best) {
                    parent = other;
                    best = distance;
                }
            }
            if (parent != null) {
                hive.phase = parent.phase;
                hive.points = parent.points;
                hive.generation = parent.generation;
                hive.generationTime = parent.generationTime;
            }
        }
        hive.radius = radiusFor(hive.phase);
        data.put(hive);
        LOG.info("Hive created at {} {} {} (phase {}, core blocks {})", x, y, z, hive.phase, hive.core.length);
        afterChange(world);
    }

    private static void scanCore(World world, Hive hive) {
        List<Long> found = new ArrayList<>();
        int minY = Math.max(1, hive.y - CORE_SCAN_Y), maxY = Math.min(255, hive.y + CORE_SCAN_Y);
        for (int dx = -CORE_SCAN_XZ; dx <= CORE_SCAN_XZ; dx++) {
            for (int dz = -CORE_SCAN_XZ; dz <= CORE_SCAN_XZ; dz++) {
                if (!world.func_175667_e(new BlockPos(hive.x + dx, 64, hive.z + dz))) continue;
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(hive.x + dx, y, hive.z + dz);
                    if (isCore(world.func_180495_p(pos).func_177230_c())) found.add(pos.func_177986_g());
                }
            }
        }
        hive.core = new long[found.size()];
        for (int i = 0; i < hive.core.length; i++) hive.core[i] = found.get(i);
        hive.coreLeft = hive.core.length;
    }

    private static boolean isCore(Block block) {
        return block == SRPBlocks.Alveoli || block == SRPBlocks.DermoidCyst;
    }

    /** Replaces SRPWorldData.updateOriginValues in the hive dimension: daily health growth by each hive's phase. */
    public static boolean growOrigins(World world, SRPWorldData origins, int updates, SRPSaveData save) {
        if (world == null || world.field_72995_K || world.field_73011_w.getDimension() != DIM) return false;
        List<Integer> xs = origins.getorigins("x"), ys = origins.getorigins("y"), zs = origins.getorigins("z");
        List<Integer> radii = origins.getorigins("a"), healths = origins.getorigins("h");
        for (int i = 0; i < xs.size(); i++) {
            Hive hive = hiveFor(world, xs.get(i), ys.get(i), zs.get(i));
            int phase = hive == null ? 0 : hive.phase;
            int radius = radiusFor(phase);
            long health = healths.get(i);
            for (int u = 0; u < updates; u++) {
                health += (long) (radius * (SRPConfigWorld.originDailyHealth + SRPWorldData.getPhaseHealth(phase)));
            }
            radii.set(i, radius);
            healths.set(i, (int) Math.min(health, SRPConfigWorld.originHealthCap));
        }
        origins.func_76185_a();
        origins.setEIVHealthToData(world, save);
        return true;
    }

    /** SRP's daily vector points (SRPSaveData.addUpdateNumber), paid to each hive from its own origin health. */
    public static void dailyPayout(SRPSaveData save) {
        World world = DimensionManager.getWorld(DIM);
        if (world == null || !SRPConfigWorld.originActivated) return;
        SRPWorldData origins = SRPWorldData.get(world);
        List<Integer> xs = origins.getorigins("x"), ys = origins.getorigins("y"), zs = origins.getorigins("z");
        List<Integer> healths = origins.getorigins("h");
        for (int i = 0; i < xs.size(); i++) {
            Hive hive = hiveFor(world, xs.get(i), ys.get(i), zs.get(i));
            if (hive == null) continue;
            long points = (long) (healths.get(i) * SRPConfigWorld.originDailyEPPoints);
            int add = (int) Math.min(points, SRPCommandEvolution.getVectorPointCap(hive.phase));
            if (add <= 0) continue;
            ((IHiveSaveData) save).decaychain$runForHive(hive, world, () -> save.setTotalKills(DIM, add, true, world, true, true, 1));
        }
        afterChange(world);
    }

    /** Keeps SRP's dimension record at the strongest hive (systems without a position, unlocks, /srp with no hive). */
    public static void afterChange(World world) {
        if (world == null) return;
        HiveData data = HiveData.get(world);
        byte phase = 0;
        int points = 0;
        for (Hive hive : data.all()) {
            if (hive.phase > phase || hive.phase == phase && hive.points > points) {
                phase = hive.phase;
                points = hive.points;
            }
        }
        ((IHiveSaveData) SRPSaveData.get(world, 0)).decaychain$setDimRecord(DIM, phase, points);
        data.func_76185_a();
    }

    // ---- messages for the hive SRP code is running for --------------------------------------------------------------

    /** Replaces SRP's dimension-wide phase alert while SRP runs for a hive; false = not a hive alert. */
    public static boolean alertHive(World world, String message, int warning) {
        Hive hive = activeHive;
        if (hive == null || world == null) return false;
        for (EntityPlayer player : playersNear(world, hive, MESSAGE_RANGE)) {
            SRPMain.network.sendTo(new SRPPacketMovingSound(warning), (EntityPlayerMP) player);
            if (warning >= 0 && warning <= 10) {
                player.func_145747_a(new TextComponentTranslation("decaychain.hive.evolved", warning, hive.x, hive.z));
            } else if ("Phase decreased".equals(message)) {
                player.func_145747_a(new TextComponentTranslation("decaychain.hive.weakened", hive.phase, hive.x, hive.z));
            } else if (!message.isEmpty()) {
                player.func_145747_a(new TextComponentString(message));
            }
        }
        if (warning == -7 && "Phase decreased".equals(message)) {
            double range = (double) radiusFor(hive.phase) * radiusFor(hive.phase);
            for (Entity entity : new ArrayList<>(world.field_72996_f)) {
                if (entity instanceof EntityParasiteBase && hive.distanceSq(entity.field_70165_t, entity.field_70161_v) <= range) {
                    ((EntityParasiteBase) entity).func_70690_d(new PotionEffect(SRPPotions.RAGE_E, 2400, 1, false, false));
                }
            }
        }
        return true;
    }

    /** Sends SRP's client phase packet only to the players around the active hive; false = no active hive. */
    public static boolean sendToHive(IMessage message) {
        Hive hive = activeHive;
        if (hive == null || activeWorld == null) return false;
        for (EntityPlayer player : playersNear(activeWorld, hive, MESSAGE_RANGE)) {
            SRPMain.network.sendTo(message, (EntityPlayerMP) player);
            sentPhase.put(player.func_110124_au(), hive.phase);
        }
        return true;
    }

    private static List<EntityPlayer> playersNear(World world, Hive hive, int extra) {
        List<EntityPlayer> near = new ArrayList<>();
        double range = Math.max(hive.radius, radiusFor(hive.phase)) + extra;
        for (EntityPlayer player : world.field_73010_i) {
            if (player instanceof EntityPlayerMP && hive.distanceSq(player.field_70165_t, player.field_70161_v) <= range * range) {
                near.add(player);
            }
        }
        return near;
    }

    // ---- periodic checks ------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.field_72995_K || world.field_73011_w.getDimension() != DIM) return;
        if (world.func_82737_E() % CHECK_INTERVAL != 0) return;
        try {
            check(world);
        } catch (RuntimeException e) {
            LOG.error("Hive check failed", e);
        }
    }

    private static void check(World world) {
        SRPWorldData origins = SRPWorldData.get(world);
        HiveData data = HiveData.get(world);
        List<Integer> xs = origins.getorigins("x"), ys = origins.getorigins("y"), zs = origins.getorigins("z");
        List<Integer> radii = origins.getorigins("a");
        Map<Long, Integer> originIndex = new HashMap<>();
        boolean originsChanged = false;
        for (int i = 0; i < xs.size(); i++) {
            Hive hive = hiveFor(world, xs.get(i), ys.get(i), zs.get(i));   // registers origins from before the hive system
            if (hive == null) continue;
            originIndex.put(hive.key(), i);
            int radius = radiusFor(hive.phase);
            if (radii.get(i) != radius) {
                radii.set(i, radius);
                originsChanged = true;
            }
            hive.radius = radius;
        }
        if (originsChanged) origins.func_76185_a();
        for (Hive hive : new ArrayList<>(data.all())) {
            if (!originIndex.containsKey(hive.key())) {
                gone(world, hive, false);   // SRP removed the origin (health drained, command)
                continue;
            }
            if (hive.core.length == 0) continue;
            int left = countCore(world, hive);
            if (left < 0) continue;   // not loaded
            if (left != hive.coreLeft) {
                hive.coreLeft = left;
                data.func_76185_a();
            }
            if (left <= hive.core.length * CORE_DEAD_FRACTION) {
                origins.removeOrigin(hive.x, hive.y, hive.z, world);
                gone(world, hive, true);
            }
        }
        syncClientPhase(world);
    }

    private static int countCore(World world, Hive hive) {
        int left = 0;
        for (long packed : hive.core) {
            BlockPos pos = BlockPos.func_177969_a(packed);
            if (!world.func_175667_e(pos)) return -1;
            if (isCore(world.func_180495_p(pos).func_177230_c())) left++;
        }
        return left;
    }

    /** A hive is gone: tell the players around it and give them their next meteor in 3-5 days. */
    private static void gone(World world, Hive hive, boolean coreDestroyed) {
        List<EntityPlayer> near = playersNear(world, hive, METEOR_DELAY_RANGE);
        HiveData.get(world).remove(hive);
        long day = world.func_72820_D() / 24000L;
        for (EntityPlayer player : near) {
            NBTTagCompound persisted = player.getEntityData().func_74775_l(EntityPlayer.PERSISTED_NBT_TAG);
            persisted.func_74772_a(METEOR_DAY_TAG, day + 3 + RNG.nextInt(3));
            player.getEntityData().func_74782_a(EntityPlayer.PERSISTED_NBT_TAG, persisted);
            player.func_145747_a(new TextComponentTranslation(coreDestroyed ? "decaychain.hive.destroyed" : "decaychain.hive.died", hive.x, hive.z));
        }
        LOG.info("Hive at {} {} {} is gone ({}, phase {})", hive.x, hive.y, hive.z, coreDestroyed ? "core destroyed" : "origin removed", hive.phase);
        afterChange(world);
    }

    /** SRP's client phase (music, evolution HUD) follows the hive the player stands in. */
    private static void syncClientPhase(World world) {
        for (EntityPlayer player : world.field_73010_i) {
            if (!(player instanceof EntityPlayerMP)) continue;
            Hive hive = hiveAt(world, player.func_180425_c());
            byte phase = hive == null ? 0 : hive.phase;
            Byte sent = sentPhase.put(player.func_110124_au(), phase);
            if (sent == null || sent != phase) {
                SRPMain.network.sendTo(new SRPPacketMusicTrackCancelUpdateClientEvoPhase(phase), (EntityPlayerMP) player);
            }
        }
    }
}
