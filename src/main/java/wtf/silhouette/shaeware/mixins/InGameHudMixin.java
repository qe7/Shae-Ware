package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.core.bus.EventManager;

@Mixin(InGameHud.class)
public final class InGameHudMixin {

  @Inject(method = "render", at = @At("TAIL"))
  private void onRenderStart(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    context.draw();

    ManagerFactory.get(EventManager.class).publishEvent(new ScreenEvent(context, tickCounter));
  }
}
