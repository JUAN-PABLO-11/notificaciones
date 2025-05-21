package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.user.User;
import co.edu.uniquindio.poo.notification.user.AdminUser;
import co.edu.uniquindio.poo.notification.user.ClientUser;
import co.edu.uniquindio.poo.notification.user.GuestUser;
import co.edu.uniquindio.poo.notification.auth.persistence.UserRepository;

/**
 * Implementación básica del autenticador que utiliza un repositorio de usuarios.
 */
public class BasicAuthenticator implements Authenticator {
    private UserRepository userRepository;
    
    public BasicAuthenticator() {
        this.userRepository = new UserRepository();
    }
    
    /**
     * Agrega un nuevo usuario al sistema.
     */
    public void addUser(String email, String password, UserRole role) {
        userRepository.addUser(email, password, role);
    }
    
    @Override
    public AuthenticationResult authenticate(String username, String password) {
        // Verificar si el usuario existe y las credenciales son correctas
        if (!userRepository.verifyCredentials(username, password)) {
            return AuthenticationResult.failure("Credenciales incorrectas");
        }
        
        // Obtener el rol del usuario
        UserCredentials credentials = userRepository.getUserCredentials(username);
        
        // Crear el usuario correspondiente según el rol
        User user;
        switch (credentials.getRole()) {
            case ADMIN:
                user = new AdminUser(username, "+1234567890");
                break;
            case GUEST:
                user = new GuestUser(username, "+1234567890");
                break;
            case CLIENT:
            default:
                user = new ClientUser(username, "+1234567890");
                break;
        }
        
        return AuthenticationResult.success(user);
    }
    
    /**
     * Obtiene el repositorio de usuarios.
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }
}
