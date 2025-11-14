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


    /**
     * Create a ChatModel bound to the provided connection and initialize the topic state.
     *
     * Initializes the model's topic property from the connection's current chat room.
     *
     * @param connection the underlying NtfyConnection used for sending, receiving, and managing the chat room
     */
    public ChatModel(NtfyConnection connection) {
        this.connection = connection;
        setNewTopic(getTopic());
    }

    /**
     * Exposes the observable list of chat messages maintained by this model.
     *
     * @return the ObservableList of messageDTO objects contained in the model
     */
    public ObservableList<messageDTO> getMessages() {
        return messages;
    }

    /**
     * Appends the given chat message to the model's observable message list.
     *
     * <p>Adding the message updates {@code messages} so any observers bound to it are notified.</p>
     *
     * @param message the message to add to the chat history
     */
    public void addMessage(messageDTO message) {
        messages.add(message);
    }

    /**
     * Get the current chat room/topic.
     *
     * @return the current chat room name
     */
    public String getTopic() {
        return connection.getChatRoom();
    }

    /**
     * Provides access to the JavaFX property for the current editable chat topic.
     *
     * @return the StringProperty representing the current/edited chat topic
     */
    public StringProperty newTopicProperty() {
        return newTopic;
    }

    /**
     * Update the chat topic, switch the underlying connection to the new topic, and begin receiving messages for it.
     *
     * <p>Validates the provided topic and, on acceptance, updates the internal topic property, restarts the connection,
     * sets the connection's chat room, and initiates message reception.</p>
     *
     * @param newTopic the desired chat room name; must not contain spaces or other special characters
     * @throws IllegalArgumentException if the provided topic contains forbidden characters
     */
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

    /**
     * Sends the given image file to the server.
     *
     * @param file the path to the image file to send
     * @return the HTTP response containing the server's response body as a String
     */
    public CompletableFuture<HttpResponse<String>> sendImage(Path file) {
        return connection.sendImage(file);
    }


    /**
     * Send a text message through the underlying connection.
     *
     * @param message the message content to send
     * @return the HTTP response whose body is the response as a String
     */
    public CompletableFuture<HttpResponse<String>> sendMessage(String message) {
        return connection.send(message);
    }

    /**
     * Registers a handler that appends incoming messages to the model's messages list on the JavaFX Application Thread.
     *
     * Each message received from the underlying connection is added to the observable messages list via a UI-thread task to ensure thread-safe updates.
     */
    public void receiveMessage() {
        connection.receive(m -> Platform.runLater(() ->
                messages.add(m)));
    }
}
