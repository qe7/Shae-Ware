package wtf.silhouette.shaeware.core.bus.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.packet.Packet;
import wtf.silhouette.shaeware.core.bus.CancellableEvent;

@RequiredArgsConstructor
@Getter
@Setter
public final class PacketEvent implements CancellableEvent {

  private final Type type;

  private final Packet<?> packet;

  private boolean cancelled;

  public boolean isIncoming() {
    return type == Type.INCOMING;
  }

  public boolean isOutgoing() {
    return type == Type.OUTGOING;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public enum Type {
    INCOMING, OUTGOING
  }
}
