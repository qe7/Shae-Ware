package wtf.silhouette.shaeware.events.api;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {

  private final Map<Class<? extends Event>, List<ListenerWrapper<? extends Event>>> listenerCache = new ConcurrentHashMap<>();
  private final Map<Handler, List<ListenerWrapper<? extends Event>>> handlerListeners = new ConcurrentHashMap<>();
  private final Map<Class<?>, List<ListenerWrapper<? extends Event>>> handlerRegistrationCache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public <T extends Event> void publishEvent(@NotNull T event) {
    var listeners = listenerCache.get(event.getClass());

    if (listeners != null) {
      for (var wrapper : listeners) {
        if (wrapper.handler().listening()) {
          ((Listener<T>) wrapper.listener()).call(event);
        }
      }
    }
  }

  public void registerHandler(@NotNull Handler handler) {
    var handlerClass = handler.getClass();

    var cachedListeners = handlerRegistrationCache.get(handlerClass);
    if (cachedListeners != null) {
      cachedListeners.forEach(wrapper -> {
        handlerListeners.computeIfAbsent(handler, k -> new CopyOnWriteArrayList<>()).add(wrapper);
        listenerCache.computeIfAbsent(wrapper.eventClass(), k -> new CopyOnWriteArrayList<>()).add(wrapper);
      });
      return;
    }

    List<ListenerWrapper<? extends Event>> newWrappers = new ArrayList<>();

    try {
      for (var field : handlerClass.getDeclaredFields()) {
        if (!field.isAnnotationPresent(SubscribeEvent.class)) continue;

        if (!field.trySetAccessible()) {
          throw new RuntimeException("Couldn't set field accessible: " + handlerClass.getName() + "#" + field.getName());
        }

        var listener = (Listener<? extends Event>) field.get(handler);

        @SuppressWarnings("unchecked") var eventClass = (Class<? extends Event>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

        var wrapper = new ListenerWrapper<>(handler, listener, eventClass);
        newWrappers.add(wrapper);

        handlerListeners.computeIfAbsent(handler, k -> new CopyOnWriteArrayList<>()).add(wrapper);
        listenerCache.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(wrapper);
      }

      if (!newWrappers.isEmpty()) {
        handlerRegistrationCache.put(handlerClass, newWrappers);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to register handler: " + handlerClass.getName(), e);
    }
  }

  private record ListenerWrapper<T extends Event>(Handler handler, Listener<T> listener,
                                                  Class<? extends Event> eventClass) {
  }
}
