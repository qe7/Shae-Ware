package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.silhouette.shaeware.events.impl.player.JumpEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

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
