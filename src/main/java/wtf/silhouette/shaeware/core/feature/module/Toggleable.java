package wtf.silhouette.shaeware.core.feature.module;

public interface Toggleable {

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void toggle();

    default void onEnable() {
    }

    default void onDisable() {
    }
}
