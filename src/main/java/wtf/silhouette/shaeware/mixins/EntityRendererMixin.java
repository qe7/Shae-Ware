package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.silhouette.shaeware.core.feature.module.ModuleManager;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.features.modules.render.ESPModule;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private T capturedEntity;

    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
    private void captureEntity(T entity, float tickDelta, CallbackInfoReturnable<S> cir) {
        capturedEntity = entity;
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void interceptRenderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ManagerFactory.isRegistered(ModuleManager.class)) {
            return;
        }

        ESPModule module = (ESPModule) ManagerFactory.get(ModuleManager.class).get(ESPModule.class);

        if (module.isEnabled() && module.getNameTagMaster()) {
            if (capturedEntity instanceof PlayerEntity) {
                ci.cancel();
            }
        }
    }
}
