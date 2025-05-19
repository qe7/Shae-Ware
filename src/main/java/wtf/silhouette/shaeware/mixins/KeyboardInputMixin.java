package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.events.impl.player.MovementInputEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

@Mixin(KeyboardInput.class)
public final class KeyboardInputMixin {

  @Inject(at = @At("RETURN"), method = "tick()V")
  public void tick(CallbackInfo ci) {
    ManagerFactory.get(EventManager.class).publishEvent(new MovementInputEvent());
  }
}
