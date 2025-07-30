package wtf.silhouette.shaeware.core.bus.impl.player;

import wtf.silhouette.shaeware.core.bus.CancellableEvent;

public final class KeepSprintEvent implements CancellableEvent {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
