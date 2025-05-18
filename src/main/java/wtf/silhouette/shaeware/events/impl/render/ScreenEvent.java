package wtf.silhouette.shaeware.events.impl.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import wtf.silhouette.shaeware.events.api.Event;

public record ScreenEvent(DrawContext context, RenderTickCounter tickCounter) implements Event {
}
