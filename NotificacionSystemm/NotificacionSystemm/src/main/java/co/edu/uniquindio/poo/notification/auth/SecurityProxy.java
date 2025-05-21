package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.model.Notification;
import co.edu.uniquindio.poo.notification.user.User;
import co.edu.uniquindio.poo.notification.user.AdminUser;
import co.edu.uniquindio.poo.notification.user.GuestUser;

/**
 * Implementación del patrón Proxy para controlar el acceso a recursos protegidos.
 */
public class SecurityProxy {
    private SessionManager sessionManager;
    
    public SecurityProxy() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Verifica si el usuario actual tiene permiso para enviar una notificación.
     * 
     * @param notification La notificación que se intenta enviar
     * @return true si el usuario tiene permiso, false en caso contrario
     */
    public boolean canSendNotification(Notification notification) {
        if (!sessionManager.isAuthenticated()) {
            return false;
        }

        User currentUser = sessionManager.getCurrentUser();
        User targetUser = notification.getUser();

        if (currentUser.isBlocked()) {
            return false;
        }

        return true;
    }
    
    /**
     * Verifica si el usuario actual tiene permiso para ver los registros.
     * 
     * @return true si el usuario tiene permiso, false en caso contrario
     */
    public boolean canViewLogs() {
        if (!sessionManager.isAuthenticated()) {
            return false;
        }
        
        // Los invitados no pueden ver los registros
        if (sessionManager.isGuest()) {
            return false;
        }
        
        User currentUser = sessionManager.getCurrentUser();
        
        // Solo los administradores pueden ver los registros
        return currentUser instanceof AdminUser;
    }
    
    /**
     * Verifica si el usuario actual tiene permiso para administrar usuarios.
     * 
     * @return true si el usuario tiene permiso, false en caso contrario
     */
    public boolean canManageUsers() {
        if (!sessionManager.isAuthenticated() || sessionManager.isGuest()) {
            return false;
        }
        
        User currentUser = sessionManager.getCurrentUser();
        
        // Solo los administradores pueden administrar usuarios
        return currentUser instanceof AdminUser;
    }
}
