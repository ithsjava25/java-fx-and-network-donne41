package com.example;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    public HelloModel(){


    }

    public ObservableList<Message> getMessages(){
        return messages;
    }
    public void addMessage(String text, String sender){
        messages.add(new Message(text, sender));
    }


    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
    }
}
