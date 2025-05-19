package wtf.silhouette.shaeware.features.modules.impl.movement.speed;

import lombok.Getter;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.impl.movement.speed.impl.GroundSpeedMode;
import wtf.silhouette.shaeware.features.modules.impl.movement.speed.impl.LegitSpeedMode;

public final class SpeedModule extends AbstractModule {

  @Getter
  private final ModeSetting mode = new ModeSetting("Mode", "Legit", new LegitSpeedMode(this, "Legit"), new GroundSpeedMode(this, "Ground"));

  public SpeedModule() {
    super("Speed", "Modifies the player's velocity to make movement faster.", ModuleCategory.MOVEMENT);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    this.setSuffix(mode.getValue().getName());
  };
}
