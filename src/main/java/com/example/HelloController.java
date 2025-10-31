package com.example;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.regex.MatchResult;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private Label messageLabel;
    @FXML
    private HBox messageSent;
    @FXML
    private HBox messageRecived;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private VBox messageList;
    @FXML
    private ScrollPane messageScroll;
    @FXML
    private TextField outgoingMessage;

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
        connectListerners();
    }


    private void connectListerners() {
        model.getMessages().addListener((ListChangeListener.Change<? extends Message> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Message message : c.getAddedSubList()) {
                        addMessageToView(message);
                    }
                }
            }
        })
    ;}
    public void addMessageToView(Message message) {
        Label messageLabel = new Label(message.getMessage());
    }


    /**
     * Simulate recived messenges. Suppost  to be on the left side
     *
     * @param actionEvent Send button.
     */
    public void sendButtonCliked(ActionEvent actionEvent) {
        String text = outgoingMessage.getText().trim();
        if(text.isEmpty()) {
            outgoingMessage.clear();
            return;
        }
        makeNewIncomingMsg(text);
    }
    /**
     * Simulate sent messenges. Suppost to be on the right side.
     *
     * @param actionEvent Enter button
     */
    public void enterButtonSend(ActionEvent actionEvent) {
        String text = outgoingMessage.getText().trim();
        if(text.isEmpty()) {
            outgoingMessage.clear();
            return;
        }
        makeNewSentMsg(text);
    }

    private void makeNewIncomingMsg(String text) {
        model.addMessage(text, "IncomingMsg");
        HBox msgContainer = new HBox();
        BorderPane messageBox = new BorderPane(new ScrollPane(new Text(model.getMessages().getLast().getMessage())));
        messageBox.getCenter().setId("messagetextId");
        HBox timeStampBox = new HBox(new VBox(new Text(model.getMessages().getLast().getTimeStamp())));
        HBox senderTextBox = new HBox(new VBox(new Text(model.getMessages().getLast().getSender())));
        HBox topBox = new HBox();
        topBox.getChildren().addAll(timeStampBox, senderTextBox);
        HBox.setHgrow(timeStampBox, Priority.ALWAYS);
        messageBox.setTop(topBox);
        messageBox.getTop().setId("messageLabel");
        msgContainer.getChildren().add(messageBox);
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }
    private void makeNewSentMsg(String text) {
        model.addMessage(text, "SentMsg");
        HBox msgContainer = new HBox();
        BorderPane messageBox = new BorderPane(new ScrollPane(new Text(model.getMessages().getLast().getMessage())));
        messageBox.getCenter().setId("messagetextId");
        HBox timeStampBox = new HBox(new VBox(new Text(model.getMessages().getLast().getTimeStamp())));
        HBox senderTextBox = new HBox(new VBox(new Text(model.getMessages().getLast().getSender())));
        HBox topBox = new HBox();
        topBox.getChildren().addAll(senderTextBox, timeStampBox);
        HBox.setHgrow(senderTextBox, Priority.ALWAYS);
        messageBox.setTop(topBox);
        messageBox.getTop().setId("messageLabel");
        msgContainer.getChildren().add(messageBox);
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }




}
