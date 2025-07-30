package wtf.silhouette.shaeware.core.bus;

public interface Handler {
    default boolean listening() {
        return true;
    }
}
