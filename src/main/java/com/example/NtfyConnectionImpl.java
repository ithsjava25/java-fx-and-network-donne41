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


    /**
     * Creates a new NtfyConnectionImpl using the HOST_NAME environment variable and initializes the HTTP client and JSON mapper.
     *
     * @throws NullPointerException if the HOST_NAME environment variable is not set
     */
    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        this.hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
        newClient();

    }

    /**
     * Create a connection bound to the given host and initialize the HTTP client and JSON mapper.
     *
     * @param hostName the base host URL used for requests
     */
    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
        newClient();
    }

    /**
     * Creates a new NtfyConnectionImpl configured to send and receive messages for a specific host and chat room.
     *
     * @param hostName the base host URL used for requests (e.g., "https://example.com")
     * @param chatRoom the chat room path segment to append to the host (used as provided; leading slash is not enforced)
     */
    public NtfyConnectionImpl(String hostName, String chatRoom) {
        this.hostName = hostName;
        this.chatRoom = chatRoom;
        newClient();
    }

    /**
     * Initialize the HTTP client and configure the JSON mapper to handle Java Time types.
     *
     * <p>The mapper is registered with the JavaTimeModule and configured to write dates as timestamps.</p>
     */
    public void newClient() {
        client = HttpClient.newHttpClient();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }


    /**
     * Normalize and set the chat room path so it always begins with a leading slash.
     *
     * @param chatroom the chat room path; if it does not start with '/', a leading '/' is prefixed before storing
     */
    public void setChatRoom(String chatroom) {
        if (chatroom.matches("^/.*")) {
            this.chatRoom = chatroom;
        } else {
            this.chatRoom = "/" + chatroom;
        }
    }

    /**
     * Get the current chat room path.
     *
     * @return the chat room path starting with '/'.
     */
    public String getChatRoom() {
        return chatRoom;
    }

    /**
     * Shuts down the current HTTP client and reinitializes the connection components.
     *
     * <p>After this call the implementation will use a newly created HTTP client and JSON mapper.
     */
    public void restartConnection() {
        client.shutdownNow();
        newClient();
    }
    /**
     * Shuts down the internal HttpClient, cancelling any pending requests and releasing its resources.
     */
    public void shutDownClient(){
        client.shutdownNow();
    }

    /**
     * Send the file at the given path to the configured host and chat room using an HTTP POST.
     *
     * @param file the path to the file to send as the request body
     * @return a CompletableFuture whose result is the server's HttpResponse<String>, or `null` if request construction failed
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
     * Send a text message to the configured host and chat room.
     *
     * @param message the text to use as the request body
     * @return the server's HTTP response with a string body; the returned future completes exceptionally if the request cannot be built or sent
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
     * Long-polls the configured chat endpoint and delivers incoming "message" events to the given handler on the JavaFX application thread.
     *
     * <p>Starts a GET request to {@code hostName + chatRoom + "/json"}, parses each response line as a {@code messageDTO}, ignores lines that fail parsing, filters for messages whose {@code event} equals "message", and invokes {@code messageHandler} for each remaining message on the JavaFX Application Thread (falls back to inline execution if the FX toolkit is not initialized).</p>
     *
     * @param messageHandler consumer invoked for each parsed {@code messageDTO} whose {@code event} equals "message"; executed on the JavaFX application thread when available
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
    /**
     * Runs the given task on the JavaFX Application Thread when available.
     *
     * If the current thread is the FX application thread the task is executed immediately;
     * otherwise it is scheduled with Platform.runLater. If the JavaFX toolkit is not
     * initialized, the task is executed inline as a fallback.
     *
     * @param task the Runnable to execute on the FX thread or inline if FX is unavailable
     */
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