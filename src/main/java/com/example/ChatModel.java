package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class ChatModel {

    private final NtfyConnection connection;
    private final ObservableList<messageDTO> messages = FXCollections.observableArrayList();
    private final StringProperty newTopic = new SimpleStringProperty();


    public ChatModel(NtfyConnection connection) {

        this.connection = connection;
        setNewTopic(getTopic());

    }

    public ObservableList<messageDTO> getMessages() {
        return messages;
    }

    public String getTopic() {
        return connection.getChatRoom();
    }

    public StringProperty newTopicProperty() {
        return newTopic;
    }

    public void setNewTopic(String newTopic) throws IllegalArgumentException {
        if (!newTopic.matches("^(?!/).*[\\s\\W]+.*")) {
            this.newTopic.set("Chat room: " + newTopic);
            connection.restartConnection();
            connection.setChatRoom(newTopic);
            receiveMessage();
        } else {
            throw new IllegalArgumentException("Using forbidden characters!");
        }
    }

    public CompletableFuture<HttpResponse<String>> sendImage(Path file) {
        return connection.sendImage(file);
    }


    public CompletableFuture<HttpResponse<String>> sendMessage(String message) {
        return connection.send(message);
    }

    public void receiveMessage() {
        connection.receive(m -> Platform.runLater(() ->
                messages.add(m)));
    }
}

