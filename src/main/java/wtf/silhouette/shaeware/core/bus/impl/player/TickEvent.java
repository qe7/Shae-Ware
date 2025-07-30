package wtf.silhouette.shaeware.core.bus.impl.player;

import wtf.silhouette.shaeware.core.bus.TypeEvent;

public record TickEvent(Type type) implements TypeEvent {

    @Override
    public Type type() {
        return type;
    }
}
