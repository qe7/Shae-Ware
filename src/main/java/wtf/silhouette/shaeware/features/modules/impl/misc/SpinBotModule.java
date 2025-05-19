package wtf.silhouette.shaeware.features.modules.impl.misc;

import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.TickEvent;
import wtf.silhouette.shaeware.features.components.api.ComponentFactory;
import wtf.silhouette.shaeware.features.components.impl.RotationComponent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;

public final class SpinBotModule extends AbstractModule {

  private float currentYaw = -180f;
  private float currentPitch = -90f;

  public SpinBotModule() {
    super("Spin Bot", "Spin like you're anti-aiming in CSGO.", ModuleCategory.MISC);
  }

  @SubscribeEvent
  private final Listener<TickEvent> tickEventListener = event -> {
    if (mc.world == null || mc.player == null) {
      return;
    }

    float yawStep = 5f;
    currentYaw += yawStep;
    if (currentYaw > 180) {
      currentYaw = -180;
    }

    float pitchStep = 2f;
    currentPitch += pitchStep;
    if (currentPitch > 90) {
      currentPitch = -90;
    }

    ComponentFactory.get(RotationComponent.class).setNextRotations(new float[]{currentYaw, currentPitch});
  };
}
