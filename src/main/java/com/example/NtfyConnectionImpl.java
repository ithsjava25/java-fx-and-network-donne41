package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
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
    public void newClient(){
         client = HttpClient.newHttpClient();
    }
    public void setChatRoom(String chatroom){
        if(chatroom.matches("^/.*")){
            this.chatRoom = chatroom;
        }else {
            this.chatRoom = "/"+chatroom;
        }
    }
    public String getChatRoom(){
        return chatRoom;
    }

    public void restartConnection(){
        client.shutdownNow();
        newClient();
    }

    @Override
    public boolean send(String message) {
        System.out.println("NtfyConn.send, Hostname, chatRoom: " + hostName+ chatRoom);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + chatRoom))
                .build();
        try {
            var response = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding());
            System.out.println(response);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Falty argument while sending message "+ e.getMessage());
        }
        return false;
    }
    //TODO messageHandler har säker pajat funktionen.
    @Override
    public void receive(Consumer<transfereMessageDTO> messageHandler) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(hostName + chatRoom+"/json"))
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
