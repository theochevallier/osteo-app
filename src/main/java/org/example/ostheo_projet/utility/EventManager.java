package org.example.ostheo_projet.utility;

import org.example.ostheo_projet.Interface.EntityObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {

    private static EventManager instance;
    private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    private EventManager() {}

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());

        if (eventListeners == null) {
            return;
        }

        eventListeners.forEach(listener -> ((Consumer<T>) listener).accept(event));
    }
}