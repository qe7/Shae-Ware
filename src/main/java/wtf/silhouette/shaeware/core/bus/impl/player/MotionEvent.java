package wtf.silhouette.shaeware.core.bus.impl.player;

import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.core.bus.TypeEvent;

@Getter
@Setter
public final class MotionEvent implements TypeEvent {

    private final Type type;

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround, collided;

    public MotionEvent(Type type, double x, double y, double z, float yaw, float pitch, boolean onGround, boolean collided) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.collided = collided;
    }

    @Override
    public Type type() {
        return type;
    }
}
