package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import wtf.silhouette.shaeware.core.component.ComponentFactory;
import wtf.silhouette.shaeware.components.RotationComponent;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

  @Unique
  private float lastRotX = 0.0f;

  @Unique
  private long rotationToggleTime = 0;

  @Unique
  private boolean wasRotating = false;

  @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F"))
  private float hookHeadYaw(float original, LivingEntity entity, S state, float tickDelta) {
    if (entity != MinecraftClient.getInstance().player) {
      return original;
    }

    RotationComponent rotationComponent = ComponentFactory.get(RotationComponent.class);
    boolean isRotating = rotationComponent.isRotating();
    long currentTime = System.currentTimeMillis();

    if (isRotating != wasRotating) {
      wasRotating = isRotating;
      rotationToggleTime = currentTime;
    }

    long timeSinceToggle = currentTime - rotationToggleTime;
    if (timeSinceToggle < 50) {
      float t = timeSinceToggle / 50f;
      t = MathHelper.clamp(t, 0.0f, 1.0f);

      if (isRotating) {
        return MathHelper.lerpAngleDegrees(t, original, lastRotX);
      } else {
        return MathHelper.lerpAngleDegrees(t, lastRotX, original);
      }
    }

    if (isRotating) {
      float[] rot = rotationComponent.getCurrentRotation();
      if (rot != null) {
        float targetRot = rot[0] % 360.0f;
        float currentRot = lastRotX % 360.0f;

        float angleDiff = MathHelper.wrapDegrees(targetRot - currentRot);

        if (Math.abs(angleDiff) < 0.1f) {
          return lastRotX;
        }

        float deltaTime = tickDelta / 20f;
        float smoothing = 7.5f;
        float t = 1.0f - (float) Math.exp(-smoothing * deltaTime);
        lastRotX = currentRot + angleDiff * t;
        return lastRotX;

      }
    }

    return original;
  }

  @ModifyExpressionValue(
          method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;clampBodyYaw(Lnet/minecraft/entity/LivingEntity;FF)F")
  )
  private float hookBodyYaw(float original, LivingEntity entity, S state, float tickDelta) {
    if (entity != MinecraftClient.getInstance().player) {
      return original;
    }

    RotationComponent rotationComponent = ComponentFactory.get(RotationComponent.class);
    boolean isRotating = rotationComponent.isRotating();
    long currentTime = System.currentTimeMillis();

    if (isRotating != wasRotating) {
      wasRotating = isRotating;
      rotationToggleTime = currentTime;
    }

    long timeSinceToggle = currentTime - rotationToggleTime;
    if (timeSinceToggle < 50) {
      float t = timeSinceToggle / 50f;
      t = MathHelper.clamp(t, 0.0f, 1.0f);

      if (isRotating) {
        return MathHelper.lerpAngleDegrees(t, original, lastRotX);
      } else {
        return MathHelper.lerpAngleDegrees(t, lastRotX, original);
      }
    }

    if (isRotating) {
      float[] rot = rotationComponent.getCurrentRotation();
      if (rot != null) {
        lastRotX = MathHelper.lerpAngleDegrees(0.22f, lastRotX, rot[0]);
        return lastRotX;
      }
    }

    return original;
  }
}