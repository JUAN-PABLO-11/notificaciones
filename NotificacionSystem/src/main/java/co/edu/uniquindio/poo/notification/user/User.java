package co.edu.uniquindio.poo.notification.user;

import co.edu.uniquindio.poo.notification.strategy.NotificationStrategy;

/**
 * Clase abstracta que implementa el patrón Template Method para formatear mensajes
 * según el tipo de usuario.
 */
public abstract class User {
    private String email;
    private String phoneNumber;
    private NotificationStrategy preferredChannel;
    private boolean blocked;
    
    public User(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.blocked = false;
    }
    
    /**
     * Método plantilla que define el esqueleto del algoritmo para formatear mensajes.
     * Las subclases pueden personalizar partes específicas.
     */
    public final String formatMessage(String message) {
        StringBuilder formattedMessage = new StringBuilder();
        
        // Paso 1: Agregar encabezado específico del tipo de usuario
        formattedMessage.append(getHeader());
        
        // Paso 2: Agregar el contenido del mensaje (puede ser personalizado)
        formattedMessage.append(formatContent(message));
        
        // Paso 3: Agregar pie de página específico del tipo de usuario
        formattedMessage.append(getFooter());
        
        return formattedMessage.toString();
    }
    
    // Métodos que pueden ser sobrescritos por las subclases
    protected abstract String getHeader();
    
    protected String formatContent(String message) {
        return message; // Implementación por defecto
    }
    
    protected abstract String getFooter();
    
    // Getters y setters
    public String getEmail() {
        return email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public NotificationStrategy getPreferredChannel() {
        return preferredChannel;
    }
    
    public void setPreferredChannel(NotificationStrategy preferredChannel) {
        this.preferredChannel = preferredChannel;
    }
    
    public boolean isBlocked() {
        return blocked;
    }
    
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
