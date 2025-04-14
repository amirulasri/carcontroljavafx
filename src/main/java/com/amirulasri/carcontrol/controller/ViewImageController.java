package com.amirulasri.carcontrol.controller;

import java.io.File;
import java.io.IOException;
import com.amirulasri.carcontrol.App;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ViewImageController {
    @FXML
    private VBox homeviewlayout;

    @FXML
    private ImageView capturedImageView;

    @FXML
    private Text imagenametxt;
    
    @FXML
    private Text datecapturetxt;
    
    @FXML
    private Text addedtofavtxt;

    @FXML
    private void initialize() {
        if (App.imageData != null) {
            String filePath = "Captured Images/" + App.imageData.getFilename();
            Image image = new Image(new File(filePath).toURI().toString());
            capturedImageView.setImage(image);

            imagenametxt.setText("File name: " + App.imageData.getFilename());
            datecapturetxt.setText("Date captured: " + App.imageData.getDateCaptured().toString());
            addedtofavtxt.setText("Added to favourite: " + App.imageData.getAddedToFavourite().toString());
        }
    }

    @FXML
    private void navigateToCameraView() throws IOException {
        App.setRoot("Camera");
    }

    @FXML
    private void disconnectAll() throws IOException {
        App.disconnectAll();
    }

    @FXML
    private void closeApp() {
        App.terminateApplication();
    }

    @FXML
    private void navigateToHomeView() throws IOException {
        App.setRoot("Home");
    }

    @FXML
    private void navigateToCapturedImageView() throws IOException {
        App.setRoot("CapturedImage");
    }
}
