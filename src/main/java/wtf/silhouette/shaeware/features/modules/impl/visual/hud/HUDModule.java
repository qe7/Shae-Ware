package wtf.silhouette.shaeware.features.modules.impl.visual.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ColorSetting;

import java.awt.*;

public final class HUDModule extends AbstractModule {

  private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

  public HUDModule() {
    super("HUD", "Displays information related to the client.", ModuleCategory.VISUAL);

    this.setEnabled(true);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    final DrawContext drawContext = event.context();
    final TextRenderer textRenderer = mc.textRenderer;

    drawContext.drawText(textRenderer, String.format("%s %s", ShaeWare.getInstance().getName(), ShaeWare.getInstance().getVersion()), 2, 2, color.getValue().getRGB(), true);
  };
}
