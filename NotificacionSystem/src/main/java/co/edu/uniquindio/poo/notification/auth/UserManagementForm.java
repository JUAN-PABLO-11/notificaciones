package co.edu.uniquindio.poo.notification.auth;

import co.edu.uniquindio.poo.notification.auth.persistence.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Formulario para administrar usuarios (solo para administradores).
 */
public class UserManagementForm extends VBox {
    private TableView<UserViewModel> userTable;
    private TextField emailField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private Button addButton;
    private Button deleteButton;
    private Button updatePasswordButton;
    private Button changeRoleButton;
    private UserRepository userRepository;
    
    public UserManagementForm(UserRepository userRepository) {
        this.userRepository = userRepository;
        
        setPadding(new Insets(20));
        setSpacing(10);
        
        // Título
        Label titleLabel = new Label("Administración de Usuarios");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Tabla de usuarios
        createUserTable();
        
        // Formulario para añadir/actualizar usuarios
        GridPane formGrid = createUserForm();
        
        // Botones de acción
        HBox buttonBox = createButtonBox();
        
        getChildren().addAll(titleLabel, userTable, formGrid, buttonBox);
        
        // Cargar usuarios
        loadUsers();
    }
    
    private void createUserTable() {
        userTable = new TableView<>();
        
        TableColumn<UserViewModel, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(200);
        
        TableColumn<UserViewModel, String> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        roleCol.setPrefWidth(100);
        
        userTable.getColumns().addAll(emailCol, roleCol);
        userTable.setPrefHeight(200);
        
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                emailField.setText(newSelection.getEmail());
                roleComboBox.setValue(newSelection.getRole());
                passwordField.clear();
            }
        });
    }
    
    private GridPane createUserForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Email
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        
        // Contraseña
        Label passwordLabel = new Label("Contraseña:");
        passwordField = new PasswordField();
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        
        // Rol
        Label roleLabel = new Label("Rol:");
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "CLIENT", "GUEST");
        roleComboBox.setValue("CLIENT");
        grid.add(roleLabel, 0, 2);
        grid.add(roleComboBox, 1, 2);
        
        return grid;
    }
    
    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        
        addButton = new Button("Añadir Usuario");
        addButton.setOnAction(e -> addUser());
        
        deleteButton = new Button("Eliminar Usuario");
        deleteButton.setOnAction(e -> deleteUser());
        
        updatePasswordButton = new Button("Actualizar Contraseña");
        updatePasswordButton.setOnAction(e -> updatePassword());
        
        changeRoleButton = new Button("Cambiar Rol");
        changeRoleButton.setOnAction(e -> changeUserRole());
        
        buttonBox.getChildren().addAll(addButton, deleteButton, updatePasswordButton, changeRoleButton);
        
        return buttonBox;
    }
    
    private void loadUsers() {
        ObservableList<UserViewModel> userData = FXCollections.observableArrayList();
        
        Map<String, UserCredentials> users = userRepository.getAllUsers();
        for (Map.Entry<String, UserCredentials> entry : users.entrySet()) {
            UserCredentials credentials = entry.getValue();
            userData.add(new UserViewModel(credentials.getEmail(), credentials.getRole().toString()));
        }
        
        userTable.setItems(userData);
    }
    
    private void addUser() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, complete todos los campos");
            return;
        }
        
        UserRole role = UserRole.valueOf(roleStr);
        userRepository.addUser(email, password, role);
        
        loadUsers();
        clearFields();
        showAlert("Éxito", "Usuario añadido correctamente");
    }
    
    private void deleteUser() {
        UserViewModel selectedUser = userTable.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
            showAlert("Error", "Por favor, seleccione un usuario");
            return;
        }
        
        // Evitar eliminar al último administrador
        if (selectedUser.getRole().equals("ADMIN") && countAdmins() <= 1) {
            showAlert("Error", "No se puede eliminar el último administrador");
            return;
        }
        
        userRepository.removeUser(selectedUser.getEmail());
        
        loadUsers();
        clearFields();
        showAlert("Éxito", "Usuario eliminado correctamente");
    }
    
    private void updatePassword() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, complete el email y la contraseña");
            return;
        }
        
        userRepository.updatePassword(email, password);
        
        showAlert("Éxito", "Contraseña actualizada correctamente");
        clearFields();
    }
    
    private void changeUserRole() {
        UserViewModel selectedUser = userTable.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
            showAlert("Error", "Por favor, seleccione un usuario");
            return;
        }
        
        String email = selectedUser.getEmail();
        String newRoleStr = roleComboBox.getValue();
        UserRole newRole = UserRole.valueOf(newRoleStr);
        
        // Evitar cambiar el rol del último administrador
        if (selectedUser.getRole().equals("ADMIN") && !newRoleStr.equals("ADMIN") && countAdmins() <= 1) {
            showAlert("Error", "No se puede cambiar el rol del último administrador");
            return;
        }
        
        userRepository.updateUserRole(email, newRole);
        
        loadUsers();
        clearFields();
        showAlert("Éxito", "Rol de usuario actualizado correctamente");
    }
    
    private int countAdmins() {
        int count = 0;
        for (UserViewModel user : userTable.getItems()) {
            if (user.getRole().equals("ADMIN")) {
                count++;
            }
        }
        return count;
    }
    
    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("CLIENT");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Clase para representar usuarios en la tabla.
     */
    public static class UserViewModel {
        private String email;
        private String role;
        
        public UserViewModel(String email, String role) {
            this.email = email;
            this.role = role;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getRole() {
            return role;
        }
        
        public javafx.beans.property.StringProperty emailProperty() {
            return new javafx.beans.property.SimpleStringProperty(email);
        }
        
        public javafx.beans.property.StringProperty roleProperty() {
            return new javafx.beans.property.SimpleStringProperty(role);
        }
    }
}
