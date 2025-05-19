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
import wtf.silhouette.shaeware.utils.player.EffectUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class HUDModule extends AbstractModule {

  private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

  private final BooleanSetting excludeVisual = new BooleanSetting("Exclude Visual", false);

  public HUDModule() {
    super("HUD", "Displays information related to the client.", ModuleCategory.VISUAL);

    this.setEnabled(true);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    final DrawContext drawContext = event.context();
    final TextRenderer textRenderer = mc.textRenderer;
    final List<AbstractModule> moduleList = new ArrayList<>(ManagerFactory.get(ModuleManager.class).getAllModules());

    String string = String.format("%s " + Formatting.WHITE + "%s", ShaeWare.getInstance().getName(), ShaeWare.getInstance().getVersion());
    drawContext.drawText(textRenderer, string, 2, 2, color.getValue().getRGB(), true);

    moduleList.sort(Comparator.comparingDouble(o -> (-textRenderer.getWidth(o.getName() + (!Objects.equals(o.getSuffix(), "") ? Formatting.WHITE + " " + o.getSuffix() : "")))));

    final boolean hasBad = EffectUtil.hasBadEffects();
    final boolean hasGood = EffectUtil.hasGoodEffects();
    int offset = (hasBad ? 26 : 0) + (hasGood ? 26 : 0);

    for (AbstractModule module : moduleList) {
      if (!module.isEnabled()) {
        continue;
      }

      if (!module.isDrawn()) {
        continue;
      }

      if (module.getCategory() == ModuleCategory.VISUAL && excludeVisual.getValue()) {
        continue;
      }

      final String suffix = !Objects.equals(module.getSuffix(), "") ? Formatting.WHITE + " " + module.getSuffix() : "";
      final String name = module.getName() + suffix;

      final int x = event.context().getScaledWindowWidth() - textRenderer.getWidth(name);

      drawContext.drawText(textRenderer, name, x - 2, offset + 2, this.color.getValue().getRGB(), true);
      offset += (textRenderer.fontHeight + 1);
    }
  };
}
