package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private final NtfyConnection connection;
    private final ObservableList<transfereMessageDTO> messages = FXCollections.observableArrayList();
    private final StringProperty testMess = new SimpleStringProperty();


    public HelloModel(NtfyConnection connection) {

        this.connection = connection;

    }

    public ObservableList<transfereMessageDTO> getMessages() {
        return messages;
    }

    public String getTestMess() {
        return testMess.get();
    }

    public StringProperty testMessProperty() {
        return testMess;
    }
    public void setTestMess(String testMess){
        this.testMess.set(testMess);
    }
    public void setTopic(String topic){
        connection.restartConnection();
        connection.setChatRoom(topic);
        receiveMessage();
    }

    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
    }

    public void sendMessage(String message) {
        System.out.println("Model sendMessage: " + message);
        connection.send(message);


    }

    public void receiveMessage() {
        connection.receive(m -> Platform.runLater(() ->
                messages.add(m)));
    }
}

