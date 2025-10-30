package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
    private TextField outgoingMessage;

    @FXML
    private void initialize() {
        model.getScrollPaneProp().vvalueProperty().bind(model.messageListPropHight());
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
    }

    public void sendButtonCliked(ActionEvent actionEvent) {
        Label message = new Label(outgoingMessage.getText());
        HBox container = new HBox(message);
        container.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(container);
        outgoingMessage.clear();
    }
    public void enterButtonSend(ActionEvent actionEvent) {
        Label message = new Label(outgoingMessage.getText());
        HBox container = new HBox(message);
        container.setAlignment(Pos.TOP_RIGHT);
        container.setFillHeight(false);
        messageList.getChildren().add(container);
        outgoingMessage.clear();
    }
}
