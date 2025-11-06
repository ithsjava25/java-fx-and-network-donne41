package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public record messageDTO(String id, long time, String event, String topic,
                         String message,
                         Attachment attachment
)
{
    public record Attachment(
            String name,
            String type,
            Integer size,
            URL url)
    {}
}


