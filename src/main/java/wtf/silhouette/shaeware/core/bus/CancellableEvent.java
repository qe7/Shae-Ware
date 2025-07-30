package wtf.silhouette.shaeware.core.bus;

public interface CancellableEvent extends Event {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}