package co.edu.uniquindio.poo.notification.service;

import co.edu.uniquindio.poo.notification.model.Notification;
import co.edu.uniquindio.poo.notification.user.User;
import co.edu.uniquindio.poo.notification.strategy.NotificationStrategy;
import co.edu.uniquindio.poo.notification.strategy.EmailNotification;
import co.edu.uniquindio.poo.notification.auth.persistence.UserRepository;
import co.edu.uniquindio.poo.notification.command.NotificationCommand;
import co.edu.uniquindio.poo.notification.command.SendNotificationCommand;
import co.edu.uniquindio.poo.notification.command.NotificationInvoker;
import co.edu.uniquindio.poo.notification.observer.EventManager;
import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;
import co.edu.uniquindio.poo.notification.user.AdminUser;
import co.edu.uniquindio.poo.notification.user.ClientUser;
import co.edu.uniquindio.poo.notification.user.GuestUser;
import co.edu.uniquindio.poo.notification.auth.UserRole;

/**
 * Servicio para enviar notificaciones del sistema.
 */
public class NotificationService {
    private UserRepository userRepository;
    private NotificationInvoker invoker;
    private EventManager eventManager;
    
    public NotificationService(UserRepository userRepository, NotificationInvoker invoker, EventManager eventManager) {
        this.userRepository = userRepository;
        this.invoker = invoker;
        this.eventManager = eventManager;
    }
    
    /**
     * Envía una notificación del sistema a un usuario.
     */
    public void sendSystemNotification(User user, String message) {
        // Asegurarse de que el usuario tenga un canal de notificación
        if (user.getPreferredChannel() == null) {
            user.setPreferredChannel(new EmailNotification());
        }
        
        // Crear la notificación
        Notification notification = new Notification(user, message);
        
        // Crear y ejecutar el comando
        NotificationCommand command = new SendNotificationCommand(notification, eventManager);
        invoker.addCommand(command);
        invoker.executeCommands();
        
        // Notificar el evento
        SystemEventData eventData = new SystemEventData(SystemEvent.NOTIFICATION_SENT);
        eventData.addData("recipient", user.getEmail());
        eventData.addData("message", message);
        eventManager.notify(eventData);
    }
    
    /**
     * Busca un usuario por su email.
     */
    public User findUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        
        // Buscar en el repositorio de usuarios
        co.edu.uniquindio.poo.notification.auth.UserCredentials credentials = userRepository.getUserCredentials(email);
        
        if (credentials == null) {
            return null;
        }
        
        // Crear el usuario según su rol
        switch (credentials.getRole()) {
            case ADMIN:
                return new AdminUser(email, "+1234567890");
            case CLIENT:
                return new ClientUser(email, "+1234567890");
            case GUEST:
                return new GuestUser(email, "+1234567890");
            default:
                return null;
        }
    }
    
    /**
     * Envía notificaciones a todos los usuarios con un rol específico.
     */
    public void sendNotificationToRole(UserRole role, String message) {
        for (co.edu.uniquindio.poo.notification.auth.UserCredentials credentials : userRepository.getAllUsers().values()) {
            if (credentials.getRole() == role) {
                User user = findUserByEmail(credentials.getEmail());
                if (user != null) {
                    sendSystemNotification(user, message);
                }
            }
        }
    }
    
    /**
     * Envía notificaciones a todos los administradores.
     */
    public void sendNotificationToAdmins(String message) {
        sendNotificationToRole(UserRole.ADMIN, message);
    }
    
    /**
     * Envía notificaciones a todos los usuarios.
     */
    public void sendNotificationToAllUsers(String message) {
        for (co.edu.uniquindio.poo.notification.auth.UserCredentials credentials : userRepository.getAllUsers().values()) {
            User user = findUserByEmail(credentials.getEmail());
            if (user != null) {
                sendSystemNotification(user, message);
            }
        }
    }
}
