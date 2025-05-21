package co.edu.uniquindio.poo.notification.observer;

import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del patrón Observer que maneja eventos y notifica a los observadores.
 */
public class EventManager {
    private Map<String, List<EventObserver>> listeners = new HashMap<>();
    private Map<SystemEvent, List<SystemEventObserver>> typedListeners = new HashMap<>();
    
    public EventManager(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
        
        // Inicializar listeners para todos los tipos de eventos
        for (SystemEvent eventType : SystemEvent.values()) {
            this.typedListeners.put(eventType, new ArrayList<>());
        }
    }
    
    public void subscribe(String eventType, EventObserver listener) {
        List<EventObserver> users = listeners.get(eventType);
        if (users != null) {
            users.add(listener);
        }
    }
    
    public void unsubscribe(String eventType, EventObserver listener) {
        List<EventObserver> users = listeners.get(eventType);
        if (users != null) {
            users.remove(listener);
        }
    }
    
    public void notify(String eventType, String message) {
        List<EventObserver> users = listeners.get(eventType);
        if (users != null) {
            for (EventObserver listener : users) {
                listener.update(message);
            }
        }
    }
    
    /**
     * Suscribe un observador a un tipo específico de evento del sistema.
     */
    public void subscribe(SystemEvent eventType, SystemEventObserver listener) {
        List<SystemEventObserver> observers = typedListeners.get(eventType);
        if (observers != null) {
            observers.add(listener);
        }
    }
    
    /**
     * Cancela la suscripción de un observador a un tipo específico de evento del sistema.
     */
    public void unsubscribe(SystemEvent eventType, SystemEventObserver listener) {
        List<SystemEventObserver> observers = typedListeners.get(eventType);
        if (observers != null) {
            observers.remove(listener);
        }
    }
    
    /**
     * Notifica a todos los observadores suscritos a un tipo específico de evento del sistema.
     */
    public void notify(SystemEventData eventData) {
        // Notificar a los observadores tipados
        List<SystemEventObserver> observers = typedListeners.get(eventData.getEventType());
        if (observers != null) {
            for (SystemEventObserver listener : observers) {
                listener.onEvent(eventData);
            }
        }
        
        // También notificar a los observadores tradicionales
        String category;
        switch (eventData.getEventType()) {
            case USER_LOGIN:
            case USER_LOGOUT:
            case LOGIN_FAILED:
                category = "AUTHENTICATION";
                break;
            case USER_REGISTERED:
            case PASSWORD_CHANGED:
            case ROLE_CHANGED:
                category = "USER_MANAGEMENT";
                break;
            case NOTIFICATION_SENT:
                category = "NOTIFICATION";
                break;
            default:
                category = "SYSTEM";
        }
        
        notify(category, eventData.toString());
    }
}
