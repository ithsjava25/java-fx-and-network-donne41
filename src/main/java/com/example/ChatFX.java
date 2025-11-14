package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.Console;

public class ChatFX extends Application {

    /**
     * Initializes and displays the primary application window using the "hello-view.fxml" layout.
     *
     * Loads the FXML root, creates a Scene sized 640×480, sets the window title to "chatAholic",
     * assigns the scene to the provided stage, and shows the stage.
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(ChatFX.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("chatAholic");
        stage.setScene(scene);
        stage.show();


    }




    /**
     * Application entry point that starts the JavaFX runtime.
     *
     * @param args command-line arguments forwarded to the JavaFX launcher
     */
    public static void main(String[] args) {
        launch();
    }

}