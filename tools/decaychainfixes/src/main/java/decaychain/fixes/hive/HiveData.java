package decaychain.fixes.hive;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

/** The hives of one dimension (data/decaychain_hives.dat), keyed by their SRP origin coordinates. */
public class HiveData extends WorldSavedData {
    public static final String NAME = "decaychain_hives";
    private final Map<Long, Hive> hives = new LinkedHashMap<>();

    public HiveData(String name) {
        super(name);
    }

    public static HiveData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        HiveData data = (HiveData) storage.func_75742_a(HiveData.class, NAME);
        if (data == null) {
            data = new HiveData(NAME);
            storage.func_75745_a(NAME, data);
        }
        return data;
    }

    public Hive get(int x, int y, int z) {
        return hives.get(Hive.key(x, y, z));
    }

    public Collection<Hive> all() {
        return hives.values();
    }

    public void put(Hive hive) {
        hives.put(hive.key(), hive);
        func_76185_a();
    }

    public void remove(Hive hive) {
        hives.remove(hive.key());
        func_76185_a();
    }

    @Override
    public void func_76184_a(NBTTagCompound tag) {
        hives.clear();
        NBTTagList list = tag.func_150295_c("hives", 10);
        for (int i = 0; i < list.func_74745_c(); i++) {
            Hive hive = Hive.read(list.func_150305_b(i));
            hives.put(hive.key(), hive);
        }
    }

    @Override
    public NBTTagCompound func_189551_b(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Hive hive : hives.values()) list.func_74742_a(hive.write());
        tag.func_74782_a("hives", list);
        return tag;
    }
}
