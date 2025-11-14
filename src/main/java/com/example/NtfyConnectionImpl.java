package com.example;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {
    private HttpClient client;
    private final String hostName;
    private String chatRoom = "/donne41";
    private final ObjectMapper mapper = new ObjectMapper();


    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        this.hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
        newClient();

    }

    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
        newClient();
    }

    public NtfyConnectionImpl(String hostName, String chatRoom) {
        this.hostName = hostName;
        this.chatRoom = chatRoom;
        newClient();
    }

    /**
     * Start a new HttpClient with mapper configuration and
     * current hostName and chatRoom.
     */
    public void newClient() {
        client = HttpClient.newHttpClient();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }


    public void setChatRoom(String chatroom) {
        if (chatroom.matches("^/.*")) {
            this.chatRoom = chatroom;
        } else {
            this.chatRoom = "/" + chatroom;
        }
    }

    public String getChatRoom() {
        return chatRoom;
    }

    /**
     * Kills current HttpClient and starts {@link #newClient()}
     */
    public void restartConnection() {
        client.shutdownNow();
        newClient();
    }
    public void shutDownClient(){
        client.shutdownNow();
    }

    /**
     * Builds a new httpRequest with current hostName and chatRoom.
     * Messages are then sent to server asynchronously.
     * @param file in for of Path are sent to the server.
     * @return Server response as CompletableFuture.
     */
    public CompletableFuture<HttpResponse<String>> sendImage(Path file) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .uri(URI.create(hostName + chatRoom))
                    .build();
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Creating http request failed" + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Builds a new httpRequest with current hostName and chatRoom.
     * Messages are then sent to server asynchronously.
     * @param message of String to send.
     * @return Server response as CompletableFuture.
     */
    @Override
    public CompletableFuture<HttpResponse<String>> send(String message) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + chatRoom))
                .build();
        try {
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Falty argument while sending message " + e.getMessage());
        }
        return CompletableFuture.failedFuture(new Exception("unable to send message"));
    }

    /**
     * Builds a new httpRequest with current hostName and chatRoom.
     * Then starts a long-poll request and processes incoming messages.
     * @param messageHandler maps to messageDTO record
     */
    @Override
    public void receive(Consumer<messageDTO> messageHandler) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + chatRoom + "/json"))
                .build();


        client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    try {

                        response
                                .body()
                                .map(s -> {
                                    try {
                                        return mapper.readValue(s, messageDTO.class);
                                    } catch (Exception e) {
                                        System.out.println("Error parsing: "  + e.getMessage());
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .filter(message -> message.event().equals("message"))
                                .forEach(message -> runOnFx(()-> messageHandler.accept(message)));
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                });

    }
    private static void runOnFx(Runnable task) {
        try {
            if (Platform.isFxApplicationThread()) task.run();
            else Platform.runLater(task);
        } catch (IllegalStateException notInitialized) {
            // JavaFX toolkit not initialized (e.g., unit tests): run inline
            task.run();
        }
    }

}
