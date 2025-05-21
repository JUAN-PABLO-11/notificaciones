package co.edu.uniquindio.poo.notification.model;

import co.edu.uniquindio.poo.notification.user.User;

/**
 * Clase que representa una notificación en el sistema.
 */
public class Notification {
    private User user;
    private String message;
    
    public Notification(User user, String message) {
        this.user = user;
        this.message = message;
    }
    
    public User getUser() {
        return user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String send() {
        if (user.getPreferredChannel() == null) {
            return "Error: Canal de notificación no especificado";
        }
        return user.getPreferredChannel().send(this);
    }
}
