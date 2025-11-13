package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String id;
    private long time;
    private String event;
    private String topic;
    private String message;
    private String type;


    public Message(String message, String sender) {
        this.message = message;
    }


}


