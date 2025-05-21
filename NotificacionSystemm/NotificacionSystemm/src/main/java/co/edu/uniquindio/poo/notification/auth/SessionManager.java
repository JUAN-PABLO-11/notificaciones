package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.user.User;
import co.edu.uniquindio.poo.notification.observer.EventManager;
import co.edu.uniquindio.poo.notification.user.GuestUser;
import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;

/**
 * Implementación del patrón Singleton para gestionar la sesión del usuario.
 * Mantiene el estado de autenticación y el usuario actual.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private boolean authenticated;
    private boolean isGuest;
    private EventManager eventManager;
    
    private SessionManager() {
        this.authenticated = false;
        this.isGuest = false;
    }
    
    /**
     * Obtiene la única instancia de SessionManager (Singleton).
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Establece el EventManager para notificar eventos de autenticación.
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas.
     * 
     * @param username Nombre de usuario o email
     * @param password Contraseña
     * @param authenticator Estrategia de autenticación a utilizar
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    public boolean login(String username, String password, Authenticator authenticator) {
        AuthenticationResult result = authenticator.authenticate(username, password);
        
        if (result.isSuccess()) {
            this.currentUser = result.getUser();
            this.authenticated = true;
            this.isGuest = false;
            
            if (eventManager != null) {
                // Notificar evento tipado
                SystemEventData eventData = new SystemEventData(SystemEvent.USER_LOGIN);
                eventData.addData("email", username);
                eventManager.notify(eventData);
                
                // Notificar evento tradicional
                eventManager.notify("AUTHENTICATION", "Usuario " + username + " ha iniciado sesión");
            }
            
            return true;
        } else {
            if (eventManager != null) {
                // Notificar evento tipado
                SystemEventData eventData = new SystemEventData(SystemEvent.LOGIN_FAILED);
                eventData.addData("email", username);
                eventData.addData("reason", result.getMessage());
                eventManager.notify(eventData);
                
                // Notificar evento tradicional
                eventManager.notify("AUTHENTICATION", "Intento fallido de inicio de sesión para " + username + ": " + result.getMessage());
            }
            
            return false;
        }
    }
    
    /**
     * Establece un usuario invitado como usuario actual.
     */
    public void setGuestUser(GuestUser guestUser) {
        this.currentUser = guestUser;
        this.authenticated = true;
        this.isGuest = true;
        
        if (eventManager != null) {
            // Notificar evento tipado
            SystemEventData eventData = new SystemEventData(SystemEvent.USER_LOGIN);
            eventData.addData("email", guestUser.getEmail());
            eventData.addData("type", "guest");
            eventManager.notify(eventData);
            
            // Notificar evento tradicional
            eventManager.notify("AUTHENTICATION", "Usuario invitado ha accedido al sistema");
        }
    }
    
    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        if (authenticated && eventManager != null) {
            // Notificar evento tipado
            SystemEventData eventData = new SystemEventData(SystemEvent.USER_LOGOUT);
            if (currentUser != null) {
                eventData.addData("email", currentUser.getEmail());
            }
            if (isGuest) {
                eventData.addData("type", "guest");
            }
            eventManager.notify(eventData);
            
            // Notificar evento tradicional
            if (isGuest) {
                eventManager.notify("AUTHENTICATION", "Usuario invitado ha cerrado sesión");
            } else {
                eventManager.notify("AUTHENTICATION", "Usuario " + (currentUser != null ? currentUser.getEmail() : "desconocido") + " ha cerrado sesión");
            }
        }
        
        this.currentUser = null;
        this.authenticated = false;
        this.isGuest = false;
    }
    
    /**
     * Verifica si hay un usuario autenticado.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    /**
     * Verifica si el usuario actual es un invitado.
     */
    public boolean isGuest() {
        return isGuest;
    }
    
    /**
     * Obtiene el usuario actual.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
