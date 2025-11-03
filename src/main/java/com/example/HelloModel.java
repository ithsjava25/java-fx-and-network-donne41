package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
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

    private final ObservableList<transfereMessageDTO> messages = FXCollections.observableArrayList();

    private final String hostName;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();



    public HelloModel(){
    hostName = Dotenv.load().get("HOST_NAME");
        System.out.println(hostName);

    }

    public ObservableList<transfereMessageDTO> getMessages(){
        return messages;
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
        //send message to server. and http kilent.
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/donne41"))
                .build();
        try {
            var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("Error sedning message");
        } catch (InterruptedException e) {
            System.out.println("Error sedning message2");}

    }

    public void receiveMessage(){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/donne41/json"))
                .build();


        client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> response
                        .body()
                        .peek(System.out::println)
                        .map(s-> mapper.readValue(s, transfereMessageDTO.class))
                        .filter(message -> message.event().equals("message"))
                        .forEach(s->Platform.runLater(()->messages.add(s))));
    }
}

