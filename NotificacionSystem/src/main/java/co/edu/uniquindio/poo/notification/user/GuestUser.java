package co.edu.uniquindio.poo.notification.user;

/**
 * Implementación concreta para usuarios invitados.
 */
public class GuestUser extends User {
    
    public GuestUser(String email, String phoneNumber) {
        super(email, phoneNumber);
    }
    
    @Override
    protected String getHeader() {
        return "[Mensaje Informativo] ";
    }
    
    @Override
    protected String formatContent(String message) {
        // Los invitados ven una versión resumida del mensaje (primeros 50 caracteres)
        if (message.length() > 50) {
            return message.substring(0, 50) + "...";
        }
        return message;
    }
    
    @Override
    protected String getFooter() {
        return " [Regístrese para ver mensajes completos]";
    }
}
