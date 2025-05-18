package wtf.silhouette.shaeware.events.api;

public interface Handler {
  default boolean listening() {
    return true;
  }
}
