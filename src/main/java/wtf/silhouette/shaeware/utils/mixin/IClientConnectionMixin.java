package wtf.silhouette.shaeware.utils.mixin;

import net.minecraft.network.packet.Packet;

public interface IClientConnectionMixin {

    void sendPacketNoEvent(Packet<?> packet);

    void processPacket(Packet<?> packet);
}