package wtf.silhouette.shaeware.utils.player;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import wtf.silhouette.shaeware.utils.IMinecraft;
import wtf.silhouette.shaeware.utils.MathUtil;

public final class MovementUtil implements IMinecraft {

  private MovementUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static boolean isMoving() {
    if (mc.player == null) {
      return false;
    }
    return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
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
    if (mc.player == null) {
      return 0.0F;
    }
    return direction(mc.player.getYaw(), mc.player.input.movementForward, mc.player.input.movementSideways);
  }

  public static void fixMovement(final float yaw) {
    if (mc.player == null) {
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

    mc.player.input.movementForward = closestForward;
    mc.player.input.movementSideways = closestStrafe;
  }

  public static void strafe(double speed) {
    if (mc.player == null) {
      return;
    }

    strafe(mc.player.getYaw(), speed);
  }

  public static void strafe(float yaw, double speed) {
    if (mc.player == null) {
      return;
    }

    float direction = (float) Math.toRadians(yaw);
    Vec3d velocity = mc.player.getVelocity();

    mc.player.setVelocity(-Math.sin(direction) * speed, velocity.y, Math.cos(direction) * speed);
  }

  public static double getBaseSpeed(double baseSpeed) {
    if (mc.player == null) {
      return 0.0D;
    }

    StatusEffectInstance speedEffect = mc.player.getStatusEffect(StatusEffects.SPEED);
    StatusEffectInstance slowdownEffect = mc.player.getStatusEffect(StatusEffects.SLOWNESS);

    if (speedEffect != null && speedEffect.getEffectType() == StatusEffects.SPEED) {
      baseSpeed *= 1 + 0.2 * (speedEffect.getAmplifier() + 1);
    }

    if (slowdownEffect != null && slowdownEffect.getEffectType() == StatusEffects.SLOWNESS) {
      baseSpeed /= 1 + 0.2 * (slowdownEffect.getAmplifier() + 1);
    }

    return baseSpeed;
  }
}
