package co.edu.uniquindio.poo.notification.observer;

import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;
import co.edu.uniquindio.poo.notification.service.NotificationService;
import co.edu.uniquindio.poo.notification.user.User;

/**
 * Observador que envía notificaciones automáticas cuando ocurren ciertos eventos.
 */
public class AutoNotifier implements SystemEventObserver {
    private NotificationService notificationService;
    
    public AutoNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @Override
    public void onEvent(SystemEventData eventData) {
        switch (eventData.getEventType()) {
            case USER_LOGIN:
                handleUserLogin(eventData);
                break;
            case USER_LOGOUT:
                handleUserLogout(eventData);
                break;
            case LOGIN_FAILED:
                handleLoginFailed(eventData);
                break;
            case USER_REGISTERED:
                handleUserRegistered(eventData);
                break;
            case PASSWORD_CHANGED:
                handlePasswordChanged(eventData);
                break;
            case ROLE_CHANGED:
                handleRoleChanged(eventData);
                break;
            case SYSTEM_MAINTENANCE:
                handleSystemMaintenance(eventData);
                break;
            case SYSTEM_UPDATE:
                handleSystemUpdate(eventData);
                break;
            default:
                // No hacer nada para otros tipos de eventos
                break;
        }
    }
    
    private void handleUserLogin(SystemEventData eventData) {
        if (eventData.hasData("email")) {
            String email = eventData.getData("email");
            User user = notificationService.findUserByEmail(email);
            
            if (user != null) {
                notificationService.sendSystemNotification(
                    user, 
                    "Bienvenido de nuevo al sistema. Has iniciado sesión correctamente."
                );
            }
        }
    }
    
    private void handleUserLogout(SystemEventData eventData) {
        // No enviamos notificación al cerrar sesión, ya que el usuario ya no está en el sistema
    }
    
    private void handleLoginFailed(SystemEventData eventData) {
        if (eventData.hasData("email")) {
            String email = eventData.getData("email");
            User user = notificationService.findUserByEmail(email);
            
            if (user != null) {
                notificationService.sendSystemNotification(
                    user, 
                    "Detectamos un intento fallido de inicio de sesión en tu cuenta. " +
                    "Si no fuiste tú, por favor contacta al administrador."
                );
            }
        }
    }
    
    private void handleUserRegistered(SystemEventData eventData) {
        if (eventData.hasData("email")) {
            String email = eventData.getData("email");
            User user = notificationService.findUserByEmail(email);
            
            if (user != null) {
                notificationService.sendSystemNotification(
                    user, 
                    "¡Bienvenido al Sistema de Notificaciones! Tu cuenta ha sido creada correctamente."
                );
            }
            
            // Notificar a los administradores
            notificationService.sendNotificationToAdmins(
                "Nuevo usuario registrado: " + email
            );
        }
    }
    
    private void handlePasswordChanged(SystemEventData eventData) {
        if (eventData.hasData("email")) {
            String email = eventData.getData("email");
            User user = notificationService.findUserByEmail(email);
            
            if (user != null) {
                notificationService.sendSystemNotification(
                    user, 
                    "Tu contraseña ha sido actualizada correctamente. Si no realizaste este cambio, " +
                    "por favor contacta al administrador inmediatamente."
                );
            }
        }
    }
    
    private void handleRoleChanged(SystemEventData eventData) {
        if (eventData.hasData("email") && eventData.hasData("newRole")) {
            String email = eventData.getData("email");
            String newRole = eventData.getData("newRole");
            User user = notificationService.findUserByEmail(email);
            
            if (user != null) {
                notificationService.sendSystemNotification(
                    user, 
                    "Tu rol en el sistema ha sido actualizado a " + newRole + "."
                );
            }
        }
    }
    
    private void handleSystemMaintenance(SystemEventData eventData) {
        if (eventData.hasData("date") && eventData.hasData("duration")) {
            String date = eventData.getData("date");
            String duration = eventData.getData("duration");
            
            notificationService.sendNotificationToAllUsers(
                "El sistema estará en mantenimiento el " + date + " durante " + duration + ". " +
                "Disculpe las molestias."
            );
        }
    }
    
    private void handleSystemUpdate(SystemEventData eventData) {
        if (eventData.hasData("version") && eventData.hasData("features")) {
            String version = eventData.getData("version");
            String features = eventData.getData("features");
            
            notificationService.sendNotificationToAllUsers(
                "El sistema ha sido actualizado a la versión " + version + ". " +
                "Nuevas características: " + features
            );
        }
    }
}
