package wtf.silhouette.shaeware.mixins.accessors;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessor {

    @Accessor(value = "currentBreakingProgress")
    float getCurrentBlockDamageMP();

    @Accessor(value = "currentBreakingProgress")
    void setCurrentBlockDamageMP(float a);

    @Invoker("sendSequencedPacket")
    void invokeSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
}
