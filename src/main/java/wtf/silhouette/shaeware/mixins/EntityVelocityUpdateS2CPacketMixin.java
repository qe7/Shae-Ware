package wtf.silhouette.shaeware.mixins;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import wtf.silhouette.shaeware.utils.mixin.IEntityVelocityUpdateS2CPacketMixin;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public abstract class EntityVelocityUpdateS2CPacketMixin implements IEntityVelocityUpdateS2CPacketMixin {

  @Mutable
  @Accessor("velocityX")
  public abstract void setVelocityX(int velocityX);

  @Mutable
  @Accessor("velocityY")
  public abstract void setVelocityY(int velocityY);

  @Mutable
  @Accessor("velocityZ")
  public abstract void setVelocityZ(int velocityZ);

  @Override
  public void setX(int velocityX) {
    setVelocityX(velocityX);
  }

  @Override
  public void setY(int velocityY) {
    setVelocityY(velocityY);
  }

  @Override
  public void setZ(int velocityZ) {
    setVelocityZ(velocityZ);
  }
}