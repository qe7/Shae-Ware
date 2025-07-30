package wtf.silhouette.shaeware.mixins;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.impl.game.KeyPressEvent;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;

@Mixin(Keyboard.class)
public final class KeyboardMixin {

    @Inject(method = "onKey", at = @At(value = "HEAD", target = "Lnet/minecraft/client/Keyboard;onKey(JIIII)V"))
    private void onKeyEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == 1) {
            ManagerFactory.get(EventManager.class).publishEvent(new KeyPressEvent(key));
        }
    }
}