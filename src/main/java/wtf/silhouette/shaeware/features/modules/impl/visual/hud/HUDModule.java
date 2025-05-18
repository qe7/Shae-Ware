package wtf.silhouette.shaeware.features.modules.impl.visual.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.BooleanSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ColorSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.StringSetting;

import java.awt.*;

public final class HUDModule extends AbstractModule {

  private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

  private final BooleanSetting watermark = new BooleanSetting("Watermark", true);
  private final StringSetting watermarkString = new StringSetting("Text", ShaeWare.getInstance().getName());

  public HUDModule() {
    super("HUD", "Displays information related to the client.", ModuleCategory.VISUAL);

    this.watermarkString.supplyIf(watermark::getValue);

    this.setEnabled(true);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> onScreen = event -> {
    final DrawContext drawContext = event.context();
    final TextRenderer textRenderer = mc.textRenderer;

    if (watermark.getValue()) {
      drawContext.drawText(textRenderer, watermarkString.getValue(), 2, 2, color.getValue().getRGB(), true);
    }
  };
}
