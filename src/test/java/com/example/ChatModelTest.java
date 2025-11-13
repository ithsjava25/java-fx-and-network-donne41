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
        stubFor(post("/donne41").willReturn(ok()));
        var model = new ChatModel(con);

        var response=  model.sendMessage("Hello World");
        try {
            System.out.println(response.get().statusCode());
        }catch (Exception e){
            System.out.println("Error sending from TestModel");
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


        var response = model.sendImage(tempImage);


        try {
            response.get().statusCode();
        } catch (Exception e) {
            System.out.println("Error sending Image");
        }
        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withRequestBody(binaryEqualTo(Files.readAllBytes(tempImage))));
    }

    @Test
    void getTopicShouldReturnCurrentTopic(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort(), "notDefault");
        stubFor(get("/notDefault/json").willReturn(ok()));
        var model = new ChatModel(con);

        model.getTopic();

        assertThat(model.getTopic()).isEqualTo("/notDefault");
    }

    @Test
    void setNewTopicShouldReturnNewTopicFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        stubFor(post("/newTopic").willReturn(ok("changed topic")));
        var model = new ChatModel(con);

        model.setNewTopic("newTopic");
        var response = model.sendMessage("test");
        try {
            response.get().statusCode();
        } catch (Exception e) {
            System.out.println("Something went wrong");
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



}