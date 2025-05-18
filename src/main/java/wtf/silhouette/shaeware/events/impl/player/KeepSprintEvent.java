package wtf.silhouette.shaeware.events.impl.player;

import wtf.silhouette.shaeware.events.api.CancellableEvent;

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
