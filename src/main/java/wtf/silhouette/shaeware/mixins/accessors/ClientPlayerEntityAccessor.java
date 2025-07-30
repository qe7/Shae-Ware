package wtf.silhouette.shaeware.mixins.accessors;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {

    @Accessor("lastYaw")
    float getLastYaw();

    @Accessor("lastPitch")
    float getLastPitch();
}
