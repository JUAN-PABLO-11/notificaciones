package co.edu.uniquindio.poo.notification.user;

/**
 * Implementación concreta para usuarios clientes.
 */
public class ClientUser extends User {
    
    public ClientUser(String email, String phoneNumber) {
        super(email, phoneNumber);
    }
    
    @Override
    protected String getHeader() {
        return "[Notificación para Cliente] ";
    }
    
    @Override
    protected String formatContent(String message) {
        // Los clientes ven el mensaje normal
        return message;
    }
    
    @Override
    protected String getFooter() {
        return " [Gracias por usar nuestros servicios]";
    }
}
