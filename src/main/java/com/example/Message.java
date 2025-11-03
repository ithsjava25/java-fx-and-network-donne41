package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String message;
    private String sender;
    private String timeStamp;
    private String id;
    private long time;
    private String event;


    public Message(String message, String sender){
        this.message = message;
        this.sender = sender;
        this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

}

