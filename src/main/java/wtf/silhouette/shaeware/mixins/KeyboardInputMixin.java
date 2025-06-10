package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.impl.player.MovementInputEvent;
import wtf.silhouette.shaeware.core.feature.component.ComponentFactory;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.features.components.RotationComponent;
import wtf.silhouette.shaeware.utils.player.DirectionalInput;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends InputMixin {

  @ModifyExpressionValue(method = "tick", at = @At(value = "NEW", target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;"))
  private PlayerInput modifyInput(PlayerInput original) {
    this.initial = original;

    var event = new MovementInputEvent(new DirectionalInput(original), original.jump(), original.sneak());

    ManagerFactory.get(EventManager.class).publishEvent(event);

    var untransformedDirectionalInput = event.getDirectionalInput();
    var directionalInput = transformDirection(untransformedDirectionalInput);

//    var sprintEvent = new SprintEvent(directionalInput, original.sprint(), SprintEvent.Source.INPUT);
//    EventManager.INSTANCE.callEvent(sprintEvent);

    // Store the untransformed input for later use
    this.untransformed = new PlayerInput(
            untransformedDirectionalInput.forwards(),
            untransformedDirectionalInput.backwards(),
            untransformedDirectionalInput.left(),
            untransformedDirectionalInput.right(),
            MinecraftClient.getInstance().options.jumpKey.isPressed(),
            MinecraftClient.getInstance().player.isSneaking(),
            MinecraftClient.getInstance().player.isSprinting()
    );

    return new PlayerInput(
            directionalInput.forwards(),
            directionalInput.backwards(),
            directionalInput.left(),
            directionalInput.right(),
            MinecraftClient.getInstance().options.jumpKey.isPressed(),
            MinecraftClient.getInstance().player.isSneaking(),
            MinecraftClient.getInstance().player.isSprinting()
    );
  }

  @Unique
  private DirectionalInput transformDirection(DirectionalInput input) {
    var player = MinecraftClient.getInstance().player;
    var rotation = ComponentFactory.get(RotationComponent.class).getCurrentRotation();
    var configurable = ComponentFactory.get(RotationComponent.class).getNextRotation();

    float z = this.callGetMovementMultiplier(input.forwards(), input.backwards());
    float x = this.callGetMovementMultiplier(input.left(), input.right());

    if (configurable == null || ComponentFactory.get(RotationComponent.class).getMovementFix() != RotationComponent.MovementFixMode.ADVANCED
            || rotation == null || player == null) {
      return input;
    }

    float deltaYaw = player.getYaw() - rotation[0];

    float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z *
            MathHelper.sin(deltaYaw * 0.017453292f);
    float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x *
            MathHelper.sin(deltaYaw * 0.017453292f);

    var movementSideways = Math.round(newX);
    var movementForward = Math.round(newZ);

    return new DirectionalInput(movementForward, movementSideways);
  }

  @Unique
  float callGetMovementMultiplier(boolean slowDown, boolean movingSideways) {
    if (slowDown == movingSideways) {
      return 0.0F;
    } else {
      return slowDown ? 1.0F : -1.0F;
    }
  }
}
