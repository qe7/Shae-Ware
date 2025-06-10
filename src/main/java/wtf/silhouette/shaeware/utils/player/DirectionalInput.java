package wtf.silhouette.shaeware.utils.player;

import net.minecraft.util.PlayerInput;

public record DirectionalInput(boolean forwards, boolean backwards, boolean left, boolean right) {

  public DirectionalInput(PlayerInput input) {
    this(input.forward(), input.backward(), input.left(), input.right());
  }

  public DirectionalInput(float movementForward, float movementSideways) {
    this(movementForward > 0.0F, movementForward < 0.0F, movementSideways > 0.0F, movementSideways < 0.0F);
  }

  public DirectionalInput(DirectionalInput other) {
    this(other.forwards, other.backwards, other.left, other.right);
  }

  public DirectionalInput invert() {
    return new DirectionalInput(backwards, forwards, right, left);
  }

  public boolean isMoving() {
    return forwards || backwards || left || right;
  }

  public static final DirectionalInput NONE = new DirectionalInput(false, false, false, false);
  public static final DirectionalInput FORWARDS = new DirectionalInput(true, false, false, false);
  public static final DirectionalInput BACKWARDS = new DirectionalInput(false, true, false, false);
  public static final DirectionalInput LEFT = new DirectionalInput(false, false, true, false);
  public static final DirectionalInput RIGHT = new DirectionalInput(false, false, false, true);
  public static final DirectionalInput FORWARDS_LEFT = new DirectionalInput(true, false, true, false);
  public static final DirectionalInput FORWARDS_RIGHT = new DirectionalInput(true, false, false, true);
  public static final DirectionalInput BACKWARDS_LEFT = new DirectionalInput(false, true, true, false);
  public static final DirectionalInput BACKWARDS_RIGHT = new DirectionalInput(false, true, false, true);
}
