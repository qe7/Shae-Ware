package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import wtf.silhouette.shaeware.utils.mixin.InputAddition;

@Mixin(Input.class)
public abstract class InputMixin implements InputAddition {

  @Shadow
  public float movementForward;

  @Shadow
  public float movementSideways;

  @Unique
  protected PlayerInput initial = PlayerInput.DEFAULT;

  @Unique
  protected PlayerInput untransformed = PlayerInput.DEFAULT;

  @ModifyReturnValue(method = "hasForwardMovement", at = @At("RETURN"))
  private boolean hookOmnidirectionalSprint(boolean original) {
    // Allow omnidirectional sprinting
//    if (ModuleSprint.INSTANCE.getShouldSprintOmnidirectional()) {
//      return Math.abs(movementForward) > 1.0E-5F || Math.abs(movementSideways) > 1.0E-5F;
//    }

    return original;
  }

  @Override
  public PlayerInput liquid_bounce$getInitial() {
    return initial;
  }

  @Override
  public PlayerInput liquid_bounce$getUntransformed() {
    return untransformed;
  }
}
