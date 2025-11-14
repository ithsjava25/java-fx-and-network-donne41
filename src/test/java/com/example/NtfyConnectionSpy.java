package com.example;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    //detta är en Test Dubble.


    @Override
    public CompletableFuture<HttpResponse<String>> sendImage(Path file) {
        CompletableFuture<HttpResponse<String>> response = new CompletableFuture<>();

        return response;
    }

    @Override
    public String getChatRoom() {
        return "";
    }

    @Override
    public void setChatRoom(String chatRoom) {

    }

    @Override
    public void restartConnection() {

    }


    @Override
    public CompletableFuture<HttpResponse<String>> send(String message) {
        this.message = message;
        return new CompletableFuture<HttpResponse<String>>();
    }

    @Override
    public void receive(Consumer<messageDTO> messageHandler) {

    }
}
