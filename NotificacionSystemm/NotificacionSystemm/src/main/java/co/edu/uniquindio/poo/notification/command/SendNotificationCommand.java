package co.edu.uniquindio.poo.notification.command;

import co.edu.uniquindio.poo.notification.model.Notification;
import co.edu.uniquindio.poo.notification.observer.EventManager;

/**
 * Comando concreto que encapsula la acción de enviar una notificación.
 */
public class SendNotificationCommand implements NotificationCommand {
    private Notification notification;
    private EventManager eventManager;
    
    public SendNotificationCommand(Notification notification, EventManager eventManager) {
        this.notification = notification;
        this.eventManager = eventManager;
    }
    
    @Override
    public void execute() {
        String result = notification.send();
        eventManager.notify("NOTIFICATION", result);
    }
}
