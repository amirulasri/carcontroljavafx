package com.amirulasri.carcontrol.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.amirulasri.carcontrol.App;
import com.amirulasri.carcontrol.db.DBConnection;
import com.amirulasri.carcontrol.model.ImageData;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class CapturedImageController {
    @FXML
    private TableView<ImageData> imagetable;

    @FXML
    private TableColumn<ImageData, Image> imagecol;
    
    @FXML
    private TableColumn<ImageData, String> namecol;
    
    @FXML
    private void initialize() {
        imagecol.setCellValueFactory(cellData -> cellData.getValue().getImage());
        imagecol.setCellFactory(new Callback<TableColumn<ImageData, Image>, javafx.scene.control.TableCell<ImageData, Image>>() {
            @Override
            public javafx.scene.control.TableCell<ImageData, Image> call(TableColumn<ImageData, Image> param) {
                return new javafx.scene.control.TableCell<ImageData, Image>() {
                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            ImageView imageView = new ImageView(item);
                            imageView.setFitHeight(200);  // Set image height
                            imageView.setFitWidth(250);   // Set image width
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        namecol.setCellValueFactory(cellData -> cellData.getValue().getFilename());

        List<ImageData> data = fetchDataFromDatabase();
        imagetable.getItems().addAll(data);
    }

    private List<ImageData> fetchDataFromDatabase() {
        List<ImageData> imageDataList = new ArrayList<>();

        String query = "SELECT id, imagename, capturetime, isfavourite FROM images";

        try (Connection conn = DBConnection.getCon();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String imagename = rs.getString("imagename");
                String filePath = "Captured Images/" + imagename; // Adjust the path if necessary
                Image image = new Image(new File(filePath).toURI().toString());
                imageDataList.add(new ImageData(imagename, image));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageDataList;
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
}
