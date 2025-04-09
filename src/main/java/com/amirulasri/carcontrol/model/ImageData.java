package com.amirulasri.carcontrol.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class ImageData {
    private final SimpleStringProperty filename;
    private final SimpleObjectProperty<Image> image;

    public ImageData(String filename, Image image) {
        this.filename = new SimpleStringProperty(filename);
        this.image = new SimpleObjectProperty<>(image);
    }

    public SimpleStringProperty getFilename() {
        return filename;
    }

    public SimpleObjectProperty<Image> getImage() {
        return image;
    }
}
