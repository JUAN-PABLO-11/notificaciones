package co.edu.uniquindio.poo.notification.auth.persistence;

import co.edu.uniquindio.poo.notification.auth.UserCredentials;
import co.edu.uniquindio.poo.notification.auth.UserRole;
import co.edu.uniquindio.poo.notification.observer.EventManager;
import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Clase que maneja la persistencia de usuarios en un archivo JSON.
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private static final String DATA_FILE = "users.json";
    private static final String ADMIN_INVITATION_CODE = "ADMIN123"; // Código de invitación para administradores
    private Map<String, UserCredentials> users;
    private EventManager eventManager;
    
    public UserRepository() {
        this.users = new HashMap<>();
        loadUsers();
    }
    
    /**
     * Establece el EventManager para notificar eventos.
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    /**
     * Carga los usuarios desde el archivo JSON.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(DATA_FILE);
        
        // Si el archivo no existe, crear usuarios por defecto
        if (!file.exists()) {
            createDefaultUsers();
            saveUsers();
            return;
        }
        
        try {
            JSONParser parser = new JSONParser();
            JSONArray userArray = (JSONArray) parser.parse(new FileReader(DATA_FILE));
            
            for (Object obj : userArray) {
                JSONObject userJson = (JSONObject) obj;
                String email = (String) userJson.get("email");
                String hashedPassword = (String) userJson.get("password");
                UserRole role = UserRole.valueOf((String) userJson.get("role"));
                
                UserCredentials credentials = new UserCredentials(email, hashedPassword, role);
                credentials.setPasswordHashed(true);
                users.put(email, credentials);
            }
            
            LOGGER.log(Level.INFO, "Usuarios cargados desde el archivo: " + users.size());
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar usuarios desde el archivo", e);
            createDefaultUsers();
        }
    }
    
    /**
     * Guarda los usuarios en el archivo JSON.
     */
    @SuppressWarnings("unchecked")
    public void saveUsers() {
        JSONArray userArray = new JSONArray();
        
        for (Map.Entry<String, UserCredentials> entry : users.entrySet()) {
            UserCredentials credentials = entry.getValue();
            
            JSONObject userJson = new JSONObject();
            userJson.put("email", credentials.getEmail());
            
            // Asegurarse de que la contraseña esté hasheada
            String password = credentials.getPassword();
            if (!credentials.isPasswordHashed()) {
                password = hashPassword(password);
            }
            
            userJson.put("password", password);
            userJson.put("role", credentials.getRole().toString());
            
            userArray.add(userJson);
        }
        
        try (FileWriter file = new FileWriter(DATA_FILE)) {
            file.write(userArray.toJSONString());
            file.flush();
            LOGGER.log(Level.INFO, "Usuarios guardados en el archivo: " + users.size());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuarios en el archivo", e);
        }
    }
    
    /**
     * Crea usuarios por defecto si no existe el archivo.
     */
    private void createDefaultUsers() {
        addUser("admin@example.com", "admin123", UserRole.ADMIN);
        addUser("client@example.com", "client123", UserRole.CLIENT);
        addUser("guest@example.com", "guest123", UserRole.GUEST);
        LOGGER.log(Level.INFO, "Usuarios por defecto creados");
    }
    
    /**
     * Añade un nuevo usuario al repositorio.
     */
    public void addUser(String email, String password, UserRole role) {
        String hashedPassword = hashPassword(password);
        UserCredentials credentials = new UserCredentials(email, hashedPassword, role);
        credentials.setPasswordHashed(true);
        users.put(email, credentials);
        saveUsers();
        
        // Notificar evento
        if (eventManager != null) {
            SystemEventData eventData = new SystemEventData(SystemEvent.USER_REGISTERED);
            eventData.addData("email", email);
            eventData.addData("role", role.toString());
            eventManager.notify(eventData);
        }
    }
    
    /**
     * Verifica las credenciales de un usuario.
     */
    public boolean verifyCredentials(String email, String password) {
        UserCredentials credentials = users.get(email);
        
        if (credentials == null) {
            return false;
        }
        
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(credentials.getPassword());
    }
    
    /**
     * Obtiene las credenciales de un usuario.
     */
    public UserCredentials getUserCredentials(String email) {
        return users.get(email);
    }
    
    /**
     * Elimina un usuario del repositorio.
     */
    public void removeUser(String email) {
        users.remove(email);
        saveUsers();
        
        // Notificar evento
        if (eventManager != null) {
            SystemEventData eventData = new SystemEventData(SystemEvent.USER_REGISTERED);
            eventData.addData("email", email);
            eventData.addData("action", "removed");
            eventManager.notify(eventData);
        }
    }
    
    /**
     * Actualiza la contraseña de un usuario.
     */
    public void updatePassword(String email, String newPassword) {
        UserCredentials credentials = users.get(email);
        
        if (credentials != null) {
            String hashedPassword = hashPassword(newPassword);
            credentials.setPassword(hashedPassword);
            credentials.setPasswordHashed(true);
            saveUsers();
            
            // Notificar evento
            if (eventManager != null) {
                SystemEventData eventData = new SystemEventData(SystemEvent.PASSWORD_CHANGED);
                eventData.addData("email", email);
                eventManager.notify(eventData);
            }
        }
    }
    
    /**
     * Actualiza el rol de un usuario.
     */
    public void updateUserRole(String email, UserRole newRole) {
        UserCredentials credentials = users.get(email);
        
        if (credentials != null) {
            UserRole oldRole = credentials.getRole();
            UserCredentials updatedCredentials = new UserCredentials(
                email, credentials.getPassword(), newRole);
            updatedCredentials.setPasswordHashed(true);
            users.put(email, updatedCredentials);
            saveUsers();
            
            // Notificar evento
            if (eventManager != null) {
                SystemEventData eventData = new SystemEventData(SystemEvent.ROLE_CHANGED);
                eventData.addData("email", email);
                eventData.addData("oldRole", oldRole.toString());
                eventData.addData("newRole", newRole.toString());
                eventManager.notify(eventData);
            }
        }
    }
    
    /**
     * Verifica si un código de invitación para administrador es válido.
     */
    public boolean isValidAdminInvitationCode(String code) {
        return ADMIN_INVITATION_CODE.equals(code);
    }
    
    /**
     * Hashea una contraseña usando SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error al hashear contraseña", e);
            return password; // Fallback inseguro, solo para desarrollo
        }
    }
    
    /**
     * Obtiene todos los usuarios.
     */
    public Map<String, UserCredentials> getAllUsers() {
        return new HashMap<>(users);
    }
    
    /**
     * Notifica un evento de mantenimiento del sistema.
     */
    public void notifySystemMaintenance(String date, String duration) {
        if (eventManager != null) {
            SystemEventData eventData = new SystemEventData(SystemEvent.SYSTEM_MAINTENANCE);
            eventData.addData("date", date);
            eventData.addData("duration", duration);
            eventManager.notify(eventData);
        }
    }
    
    /**
     * Notifica un evento de actualización del sistema.
     */
    public void notifySystemUpdate(String version, String features) {
        if (eventManager != null) {
            SystemEventData eventData = new SystemEventData(SystemEvent.SYSTEM_UPDATE);
            eventData.addData("version", version);
            eventData.addData("features", features);
            eventManager.notify(eventData);
        }
    }
}
