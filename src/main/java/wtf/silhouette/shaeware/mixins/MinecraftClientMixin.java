package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.uis.clickgui.ImGuiClickGui;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin {

  @Unique
  private boolean initialized;

  @Inject(at = @At("HEAD"), method = "render(Z)V")
  private void render(boolean tick, CallbackInfo callbackInfo) {
    if (!initialized && ShaeWare.getInstance().isInitialized()) {
      ImGuiClickGui.INSTANCE.initialize();

      initialized = true;
    }
  }
}
