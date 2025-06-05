package wtf.silhouette.shaeware.core.feature.component;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ComponentFactory {

  private static final Map<Class<?>, Component> COMPONENT = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static <T extends Component> @NotNull T get(Class<T> clazz) {
    T component = (T) COMPONENT.get(clazz);
    if (component == null) {
      throw new IllegalStateException("Component not registered: " + clazz.getSimpleName());
    }
    return component;
  }

  public static <T extends Component> void register(Class<T> clazz, T component) {
    if (COMPONENT.containsKey(clazz)) {
      throw new IllegalStateException("Component already registered: " + clazz.getSimpleName());
    }
    COMPONENT.put(clazz, component);
  }

  public static boolean isRegistered(Class<?> clazz) {
    return COMPONENT.containsKey(clazz);
  }
}
