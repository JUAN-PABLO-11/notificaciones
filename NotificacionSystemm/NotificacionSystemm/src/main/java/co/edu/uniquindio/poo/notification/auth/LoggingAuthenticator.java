package co.edu.uniquindio.poo.notification.auth;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Decorador que añade registro de intentos de autenticación.
 */
public class LoggingAuthenticator extends AuthenticationDecorator {
    private static final Logger LOGGER = Logger.getLogger(LoggingAuthenticator.class.getName());
    
    public LoggingAuthenticator(Authenticator authenticator) {
        super(authenticator);
    }
    
    @Override
    public AuthenticationResult authenticate(String username, String password) {
        LOGGER.log(Level.INFO, "Intento de autenticación para el usuario: " + username);
        
        AuthenticationResult result = wrappee.authenticate(username, password);
        
        if (result.isSuccess()) {
            LOGGER.log(Level.INFO, "Autenticación exitosa para el usuario: " + username);
        } else {
            LOGGER.log(Level.WARNING, "Autenticación fallida para el usuario: " + username + ". Motivo: " + result.getMessage());
        }
        
        return result;
    }
}
