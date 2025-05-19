package wtf.silhouette.shaeware.features.modules.impl.combat.velocity;

import lombok.Getter;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.impl.combat.velocity.impl.JumpVelocityMode;
import wtf.silhouette.shaeware.features.modules.impl.combat.velocity.impl.PacketVelocityMode;

public final class VelocityModule extends AbstractModule {

  @Getter
  private final ModeSetting mode = new ModeSetting("Mode", "Packet", new PacketVelocityMode(this, "Packet"), new JumpVelocityMode(this, "Jump"));

  public VelocityModule() {
    super("Anti Velocity", "Reduced or removes velocity.", ModuleCategory.COMBAT);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    if (mode.getValue() instanceof PacketVelocityMode)
      return;
    this.setSuffix(mode.getValue().getName());
  };
}
