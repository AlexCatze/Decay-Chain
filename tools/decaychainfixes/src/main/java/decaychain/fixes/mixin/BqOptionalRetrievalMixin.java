package decaychain.fixes.mixin;

import betterquesting.questing.tasks.TaskOptionalRetrieval;
import betterquesting.questing.tasks.TaskRetrieval;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Questing Unofficial 4.3.2: a task created as "Optional Retrieval" in the quest editor came out as a normal
 * retrieval task. TaskOptionalRetrieval only sets its optional flag when it is read from NBT, never when the editor
 * creates a fresh one - and it saves under the plain retrieval factory id, so after saving nothing remembers what it was
 * meant to be. Setting the flag in the constructor fixes both: the flag is written with the task and the plain
 * retrieval task reads it back on load.
 */
@Mixin(value = TaskOptionalRetrieval.class, remap = false)
public abstract class BqOptionalRetrievalMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void decaychain$optionalFromStart(CallbackInfo ci) {
        ((TaskRetrieval) (Object) this).optional = true;
    }
}
