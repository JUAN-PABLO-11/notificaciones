package co.edu.uniquindio.poo.notification;

import co.edu.uniquindio.poo.notification.command.NotificationCommand;
import co.edu.uniquindio.poo.notification.command.NotificationInvoker;
import co.edu.uniquindio.poo.notification.command.SendNotificationCommand;
import co.edu.uniquindio.poo.notification.filter.BlockedUserFilter;
import co.edu.uniquindio.poo.notification.filter.EmptyMessageFilter;
import co.edu.uniquindio.poo.notification.filter.NotificationFilter;
import co.edu.uniquindio.poo.notification.model.Notification;
import co.edu.uniquindio.poo.notification.observer.EventManager;
import co.edu.uniquindio.poo.notification.observer.Logger;
import co.edu.uniquindio.poo.notification.observer.Auditor;
import co.edu.uniquindio.poo.notification.observer.AutoNotifier;
import co.edu.uniquindio.poo.notification.strategy.EmailNotification;
import co.edu.uniquindio.poo.notification.strategy.PushNotification;
import co.edu.uniquindio.poo.notification.strategy.SMSNotification;
import co.edu.uniquindio.poo.notification.user.AdminUser;
import co.edu.uniquindio.poo.notification.user.ClientUser;
import co.edu.uniquindio.poo.notification.user.GuestUser;
import co.edu.uniquindio.poo.notification.user.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import co.edu.uniquindio.poo.notification.auth.SessionManager;
import co.edu.uniquindio.poo.notification.auth.SecurityProxy;
import co.edu.uniquindio.poo.notification.auth.LoginForm;
import co.edu.uniquindio.poo.notification.auth.BasicAuthenticator;
import co.edu.uniquindio.poo.notification.auth.UserManagementForm;
import co.edu.uniquindio.poo.notification.service.NotificationService;
import co.edu.uniquindio.poo.notification.event.SystemEvent;
import co.edu.uniquindio.poo.notification.event.SystemEventData;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    private TextArea logArea;
    private EventManager eventManager;
    private NotificationFilter filterChain;
    private NotificationInvoker invoker;
    private SecurityProxy securityProxy;
    private BasicAuthenticator authenticator;
    private NotificationService notificationService;

    @Override
    public void start(Stage primaryStage) {
        // Inicializar componentes del sistema
        initializeSystem();

        // Configurar la interfaz de usuario
        primaryStage.setTitle("Sistema de Notificaciones");
        
        // Crear la escena de login
        LoginForm loginForm = new LoginForm(success -> {
            if (success) {
                showMainInterface(primaryStage);
            }
        });
        
        // Usar el autenticador de LoginForm
        authenticator = loginForm.getAuthenticator();
        
        // Configurar el EventManager en el UserRepository
        authenticator.getUserRepository().setEventManager(eventManager);
        
        Scene loginScene = new Scene(loginForm, 450, 350);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    
    private void showMainInterface(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        
        Tab sendTab = new Tab("Enviar Notificación");
        sendTab.setContent(createSendNotificationPane());
        sendTab.setClosable(false);
        
        Tab logTab = new Tab("Registro de Actividad");
        logTab.setContent(createLogPane());
        logTab.setClosable(false);
        
        tabPane.getTabs().addAll(sendTab, logTab);
        
        // Añadir pestaña de administración de usuarios (solo para administradores)
        if (securityProxy.canManageUsers()) {
            Tab userManagementTab = new Tab("Administrar Usuarios");
            userManagementTab.setContent(new UserManagementForm(authenticator.getUserRepository()));
            userManagementTab.setClosable(false);
            tabPane.getTabs().add(userManagementTab);
            
            // Añadir pestaña de notificaciones del sistema (solo para administradores)
            Tab systemNotificationsTab = new Tab("Notificaciones del Sistema");
            systemNotificationsTab.setContent(createSystemNotificationsPane());
            systemNotificationsTab.setClosable(false);
            tabPane.getTabs().add(systemNotificationsTab);
        }
        
        // Añadir botón de cierre de sesión
        Button logoutButton = new Button("Cerrar Sesión");
        logoutButton.setOnAction(e -> {
            SessionManager.getInstance().logout();
            start(primaryStage);
        });
        
        // Mostrar información del usuario actual
        String userInfo;
        if (SessionManager.getInstance().isGuest()) {
            userInfo = "Usuario: Invitado";
        } else {
            userInfo = "Usuario: " + SessionManager.getInstance().getCurrentUser().getEmail();
        }
        Label userLabel = new Label(userInfo);
        
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.getChildren().addAll(userLabel, logoutButton);
        
        VBox root = new VBox(10);
        root.getChildren().addAll(topBar, tabPane);
        
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }

    private void initializeSystem() {
        // Inicializar Event Manager (Observer)
        eventManager = new EventManager("NOTIFICATION", "AUTHENTICATION", "USER_MANAGEMENT", "SYSTEM");
        
        // Inicializar logArea (necesario para los observadores)
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(400);
        
        eventManager.subscribe("NOTIFICATION", new Logger(message -> logArea.appendText(message + "\n")));
        eventManager.subscribe("NOTIFICATION", new Auditor(message -> logArea.appendText("[AUDIT] " + message + "\n")));
        eventManager.subscribe("AUTHENTICATION", new Logger(message -> logArea.appendText("[AUTH] " + message + "\n")));
        eventManager.subscribe("USER_MANAGEMENT", new Logger(message -> logArea.appendText("[USER] " + message + "\n")));
        eventManager.subscribe("SYSTEM", new Logger(message -> logArea.appendText("[SYSTEM] " + message + "\n")));
        
        // Inicializar Chain of Responsibility
        NotificationFilter emptyMessageFilter = new EmptyMessageFilter();
        NotificationFilter blockedUserFilter = new BlockedUserFilter();
        emptyMessageFilter.setNext(blockedUserFilter);
        filterChain = emptyMessageFilter;
        
        // Inicializar Command
        invoker = new NotificationInvoker();
        
        // Inicializar Security Proxy
        securityProxy = new SecurityProxy();
        
        // Configurar Session Manager
        SessionManager.getInstance().setEventManager(eventManager);
        
        // Inicializar NotificationService
        notificationService = new NotificationService(null, invoker, eventManager);
        
        // Registrar AutoNotifier para eventos tipados
        AutoNotifier autoNotifier = new AutoNotifier(notificationService);
        for (SystemEvent eventType : SystemEvent.values()) {
            eventManager.subscribe(eventType, autoNotifier);
        }
    }

    private GridPane createSendNotificationPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Campos para la notificación
        Label userTypeLabel = new Label("Tipo de Usuario:");
        ComboBox<String> userTypeCombo = new ComboBox<>();
        
        // Si es invitado, solo mostrar la opción de invitado
        if (SessionManager.getInstance().isGuest()) {
            userTypeCombo.getItems().add("Invitado");
            userTypeCombo.setValue("Invitado");
            userTypeCombo.setDisable(true);
        } else {
            userTypeCombo.getItems().addAll("Administrador", "Cliente", "Invitado");
            userTypeCombo.setValue("Cliente");
        }
        
        Label channelLabel = new Label("Canal de Notificación:");
        ComboBox<String> channelCombo = new ComboBox<>();
        channelCombo.getItems().addAll("Email", "SMS", "Push");
        channelCombo.setValue("Email");
        
        Label messageLabel = new Label("Mensaje:");
        TextArea messageArea = new TextArea();
        messageArea.setPrefRowCount(5);
        
        CheckBox blockedCheckbox = new CheckBox("Usuario Bloqueado");
        
        Button sendButton = new Button("Enviar Notificación");
        
        // Agregar componentes al grid
        grid.add(userTypeLabel, 0, 0);
        grid.add(userTypeCombo, 1, 0);
        grid.add(channelLabel, 0, 1);
        grid.add(channelCombo, 1, 1);
        grid.add(messageLabel, 0, 2);
        grid.add(messageArea, 1, 2);
        grid.add(blockedCheckbox, 1, 3);
        grid.add(sendButton, 1, 4);
        
        // Configurar acción del botón
        sendButton.setOnAction(e -> {
            // Crear usuario según el tipo seleccionado
            User user;
            switch (userTypeCombo.getValue()) {
                case "Administrador":
                    user = new AdminUser("admin@example.com", "+1234567890");
                    break;
                case "Invitado":
                    user = new GuestUser("guest@example.com", "+1234567890");
                    break;
                default:
                    user = new ClientUser("client@example.com", "+1234567890");
            }
            
            // Establecer si el usuario está bloqueado
            user.setBlocked(blockedCheckbox.isSelected());
            
            // Seleccionar estrategia según el canal
            switch (channelCombo.getValue()) {
                case "Email":
                    user.setPreferredChannel(new EmailNotification());
                    break;
                case "SMS":
                    user.setPreferredChannel(new SMSNotification());
                    break;
                case "Push":
                    user.setPreferredChannel(new PushNotification());
                    break;
            }
            
            // Crear notificación
            Notification notification = new Notification(user, messageArea.getText());
            
            // Verificar permisos
            if (!securityProxy.canSendNotification(notification)) {
                logArea.appendText("No tiene permisos para enviar esta notificación\n");
                return;
            }
            
            // Validar con Chain of Responsibility
            if (filterChain.filter(notification)) {
                // Crear y ejecutar comando
                NotificationCommand command = new SendNotificationCommand(notification, eventManager);
                invoker.addCommand(command);
                invoker.executeCommands();
            } else {
                logArea.appendText("La notificación no pasó las validaciones\n");
            }
        });
        
        return grid;
    }

    private VBox createLogPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Button clearButton = new Button("Limpiar Registro");
        clearButton.setOnAction(e -> {
            if (securityProxy.canViewLogs()) {
                logArea.clear();
            } else {
                logArea.appendText("No tiene permisos para limpiar los registros\n");
            }
        });
        
        vbox.getChildren().addAll(logArea, clearButton);
        
        return vbox;
    }
    
    private GridPane createSystemNotificationsPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Sección de mantenimiento del sistema
        Label maintenanceLabel = new Label("Notificar Mantenimiento del Sistema:");
        grid.add(maintenanceLabel, 0, 0, 2, 1);
        
        Label dateLabel = new Label("Fecha:");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        
        Label durationLabel = new Label("Duración:");
        TextField durationField = new TextField("2 horas");
        grid.add(durationLabel, 0, 2);
        grid.add(durationField, 1, 2);
        
        Button notifyMaintenanceButton = new Button("Notificar Mantenimiento");
        grid.add(notifyMaintenanceButton, 1, 3);
        
        // Sección de actualización del sistema
        Label updateLabel = new Label("Notificar Actualización del Sistema:");
        grid.add(updateLabel, 0, 4, 2, 1);
        
        Label versionLabel = new Label("Versión:");
        TextField versionField = new TextField("2.0");
        grid.add(versionLabel, 0, 5);
        grid.add(versionField, 1, 5);
        
        Label featuresLabel = new Label("Características:");
        TextArea featuresArea = new TextArea("- Mejoras de rendimiento\n- Nuevas funcionalidades\n- Corrección de errores");
        featuresArea.setPrefRowCount(3);
        grid.add(featuresLabel, 0, 6);
        grid.add(featuresArea, 1, 6);
        
        Button notifyUpdateButton = new Button("Notificar Actualización");
        grid.add(notifyUpdateButton, 1, 7);
        
        // Configurar acciones de los botones
        notifyMaintenanceButton.setOnAction(e -> {
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String duration = durationField.getText();
            
            authenticator.getUserRepository().notifySystemMaintenance(date, duration);
            logArea.appendText("[SYSTEM] Notificación de mantenimiento enviada para el " + date + "\n");
        });
        
        notifyUpdateButton.setOnAction(e -> {
            String version = versionField.getText();
            String features = featuresArea.getText();
            
            authenticator.getUserRepository().notifySystemUpdate(version, features);
            logArea.appendText("[SYSTEM] Notificación de actualización enviada para la versión " + version + "\n");
        });
        
        return grid;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
