package wtf.silhouette.shaeware.features.modules.combat.velocity.impl;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.PacketEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.TickEvent;
import wtf.silhouette.shaeware.core.feature.module.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.combat.velocity.VelocityModule;

public final class JumpVelocityMode extends AbstractModuleMode<VelocityModule> {

  private boolean hasJumped;

  public JumpVelocityMode(VelocityModule module, String name) {
    super(module, name);
  }

  @SubscribeEvent
  private final Listener<TickEvent> tickEventListener = event -> {
    if (hasJumped) {
      mc.options.jumpKey.setPressed(false);
      hasJumped = false;
    }
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

      if (mc.player.isOnGround()) {
        mc.options.jumpKey.setPressed(true);
        hasJumped = true;
      }
    }
  };
}
