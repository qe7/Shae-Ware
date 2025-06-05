package wtf.silhouette.shaeware.core.bus.impl.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import wtf.silhouette.shaeware.core.bus.Event;

public record ScreenEvent(DrawContext context, RenderTickCounter tickCounter) implements Event {
}
