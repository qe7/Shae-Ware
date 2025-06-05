package wtf.silhouette.shaeware.core.bus.impl.game;

import wtf.silhouette.shaeware.core.bus.Event;

public record KeyPressEvent(int keyCode) implements Event {
}
