package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface NtfyConnection {

    String getChatRoom();
    void setChatRoom(String chatRoom);
    void restartConnection();
    boolean send(String message);
    boolean sendImage(Path file);
    void receive(Consumer<messageDTO> messageHandler);
}
