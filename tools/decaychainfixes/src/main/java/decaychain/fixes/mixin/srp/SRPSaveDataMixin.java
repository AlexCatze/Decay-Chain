package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import decaychain.fixes.hive.Hive;
import decaychain.fixes.hive.Hives;
import decaychain.fixes.hive.IHiveSaveData;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Decay Chain hives (see decaychain.fixes.hive.Hives): SRP keeps one evolution record per dimension. In the hive
 * dimension, queries made at a known position answer with the local hive, and points earned there are applied to
 * that hive by running SRP's own point code on the hive's values (difficulty multiplier, cooldowns, generations and
 * phase thresholds stay exactly SRP's).
 */
@Mixin(value = SRPSaveData.class, remap = false)
public abstract class SRPSaveDataMixin implements IHiveSaveData {
    @Shadow private ArrayList<Integer> dimEPid;
    @Shadow private ArrayList<Integer> dimEPtotalKills;
    @Shadow private ArrayList<Byte> dimEPevolution;
    @Shadow private ArrayList<Integer> dimEPtimeEvolution;
    @Shadow private ArrayList<Byte> dimGeneration;
    @Shadow private ArrayList<Integer> dimGenerationTime;

    @Shadow public abstract byte getEvolutionPhase(int id);
    @Shadow public abstract boolean setTotalKills(int dimID, int in, boolean plus, World worldIn, boolean canChangePhase, boolean isFromSleep, int srcID);
    @Shadow public abstract boolean[] getGeneModi(int id);
    @Shadow public abstract float[] getGeneModi2(int id);

    @Inject(method = "getEvolutionPhase", at = @At("HEAD"), cancellable = true)
    private void decaychain$localPhase(int id, CallbackInfoReturnable<Byte> cir) {
        Byte phase = Hives.contextPhase(id);
        if (phase != null) cir.setReturnValue(phase);
    }

    @Inject(method = "getTotalKills", at = @At("HEAD"), cancellable = true)
    private void decaychain$localPoints(int id, CallbackInfoReturnable<Integer> cir) {
        Integer points = Hives.contextPoints(id);
        if (points != null) cir.setReturnValue(points);
    }

    @Inject(method = "setTotalKills(IIZLnet/minecraft/world/World;ZZI)Z", at = @At("HEAD"), cancellable = true)
    private void decaychain$hivePoints(int dimID, int in, boolean plus, World worldIn, boolean canChangePhase, boolean isFromSleep,
                                       int srcID, CallbackInfoReturnable<Boolean> cir) {
        if (!plus || !Hives.handles(dimID)) return;   // direct sets (SRP's phase setup, commands) stay SRP's own
        Hive hive = Hives.pointsTarget(srcID);
        World world = worldIn != null ? worldIn : Hives.contextWorld();
        if (hive == null || world == null) {
            cir.setReturnValue(false);   // no position, or outside every zone
            return;
        }
        cir.setReturnValue(decaychain$runForHive(hive, world, () -> setTotalKills(dimID, in, plus, worldIn, canChangePhase, isFromSleep, srcID)));
        Hives.afterChange(world);
    }

    @Inject(method = "getGeneModi", at = @At("HEAD"), cancellable = true)
    private void decaychain$localGenes(int id, CallbackInfoReturnable<boolean[]> cir) {
        Hive hive = Hives.contextHive(id);
        if (hive != null) cir.setReturnValue(decaychain$runForHive(hive, Hives.contextWorld(), () -> getGeneModi(id)));
    }

    @Inject(method = "getGeneModi2", at = @At("HEAD"), cancellable = true)
    private void decaychain$localGenes2(int id, CallbackInfoReturnable<float[]> cir) {
        Hive hive = Hives.contextHive(id);
        if (hive != null) cir.setReturnValue(decaychain$runForHive(hive, Hives.contextWorld(), () -> getGeneModi2(id)));
    }

    /** The daily vector points: each hive earns from its own origin (SRP's dimension-wide payout then finds no position). */
    @Inject(method = "addUpdateNumber", at = @At("HEAD"))
    private void decaychain$hivePayout(int in, CallbackInfo ci) {
        if (Hives.handles(Hives.DIM)) Hives.dailyPayout((SRPSaveData) (Object) this);
    }

    /** SRP tells every client in the dimension about a phase change; for a hive only its surroundings. */
    @Redirect(method = "setEvolutionPhase", at = @At(value = "INVOKE",
              target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToDimension(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;I)V"))
    private void decaychain$hivePhasePacket(SimpleNetworkWrapper network, IMessage message, int dim) {
        if (!Hives.sendToHive(message)) network.sendToDimension(message, dim);
    }

    @Override
    public <T> T decaychain$runForHive(Hive hive, World world, Supplier<T> op) {
        int i = decaychain$index(Hives.DIM);
        Integer points = dimEPtotalKills.get(i), phaseTime = dimEPtimeEvolution.get(i), generationTime = dimGenerationTime.get(i);
        Byte phase = dimEPevolution.get(i), generation = dimGeneration.get(i);
        dimEPtotalKills.set(i, hive.points);
        dimEPevolution.set(i, hive.phase);
        dimEPtimeEvolution.set(i, hive.phaseTime);
        dimGeneration.set(i, hive.generation);
        dimGenerationTime.set(i, hive.generationTime);
        Hive previous = Hives.activeHive();
        World previousWorld = Hives.activeWorld();
        Hives.enter(hive, world);
        try {
            return op.get();
        } finally {
            Hives.exit(previous, previousWorld);
            hive.points = dimEPtotalKills.get(i);
            hive.phase = dimEPevolution.get(i);
            hive.phaseTime = dimEPtimeEvolution.get(i);
            hive.generation = dimGeneration.get(i);
            hive.generationTime = dimGenerationTime.get(i);
            dimEPtotalKills.set(i, points);
            dimEPevolution.set(i, phase);
            dimEPtimeEvolution.set(i, phaseTime);
            dimGeneration.set(i, generation);
            dimGenerationTime.set(i, generationTime);
        }
    }

    @Override
    public void decaychain$setDimRecord(int dim, byte phase, int points) {
        int i = decaychain$index(dim);
        if (dimEPevolution.get(i) == phase && dimEPtotalKills.get(i) == points) return;
        dimEPevolution.set(i, phase);
        dimEPtotalKills.set(i, points);
        ((WorldSavedData) (Object) this).func_76185_a();
    }

    private int decaychain$index(int dim) {
        int i = dimEPid.indexOf(dim);
        if (i < 0) {
            Hives.bypassed(() -> getEvolutionPhase(dim));   // SRP adds a dimension's record on first access
            i = dimEPid.indexOf(dim);
        }
        return i;
    }
}
