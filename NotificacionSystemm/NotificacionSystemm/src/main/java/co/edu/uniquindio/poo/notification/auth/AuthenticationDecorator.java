package co.edu.uniquindio.poo.notification.auth;

/**
 * Implementación del patrón Decorator para añadir funcionalidades adicionales
 * a los autenticadores existentes.
 */
public abstract class AuthenticationDecorator implements Authenticator {
    protected Authenticator wrappee;
    
    public AuthenticationDecorator(Authenticator authenticator) {
        this.wrappee = authenticator;
    }
}
