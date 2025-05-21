package co.edu.uniquindio.poo.notification.auth;

/**
 * Clase que almacena las credenciales de un usuario.
 */
public class UserCredentials {
    private String email;
    private String password;
    private UserRole role;
    private boolean passwordHashed;
    
    public UserCredentials(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.passwordHashed = false;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public boolean isPasswordHashed() {
        return passwordHashed;
    }
    
    public void setPasswordHashed(boolean passwordHashed) {
        this.passwordHashed = passwordHashed;
    }
}
