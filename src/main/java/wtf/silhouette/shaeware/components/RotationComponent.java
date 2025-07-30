package wtf.silhouette.shaeware.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.JumpEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.MotionEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.MovementInputEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.StrafeEvent;
import wtf.silhouette.shaeware.core.component.Component;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.features.modules.movement.MovementFixModule;
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
            this.setRotating(true);
            event.setYaw(this.getNextRotation()[0]);
            event.setPitch(this.getNextRotation()[1]);
            this.setNextRotation(null);
            this.setMovementFix(MovementFixMode.OFF);
        }

        this.setCurrentRotation(new float[]{event.getYaw(), event.getPitch()});
    };

    @SubscribeEvent
    private final Listener<MovementInputEvent> movementInputEventListener = event -> {
        if (this.getNextRotation() != null && this.getMovementFix() == MovementFixMode.ADVANCED) {
            MovementUtil.fixMovement(this.getNextRotation()[0]);
        }
    };

    @SubscribeEvent
    private final Listener<StrafeEvent> strafeEventListener = event -> {
        if (this.getNextRotation() != null && this.getMovementFix() != MovementFixMode.OFF) {
            event.setYaw(this.getNextRotation()[0]);
        }
    };

    @SubscribeEvent
    private final Listener<JumpEvent> jumpEventListener = event -> {
        if (this.getNextRotation() != null && this.getMovementFix() != MovementFixMode.OFF) {
            event.setYaw(this.getNextRotation()[0]);
        }
    };

    public void setNextRotations(final float[] rotations) {
        if (this.getNextRotation() == null) {
            this.setNextRotation(rotations);
            this.setMovementFix(MovementFixModule.getMovementFixMode());
        }
    }

    public void setNextRotations(final float[] rotations, final MovementFixMode movementFixMode) {
        if (this.getNextRotation() == null) {
            this.setNextRotations(rotations);
            this.setMovementFix(movementFixMode);
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
