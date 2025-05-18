package wtf.silhouette.shaeware.events.api;

public interface Listener<T extends Event> {
  void call(T event);
}
