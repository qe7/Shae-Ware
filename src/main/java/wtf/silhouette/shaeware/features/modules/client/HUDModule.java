package wtf.silhouette.shaeware.features.modules.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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

  private final BooleanSetting excludeVisual = new BooleanSetting("Exclude Visual", false);

  private final Map<String, Animation> xAnimations = new HashMap<>();
  private final Map<String, Animation> yAnimations = new HashMap<>();

  public HUDModule() {
    super("HUD", "Displays information related to the client.", ModuleCategory.CLIENT);

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
    final List<AbstractModule> moduleList = new ArrayList<>(ManagerFactory.get(ModuleManager.class).getAllModules());

    xAnimations.entrySet().removeIf(entry -> moduleList.stream().noneMatch(m -> m.getName().equals(entry.getKey()) && m.isEnabled() && m.isDrawn()));
    yAnimations.entrySet().removeIf(entry -> moduleList.stream().noneMatch(m -> m.getName().equals(entry.getKey()) && m.isEnabled() && m.isDrawn()));

    String string = String.format("%s " + Formatting.WHITE + "%s", ShaeWare.getInstance().getName(), ShaeWare.getInstance().getVersion());

    drawContext.drawText(textRenderer, string, 2, 2, color.getValue().getRGB(), true);

    moduleList.sort((a, b) -> Double.compare(textRenderer.getWidth(b.getName() + (!Objects.equals(b.getSuffix(), "") ? Formatting.WHITE + " " + b.getSuffix() : "")), textRenderer.getWidth(a.getName() + (!Objects.equals(a.getSuffix(), "") ? Formatting.WHITE + " " + a.getSuffix() : ""))));

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

      if (module.getCategory() == ModuleCategory.Render && excludeVisual.getValue()) {
        continue;
      }

      final String suffix = !Objects.equals(module.getSuffix(), "") ? Formatting.WHITE + " " + module.getSuffix() : "";
      final String name = module.getName() + suffix;

      final int targetX = event.context().getScaledWindowWidth() - textRenderer.getWidth(name) - 2;

      Animation xAnim = getOrCreateAnimation(module.getName(), xAnimations);
      Animation yAnim = getOrCreateAnimation(module.getName(), yAnimations);

      xAnim.run(targetX);
      yAnim.run(targetOffset);

      drawContext.drawText(textRenderer, name, (int) xAnim.getValue(), (int) yAnim.getValue() + 2, this.color.getValue().getRGB(), true);
      targetOffset += (textRenderer.fontHeight + 1);
    }
  };
}
