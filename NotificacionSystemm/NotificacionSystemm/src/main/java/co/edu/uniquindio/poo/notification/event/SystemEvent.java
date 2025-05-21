package co.edu.uniquindio.poo.notification.event;

/**
 * Enumeración que define los tipos de eventos del sistema que pueden generar notificaciones automáticas.
 */
public enum SystemEvent {
    // Eventos de autenticación
    USER_LOGIN("Usuario ha iniciado sesión"),
    USER_LOGOUT("Usuario ha cerrado sesión"),
    LOGIN_FAILED("Intento fallido de inicio de sesión"),
    
    // Eventos de gestión de usuarios
    USER_REGISTERED("Usuario registrado"),
    PASSWORD_CHANGED("Contraseña actualizada"),
    ROLE_CHANGED("Rol de usuario actualizado"),
    
    // Eventos de notificaciones
    NOTIFICATION_SENT("Notificación enviada"),
    
    // Eventos de sistema
    SYSTEM_MAINTENANCE("Mantenimiento del sistema"),
    SYSTEM_UPDATE("Actualización del sistema");
    
    private final String description;
    
    SystemEvent(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
