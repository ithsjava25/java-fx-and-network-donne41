package com.example;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    /**
 * Retrieves the current chat room identifier.
 *
 * @return the current chat room identifier
 */
String getChatRoom();
    /**
 * Set the current chat room identifier used by this connection.
 *
 * @param chatRoom the chat room identifier to use for subsequent send and receive operations
 */
void setChatRoom(String chatRoom);
    /**
 * Reinitializes the connection to the messaging service.
 *
 * This resets transient connection state so subsequent send and receive operations use a refreshed connection.
 */
void restartConnection();
    /**
 * Send a text message to the configured chat room.
 *
 * @param message the message text to send
 * @return the HTTP response containing the server's response body as a `String`
 */
CompletableFuture<HttpResponse<String>> send(String message);
    /**
 * Send an image file to the current chat room.
 *
 * @param file the filesystem path to the image file to send; must reference a readable image file
 * @return the HTTP response from the server containing the server's reply as a `String`
 */
CompletableFuture<HttpResponse<String>> sendImage(Path file);
    /**
 * Registers a handler to be invoked for each incoming message.
 *
 * @param messageHandler consumer called with a received {@code messageDTO} for processing
 */
void receive(Consumer<messageDTO> messageHandler);
}