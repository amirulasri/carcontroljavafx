module com.amirulasri.carcontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires java.sql;
    requires transitive org.java_websocket;
    requires transitive javafx.graphics;

    opens com.amirulasri.carcontrol.controller;
    opens com.amirulasri.carcontrol to javafx.fxml;
    exports com.amirulasri.carcontrol;
    exports com.amirulasri.carcontrol.model;
}
