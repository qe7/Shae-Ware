package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public final class KeyboardInputMixin {

  @Inject(at = @At("RETURN"), method = "tick()V")
  public void tick(CallbackInfo ci) {
    
  }
}
