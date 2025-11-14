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


    /**
     * Create a Message initialized with the provided message text.
     *
     * The constructor sets the instance's `message` field to the provided text.
     * Other fields remain unset. The `sender` parameter is accepted but not used.
     *
     * @param message the message text to store in this Message
     * @param sender  the sender identifier (currently ignored)
     */
    public Message(String message, String sender) {
        this.message = message;
    }


}

