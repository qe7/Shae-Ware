package wtf.silhouette.shaeware.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import wtf.silhouette.shaeware.events.impl.player.StrafeEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

@Mixin(Entity.class)
public abstract class EntityMixin {

  @Shadow
  protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw){return null;}

  @Shadow
  public abstract float getYaw();

  @Shadow
  public abstract Vec3d getVelocity();

  @Shadow
  public abstract void setVelocity(Vec3d velocity);

  /**
   * @author Shae
   * @reason Player strafe event implementation
   */
  @Overwrite
  public void updateVelocity(float speed, Vec3d movementInput) {
    StrafeEvent event = new StrafeEvent(speed, getYaw());
    ManagerFactory.get(EventManager.class).publishEvent(event);
    Vec3d vec3d = movementInputToVelocity(movementInput, event.getMoveSpeed(), event.getYaw());
    this.setVelocity(this.getVelocity().add(vec3d));
  }
}
