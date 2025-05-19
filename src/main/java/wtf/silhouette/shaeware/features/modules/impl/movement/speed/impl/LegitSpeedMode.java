package wtf.silhouette.shaeware.features.modules.impl.movement.speed.impl;

import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.MotionEvent;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.impl.movement.speed.SpeedModule;
import wtf.silhouette.shaeware.utils.player.MovementUtil;

public final class LegitSpeedMode extends AbstractModuleMode<SpeedModule> {

  public LegitSpeedMode(SpeedModule module, String name) {
    super(module, name);
  }

  @Override
  public void onDisable() {
    super.onDisable();

    mc.options.jumpKey.setPressed(false);
  }

  @SubscribeEvent
  private final Listener<MotionEvent> motionEventListener = event -> {
    if (mc.world == null || mc.player == null) {
      return;
    }

    if (!MovementUtil.isMoving()) {
      if (!mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(false);
      return;
    }

    if (!mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(mc.player.isOnGround());
  };
}
