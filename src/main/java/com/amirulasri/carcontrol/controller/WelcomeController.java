package com.amirulasri.carcontrol.controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.amirulasri.carcontrol.App;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class WelcomeController {
    @FXML
    private VBox homeviewlayout;

    @FXML
    private PasswordField proceedfield;

    @FXML
    private void initialize() {
    }

    @FXML
    private void navigateToConnectCarView() throws IOException {
        if(!proceedfield.getText().equalsIgnoreCase("PROCEED")){
            JOptionPane.showMessageDialog(null, "Invalid text entered, please type PROCEED to continue.", "Entered field not match", 0);
            return;
        }
        App.setRoot("ConnectCar");
    }
}
