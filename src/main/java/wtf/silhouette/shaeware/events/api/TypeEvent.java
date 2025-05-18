package wtf.silhouette.shaeware.events.api;

public interface TypeEvent extends Event {

  Type type();

  default boolean isPre() {
    return type() == Type.PRE;
  }

  default boolean isPost() {
    return type() == Type.POST;
  }

  enum Type {
    PRE, POST
  }
}