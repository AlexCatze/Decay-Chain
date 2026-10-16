package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: /srp evolution reads and changes the hive at the command sender. */
@Mixin(value = SRPCommandEvolution.class, remap = false)
public abstract class CtxCommandMixin {
    @WrapMethod(method = "func_184881_a")
    private void decaychain$ctxCommand(MinecraftServer server, ICommandSender sender, String[] args, Operation<Void> original) {
        HiveContext.push(sender.func_130014_f_(), sender.func_180425_c());
        try {
            original.call(server, sender, args);
        } finally {
            HiveContext.pop();
        }
    }
}
