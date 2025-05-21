module co.edu.uniquindio.poo.notification {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.logging;
    
    exports co.edu.uniquindio.poo.notification;
    exports co.edu.uniquindio.poo.notification.auth;
    exports co.edu.uniquindio.poo.notification.auth.persistence;
    exports co.edu.uniquindio.poo.notification.command;
    exports co.edu.uniquindio.poo.notification.filter;
    exports co.edu.uniquindio.poo.notification.model;
    exports co.edu.uniquindio.poo.notification.observer;
    exports co.edu.uniquindio.poo.notification.strategy;
    exports co.edu.uniquindio.poo.notification.user;
}
