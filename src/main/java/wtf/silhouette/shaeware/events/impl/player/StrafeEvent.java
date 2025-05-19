package wtf.silhouette.shaeware.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.events.api.Event;

@AllArgsConstructor
@Getter
@Setter
public final class StrafeEvent implements Event {

  private float moveSpeed, yaw;
}
