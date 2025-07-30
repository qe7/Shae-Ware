package wtf.silhouette.shaeware.utils.player;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Portal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
public final class EffectUtil {

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

    public static Text getStatusEffectDescription(@NotNull StatusEffectInstance statusEffect) {
        MutableText mutableText = statusEffect.getEffectType().value().getName().copy();
        if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
            MutableText var10000 = mutableText.append(ScreenTexts.SPACE);
            int var10001 = statusEffect.getAmplifier();
            var10000.append(Text.translatable("enchantment.level." + (var10001 + 1)));
        }

        return mutableText;
    }

    /**
     * Gets the Sprite associated with a given StatusEffect.
     *
     * @param effect The StatusEffect to get the sprite for.
     * @return The Sprite for the given effect, or null if not available.
     */
    public static Sprite getEffectSprite(@NotNull RegistryEntry<StatusEffect> effect) {
        MinecraftClient client = MinecraftClient.getInstance();
        StatusEffectSpriteManager manager = client.getStatusEffectSpriteManager();
        return manager.getSprite(effect);
    }
}
