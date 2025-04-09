package com.amirulasri.carcontrol.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;

import com.amirulasri.carcontrol.App;

public class ConnectCarController {
    @FXML
    private TextField caripfield;

    @FXML
    private TextField cameraipfield;

    @FXML
    private void connectCar() throws IOException {
        String carIP = caripfield.getText();
        String cameraIP = cameraipfield.getText();

        if (!isReachable(carIP) || carIP.isEmpty()) {
            showError("Cannot reach car IP: " + carIP);
            return;
        }

        if (!isReachable(cameraIP) || cameraIP.isEmpty()) {
            showError("Cannot reach camera IP: " + cameraIP);
            return;
        }

        App.cameraip = cameraIP;
        App.carip = carIP;

        App.setRoot("Home");
    }

    private boolean isReachable(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(2000); // 2000 ms timeout
        } catch (Exception e) {
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("IP Not Reachable");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
