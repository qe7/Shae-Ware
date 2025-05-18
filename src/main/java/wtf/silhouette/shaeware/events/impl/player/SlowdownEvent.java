package wtf.silhouette.shaeware.events.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wtf.silhouette.shaeware.events.api.CancellableEvent;

@RequiredArgsConstructor
@Getter
public final class SlowdownEvent implements CancellableEvent {

  private final boolean shouldSlowDown;

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
