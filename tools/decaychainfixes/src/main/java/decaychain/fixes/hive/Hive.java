package decaychain.fixes.hive;

import net.minecraft.nbt.NBTTagCompound;

/**
 * One meteorite hive: an SRP infection origin (EIV) with its own evolution. The fields mirror SRP's per-dimension
 * record in SRPSaveData (points, phase, phase-change time, generation, generation time); they are swapped into that
 * record while SRP's own code runs for this hive (see IHiveSaveData).
 */
public final class Hive {
    public final int x, y, z;
    public int points;
    public byte phase;
    public int phaseTime;          // world time of the last phase change (SRP cooldown), 0 = none yet
    public byte generation;
    public int generationTime;     // world time of the last generation change
    public long born;
    public int radius;             // last known SRP origin radius, for messages after the origin is gone
    public long[] core = new long[0];   // living core blocks (alveoli, dermoid cysts) planted by the meteor
    public int coreLeft;

    public Hive(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long key() {
        return key(x, y, z);
    }

    public static long key(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }

    public double distanceSq(double px, double pz) {
        double dx = px - x, dz = pz - z;
        return dx * dx + dz * dz;
    }

    NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.func_74768_a("x", x);
        tag.func_74768_a("y", y);
        tag.func_74768_a("z", z);
        tag.func_74768_a("points", points);
        tag.func_74774_a("phase", phase);
        tag.func_74768_a("phaseTime", phaseTime);
        tag.func_74774_a("generation", generation);
        tag.func_74768_a("generationTime", generationTime);
        tag.func_74772_a("born", born);
        tag.func_74768_a("radius", radius);
        int[] packed = new int[core.length * 2];
        for (int i = 0; i < core.length; i++) {
            packed[2 * i] = (int) (core[i] >>> 32);
            packed[2 * i + 1] = (int) core[i];
        }
        tag.func_74783_a("core", packed);
        tag.func_74768_a("coreLeft", coreLeft);
        return tag;
    }

    static Hive read(NBTTagCompound tag) {
        Hive hive = new Hive(tag.func_74762_e("x"), tag.func_74762_e("y"), tag.func_74762_e("z"));
        hive.points = tag.func_74762_e("points");
        hive.phase = tag.func_74771_c("phase");
        hive.phaseTime = tag.func_74762_e("phaseTime");
        hive.generation = tag.func_74771_c("generation");
        hive.generationTime = tag.func_74762_e("generationTime");
        hive.born = tag.func_74763_f("born");
        hive.radius = tag.func_74762_e("radius");
        int[] packed = tag.func_74759_k("core");
        hive.core = new long[packed.length / 2];
        for (int i = 0; i < hive.core.length; i++) {
            hive.core[i] = (long) packed[2 * i] << 32 | packed[2 * i + 1] & 0xFFFFFFFFL;
        }
        hive.coreLeft = tag.func_74762_e("coreLeft");
        return hive;
    }
}
