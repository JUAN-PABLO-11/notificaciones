package co.edu.uniquindio.poo.notification.observer;

/**
 * Interfaz para el patrón Observer que define cómo los observadores reciben actualizaciones.
 */
public interface EventObserver {
    void update(String message);
}
