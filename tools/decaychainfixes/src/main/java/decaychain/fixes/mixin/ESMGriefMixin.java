package decaychain.fixes.mixin;

import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Epic Siege Mod 13.169 griefing AI: while the mob has no path to its marked block, updateTask re-runs the
 * pathfinder every tick until the block is gone. Unreachable targets (glass high in a wall, any light-emitting
 * block behind a ceiling - Lost Cities ruins are full of both) made every griefing mob pathfind each tick.
 * Re-path at most once per second and give up on the target after a few tries.
 */
@Mixin(targets = "funwayguy.epicsiegemod.ai.ESM_EntityAIGrief", remap = false)
public abstract class ESMGriefMixin {
    @Unique private static final int REPATH_INTERVAL = 20;
    @Unique private static final int MAX_REPATHS = 5;

    @Shadow private BlockPos markedLoc;
    @Unique private int decaychain$cooldown;
    @Unique private int decaychain$repaths;

    // shouldExecute just picked a target and pathed to it
    @Inject(method = "func_75250_a", at = @At("RETURN"))
    private void decaychain$newTarget(CallbackInfoReturnable<Boolean> cir) {
        decaychain$cooldown = REPATH_INTERVAL;
        decaychain$repaths = 0;
    }

    @Redirect(method = "func_75246_d",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/pathfinding/PathNavigate;func_75492_a(DDDD)Z"))
    private boolean decaychain$throttleRepath(PathNavigate navigator, double x, double y, double z, double speed) {
        if (--decaychain$cooldown > 0) return false;
        decaychain$cooldown = REPATH_INTERVAL;
        if (++decaychain$repaths > MAX_REPATHS) {
            markedLoc = null;  // shouldContinueExecuting() ends the task next tick
            return false;
        }
        return navigator.func_75492_a(x, y, z, speed);
    }
}
