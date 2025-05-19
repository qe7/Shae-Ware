package wtf.silhouette.shaeware.features.components.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.player.JumpEvent;
import wtf.silhouette.shaeware.events.impl.player.MotionEvent;
import wtf.silhouette.shaeware.events.impl.player.MovementInputEvent;
import wtf.silhouette.shaeware.events.impl.player.StrafeEvent;
import wtf.silhouette.shaeware.features.components.api.Component;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;
import wtf.silhouette.shaeware.utils.player.MovementUtil;

@Getter
public final class RotationComponent extends Component {

  @Setter
  private MovementFixMode movementFix = MovementFixMode.OFF;

  @Setter
  private float[] currentRotation;

  @Setter
  private float[] nextRotation;

  @Setter
  private boolean rotating;

  public RotationComponent() {
    ManagerFactory.get(EventManager.class).registerHandler(this);
  }

  @SubscribeEvent
  private final Listener<MotionEvent> motionEventListener = event -> {
    if (event.isPost()) {
      return;
    }

    this.setRotating(false);

    if (this.getNextRotation() != null) {
      event.setYaw(this.getNextRotation()[0]);
      event.setPitch(this.getNextRotation()[1]);
      this.setNextRotation(null);
      this.setMovementFix(MovementFixMode.OFF);
      this.setRotating(true);
    }

    this.setCurrentRotation(new float[]{event.getYaw(), event.getPitch()});
  };

  @SubscribeEvent
  private final Listener<MovementInputEvent> movementInputEventListener = event -> {
    if (this.getMovementFix() == MovementFixMode.ADVANCED) {
      MovementUtil.fixMovement(this.getNextRotation()[0]);
    }
  };

  @SubscribeEvent
  private final Listener<StrafeEvent> strafeEventListener = event -> {
    if (this.getMovementFix() != MovementFixMode.OFF) {
      event.setYaw(this.getNextRotation()[0]);
    }
  };

  @SubscribeEvent
  private final Listener<JumpEvent> jumpEventListener = event -> {
    if (this.getMovementFix() != MovementFixMode.OFF) {
      event.setYaw(this.getNextRotation()[0]);
    }
  };

  private void setNextRotations(final float[] rotations, final MovementFixMode movementFix) {
    if (this.getNextRotation() == null) {
      this.setNextRotation(rotations);
      this.setMovementFix(movementFix);
    }
  }

  @AllArgsConstructor
  public enum MovementFixMode implements IEnumSetting {
    OFF("Off"), BASIC("Basic"), ADVANCED("Advanced");

    private final String name;

    @Override
    public String getName() {
      return name;
    }
  }
}
