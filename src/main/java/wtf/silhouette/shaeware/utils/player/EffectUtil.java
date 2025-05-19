package wtf.silhouette.shaeware.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class EffectUtil {

  private EffectUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static boolean hasGoodEffects() {
    if (MinecraftClient.getInstance().player == null) {
      return false;
    }

    for (StatusEffectInstance statusEffect : MinecraftClient.getInstance().player.getStatusEffects().stream().toList()) {
      if (statusEffect.getEffectType().value().isBeneficial()) {
        return true;
      }
    }

    return false;
  }

  public static boolean hasBadEffects() {
    if (MinecraftClient.getInstance().player == null) {
      return false;
    }

    for (StatusEffectInstance statusEffect : MinecraftClient.getInstance().player.getStatusEffects().stream().toList()) {
      if (!statusEffect.getEffectType().value().isBeneficial()) {
        return true;
      }
    }

    return false;
  }
}
