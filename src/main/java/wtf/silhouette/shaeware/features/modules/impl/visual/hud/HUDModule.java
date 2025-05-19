package wtf.silhouette.shaeware.features.modules.impl.visual.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.BooleanSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ColorSetting;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.ModuleManager;
import wtf.silhouette.shaeware.utils.animation.Animation;
import wtf.silhouette.shaeware.utils.animation.Easing;
import wtf.silhouette.shaeware.utils.player.EffectUtil;

import java.awt.*;
import java.util.List;
import java.util.*;

public final class HUDModule extends AbstractModule {

  private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

  private final BooleanSetting excludeVisual = new BooleanSetting("Exclude Visual", false);

  private final Map<AbstractModule, Animation> moduleContextAnimationMap = new HashMap<>();

  public HUDModule() {
    super("HUD", "Displays information related to the client.", ModuleCategory.VISUAL);

    this.setEnabled(true);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    final DrawContext drawContext = event.context();
    final TextRenderer textRenderer = mc.textRenderer;
    final List<AbstractModule> moduleList = new ArrayList<>(ManagerFactory.get(ModuleManager.class).getAllModules());

    drawContext.drawText(textRenderer, String.format("%s " + Formatting.WHITE + "%s", ShaeWare.getInstance().getName(), ShaeWare.getInstance().getVersion()), 2, 2, color.getValue().getRGB(), true);

    for (AbstractModule module : moduleList) {
      if (module.getCategory() == ModuleCategory.VISUAL && excludeVisual.getValue()) {
        continue;
      }

      this.moduleContextAnimationMap.putIfAbsent(module, new Animation(Easing.LINEAR, module.isEnabled() ? 500 : 350));

      final String suffix = module.getSuffix() != null ? " " + module.getSuffix() : "";
      final String name = module.getName() + suffix;

      this.moduleContextAnimationMap.get(module).setDuration(module.isEnabled() ? 500 : 350);

      final float textWidthForAnim = textRenderer.getWidth(name);
      this.moduleContextAnimationMap.get(module).run(module.isEnabled() ? 0 : -(textWidthForAnim + 5));
    }

    if (this.moduleContextAnimationMap.isEmpty()) {
      return;
    }

    moduleList.sort(Comparator.comparing((AbstractModule o) -> !o.isEnabled())
            .thenComparingDouble(o -> ((this.moduleContextAnimationMap.get(o) != null ? -this.moduleContextAnimationMap.get(o).getValue() : 0) + -textRenderer.getWidth(o.getName() + (o.getSuffix() != null ? " " + o.getSuffix() : "")))));

    final boolean hasBad = EffectUtil.hasBadEffects();
    final boolean hasGood = EffectUtil.hasGoodEffects();
    int offset = (hasBad ? 26 : 0) + (hasGood ? 26 : 0);

    for (AbstractModule module : moduleList) {
      if (module.getCategory() == ModuleCategory.VISUAL && excludeVisual.getValue()) {
        continue;
      }

      final Animation animation = this.moduleContextAnimationMap.get(module);

      final String suffix = module.getSuffix() != null ? Formatting.WHITE + " " + module.getSuffix() : "";
      final String name = module.getName() + suffix;

      final int x = (int) (event.context().getScaledWindowWidth() - animation.getValue() - textRenderer.getWidth(name) + 2);

      drawContext.drawText(textRenderer, name, x - 2, offset + 2, this.color.getValue().getRGB(), true);
      offset += (textRenderer.fontHeight);
    }
  };
}
