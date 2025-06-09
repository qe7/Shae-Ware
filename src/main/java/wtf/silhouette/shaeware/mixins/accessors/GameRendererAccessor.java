package wtf.silhouette.shaeware.mixins.accessors;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("bobView")
    void invokerBobView(MatrixStack matrices, float tickDelta);

    @Invoker("tiltViewWhenHurt")
    void invokerTiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Invoker("getFov")
    float invokerGetFov(Camera camera, float tickDelta, boolean changingFov);

    @Accessor
    Pool getPool();
}