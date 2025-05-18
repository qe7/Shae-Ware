package wtf.silhouette.shaeware.events.impl.game;

import wtf.silhouette.shaeware.events.api.Event;

public record KeyPressEvent(int keyCode) implements Event {
}
