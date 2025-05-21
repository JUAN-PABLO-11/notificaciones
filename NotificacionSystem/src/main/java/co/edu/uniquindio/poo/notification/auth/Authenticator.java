package co.edu.uniquindio.poo.notification.auth;

/**
 * Interfaz para el patrón Strategy que define diferentes métodos de autenticación.
 */
public interface Authenticator {
    /**
     * Autentica un usuario con las credenciales proporcionadas.
     * 
     * @param username Nombre de usuario o email
     * @param password Contraseña
     * @return Resultado de la autenticación
     */
    AuthenticationResult authenticate(String username, String password);
}
