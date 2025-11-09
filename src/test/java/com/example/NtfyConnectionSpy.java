package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    //detta är en Test Dubble.


    @Override
    public boolean sendImage(Path file) {
        return false;
    }

    @Override
    public String getChatRoom() {
        return "";
    }

    @Override
    public void setChatRoom(String chatRoom) {

    }

    @Override
    public void restartConnection() {

    }


    @Override
    public boolean send(String message) {
        this.message = message;
        return true;
    }

    @Override
    public void receive(Consumer<messageDTO> messageHandler) {

    }
}
