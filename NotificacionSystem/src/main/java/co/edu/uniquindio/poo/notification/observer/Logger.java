package co.edu.uniquindio.poo.notification.observer;

import java.util.function.Consumer;

/**
 * Observador concreto que registra eventos en un log.
 */
public class Logger implements EventObserver {
    private Consumer<String> logConsumer;
    
    public Logger(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }
    
    @Override
    public void update(String message) {
        logConsumer.accept("[LOG] " + message);
    }
}
