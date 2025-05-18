package wtf.silhouette.shaeware.events.impl.player;

import wtf.silhouette.shaeware.events.api.TypeEvent;

public record TickEvent(Type type) implements TypeEvent {

  @Override
  public Type type() {
    return type;
  }
}
