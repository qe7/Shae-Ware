package wtf.silhouette.shaeware.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.silhouette.shaeware.events.impl.player.JumpEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;
import wtf.silhouette.shaeware.utils.IMinecraft;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IMinecraft {

  @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
  public float redirectYaw(LivingEntity entity) {
    if (entity == mc.player) {
      JumpEvent event = new JumpEvent(entity.getYaw());
      ManagerFactory.get(EventManager.class).publishEvent(event);
      return event.getYaw();
    }

    return entity.getYaw();
  }
}
