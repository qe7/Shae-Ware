package wtf.silhouette.shaeware.utils.player;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import wtf.silhouette.shaeware.utils.MathUtil;

@UtilityClass
public final class MovementUtil {

  public static final double WALK_SPEED = 0.2873;

  public static boolean isMoving() {
    if (MinecraftClient.getInstance().player == null) {
      return false;
    }
    return MinecraftClient.getInstance().player.input.movementForward != 0 || MinecraftClient.getInstance().player.input.movementSideways != 0;
  }

  public static float direction(float rotationYaw, final double moveForward, final double moveStrafing) {
    if (moveForward < 0F) rotationYaw += 180F;

    float forward = 1F;

    if (moveForward < 0F) forward = -0.5F;
    else if (moveForward > 0F) forward = 0.5F;

    if (moveStrafing > 0F) rotationYaw -= 90F * forward;
    if (moveStrafing < 0F) rotationYaw += 90F * forward;

    return (float) Math.toRadians(rotationYaw);
  }

  public static float direction() {
    if (MinecraftClient.getInstance().player == null) {
      return 0.0F;
    }
    return direction(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.input.movementForward, MinecraftClient.getInstance().player.input.movementSideways);
  }

  public static void fixMovement(final float yaw) {
    if (MinecraftClient.getInstance().player == null) {
      return;
    }

    if (!isMoving()) {
      return;
    }

    final double angle = MathHelper.wrapDegrees(Math.toDegrees(direction()));

    float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

    for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
      for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
        if (predictedStrafe == 0 && predictedForward == 0) continue;

        final double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
        final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

        if (difference < closestDifference) {
          closestDifference = (float) difference;
          closestForward = predictedForward;
          closestStrafe = predictedStrafe;
        }
      }
    }

    MinecraftClient.getInstance().player.input.movementForward = closestForward;
    MinecraftClient.getInstance().player.input.movementSideways = closestStrafe;
  }

  public static float getMovementDirection(final float yaw, final float forward, final float strafe) {
    if (MinecraftClient.getInstance().player == null) {
      return 0;
    }

    final boolean backwards = forward < 0;
    float direction = yaw;

    if (backwards) {
      direction += 180.0f;
    }

    final float strafeSign = Math.signum(strafe);
    final float forwardSign = Math.signum(forward);
    direction -= 90.0f * strafeSign / (backwards ? -1 : 1);

    if (strafeSign != 0) {
      direction += forwardSign * strafeSign * 45.0f;
    }

    return direction;
  }

  public static void setSpeed(double speed) {
    if (MinecraftClient.getInstance().player == null) {
      return;
    }

    final AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;
    final float forward = player.forwardSpeed;
    final float strafe = player.sidewaysSpeed;

    if (forward == 0 && strafe == 0) {
      return;
    }

    final float dir = getMovementDirection(player.getYaw(), forward, strafe);
    final double rad = Math.toRadians(dir);

    player.setVelocity(-Math.sin(rad) * speed, player.getVelocity().y, Math.cos(rad) * speed);
  }

  public static double getBaseSpeed(double baseSpeed) {
    if (MinecraftClient.getInstance().player == null) {
      return 0.0D;
    }

    StatusEffectInstance speedEffect = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.SPEED);
    StatusEffectInstance slowdownEffect = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.SLOWNESS);

    if (speedEffect != null && speedEffect.getEffectType() == StatusEffects.SPEED) {
      baseSpeed *= 1 + 0.2 * (speedEffect.getAmplifier() + 1);
    }

    if (slowdownEffect != null && slowdownEffect.getEffectType() == StatusEffects.SLOWNESS) {
      baseSpeed /= 1 + 0.2 * (slowdownEffect.getAmplifier() + 1);
    }

    return baseSpeed;
  }
}
