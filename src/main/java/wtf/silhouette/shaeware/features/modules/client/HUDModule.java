package wtf.silhouette.shaeware.features.modules.client;

import com.google.common.collect.Ordering;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.ModuleManager;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.BooleanSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.ColorSetting;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.utils.animation.Animation;
import wtf.silhouette.shaeware.utils.animation.Easing;
import wtf.silhouette.shaeware.utils.player.EffectUtil;

import java.awt.*;
import java.util.List;
import java.util.*;

public final class HUDModule extends AbstractModule {

    private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

    private final BooleanSetting watermark = new BooleanSetting("Watermark", true);

    private final BooleanSetting fps = new BooleanSetting("FPS", true);

    private final BooleanSetting coordinates = new BooleanSetting("Coordinates", true);

    private final BooleanSetting effects = new BooleanSetting("Effects", true);

    private final BooleanSetting arrayList = new BooleanSetting("Array List", true);
    private final BooleanSetting suffix = new BooleanSetting("Suffix", true);

    private final BooleanSetting lowerCase = new BooleanSetting("Lower Case", false);


    private final Map<String, Animation> xAnimations = new HashMap<>();
    private final Map<String, Animation> yAnimations = new HashMap<>();

    public HUDModule() {
        super("HUD", "Displays information related to the client.", ModuleCategory.CLIENT);

        suffix.supplyIf(arrayList::getValue);

        this.setEnabled(true);
    }

    private @NotNull Animation getOrCreateAnimation(String moduleName, @NotNull Map<String, Animation> map) {
        Animation animation = map.get(moduleName);
        if (animation == null) {
            animation = new Animation(Easing.EASE_OUT_EXPO, 200);
            if (map == xAnimations) {
                animation.setValue(mc.getWindow().getScaledWidth() + mc.textRenderer.getWidth(moduleName) + 20);
            }
            map.put(moduleName, animation);
        }
        return animation;
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        final DrawContext drawContext = event.context();
        final TextRenderer textRenderer = mc.textRenderer;

        int offsetLeftTop = 2;
        int offsetLeftBottom = event.context().getScaledWindowHeight() - textRenderer.fontHeight - 2 - (mc.currentScreen instanceof ChatScreen ? 14 : 0);
        int offsetRightBottom = event.context().getScaledWindowHeight() - textRenderer.fontHeight - 2 - (mc.currentScreen instanceof ChatScreen ? 14 : 0);

        if (watermark.getValue()) {
            String string = String.format("%s " + Formatting.WHITE + "%s", ShaeWare.getInstance().getName(), ShaeWare.getInstance().getVersion());
            if (lowerCase.getValue()) string = string.toLowerCase(Locale.ROOT);
            drawContext.drawText(textRenderer, string, 2, offsetLeftTop, color.getValue().getRGB(), true);
            offsetLeftTop += textRenderer.fontHeight + 1;
        }

        if (fps.getValue()) {
            String string = "FPS: " + Formatting.WHITE + mc.getCurrentFps();
            if (lowerCase.getValue()) string = string.toLowerCase(Locale.ROOT);
            drawContext.drawText(textRenderer, string, 2, offsetLeftTop, color.getValue().getRGB(), true);
            offsetLeftTop += textRenderer.fontHeight + 1;
        }

        if (coordinates.getValue()) {
            String string = "XYZ: " + Formatting.WHITE + String.format("%.2f, %.2f, %.2f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            if (lowerCase.getValue()) string = string.toLowerCase(Locale.ROOT);
            drawContext.drawText(textRenderer, string, 2, offsetLeftBottom, color.getValue().getRGB(), true);
            offsetLeftBottom -= textRenderer.fontHeight + 1;
        }

        if (effects.getValue()) {
            if (EffectUtil.hasEffects()) {
                final List<StatusEffectInstance> effects = new ArrayList<>(EffectUtil.getEffects());

                effects.sort(Comparator.comparingInt(o -> textRenderer.getWidth(EffectUtil.getStatusEffectDescription((StatusEffectInstance) o).getString() + ": " + Formatting.WHITE + ((StatusEffectInstance) o).getDuration() / 20 + "s")).reversed());

                int targetOffsetRightBottom = offsetRightBottom;

                for (StatusEffectInstance effect : effects) {
                    String string = EffectUtil.getStatusEffectDescription(effect).getString() + ": " + Formatting.WHITE + effect.getDuration() / 20 + "s";

                    // render image if the effect has an icon
                    Sprite sprite = EffectUtil.getEffectSprite(effect.getEffectType());
                    if (sprite != null) {
                        drawContext.drawSpriteStretched(
                                RenderLayer::getGuiTextured,
                                sprite,
                                event.context().getScaledWindowWidth() - textRenderer.getWidth(string) - 15,
                                targetOffsetRightBottom - 1,
                                9, 9
                        );
                    }

                    if (lowerCase.getValue()) string = string.toLowerCase(Locale.ROOT);
                    drawContext.drawText(textRenderer, string, event.context().getScaledWindowWidth() - textRenderer.getWidth(string) - 2, targetOffsetRightBottom, color.getValue().getRGB(), true);
                    targetOffsetRightBottom -= textRenderer.fontHeight + 1;
                }

                if (targetOffsetRightBottom < offsetRightBottom - textRenderer.fontHeight - 1) {
                    offsetRightBottom = targetOffsetRightBottom;
                }
            }
        }

        if (arrayList.getValue()) {
            final List<AbstractModule> moduleList = new ArrayList<>(ManagerFactory.get(ModuleManager.class).getAllModules());

            xAnimations.entrySet().removeIf(entry -> moduleList.stream().noneMatch(m -> m.getName().equals(entry.getKey()) && m.isEnabled() && m.isDrawn()));
            yAnimations.entrySet().removeIf(entry -> moduleList.stream().noneMatch(m -> m.getName().equals(entry.getKey()) && m.isEnabled() && m.isDrawn()));

            moduleList.sort((a, b) -> Double.compare(textRenderer.getWidth(b.getName() + (!Objects.equals((this.suffix.getValue() ? b.getSuffix() : ""), "") ? Formatting.WHITE + " " + b.getSuffix() : "")), textRenderer.getWidth(a.getName() + (!Objects.equals((this.suffix.getValue() ? a.getSuffix() : ""), "") ? Formatting.WHITE + " " + a.getSuffix() : ""))));

            final boolean hasBad = EffectUtil.hasBadEffects();
            final boolean hasGood = EffectUtil.hasGoodEffects();
            int targetOffset = (hasBad ? 26 : 0) + (hasGood ? 26 : 0);

            for (AbstractModule module : moduleList) {
                if (!module.isEnabled()) {
                    Animation xAnim = xAnimations.get(module.getName());
                    Animation yAnim = yAnimations.get(module.getName());
                    if (xAnim != null) xAnim.reset();
                    if (yAnim != null) yAnim.reset();
                    continue;
                }

                if (!module.isDrawn()) {
                    continue;
                }

                final String suffix = !Objects.equals(module.getSuffix(), "") ? Formatting.WHITE + " " + module.getSuffix() : "";
                String name = module.getName() + (this.suffix.getValue() ? suffix : "");

                if (lowerCase.getValue()) {
                    name = name.toLowerCase(Locale.ROOT);
                }

                final int targetX = event.context().getScaledWindowWidth() - textRenderer.getWidth(name) - 2;

                Animation xAnim = getOrCreateAnimation(module.getName(), xAnimations);
                Animation yAnim = getOrCreateAnimation(module.getName(), yAnimations);

                xAnim.run(targetX);
                yAnim.run(targetOffset);

                drawContext.drawText(textRenderer, name, (int) xAnim.getValue(), (int) yAnim.getValue() + 2, this.color.getValue().getRGB(), true);
                targetOffset += (textRenderer.fontHeight + 1);
            }
        }
    };
}