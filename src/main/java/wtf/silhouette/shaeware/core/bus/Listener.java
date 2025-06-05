package wtf.silhouette.shaeware.core.bus;

public interface Listener<T extends Event> {
  void call(T event);
}
