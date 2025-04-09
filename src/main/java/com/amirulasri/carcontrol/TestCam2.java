package com.amirulasri.carcontrol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TestCam2 extends Application {

    private static final String STREAM_URL = "http://192.168.0.103:81/stream"; // Replace with your ESP32 IP

    private volatile boolean streaming = false;
    private Thread streamThread;
    private ImageView imageView = new ImageView();
    private Image currentImage;

    @Override
    public void start(Stage primaryStage) {
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        Button startButton = new Button("Start Stream");
        Button stopButton = new Button("Stop Stream");
        Button captureButton = new Button("Capture Image");

        startButton.setOnAction(e -> startStream());
        stopButton.setOnAction(e -> stopStream());
        captureButton.setOnAction(e -> captureImage());

        HBox buttonBox = new HBox(10, startButton, stopButton, captureButton);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(imageView);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 680, 540);
        primaryStage.setTitle("ESP32-CAM Stream with Controls");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startStream() {
        if (streaming) return;

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

    private void streamMJPEG() {
        try {
            URL url = new URL(STREAM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(0);
            connection.setConnectTimeout(5000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            while (streaming) {
                while (inputStream.read() != 0xFF) {}
                if (inputStream.read() != 0xD8) continue;

                ByteArrayOutputStream jpegBuffer = new ByteArrayOutputStream();
                jpegBuffer.write(0xFF);
                jpegBuffer.write(0xD8);

                int prev = 0, cur;
                while (true) {
                    cur = inputStream.read();
                    if (cur == -1 || !streaming) break;
                    jpegBuffer.write(cur);
                    if (prev == 0xFF && cur == 0xD9) break;
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

    private void captureImage() {
        if (currentImage == null) return;

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File("snapshot_" + timestamp + ".png");

            BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(currentImage, null);
            ImageIO.write(bufferedImage, "png", file);
            System.out.println("Snapshot saved: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        stopStream(); // Ensure streaming stops on app exit
    }

    public static void main(String[] args) {
        launch(args);
    }
}
