package wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown;

import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.impl.VanillaNoSlowdownMode;
import wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.impl.WatchdogNoSlowdownMode;

public final class NoSlowdownModule extends AbstractModule {

  private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", new VanillaNoSlowdownMode(this, "Vanilla"), new WatchdogNoSlowdownMode(this, "Watchdog"));

  public NoSlowdownModule() {
    super("No Slowdown", "Prevent the player from slowing down.", ModuleCategory.MOVEMENT);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> onScreen = event -> this.setSuffix(this.mode.getValue().getName());
}
