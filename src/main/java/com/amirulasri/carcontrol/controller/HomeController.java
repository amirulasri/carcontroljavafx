package com.amirulasri.carcontrol.controller;

import java.io.IOException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.amirulasri.carcontrol.App;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HomeController {
    @FXML
    private VBox homeviewlayout;
    
    @FXML
    private void initialize() {
        connectWebSocket("ws://" + App.carip + ":81");

        homeviewlayout.setFocusTraversable(true);
        homeviewlayout.setOnKeyPressed(event -> {
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
    
        homeviewlayout.setOnKeyReleased(event -> {
            // Stop movement when any key is released
            stopMove();
        });
    }

    @FXML
    private void moveForward() {
        sendCommand("FORWARD");
    }

    @FXML
    private void moveBackward() {
        sendCommand("BACKWARD");
    }

    @FXML
    private void moveLeft() {
        sendCommand("LEFT");
    }

    @FXML
    private void moveRight() {
        sendCommand("RIGHT");
    }

    @FXML
    private void stopMove() {
        sendCommand("STOP");
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

    private void connectWebSocket(String serverUri) {
        if (App.webSocketClient != null && App.webSocketClient.isOpen()) {
            // WebSocket is already connected, so do not reconnect
            System.out.println("WebSocket is already connected.");
            return;
        }
        
        try {
            App.webSocketClient = new WebSocketClient(new URI(serverUri)) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to ESP32");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error: " + ex.getMessage());
                }
            };
            App.webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String command) {
        if (App.webSocketClient != null && App.webSocketClient.isOpen()) {
            App.webSocketClient.send(command);
        } else {
            System.out.println("WebSocket not connected.");
        }
    }
}
