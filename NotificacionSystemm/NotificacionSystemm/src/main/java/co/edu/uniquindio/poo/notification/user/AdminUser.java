package co.edu.uniquindio.poo.notification.user;

/**
 * Implementación concreta para usuarios administradores.
 */
public class AdminUser extends User {
    
    public AdminUser(String email, String phoneNumber) {
        super(email, phoneNumber);
    }
    
    @Override
    protected String getHeader() {
        return "[ADMIN NOTIFICATION] ";
    }
    
    @Override
    protected String formatContent(String message) {
        // Los administradores ven el mensaje completo y en mayúsculas
        return message.toUpperCase();
    }
    
    @Override
    protected String getFooter() {
        return " [Este mensaje contiene información privilegiada]";
    }
}
