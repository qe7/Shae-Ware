package wtf.silhouette.shaeware.events.api;

public interface CancellableEvent extends Event {

  boolean isCancelled();

  void setCancelled(boolean cancelled);
}