package wtf.silhouette.shaeware.features.modules.movement;

import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.TickEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;

public final class SprintModule extends AbstractModule {

  public SprintModule() {
    super("Sprint", "Automatically sprints for the player.", ModuleCategory.MOVEMENT);
  }

  @Override
  public void onDisable() {
    super.onDisable();

    mc.options.sprintKey.setPressed(false);
  }

  @SubscribeEvent
  private final Listener<TickEvent> tickEventListener = event -> {
    mc.options.sprintKey.setPressed(true);
  };
}
