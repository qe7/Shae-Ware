package wtf.silhouette.shaeware.features.modules.impl.combat.velocity.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.game.PacketEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.IntSetting;
import wtf.silhouette.shaeware.features.modules.impl.combat.velocity.VelocityModule;
import wtf.silhouette.shaeware.utils.mixin.IEntityVelocityUpdateS2CPacketMixin;

import java.util.function.BooleanSupplier;

public final class PacketVelocityMode extends AbstractModuleMode<VelocityModule> {

  private final IntSetting horizontal = new IntSetting("Horizontal", 0, 0, 100, 1);
  private final IntSetting vertical = new IntSetting("Vertical", 0, 0, 100, 1);

  public PacketVelocityMode(VelocityModule module, String name) {
    super(module, name);

    BooleanSupplier booleanSupplier = () -> module.getMode().getValue() instanceof PacketVelocityMode;

    horizontal.supplyIf(booleanSupplier);
    vertical.supplyIf(booleanSupplier);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    module.setSuffix(String.format("%s %s", horizontal.getValue() + "%", vertical.getValue() + "%"));
  };

  @SubscribeEvent
  private final Listener<PacketEvent> packetEventListener = event -> {
    if (!event.isIncoming()) {
      return;
    }

    if (mc.player == null) {
      return;
    }

    if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
      if (packet.getEntityId() != mc.player.getId()) {
        return;
      }

      if (horizontal.getValue() == 0) {
        event.setCancelled(true);

        if (vertical.getValue() > 0) {
          final double x = mc.player.getVelocity().x;
          final double y = packet.getVelocityY();
          final double z = mc.player.getVelocity().z;

          mc.player.setVelocity(x, y, z);
        }
      }

      ((IEntityVelocityUpdateS2CPacketMixin) packet).setX((int) ((packet.getVelocityX() * 8000 * (horizontal.getValue()) / 100)));
      ((IEntityVelocityUpdateS2CPacketMixin) packet).setY((int) ((packet.getVelocityY() * 8000 * (vertical.getValue()) / 100)));
      ((IEntityVelocityUpdateS2CPacketMixin) packet).setZ((int) ((packet.getVelocityZ() * 8000 * (horizontal.getValue()) / 100)));
    }
  };
}
