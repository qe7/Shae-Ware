package wtf.silhouette.shaeware.utils.player;

import com.google.common.base.Predicates;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class RayCastUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static HitResult raycast(final double maxDistance, final float tickDelta, final boolean includeFluids, final float yaw, final float pitch) {
        final Vec3d vec3d = getCameraPosVec(tickDelta, mc.player);
        final Vec3d vec3d2 = getRotationVector(pitch, yaw);
        final Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
    }

    public static HitResult raycast(final Entity entity, final double maxDistance, final float tickDelta, final boolean includeFluids, final float yaw, final float pitch) {
        final Vec3d vec3d = getCameraPosVec(tickDelta, entity);
        final Vec3d vec3d2 = getRotationVector(pitch, yaw);
        final Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);

        final Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1, 1, 1);

        return ProjectileUtil.raycast(mc.player, vec3d, vec3d3, box, Predicates.alwaysTrue(), maxDistance * maxDistance);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vec3d getCameraPosVec(final float tickDelta, final @NotNull Entity entity) {
        final double d = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        final double e = MathHelper.lerp(tickDelta, entity.prevY, entity.getY()) + (double) entity.getStandingEyeHeight();
        final double f = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        return new Vec3d(d, e, f);
    }


    @Contract("_, _ -> new")
    public static @NotNull Vec3d getRotationVector(final float pitch, final float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public static boolean isOverBlock(@NotNull Vec2f rotations, BlockPos blockPos, Direction direction) {
        final HitResult hitResult = raycast(5, 1, false, rotations.x, rotations.y);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            final BlockPos hitBlockPos = blockHitResult.getBlockPos();
            final Direction hitDirection = blockHitResult.getSide();
            return hitBlockPos.equals(blockPos) && hitDirection == direction;
        }

        return false;
    }
}
