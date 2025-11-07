package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private final NtfyConnection connection;
    private final ObservableList<messageDTO> messages = FXCollections.observableArrayList();
    private final StringProperty newTopic = new SimpleStringProperty();


    public HelloModel(NtfyConnection connection) {

        this.connection = connection;
        setNewTopic(connection.getChatRoom());

    }

    public ObservableList<messageDTO> getMessages() {
        return messages;
    }

    public String getNewTopic() {
        return newTopic.get();
    }

    public StringProperty newTopicProperty() {
        return newTopic;
    }
    public void setNewTopic(String newTopic){
        this.newTopic.set("Chat room: " +newTopic);
        connection.restartConnection();
        connection.setChatRoom(newTopic);
        receiveMessage();
    }
    public void sendImage(Path file){
        connection.sendImage(file);
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

