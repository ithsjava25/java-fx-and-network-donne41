package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class HelloModelTest {


    @Test
    @DisplayName("When calling sendMessage should call connetion send")
    void sendMessageCallsConnectionWithMessageToSend(){
        //arrange  Given
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        //act   When
        model.sendMessage("Hello World");
        //assert    Then
        assertThat(spy.message).isEqualTo("Hello World");
    }

    @Test
    void sendMessageToFakeServer(WireMockRuntimeInfo wmRuntimeInfo){
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new HelloModel(con);
        stubFor(post("/donne41").willReturn(ok()));

        model.sendMessage("Hello World");

        //verify
        verify(postRequestedFor(WireMock.urlEqualTo("/donne41"))
                .withRequestBody(matching("Hello World")));
    }

}