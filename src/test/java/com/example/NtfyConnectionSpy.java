package com.example;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    /**
     * Provides a test stub for sending an image via the connection.
     *
     * @param file the path to the image file (ignored by this test double)
     * @return a new, uncompleted {@code CompletableFuture<HttpResponse<String>>} that does not perform any network activity
     */


    @Override
    public CompletableFuture<HttpResponse<String>> sendImage(Path file) {
        CompletableFuture<HttpResponse<String>> response = new CompletableFuture<>();

        return response;
    }

    /**
     * Returns the chat room identifier used by this connection spy; always an empty string.
     *
     * @return the chat room identifier, always an empty string
     */
    @Override
    public String getChatRoom() {
        return "";
    }

    /**
     * No-op implementation that accepts a chat room identifier but does not change the spy's state.
     *
     * @param chatRoom the chat room identifier to set (ignored by this test spy)
     */
    @Override
    public void setChatRoom(String chatRoom) {

    }

    /**
     * No-op restart method for the test spy.
     *
     * Does nothing and does not change internal state; present to satisfy the NtfyConnection contract in tests.
     */
    @Override
    public void restartConnection() {

    }


    /**
     * Records the provided message for later inspection by the test spy.
     *
     * @param message the message to record
     * @return a new, uncompleted CompletableFuture containing the HTTP response placeholder
     */
    @Override
    public CompletableFuture<HttpResponse<String>> send(String message) {
        this.message = message;
        return new CompletableFuture<HttpResponse<String>>();
    }

    /**
     * Accepts a message handler but does not register or invoke it in this test spy.
     *
     * @param messageHandler consumer to handle incoming messages; ignored by this implementation
     */
    @Override
    public void receive(Consumer<messageDTO> messageHandler) {

    }
}