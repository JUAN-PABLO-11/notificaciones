package co.edu.uniquindio.poo.notification.filter;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Filtro concreto que verifica que el usuario no esté bloqueado.
 */
public class BlockedUserFilter extends NotificationFilter {
    
    @Override
    protected boolean doFilter(Notification notification) {
        boolean valid = !notification.getUser().isBlocked();
        
        if (!valid) {
            System.out.println("Filtro: El usuario está bloqueado");
        }
        
        return valid;
    }
}
