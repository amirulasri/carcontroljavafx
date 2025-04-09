package com.amirulasri.carcontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.java_websocket.client.WebSocketClient;

import com.amirulasri.carcontrol.db.DBConnection;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static String carip;
    public static String cameraip;
    public static WebSocketClient webSocketClient;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("ConnectCar"), 740, 460);
        scene.getStylesheets().add(App.class.getResource("appdesign.css").toExternalForm());
        stage.setTitle("Car Control with Image Capture");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void terminateApplication() {
        System.out.println("Terminating the application...");
        System.exit(0); // Exit the application with a normal status
    }

    public static void disconnectAll() throws IOException {
        disconnectWebSocket();
        carip = null;
        cameraip = null;
        setRoot("ConnectCar");
    }

    private static void disconnectWebSocket() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
            System.out.println("WebSocket disconnected.");
        } else {
            System.out.println("WebSocket is not connected.");
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        try {
            DBConnection connection = new DBConnection();
            connection.getDBConn();
            launch();
        } catch (Exception e) {
            System.out.println("Error to start: " + e.getMessage());
        }
    }

}