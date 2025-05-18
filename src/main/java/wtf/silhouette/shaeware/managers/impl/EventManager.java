package wtf.silhouette.shaeware.managers.impl;

import org.jetbrains.annotations.NotNull;
import wtf.silhouette.shaeware.events.api.Event;
import wtf.silhouette.shaeware.events.api.Handler;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.managers.api.Manager;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager extends Manager<Class<? extends Handler>, Handler> {

  private final Map<Class<? extends Event>, CopyOnWriteArrayList<ListenerWrapper>> listenerCache = new ConcurrentHashMap<>();
  private final Map<Handler, CopyOnWriteArrayList<ListenerWrapper>> handlerListeners = new ConcurrentHashMap<>();
  private final Map<Class<?>, List<ListenerWrapper>> handlerRegistrationCache = new ConcurrentHashMap<>();

  public <T extends Event> void publishEvent(@NotNull T event) {
    var eventClass = event.getClass();
    var listeners = listenerCache.get(eventClass);
    if (listeners != null) {
      for (var wrapper : listeners) {
        if (wrapper.handler.listening()) {
          @SuppressWarnings("unchecked") var listener = (Listener<T>) wrapper.listener;
          listener.call(event);
        }
      }
    }
  }

  @Override
  public void add(Class<? extends Handler> key, Handler handler) {
    super.add(key, handler);
    registerHandler(handler);
  }

  @Override
  public void remove(Class<? extends Handler> key) {
    var handler = get(key);
    if (handler != null) unregisterHandler(handler);
    super.remove(key);
  }

  public void registerHandler(@NotNull Handler handler) {
    var handlerClass = handler.getClass();
    var cached = handlerRegistrationCache.get(handlerClass);
    if (cached != null) {
      cached.forEach(wrapper -> {
        listenerCache.computeIfAbsent(wrapper.eventClass, k -> new CopyOnWriteArrayList<>()).add(wrapper);
        handlerListeners.computeIfAbsent(handler, k -> new CopyOnWriteArrayList<>()).add(wrapper);
      });
      return;
    }

    var discovered = new CopyOnWriteArrayList<ListenerWrapper>();
    for (var field : handlerClass.getDeclaredFields()) {
      if (!field.isAnnotationPresent(SubscribeEvent.class)) continue;
      field.setAccessible(true);
      try {
        var listener = (Listener<? extends Event>) field.get(handler);
        var type = (ParameterizedType) field.getGenericType();
        @SuppressWarnings("unchecked") var eventClass = (Class<? extends Event>) type.getActualTypeArguments()[0];

        var wrapper = new ListenerWrapper(handler, listener, eventClass);
        discovered.add(wrapper);

        listenerCache.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(wrapper);
        handlerListeners.computeIfAbsent(handler, k -> new CopyOnWriteArrayList<>()).add(wrapper);

      } catch (Exception e) {
        throw new RuntimeException("Failed to register listener " + field.getName(), e);
      }
    }
    if (!discovered.isEmpty()) {
      handlerRegistrationCache.put(handlerClass, discovered);
    }
  }

  public void unregisterHandler(Handler handler) {
    var wrappers = handlerListeners.remove(handler);
    if (wrappers == null) return;

    for (var wrapper : wrappers) {
      var eventClass = wrapper.eventClass;
      var current = listenerCache.get(eventClass);
      if (current != null) {
        current.remove(wrapper);
        if (current.isEmpty()) {
          listenerCache.remove(eventClass);
        }
      }
    }
  }

  private record ListenerWrapper(Handler handler, Listener<? extends Event> listener,
                                 Class<? extends Event> eventClass) {
  }
}
