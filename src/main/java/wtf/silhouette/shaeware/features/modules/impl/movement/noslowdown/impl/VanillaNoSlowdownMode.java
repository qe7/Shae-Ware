package wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.impl;

import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.SlowdownEvent;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.NoSlowdownModule;

public final class VanillaNoSlowdownMode extends AbstractModuleMode<NoSlowdownModule> {

  public VanillaNoSlowdownMode(NoSlowdownModule module, String name) {
    super(module, name);
  }

  @SubscribeEvent
  private final Listener<SlowdownEvent> onSlowdown = event -> event.setCancelled(true);
}
