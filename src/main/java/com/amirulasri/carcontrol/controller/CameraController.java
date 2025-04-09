package com.amirulasri.carcontrol.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.amirulasri.carcontrol.App;
import com.amirulasri.carcontrol.db.DBConnection;

import javafx.application.Application;
import java.awt.image.BufferedImage;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class CameraController extends Application {

    private static final String STREAM_URL = "http://192.168.0.103:81/stream";
    private volatile boolean streaming = false;
    private Thread streamThread;

    @FXML
    private ImageView imageView;

    @FXML 
    private StackPane imageContainer;

    private Image currentImage;

    @FXML
    private VBox cameraviewlayout;

    @FXML
    private void initialize() {

        cameraviewlayout.setFocusTraversable(true);
        cameraviewlayout.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    moveForward();
                    break;
                case A:
                    moveLeft();
                    break;
                case S:
                    moveBackward();
                    break;
                case D:
                    moveRight();
                    break;
                default:
                    break;
            }
        });
    
        cameraviewlayout.setOnKeyReleased(event -> {
            // Stop movement when any key is released
            stopMove();
        });

        // Maintain image aspect ratio
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Bind size of imageView to StackPane, it will crop the rest
        imageView.fitWidthProperty().bind(imageContainer.widthProperty());
        imageView.fitHeightProperty().bind(imageContainer.heightProperty());

        cameraviewlayout.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Bind ImageView width/height to Scene's dimensions
                imageView.fitWidthProperty().bind(newScene.widthProperty().subtract(20)); // padding margin
                imageView.fitHeightProperty().bind(newScene.heightProperty().subtract(160)); // adjust for
                                                                                             // toolbar/header
            }
        });
        startStream();
    }

    private void moveForward() {
        sendCommand("FORWARD");
    }

    private void moveBackward() {
        sendCommand("BACKWARD");
    }

    private void moveLeft() {
        sendCommand("LEFT");
    }

    private void moveRight() {
        sendCommand("RIGHT");
    }

    private void stopMove() {
        sendCommand("STOP");
    }

    private void sendCommand(String command) {
        if (App.webSocketClient != null && App.webSocketClient.isOpen()) {
            App.webSocketClient.send(command);
        } else {
            System.out.println("WebSocket not connected.");
        }
    }

    private void startStream() {
        if (streaming)
            return;

        streaming = true;
        streamThread = new Thread(this::streamMJPEG);
        streamThread.setDaemon(true);
        streamThread.start();
    }

    private void stopStream() {
        streaming = false;
        if (streamThread != null) {
            streamThread.interrupt();
            streamThread = null;
        }
    }

    @FXML
    private void restartStream() {
        stopStream();
        startStream();
    }

    private void streamMJPEG() {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(STREAM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(0);
            connection.setConnectTimeout(5000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            while (streaming) {
                while (inputStream.read() != 0xFF) {
                }
                if (inputStream.read() != 0xD8)
                    continue;

                ByteArrayOutputStream jpegBuffer = new ByteArrayOutputStream();
                jpegBuffer.write(0xFF);
                jpegBuffer.write(0xD8);

                int prev = 0, cur;
                while (true) {
                    cur = inputStream.read();
                    if (cur == -1 || !streaming)
                        break;
                    jpegBuffer.write(cur);
                    if (prev == 0xFF && cur == 0xD9)
                        break;
                    prev = cur;
                }

                byte[] jpegBytes = jpegBuffer.toByteArray();
                Image fxImage = new Image(new ByteArrayInputStream(jpegBytes));
                currentImage = fxImage;

                Platform.runLater(() -> imageView.setImage(fxImage));
            }

            inputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
private void captureImage() {
    if (currentImage == null)
        return;

    try {
        // Define the folder path
        Path imagesFolderPath = Paths.get("Captured Images");
        
        // Create the folder if it doesn't exist
        if (!Files.exists(imagesFolderPath)) {
            Files.createDirectories(imagesFolderPath);
        }

        // Create timestamped file name
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "snapshot_" + timestamp + ".png";
        File file = new File(imagesFolderPath.toFile(), filename);

        // Convert currentImage to BufferedImage and save it
        BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(currentImage, null);
        ImageIO.write(bufferedImage, "png", file);
        System.out.println("Snapshot saved: " + file.getAbsolutePath());

        try {
            String insertSQL = "INSERT INTO `images`(`id`, `imagename`) VALUES (NULL,?)";
            PreparedStatement preparedStatement = DBConnection.getCon().prepareStatement(insertSQL);
            preparedStatement.setString(1, filename);
            preparedStatement.executeUpdate();
            System.out.println("Data inserted sucessfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error adding data", 0);
            System.out.println("Error occurred: " + e.getMessage());
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void closeApp() {
        App.terminateApplication();
    }

    @FXML
    private void disconnectAll() throws IOException {
        stopStream();
        App.disconnectAll();
    }

    @FXML
    private void navigateToHomeView() throws IOException {
        stopStream();
        App.setRoot("Home");
    }

    @Override
    public void stop() {
        stopStream(); // Ensure streaming stops on app exit
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}