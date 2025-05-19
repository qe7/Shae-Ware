package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import wtf.silhouette.shaeware.events.impl.player.JumpEvent;
import wtf.silhouette.shaeware.features.components.api.ComponentFactory;
import wtf.silhouette.shaeware.features.components.impl.RotationComponent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;
import wtf.silhouette.shaeware.utils.IMinecraft;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IMinecraft {

  @ModifyExpressionValue(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
  private float hookHeadRotations(float original) {
    if ((Object) this == mc.player) {
      RotationComponent rotationComponent = ComponentFactory.get(RotationComponent.class);
      float[] currentRotation = rotationComponent.getCurrentRotation();

      if (currentRotation != null) {
        return currentRotation[0];
      }
    } else {
      return original;
    }
    return original;
  }

  @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
  private float tickHeadRotations(float original) {
    if ((Object) this == mc.player) {
      RotationComponent rotationComponent = ComponentFactory.get(RotationComponent.class);
      float[] currentRotation = rotationComponent.getCurrentRotation();

      if (currentRotation != null) {
        return currentRotation[0];
      }
    } else {
      return original;
    }
    return original;
  }

  @ModifyExpressionValue(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
  private float modifyGetYaw(float original) {
    if ((Object) this != mc.player) return original;
    if (MinecraftClient.getInstance().player == null) return original;

    float forward = Math.signum(mc.player.input.movementForward);
    float strafe = 90 * Math.signum(mc.player.input.movementSideways);
    if (forward != 0) strafe *= (forward * 0.5f);

    original -= strafe;
    if (forward < 0) original -= 180;

    return original;
  }

  @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
  public float redirectYaw(LivingEntity entity) {
    if (entity == MinecraftClient.getInstance().player) {
      JumpEvent event = new JumpEvent(entity.getYaw());
      ManagerFactory.get(EventManager.class).publishEvent(event);
      return event.getYaw();
    }

    return entity.getYaw();
  }
}
