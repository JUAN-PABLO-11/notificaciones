package co.edu.uniquindio.poo.notification.strategy;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Implementación concreta del patrón Strategy para enviar notificaciones por SMS.
 */
public class SMSNotification implements NotificationStrategy {
    
    @Override
    public String send(Notification notification) {
        String formattedMessage = notification.getUser().formatMessage(notification.getMessage());
        String phoneNumber = notification.getUser().getPhoneNumber();
        
        // Simulación de envío de SMS
        return String.format("SMS enviado a %s con mensaje: %s", phoneNumber, formattedMessage);
    }
}
