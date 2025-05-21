package co.edu.uniquindio.poo.notification.filter;

import co.edu.uniquindio.poo.notification.model.Notification;

/**
 * Clase abstracta para el patrón Chain of Responsibility que define
 * la cadena de filtros para validar notificaciones.
 */
public abstract class NotificationFilter {
    private NotificationFilter next;
    
    public NotificationFilter setNext(NotificationFilter next) {
        this.next = next;
        return next;
    }
    
    /**
     * Ejecuta el filtro actual y pasa la notificación al siguiente filtro si existe.
     * 
     * @param notification La notificación a filtrar
     * @return true si la notificación pasa todos los filtros, false en caso contrario
     */
    public boolean filter(Notification notification) {
        boolean result = doFilter(notification);
        
        if (result && next != null) {
            return next.filter(notification);
        }
        
        return result;
    }
    
    /**
     * Método que implementa la lógica específica de cada filtro.
     * 
     * @param notification La notificación a filtrar
     * @return true si la notificación pasa el filtro, false en caso contrario
     */
    protected abstract boolean doFilter(Notification notification);
}
