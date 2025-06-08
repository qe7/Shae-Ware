package wtf.silhouette.shaeware.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.impl.player.JumpEvent;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public final class LivingEntityMixin {

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
