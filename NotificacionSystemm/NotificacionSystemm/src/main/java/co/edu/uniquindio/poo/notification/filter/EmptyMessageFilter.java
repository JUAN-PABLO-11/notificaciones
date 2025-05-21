package co.edu.uniquindio.poo.notification.filter;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Filtro concreto que verifica que el mensaje no esté vacío.
 */
public class EmptyMessageFilter extends NotificationFilter {
    
    @Override
    protected boolean doFilter(Notification notification) {
        String message = notification.getMessage();
        boolean valid = message != null && !message.trim().isEmpty();
        
        if (!valid) {
            System.out.println("Filtro: El mensaje está vacío");
        }
        
        return valid;
    }
}
