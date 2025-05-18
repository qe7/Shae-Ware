package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.silhouette.shaeware.events.impl.player.KeepSprintEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

@Mixin(PlayerEntity.class)
public final class PlayerEntityMixin {

  @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
  public void stopSettingSprint(PlayerEntity player, boolean fix) {
    KeepSprintEvent event = new KeepSprintEvent();
    ManagerFactory.get(EventManager.class).publishEvent(event);
    if (!event.isCancelled()) {
      if (MinecraftClient.getInstance().player != null) {
        MinecraftClient.getInstance().player.setSprinting(false);
      }
    }
  }

  @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
  public Vec3d stopSettingVelocity(Vec3d vector, double x, double y, double z) {
    KeepSprintEvent event = new KeepSprintEvent();
    ManagerFactory.get(EventManager.class).publishEvent(event);
    if (!event.isCancelled()) vector.multiply(0.6d, 1, 0.6d);
    return vector;
  }
}
