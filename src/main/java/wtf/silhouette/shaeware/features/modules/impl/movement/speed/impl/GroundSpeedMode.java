package wtf.silhouette.shaeware.features.modules.impl.movement.speed.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.MotionEvent;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.FloatSetting;
import wtf.silhouette.shaeware.features.modules.impl.movement.speed.SpeedModule;
import wtf.silhouette.shaeware.utils.player.MovementUtil;

import java.util.function.BooleanSupplier;

public final class GroundSpeedMode extends AbstractModuleMode<SpeedModule> {

  private final FloatSetting speed = new FloatSetting("Speed", 0.3F, 0.1F, 1.0F, 0.05f);

  public GroundSpeedMode(SpeedModule module, String name) {
    super(module, name);

    BooleanSupplier booleanSupplier = () -> module.getMode().getValue().getName() == name;

    speed.supplyIf(booleanSupplier);
  }

  @SubscribeEvent
  private final Listener<MotionEvent> motionEventListener = event -> {
    if (mc.world == null || mc.player == null) {
      return;
    }

    if (!MovementUtil.isMoving()) {
      return;
    }

    float speed = this.speed.getValue();

    StatusEffectInstance speedEffect = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.SPEED);
    StatusEffectInstance slowdownEffect = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.SLOWNESS);

    if (speedEffect != null && speedEffect.getEffectType() == StatusEffects.SPEED) {
      speed *= (float) (1 + 0.2 * (speedEffect.getAmplifier() + 1));
    }

    if (slowdownEffect != null && slowdownEffect.getEffectType() == StatusEffects.SLOWNESS) {
      speed /= (float) (1 + 0.2 * (slowdownEffect.getAmplifier() + 1));
    }

    if (mc.player.isOnGround()) {
      MovementUtil.setSpeed(speed);
    }
  };
}
