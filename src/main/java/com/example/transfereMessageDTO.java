package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record transfereMessageDTO(String id, long time, String event, String topic, String message) {
}
