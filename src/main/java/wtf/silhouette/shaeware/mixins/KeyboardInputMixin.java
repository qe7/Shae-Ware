package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.core.bus.impl.player.MovementInputEvent;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.core.bus.EventManager;

@Mixin(KeyboardInput.class)
public final class KeyboardInputMixin {

  @Inject(method = "tick()V", at = @At("RETURN"))
  public void tick(CallbackInfo ci) {
    ManagerFactory.get(EventManager.class).publishEvent(new MovementInputEvent());
  }
}
