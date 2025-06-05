package wtf.silhouette.shaeware.core.manager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ManagerFactory {

  private static final Map<Class<?>, Manager<?, ?>> MANAGERS = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static <T extends Manager<?, ?>> @NotNull T get(Class<T> clazz) {
    T manager = (T) MANAGERS.get(clazz);
    if (manager == null) {
      throw new IllegalStateException("Manager not registered: " + clazz.getSimpleName());
    }
    return manager;
  }

  public static <T extends Manager<?, ?>> void register(Class<T> clazz, T manager) {
    if (MANAGERS.containsKey(clazz)) {
      throw new IllegalStateException("Manager already registered: " + clazz.getSimpleName());
    }
    MANAGERS.put(clazz, manager);
  }

  public static boolean isRegistered(Class<?> clazz) {
    return MANAGERS.containsKey(clazz);
  }
}
