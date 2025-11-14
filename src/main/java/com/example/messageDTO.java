package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URL;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record messageDTO(String id, Instant time, String event, String topic,
                         String message,
                         String sender,
                         Attachment attachment
)
{
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attachment(
            String name,
            String type,
            Integer size,
            URL url)
    {}
}


