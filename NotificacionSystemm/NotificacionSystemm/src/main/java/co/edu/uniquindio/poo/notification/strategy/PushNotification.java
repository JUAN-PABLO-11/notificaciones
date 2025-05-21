package co.edu.uniquindio.poo.notification.strategy;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Implementación concreta del patrón Strategy para enviar notificaciones push.
 */
public class PushNotification implements NotificationStrategy {
    
    @Override
    public String send(Notification notification) {
        String formattedMessage = notification.getUser().formatMessage(notification.getMessage());
        String deviceId = notification.getUser().getEmail(); // Usamos email como identificador del dispositivo
        
        // Simulación de envío de notificación push
        return String.format("Notificación Push enviada al dispositivo de %s con mensaje: %s", deviceId, formattedMessage);
    }
}
