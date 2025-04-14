package com.amirulasri.carcontrol.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.amirulasri.carcontrol.App;
import com.amirulasri.carcontrol.db.DBConnection;
import com.amirulasri.carcontrol.model.ImageData;
import com.amirulasri.carcontrol.model.ImageData.addedToFavouriteType;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
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
    private RadioButton showallfilter;

    @FXML
    private RadioButton showfavfilter;

    private String filterimage = "all";

    @FXML
    private void initialize() {
        ToggleGroup filterImages = new ToggleGroup();
        showallfilter.setToggleGroup(filterImages);
        showfavfilter.setToggleGroup(filterImages);
        showallfilter.setSelected(true);

        filterImages.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                if(selected.getId().equalsIgnoreCase("showallfilter")){
                    this.filterimage = "all";
                    getImageData(filterimage);
                } else {
                    this.filterimage = "fav";
                    getImageData(filterimage);
                }
            }
        });

        imagecol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getImage()));
        imagecol.setCellFactory(
                new Callback<TableColumn<ImageData, Image>, TableCell<ImageData, Image>>() {
                    @Override
                    public TableCell<ImageData, Image> call(TableColumn<ImageData, Image> param) {
                        return new TableCell<ImageData, Image>() {
                            @Override
                            protected void updateItem(Image item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setGraphic(null);
                                } else {
                                    ImageView imageView = new ImageView(item);
                                    imageView.setFitHeight(100); // Set image height
                                    imageView.setFitWidth(150); // Set image width
                                    setGraphic(imageView);
                                }
                            }
                        };
                    }
                });

        namecol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFilename()));

        imagetable.setRowFactory(tableView -> {
            TableRow<ImageData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                App.imageData = row.getItem();
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        App.setRoot("ViewImage");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error occurred", 0);
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        getImageData(this.filterimage);
    }

    private void getImageData(String filterShow) {
        List<ImageData> data = fetchDataFromDatabase(filterShow);
        ObservableList<ImageData> imageList = FXCollections.observableArrayList();
        imageList.setAll(data);
        imagetable.setItems(imageList);
    }

    private List<ImageData> fetchDataFromDatabase(String filterShow) {
        List<ImageData> imageDataList = new ArrayList<>();

        String query;
        if (filterShow.equalsIgnoreCase("fav")){
            query = "SELECT id, imagename, capturetime, isfavourite FROM images WHERE isfavourite = 'yes'";
        } else {
            query = "SELECT id, imagename, capturetime, isfavourite FROM images";
        }

        try (Connection conn = DBConnection.getDBConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String imagename = rs.getString("imagename");
                int id = rs.getInt("id");
                LocalDate dateCaptured = rs.getDate("capturetime").toLocalDate();
                addedToFavouriteType addedFavourite = addedToFavouriteType.valueOf(rs.getString("isfavourite").toUpperCase());
                String filePath = "Captured Images/" + imagename;
                Image image = new Image(new File(filePath).toURI().toString());
                imageDataList.add(new ImageData(id, imagename, image, dateCaptured, addedFavourite));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageDataList;
    }

    @FXML
    private void deleteData() throws IOException {
        if (App.imageData != null) {
            try {
                String deleteSQL = "DELETE FROM `images` WHERE `id`=?";
                PreparedStatement preparedStatement = DBConnection.getDBConn().prepareStatement(deleteSQL);
                preparedStatement.setInt(1, App.imageData.getId());
                preparedStatement.executeUpdate();
                System.out.println("Data deleted sucessfully");
                getImageData(this.filterimage);
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
    private void viewImage() throws IOException {
        App.setRoot("ViewImage");
    }

    @FXML
    private void addToFavourite() {
        if (App.imageData != null) {
            try {
                String deleteSQL = "UPDATE `images` SET `isfavourite` = ? WHERE `id`=?";
                PreparedStatement preparedStatement = DBConnection.getDBConn().prepareStatement(deleteSQL);
                preparedStatement.setString(1, "yes");
                preparedStatement.setInt(2, App.imageData.getId());
                preparedStatement.executeUpdate();
                System.out.println("Added to favourite sucessfully");
                JOptionPane.showMessageDialog(null, "Added to favourite sucessfully", "Add to favourite", JOptionPane.INFORMATION_MESSAGE);
                getImageData(this.filterimage);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error occurred when adding to favourite", 0);
                System.out.println("Error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please choose data first from table to addto favourite",
                    "Choose row first",
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
}
