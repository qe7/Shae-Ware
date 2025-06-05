package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.core.feature.component.ComponentFactory;
import wtf.silhouette.shaeware.features.components.RotationComponent;

@Mixin(PlayerEntityRenderer.class)
public final class PlayerEntityRendererMixin {

  @Unique
  private float lastPitch = 0.0f;
  @Unique
  private boolean wasRotating = false;
  @Unique
  private long pitchToggleTime = 0;

  @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("RETURN"))
  private void updateRenderStateRotations(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta, CallbackInfo info) {
    if (player == MinecraftClient.getInstance().player) {
      RotationComponent rotationComponent = ComponentFactory.get(RotationComponent.class);
      boolean isRotating = rotationComponent.isRotating();
      long currentTime = System.currentTimeMillis();

      if (isRotating != wasRotating) {
        wasRotating = isRotating;
        pitchToggleTime = currentTime;
      }

      long timeSinceToggle = currentTime - pitchToggleTime;

      if (timeSinceToggle < 50) {
        float t = timeSinceToggle / 50f;
        t = MathHelper.clamp(t, 0.0f, 1.0f);

        if (isRotating) {
          state.pitch = MathHelper.lerpAngleDegrees(t, state.pitch, lastPitch);
        } else {
          state.pitch = MathHelper.lerpAngleDegrees(t, lastPitch, state.pitch);
        }
      } else if (isRotating) {
        float[] rotation = rotationComponent.getCurrentRotation();
        if (rotation != null) {
          float targetPitch = rotation[1] % 360.0f;
          float currentPitch = lastPitch % 360.0f;

          float angleDiff = MathHelper.wrapDegrees(targetPitch - currentPitch);

          if (Math.abs(angleDiff) < 0.1f) {
            state.pitch = lastPitch;
            return;
          }

          float deltaTime = tickDelta / 20f;
          float smoothing = 7.5f;
          ;
          float t = 1.0f - (float) Math.exp(-smoothing * deltaTime);

          lastPitch = currentPitch + angleDiff * t;
          state.pitch = lastPitch;
        }
      }
    }
  }
}
