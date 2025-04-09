package com.amirulasri.carcontrol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayInputStream;

public class TestCam extends Application {

    private static final String STREAM_URL = "http://192.168.0.103:81/stream"; // change to your ESP32 IP
    private ImageView imageView = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("ESP32-CAM Stream");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this::streamMJPEG).start();
    }

    private void streamMJPEG() {
        try {
            URL url = new URL(STREAM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(0);
            connection.setConnectTimeout(5000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            while (true) {
                // Read until JPEG start
                while (inputStream.read() != 0xFF);
                if (inputStream.read() != 0xD8) continue;

                // Start collecting JPEG bytes
                ByteArrayOutputStream jpegBuffer = new ByteArrayOutputStream();
                jpegBuffer.write(0xFF);
                jpegBuffer.write(0xD8);

                int prev = 0, cur;
                while (true) {
                    cur = inputStream.read();
                    if (cur == -1) break;
                    jpegBuffer.write(cur);

                    // End of JPEG = 0xFFD9
                    if (prev == 0xFF && cur == 0xD9) break;
                    prev = cur;
                }

                byte[] jpegBytes = jpegBuffer.toByteArray();
                Image fxImage = new Image(new ByteArrayInputStream(jpegBytes));
                Platform.runLater(() -> imageView.setImage(fxImage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
