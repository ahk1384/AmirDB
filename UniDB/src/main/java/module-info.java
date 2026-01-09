module com.example.unidb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires java.sql;

    opens com.example.unidb to javafx.fxml;
    exports com.example.unidb;
    exports Engine;
    exports Engine.Commands;
    exports Models;
    exports Storage;}