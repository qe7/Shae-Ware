package wtf.silhouette.shaeware.features.modules.movement.speed.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.MotionEvent;
import wtf.silhouette.shaeware.core.feature.module.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.movement.speed.SpeedModule;
import wtf.silhouette.shaeware.utils.player.MovementUtil;

public final class GroundStrafeSpeedMode extends AbstractModuleMode<SpeedModule> {

  public GroundStrafeSpeedMode(SpeedModule module, String name) {
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
      mc.options.jumpKey.setPressed(false);
      return;
    }

    if (mc.player.isOnGround()) {
      MovementUtil.setSpeed(MovementUtil.getBaseSpeed(MovementUtil.WALK_SPEED - (MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.SPEED) ? 0 : 0.03)));
    }

    if (!mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(mc.player.isOnGround());
  };
}
