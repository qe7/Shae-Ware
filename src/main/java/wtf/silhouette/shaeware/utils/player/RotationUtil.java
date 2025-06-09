package wtf.silhouette.shaeware.utils.player;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.silhouette.shaeware.mixins.accessors.ClientPlayerEntityAccessor;

@UtilityClass
public final class RotationUtil {

  public static float[] applyGCDPatch(final float @NotNull [] rotation) {
    float lastYaw = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastYaw();
    float lastPitch = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastPitch();

    float[] lastRotation = new float[]{lastYaw, lastPitch};

    // Calculate sensitivity curve (0.2 to 0.8 range mapped from 0-1 sensitivity)
    float[] NullLastRotation = lastRotation;
    if (NullLastRotation == null) {
      NullLastRotation = new float[0];
    }
    final double sensitivity = MinecraftClient.getInstance().options.getMouseSensitivity().getValue() * 0.6 + 0.2;
    // GCD formula: sensitivity^3 * 8 * 0.15 (Minecraft's angular resolution)
    final double gcd = Math.pow(sensitivity, 3) * 8.0 * 0.15;

    // Snap rotation to nearest GCD increment
    final float yaw = NullLastRotation[0] + (float) (Math.round((rotation[0] - NullLastRotation[0]) / gcd) * gcd);
    final float pitch = NullLastRotation[1] + (float) (Math.round((rotation[1] - NullLastRotation[1]) / gcd) * gcd);

    return new float[]{yaw, MathHelper.clamp(pitch, -90.0f, 90.0f)};
  }

  public static float @Nullable [] getEntityRotations(final Entity entity) {
    if (MinecraftClient.getInstance().player == null || entity == null) {
      return null;
    }

    // Return player's own rotation if targeting self
    if (entity == MinecraftClient.getInstance().player) {
      return new float[]{MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch()};
    }

    // Calculate position delta with partial tick interpolation
    final double deltaX = entity.getX() + (entity.getX() - entity.prevX) - MinecraftClient.getInstance().player.getX();
    final double deltaY = entity.getY() + (entity.getEyeHeight(entity.getPose()) * 0.8) - (MinecraftClient.getInstance().player.getY() + MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()));
    final double deltaZ = entity.getZ() + (entity.getZ() - entity.prevZ) - MinecraftClient.getInstance().player.getZ();

    // Calculate horizontal distance
    final double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

    // Initial angle calculations
    float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));
    final float pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

    // Quadrant correction for full 360-degree coverage
    final double quadrantAdjustment = Math.toDegrees(Math.atan(deltaZ / deltaX));
    if (deltaX < 0 && deltaZ < 0) {
      yaw = (float) (90 + quadrantAdjustment);
    } else if (deltaX > 0 && deltaZ < 0) {
      yaw = (float) (-90 + quadrantAdjustment);
    }

    float lastYaw = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastYaw();
    float lastPitch = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastPitch();

    return new float[]{lastYaw + MathHelper.wrapDegrees(yaw - lastYaw), lastPitch + MathHelper.wrapDegrees(pitch - lastPitch)};
  }

  public static float[] getPositionRotations(Vec3d position) {
    Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
    double x = position.x - playerPos.x;
    double y = position.y - (MinecraftClient.getInstance().player.getY() + MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()));
    double z = position.z - playerPos.z;

    double distance = Math.sqrt(x * x + z * z);
    float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
    float pitch = (float) -Math.toDegrees(Math.atan2(y, distance));

    float lastYaw = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastYaw();
    float lastPitch = ((ClientPlayerEntityAccessor) MinecraftClient.getInstance().player).getLastPitch();

    return new float[]{lastYaw + MathHelper.wrapDegrees(yaw - lastYaw), lastPitch + MathHelper.wrapDegrees(pitch - lastPitch)};
  }
}