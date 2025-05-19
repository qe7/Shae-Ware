package wtf.silhouette.shaeware.features.modules.impl.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.game.PacketEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.IntSetting;
import wtf.silhouette.shaeware.utils.mixin.IEntityVelocityUpdateS2CPacketMixin;

public final class VelocityModule extends AbstractModule {

  private final IntSetting horizontal = new IntSetting("Horizontal", 0, 0, 100, 1);
  private final IntSetting vertical = new IntSetting("Vertical", 0, 0, 100, 1);

  public VelocityModule() {
    super("Anti Velocity", "Reduced or removes velocity.", ModuleCategory.COMBAT);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    this.setSuffix(String.format("%s %s", horizontal.getValue() + "%", vertical.getValue() + "%"));
  };

  @SubscribeEvent
  private final Listener<PacketEvent> packetEventListener = event -> {
    if (!event.isIncoming()) {
      return;
    }

    if (MinecraftClient.getInstance().player == null) {
      return;
    }

    if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
      if (packet.getEntityId() != MinecraftClient.getInstance().player.getId()) {
        return;
      }

      if (horizontal.getValue() == 0) {
        event.setCancelled(true);

        if (vertical.getValue() > 0) {
          final double x = MinecraftClient.getInstance().player.getVelocity().x;
          final double y = packet.getVelocityY();
          final double z = MinecraftClient.getInstance().player.getVelocity().z;

          MinecraftClient.getInstance().player.setVelocity(x, y, z);
        }
      }

      ((IEntityVelocityUpdateS2CPacketMixin) packet).setX((int) ((packet.getVelocityX() * 8000 * (horizontal.getValue()) / 100)));
      ((IEntityVelocityUpdateS2CPacketMixin) packet).setY((int) ((packet.getVelocityY() * 8000 * (vertical.getValue()) / 100)));
      ((IEntityVelocityUpdateS2CPacketMixin) packet).setZ((int) ((packet.getVelocityZ() * 8000 * (horizontal.getValue()) / 100)));
    }
  };
}
