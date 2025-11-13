package com.example;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    String getChatRoom();
    void setChatRoom(String chatRoom);
    void restartConnection();
    CompletableFuture<HttpResponse<String>> send(String message);
    CompletableFuture<HttpResponse<String>> sendImage(Path file);
    void receive(Consumer<messageDTO> messageHandler);
}
