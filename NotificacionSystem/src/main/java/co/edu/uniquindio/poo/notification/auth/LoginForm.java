package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.auth.persistence.UserRepository;
import co.edu.uniquindio.poo.notification.user.GuestUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.function.Consumer;

/**
 * Formulario de inicio de sesión para la interfaz gráfica.
 */
public class LoginForm extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;
    private Button guestButton;
    private Text statusText;
    private Consumer<Boolean> onLoginResult;
    private BasicAuthenticator authenticator;
    
    public LoginForm(Consumer<Boolean> onLoginResult) {
        this.onLoginResult = onLoginResult;
        this.authenticator = new BasicAuthenticator();
        
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.CENTER);
        
        // Título
        Label titleLabel = new Label("Sistema de Notificaciones");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Formulario de login
        GridPane loginGrid = createLoginGrid();
        
        // Botones adicionales
        HBox additionalButtons = createAdditionalButtons();
        
        // Texto de estado
        statusText = new Text();
        statusText.setStyle("-fx-fill: red;");
        
        getChildren().addAll(titleLabel, loginGrid, additionalButtons, statusText);
    }
    
    private GridPane createLoginGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Campo de usuario
        Label usernameLabel = new Label("Email:");
        usernameField = new TextField();
        usernameField.setPromptText("Ingrese su email");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        
        // Campo de contraseña
        Label passwordLabel = new Label("Contraseña:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese su contraseña");
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        
        // Botón de login
        loginButton = new Button("Iniciar Sesión");
        loginButton.setPrefWidth(150);
        HBox loginButtonBox = new HBox(loginButton);
        loginButtonBox.setAlignment(Pos.CENTER);
        grid.add(loginButtonBox, 0, 2, 2, 1);
        
        // Configurar acción del botón
        loginButton.setOnAction(e -> handleLogin());
        
        return grid;
    }
    
    private HBox createAdditionalButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Botón de registro
        registerButton = new Button("Registrarse");
        registerButton.setPrefWidth(120);
        registerButton.setOnAction(e -> showRegistrationForm());
        
        // Botón de invitado
        guestButton = new Button("Continuar como Invitado");
        guestButton.setPrefWidth(180);
        guestButton.setOnAction(e -> loginAsGuest());
        
        buttonBox.getChildren().addAll(registerButton, guestButton);
        return buttonBox;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("Por favor, complete todos los campos");
            return;
        }
        
        // Crear autenticador con decorador de logging
        Authenticator authenticator = new LoggingAuthenticator(this.authenticator);
        
        // Intentar iniciar sesión
        boolean success = SessionManager.getInstance().login(username, password, authenticator);
        
        if (success) {
            statusText.setText("");
            clearFields();
            onLoginResult.accept(true);
        } else {
            statusText.setText("Credenciales incorrectas");
        }
    }
    
    private void showRegistrationForm() {
        // Crear una ventana de diálogo para el registro
        Dialog<UserCredentials> dialog = new Dialog<>();
        dialog.setTitle("Registro de Usuario");
        dialog.setHeaderText("Complete los campos para registrarse");
        
        // Botones
        ButtonType registerButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);
        
        // Crear el grid para el formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmar Contraseña");
        
        // Añadir campo para código de invitación de administrador
        TextField invitationCodeField = new TextField();
        invitationCodeField.setPromptText("Código de invitación (opcional)");
        CheckBox adminCheckBox = new CheckBox("Registrarse como Administrador");
        
        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Confirmar Contraseña:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(adminCheckBox, 0, 3, 2, 1);
        grid.add(new Label("Código de invitación:"), 0, 4);
        grid.add(invitationCodeField, 1, 4);
        
        // Mostrar/ocultar campo de código de invitación según checkbox
        invitationCodeField.setDisable(!adminCheckBox.isSelected());
        adminCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            invitationCodeField.setDisable(!newVal);
        });
        
        dialog.getDialogPane().setContent(grid);
        
        // Habilitar/deshabilitar botón de registro según validación
        Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.setDisable(true);
        
        // Validar campos
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateRegistrationForm(
            emailField, passwordField, confirmPasswordField, adminCheckBox, invitationCodeField, registerButton));
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateRegistrationForm(
            emailField, passwordField, confirmPasswordField, adminCheckBox, invitationCodeField, registerButton));
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateRegistrationForm(
            emailField, passwordField, confirmPasswordField, adminCheckBox, invitationCodeField, registerButton));
        
        invitationCodeField.textProperty().addListener((observable, oldValue, newValue) -> validateRegistrationForm(
            emailField, passwordField, confirmPasswordField, adminCheckBox, invitationCodeField, registerButton));
        
        adminCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validateRegistrationForm(
            emailField, passwordField, confirmPasswordField, adminCheckBox, invitationCodeField, registerButton));
        
        // Convertir el resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                UserRole role = adminCheckBox.isSelected() ? UserRole.ADMIN : UserRole.CLIENT;
                return new UserCredentials(emailField.getText(), passwordField.getText(), role);
            }
            return null;
        });
        
        // Mostrar diálogo y procesar resultado
        dialog.showAndWait().ifPresent(credentials -> {
            // Verificar si es un registro de administrador
            if (credentials.getRole() == UserRole.ADMIN) {
                String code = invitationCodeField.getText().trim();
                if (!authenticator.getUserRepository().isValidAdminInvitationCode(code)) {
                    showAlert("Error", "Código de invitación para administrador inválido");
                    return;
                }
            }
            
            // Registrar el nuevo usuario
            authenticator.addUser(credentials.getEmail(), credentials.getPassword(), credentials.getRole());
            statusText.setText("Usuario registrado correctamente. Ahora puede iniciar sesión.");
            statusText.setStyle("-fx-fill: green;");
        });
    }
    
    private void validateRegistrationForm(
        TextField emailField, 
        PasswordField passwordField, 
        PasswordField confirmPasswordField,
        CheckBox adminCheckBox,
        TextField invitationCodeField,
        Button registerButton) {
        
        boolean emailValid = !emailField.getText().trim().isEmpty();
        boolean passwordValid = !passwordField.getText().trim().isEmpty();
        boolean passwordsMatch = passwordField.getText().equals(confirmPasswordField.getText());
        boolean invitationCodeValid = true;
        
        if (adminCheckBox.isSelected()) {
            invitationCodeValid = !invitationCodeField.getText().trim().isEmpty();
        }
        
        registerButton.setDisable(!(emailValid && passwordValid && passwordsMatch && invitationCodeValid));
    }
    
    private void loginAsGuest() {
        // Crear un usuario invitado temporal
        GuestUser guestUser = new GuestUser("guest_" + System.currentTimeMillis() + "@temp.com", "+0000000000");
        
        // Establecer el usuario en el SessionManager
        SessionManager.getInstance().setGuestUser(guestUser);
        
        // Notificar éxito
        onLoginResult.accept(true);
    }
    
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public BasicAuthenticator getAuthenticator() {
        return authenticator;
    }
}
