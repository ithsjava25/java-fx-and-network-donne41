package com.example;

import java.util.function.Consumer;

public interface NtfyConnection {

    String getChatRoom();
    void setChatRoom(String chatRoom);
    void restartConnection();
    boolean send(String message);
    void receive(Consumer<transfereMessageDTO> messageHandler);
}
