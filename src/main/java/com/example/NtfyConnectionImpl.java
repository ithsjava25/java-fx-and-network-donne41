package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();



    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        this.hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));

    }
    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public boolean send(String message) {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/donne41"))
                .build();
        try {
            var response = client.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            return true;
        } catch (IOException e) {
            System.out.println("Error sedning message");
        } catch (InterruptedException e) {
            System.out.println("Interruppted sending message2");
        }
        return false;
    }
    //TODO messageHandler har säker pajat funktionen.
    @Override
    public void receive(Consumer<transfereMessageDTO> messageHandler) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(hostName + "/donne41/json"))
                    .build();


            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                    .thenAccept(response -> response
                            .body()
                            .peek(System.out::println)
                            .map(s -> mapper.readValue(s, transfereMessageDTO.class))
                            .filter(message -> message.event().equals("message"))
                            .forEach(messageHandler));
        }

}
