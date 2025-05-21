package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.user.User;

/**
 * Clase que encapsula el resultado de un intento de autenticación.
 */
public class AuthenticationResult {
    private boolean success;
    private String message;
    private User user;
    
    /**
     * Constructor para un resultado exitoso.
     */
    public static AuthenticationResult success(User user) {
        AuthenticationResult result = new AuthenticationResult();
        result.success = true;
        result.message = "Autenticación exitosa";
        result.user = user;
        return result;
    }
    
    /**
     * Constructor para un resultado fallido.
     */
    public static AuthenticationResult failure(String message) {
        AuthenticationResult result = new AuthenticationResult();
        result.success = false;
        result.message = message;
        return result;
    }
    
    /**
     * Verifica si la autenticación fue exitosa.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Obtiene el mensaje asociado al resultado.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Obtiene el usuario autenticado (si la autenticación fue exitosa).
     */
    public User getUser() {
        return user;
    }
}
