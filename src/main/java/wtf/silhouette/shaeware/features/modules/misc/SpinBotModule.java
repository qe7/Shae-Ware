package wtf.silhouette.shaeware.features.modules.misc;

import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.TickEvent;
import wtf.silhouette.shaeware.core.component.ComponentFactory;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.components.RotationComponent;

public final class SpinBotModule extends AbstractModule {

  private float currentYaw = -180f;
  private float currentPitch = -90f;

  public SpinBotModule() {
    super("Spin Bot", "Spin like you're anti-aiming in CSGO.", ModuleCategory.MISC);
  }

  @SubscribeEvent
  private final Listener<TickEvent> tickEventListener = event -> {
    if (mc.world == null || mc.player == null) return;

    float yawStep = 15f;
    currentYaw += yawStep;
    if (currentYaw > 180) {
      currentYaw = -180;
    }

    currentPitch = 90;

    ComponentFactory.get(RotationComponent.class).setNextRotations(new float[]{currentYaw, currentPitch});
  };
}
