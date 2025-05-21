package co.edu.uniquindio.poo.notification.strategy;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Implementación concreta del patrón Strategy para enviar notificaciones por email.
 */
public class EmailNotification implements NotificationStrategy {
    
    @Override
    public String send(Notification notification) {
        String formattedMessage = notification.getUser().formatMessage(notification.getMessage());
        String email = notification.getUser().getEmail();
        
        // Simulación de envío de email
        return String.format("Email enviado a %s con mensaje: %s", email, formattedMessage);
    }
}
