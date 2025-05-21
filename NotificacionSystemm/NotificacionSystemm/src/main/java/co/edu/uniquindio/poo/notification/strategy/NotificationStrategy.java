package co.edu.uniquindio.poo.notification.strategy;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Interfaz para el patrón Strategy que define cómo se envían las notificaciones
 * por diferentes canales.
 */
public interface NotificationStrategy {
    /**
     * Envía una notificación por un canal específico.
     * 
     * @param notification La notificación a enviar
     * @return Un mensaje que describe el resultado del envío
     */
    String send(Notification notification);
}
