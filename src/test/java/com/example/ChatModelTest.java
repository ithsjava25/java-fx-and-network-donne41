package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class ChatModelTest {


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

        //verify
        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withRequestBody(matching("Hello World")));
    }

    @Test
    void sendImagetoFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new ChatModel(con);
        stubFor(post("/donne41").willReturn(ok("File received")));


        Path tempFile = Files.createTempFile("TestImage", ".jpg");
        Files.writeString(tempFile, "fake Image");

        model.sendImage(tempFile);

        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withRequestBody(matching("fake Image")));
    }
    @Test
    void setNewTopicShouldReturnNewTopicFakeServer(WireMockRuntimeInfo wmRuntimeInfo){
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new ChatModel(con);
        stubFor(post("/newTopic").willReturn(ok("changed topic")));

        model.setNewTopic("newTopic");
        model.sendMessage("test");

        //assertThat(model.getTopic()).isEqualTo("/newTopic");
        verify(postRequestedFor(WireMock.urlEqualTo("/newTopic"))
                .withRequestBody(matching("test")));
    }
    @Test
    void sentMessageShoudBeSameAsReveivedMessage(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new ChatModel(con);
        stubFor(post("/donne41").willReturn(ok("Something here")));

        model.sendMessage("Hello should be same in recevied!");

//        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
//                .withRequestBody(matching("Hello should be same in recevied!")));
        //denna måste ha en riktigt server för att få svar från connection.receive
        assertThat(model.getMessages().getLast().message()).isEqualTo("Hello should be same in recevied!");
    }

}