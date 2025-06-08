package wtf.silhouette.shaeware.core.feature.component;

import net.minecraft.client.MinecraftClient;
import wtf.silhouette.shaeware.core.bus.Handler;

public abstract class Component implements Handler {

  protected final MinecraftClient mc = MinecraftClient.getInstance();
}
