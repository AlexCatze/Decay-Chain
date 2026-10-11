package decaychain.fixes.mixin;

import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * First Aid 1.6.22 with scaleMaxHealth (body parts scale with max health / 20):
 * - When the player's max health rises (Spice of Life: Carrot Edition milestone hearts), every body part gets a higher
 *   maximum but keeps its current health - the new hearts arrive "empty", the parts count as injured and First Aid
 *   applies its injury debuffs. Give each part the health it gained along with the new maximum.
 * - Decay Chain starts players at 3 hearts. Plain scaling would round head and body down to 2-4 HP there, and a single
 *   hit to either is lethal. Head and body never scale below HEAD_BODY_MIN; they grow normally above ~5 hearts. The
 *   limbs take the smaller share instead.
 * Decreases otherwise behave as before (current health is capped to the new maximum).
 */
@Mixin(targets = "ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel", remap = false)
public abstract class FirstAidScaleHealMixin {
    @Unique private static final int HEAD_BODY_MIN = 6;   // 3 hearts

    @Redirect(method = "runScaleLogic", remap = false,
              at = @At(value = "INVOKE", remap = false,
                       target = "Lichttt/mods/firstaid/api/damagesystem/AbstractDamageablePart;setMaxHealth(I)V"))
    private void decaychain$scalePart(AbstractDamageablePart part, int maxHealth) {
        if (part.canCauseDeath) maxHealth = Math.max(maxHealth, HEAD_BODY_MIN);   // head and body
        int before = part.getMaxHealth();
        part.setMaxHealth(maxHealth);
        int gained = part.getMaxHealth() - before;
        if (gained > 0) {
            part.currentHealth = Math.min(part.getMaxHealth(), part.currentHealth + gained);
        }
    }
}
