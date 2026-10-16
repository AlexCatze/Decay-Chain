package decaychain.fixes.mixin.srp;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import decaychain.fixes.hive.HiveContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;

/** Decay Chain hives: SRP's phase rules for crops, healing, fishing, loot, sleeping and COTH tagging are local. */
@Mixin(value = SRPEventHandlerBus.class, remap = false)
public abstract class CtxEventBusMixin {
    @WrapMethod(method = "writeCOTHTag")
    private void decaychain$ctxCoth(EntityLivingBase entity, String name, NBTTagCompound tags, Operation<Void> original) {
        HiveContext.push(entity);
        try {
            original.call(entity, name, tags);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "cropGrow")
    private void decaychain$ctxCrop(BlockEvent.CropGrowEvent.Pre event, Operation<Void> original) {
        HiveContext.push(event.getWorld(), event.getPos());
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "entityHeal")
    private void decaychain$ctxHeal(LivingHealEvent event, Operation<Void> original) {
        HiveContext.push(event.getEntityLiving());
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "playerFishing")
    private void decaychain$ctxFishing(ItemFishedEvent event, Operation<Void> original) {
        HiveContext.push(event.getEntityPlayer());
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "setLoot")
    private void decaychain$ctxLoot(LivingDropsEvent event, Operation<Void> original) {
        HiveContext.push(event.getEntityLiving());
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }

    @WrapMethod(method = "playerUp")
    private void decaychain$ctxWakeUp(PlayerWakeUpEvent event, Operation<Void> original) {
        HiveContext.push(event.getEntityPlayer());
        try {
            original.call(event);
        } finally {
            HiveContext.pop();
        }
    }
}
