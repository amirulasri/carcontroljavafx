package com.amirulasri.carcontrol.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;

import com.amirulasri.carcontrol.App;
import com.amirulasri.carcontrol.db.DBConnection;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

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
    private void downloadImage() {
        try {
            // Construct image path
            String filePath = "Captured Images/" + App.imageData.getFilename();
            File sourceFile = new File(filePath);

            if (!sourceFile.exists()) {
                System.out.println("Image not found: " + filePath);
                return;
            }

            // Open Save As dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image As");
            fileChooser.setInitialFileName(sourceFile.getName()); // Suggest original filename

            // Restrict to PNG or all images
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png");
            fileChooser.getExtensionFilters().addAll(extFilter);

            File selectedFile = fileChooser.showSaveDialog(capturedImageView.getScene().getWindow());

            if (selectedFile != null) {
                // Copy the image file to selected location
                Files.copy(sourceFile.toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image saved to: " + selectedFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteImage() {
        if (App.imageData != null) {
            try {
                String deleteSQL = "DELETE FROM `images` WHERE `id`=?";
                PreparedStatement preparedStatement = DBConnection.getDBConn().prepareStatement(deleteSQL);
                preparedStatement.setInt(1, App.imageData.getId());
                preparedStatement.executeUpdate();
                System.out.println("Data deleted sucessfully");
                App.setRoot("CapturedImage");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error delete data", 0);
                System.out.println("Error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please choose data first from table to delete", "Choose row first",
                    JOptionPane.INFORMATION_MESSAGE);
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
