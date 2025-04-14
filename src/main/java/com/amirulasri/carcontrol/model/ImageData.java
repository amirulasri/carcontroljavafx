package com.amirulasri.carcontrol.model;

import java.time.LocalDate;

import javafx.scene.image.Image;

public class ImageData {
    private final int id;
    private final String filename;
    private final Image image;
    private final LocalDate dateCaptured;
    public enum addedToFavouriteType {
        YES,
        NO
    };
    private addedToFavouriteType addedToFavourite;

    public ImageData(int id, String filename, Image image, LocalDate dateCaptured, addedToFavouriteType addedToFavourite) {
        this.id = id;
        this.filename = filename;
        this.image = image;
        this.dateCaptured = dateCaptured;
        this.addedToFavourite = addedToFavourite;
    }

    public String getFilename() {
        return filename;
    }

    public Image getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDateCaptured() {
        return dateCaptured;
    }

    public addedToFavouriteType getAddedToFavourite() {
        return addedToFavourite;
    }
}
