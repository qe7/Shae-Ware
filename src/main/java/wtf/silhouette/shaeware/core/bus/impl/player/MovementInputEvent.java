package wtf.silhouette.shaeware.core.bus.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.core.bus.Event;
import wtf.silhouette.shaeware.utils.player.DirectionalInput;

@AllArgsConstructor
@Getter
@Setter
public class MovementInputEvent implements Event {

    private DirectionalInput directionalInput;
    private boolean jump;
    private boolean sneak;
}
