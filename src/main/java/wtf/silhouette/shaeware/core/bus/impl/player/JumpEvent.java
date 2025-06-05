package wtf.silhouette.shaeware.core.bus.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.core.bus.Event;

@AllArgsConstructor
@Getter
@Setter
public final class JumpEvent implements Event {

  private float yaw;
}
