package wtf.silhouette.shaeware.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

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

  public static boolean hasEffects() {
    return MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.getStatusEffects().isEmpty();
  }

  public static List<StatusEffectInstance> getEffects() {
    if (MinecraftClient.getInstance().player == null) {
      return List.of();
    }
    return MinecraftClient.getInstance().player.getStatusEffects().stream().toList();
  }

  public static Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
    MutableText mutableText = ((StatusEffect)statusEffect.getEffectType().value()).getName().copy();
    if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
      MutableText var10000 = mutableText.append(ScreenTexts.SPACE);
      int var10001 = statusEffect.getAmplifier();
      var10000.append(Text.translatable("enchantment.level." + (var10001 + 1)));
    }

    return mutableText;
  }
}
