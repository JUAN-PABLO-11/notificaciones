package co.edu.uniquindio.poo.notification.observer;

import java.util.function.Consumer;

/**
 * Observador concreto que audita eventos.
 */
public class Auditor implements EventObserver {
    private Consumer<String> logConsumer;
    
    public Auditor(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }
    
    @Override
    public void update(String message) {
        logConsumer.accept("[AUDIT] " + message);
    }
}
