package wtf.silhouette.shaeware.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import wtf.silhouette.shaeware.mixins.accessors.GameRendererAccessor;

@UtilityClass
public final class RenderUtil {

  public static Vec3d getInterpolatedPos(final @NotNull LivingEntity entity, final float tickDelta) {
    final Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
    return entity.getPos().add(MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - entity.getX(), MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - entity.getY(), MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ()).subtract(camera.getPos());
  }

  public static float[] projectEntity(final int[] viewport, final Matrix4f matrix, final Entity entity) {
    if (entity == null) {
      return null;
    }

    final Vector4f windowCoordinates = new Vector4f();

    Vec3d interpolatedPos = getInterpolatedPos((LivingEntity) entity, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));

    final Vec3d[] corners = getBoundingBox(entity, interpolatedPos);

    float[] projected = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};

    for (final Vec3d pos : corners) {
      matrix.project(pos.toVector3f(), viewport, windowCoordinates);
      windowCoordinates.y = viewport[3] - windowCoordinates.y;

      if (windowCoordinates.w != 1) {
        break;
      }

      final double windowX = windowCoordinates.x;
      final double windowY = windowCoordinates.y;

      projected[0] = (float) Math.min(windowX, projected[0]);
      projected[1] = (float) Math.min(windowY, projected[1]);
      projected[2] = (float) Math.max(windowX, projected[2]);
      projected[3] = (float) Math.max(windowY, projected[3]);
    }

    return projected;
  }

  public static float[] projectBox(final int[] viewport, final Matrix4f matrix, final Box box) {
    if (box == null) {
      return null;
    }

    final Vector4f windowCoordinates = new Vector4f();

    float[] projected = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};

    final Vec3d[] corners = new Vec3d[]{new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.minX, box.minY, box.maxZ), new Vec3d(box.minX, box.maxY, box.minZ), new Vec3d(box.minX, box.maxY, box.maxZ), new Vec3d(box.maxX, box.minY, box.minZ), new Vec3d(box.maxX, box.minY, box.maxZ), new Vec3d(box.maxX, box.maxY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ)};

    for (final Vec3d pos : corners) {
      matrix.project(pos.toVector3f(), viewport, windowCoordinates);
      windowCoordinates.y = viewport[3] - windowCoordinates.y;

      if (windowCoordinates.w != 1) {
        break;
      }

      final double windowX = windowCoordinates.x;
      final double windowY = windowCoordinates.y;

      projected[0] = (float) Math.min(windowX, projected[0]);
      projected[1] = (float) Math.min(windowY, projected[1]);
      projected[2] = (float) Math.max(windowX, projected[2]);
      projected[3] = (float) Math.max(windowY, projected[3]);
    }

    return projected;
  }

  public static Vec3d @NotNull [] getBoundingBox(@NotNull Entity entity, @NotNull Vec3d interpolatedPos) {
    double minX = interpolatedPos.x - 0.5;
    double minY = interpolatedPos.y;
    double minZ = interpolatedPos.z - 0.5;
    double maxX = interpolatedPos.x + 0.5;
    double maxY = interpolatedPos.y + entity.getHeight();
    double maxZ = interpolatedPos.z + 0.5;

    // Create the corners of the bounding box
    return new Vec3d[]{new Vec3d(minX, minY, minZ), new Vec3d(minX, minY, maxZ), new Vec3d(minX, maxY, minZ), new Vec3d(minX, maxY, maxZ), new Vec3d(maxX, minY, minZ), new Vec3d(maxX, minY, maxZ), new Vec3d(maxX, maxY, minZ), new Vec3d(maxX, maxY, maxZ)};
  }

  public static @NotNull MatrixStack createMatrixStack(final float tickDelta) {
    MatrixStack matrixStack = new MatrixStack();
    matrixStack.multiplyPositionMatrix(MinecraftClient.getInstance().gameRenderer.getBasicProjectionMatrix(((GameRendererAccessor) MinecraftClient.getInstance().gameRenderer).invokerGetFov(MinecraftClient.getInstance().gameRenderer.getCamera(), tickDelta, true)));

    ((GameRendererAccessor) MinecraftClient.getInstance().gameRenderer).invokerTiltViewWhenHurt(matrixStack, tickDelta);

    if (MinecraftClient.getInstance().options.getBobView().getValue()) {
      ((GameRendererAccessor) MinecraftClient.getInstance().gameRenderer).invokerBobView(matrixStack, tickDelta);
    }

    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MinecraftClient.getInstance().gameRenderer.getCamera().getPitch()));
    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().gameRenderer.getCamera().getYaw() + 180.0f));
    return matrixStack;
  }
}
