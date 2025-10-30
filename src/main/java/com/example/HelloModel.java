package com.example;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    Image background;
    Label message;
    VBox messageList;
    ScrollPane messageScroll;

    ObjectProperty<Label> messageProp; //might do send and recived
    ObjectProperty<VBox> messageListProp;
    ObjectProperty<ScrollPane> scrollPaneProp;

    public HelloModel(){
        messageProp = new SimpleObjectProperty<Label>();
        messageListProp = new SimpleObjectProperty<>();
        scrollPaneProp = new SimpleObjectProperty<>();

    }

    public ScrollPane getMessageScroll() {
        return messageScroll;
    }
    public ReadOnlyDoubleProperty messageListPropHight(){
        return messageListProp.get().heightProperty();
    }

    public void setMessageScroll(ScrollPane messageScroll) {
        this.messageScroll = messageScroll;
    }

    public ScrollPane getScrollPaneProp() {
        if(scrollPaneProp == null) {
            scrollPaneProp = new SimpleObjectProperty<>();
        }
        return scrollPaneProp.get();
    }

    public ObjectProperty<ScrollPane> scrollPanePropProperty() {
        return scrollPaneProp;
    }

    public void setScrollPaneProp(ScrollPane scrollPaneProp) {
        this.scrollPaneProp.set(scrollPaneProp);
    }

    public VBox getMessageList() {
        return messageList;
    }

    public void setMessageList(VBox messageList) {
        this.messageList = messageList;
    }

    public VBox getMessageListProp() {
        return messageListProp.get();
    }

    public ObjectProperty<VBox> messageListPropProperty() {
        return messageListProp;
    }

    public void setMessageListProp(VBox messageListProp) {
        this.messageListProp.set(messageListProp);
    }

    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
    }
}
