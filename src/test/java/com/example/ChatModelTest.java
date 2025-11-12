package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@WireMockTest
class ChatModelTest {

    @BeforeAll
    public static void initToolKit() {
        try {
            Platform.startup(() -> {
            });
        } catch (Exception e) {
            System.out.println("ToolKit got fucked");
        }
    }

    @Test
    @DisplayName("When calling sendMessage should call connetion send")
    void sendMessageCallsConnectionWithMessageToSend() {
        //arrange  Given
        var spy = new NtfyConnectionSpy();
        var model = new ChatModel(spy);
        //act   When
        model.sendMessage("Hello World");
        //assert    Then
        assertThat(spy.message).isEqualTo("Hello World");
    }

    @Test
    void sendMessageToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new ChatModel(con);
        stubFor(post("/donne41").willReturn(ok()));


        model.sendMessage("Hello World");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep was interrupted");
        }

        //verify
        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withRequestBody(matching("Hello World")));
    }

    @Test
    void sendImagetoFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        byte[] fakeBytes = new byte[]{0x23, 0x34, 0x56};
        Path tempImage = Files.createTempFile("fakeImage", ".jpg");
        Files.write(tempImage, fakeBytes);
        stubFor(post("/donne41").willReturn(okJson(
                """
                         {
                         "time": "200000",
                         "event": "message",
                         "message": "IncomingFile",
                         "attachment": {
                         "name": "fakeImage",
                         "type": "image/jpeg",
                         "url": "tempImage" }
                         }
                        """))); //dummy bytes

        var model = new ChatModel(con);


        model.sendImage(tempImage);


        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep was interrupted");
        }
        System.out.println("Test print wiremock" + WireMock.findAll(postRequestedFor(urlEqualTo("/donne41"))));
        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withHeader("Content-Type", equalTo("image/jpeg"))
                .withRequestBody(binaryEqualTo(Files.readAllBytes(tempImage))));
    }

    @Test
    void getTopicShouldReturnCurrentTopic(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort(), "notDefault");
        var model = new ChatModel(con);

        model.getTopic();

        assertThat(model.getTopic()).isEqualTo("/notDefault");
    }

    @Test
    void setNewTopicShouldReturnNewTopicFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new ChatModel(con);
        stubFor(post("/newTopic").willReturn(ok("changed topic")));

        model.setNewTopic("newTopic");
        model.sendMessage("test");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep was interrupted");
        }
        //assertThat(model.getTopic()).isEqualTo("/newTopic");
        verify(postRequestedFor(WireMock.urlEqualTo("/newTopic"))
                .withRequestBody(matching("test")));
    }

    @Test
    void newTopicCannotContainSpecialChars(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort(), "validtopic");
        var model = new ChatModel(con);

        String[] forbidden = {"!", "@", "#", "$", "¤", "%", "&", "(", ")", "=", "?", "*", "§", "½"};
        for (String symbol : forbidden) {
            String topic = "invalid" + symbol;
            model.setNewTopic(topic);
            assertThat(model.getTopic().equals("/validtopic")).isEqualTo(true);
        }
    }

    @Test
    void sentMessageShoudBeSameAsReveivedMessage(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        stubFor(get("/donne41/json").willReturn(aResponse()
                .withHeader("Content-type", "application/json")
                .withBody("{\"message\": \"hello world\", " +
                        "\"time\": \"2000000\", " +
                        "\"event\": \"message\"}")));
        var model = new ChatModel(con);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
//                .withRequestBody(matching("Hello should be same in recevied!")));
        //denna måste ha en riktigt server för att få svar från connection.receive
        assertThat(model.getMessages().getLast().message()).isEqualTo("hello world");
    }

}