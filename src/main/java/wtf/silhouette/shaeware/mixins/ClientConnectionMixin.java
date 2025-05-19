package wtf.silhouette.shaeware.mixins;

import com.google.common.collect.Queues;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.events.impl.game.PacketEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;
import wtf.silhouette.shaeware.utils.mixin.IClientConnectionMixin;

import java.util.Queue;
import java.util.function.Consumer;

@Mixin(net.minecraft.network.ClientConnection.class)
public abstract class ClientConnectionMixin implements IClientConnectionMixin {

  @Shadow
  public abstract boolean isOpen();

  @Shadow
  protected abstract void handleQueuedTasks();

  @Shadow
  private void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush) {
  }

  @Shadow
  private final Queue<Consumer<net.minecraft.network.ClientConnection>> queuedTasks = Queues.newConcurrentLinkedQueue();

  @Shadow
  private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
  }

  @Shadow
  private PacketListener packetListener;

  @Inject(method = "channelRead0*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0), cancellable = true)
  private void onChannelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
    PacketEvent event = new PacketEvent(PacketEvent.Type.INCOMING, packet);
    ManagerFactory.get(EventManager.class).publishEvent(event);
    if (event.isCancelled()) {
      ci.cancel();
    }
  }

  @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
  private void onSend(Packet<?> packet, @Nullable PacketCallbacks callback, CallbackInfo ci) {
    PacketEvent event = new PacketEvent(PacketEvent.Type.OUTGOING, packet);
    ManagerFactory.get(EventManager.class).publishEvent(event);
    if (event.isCancelled()) {
      ci.cancel();
    }
  }

  @Override
  public void sendPacketNoEvent(Packet<?> packet) {
    sendPacketNoEvent(packet, null, true);
  }

  @Unique
  private void sendPacketNoEvent(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush) {
    if (this.isOpen()) {
      this.handleQueuedTasks();
      this.sendImmediately(packet, callbacks, flush);
    } else {
      this.queuedTasks.add(conn -> this.sendImmediately(packet, callbacks, flush));
    }
  }

  @Override
  public void processPacket(Packet<?> packet) {
    handlePacket(packet, packetListener);
  }
}