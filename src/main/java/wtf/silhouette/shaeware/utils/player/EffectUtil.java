package wtf.silhouette.shaeware.utils.player;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import wtf.silhouette.shaeware.utils.IMinecraft;

public final class EffectUtil implements IMinecraft {

  private EffectUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  private boolean hasGoodEffects() {
    if (mc.player == null) {
      return false;
    }

    for (StatusEffectInstance statusEffect : mc.player.getStatusEffects().stream().toList()) {
      if (statusEffect.getEffectType().value().isBeneficial()) {
        return true;
      }
    }

    return false;
  }

  private boolean hasBadEffects() {
    if (mc.player == null) {
      return false;
    }

    for (StatusEffectInstance statusEffect : mc.player.getStatusEffects().stream().toList()) {
      if (!statusEffect.getEffectType().value().isBeneficial()) {
        return true;
      }
    }

    return false;
  }
}
