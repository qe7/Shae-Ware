package wtf.silhouette.shaeware.features.modules.impl.movement;

import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.TickEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;

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
