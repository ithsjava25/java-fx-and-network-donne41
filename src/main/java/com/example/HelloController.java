package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private Label messageLabel;
    private Label messageSent;


    @FXML
    private ImageView backgroundImage;
    @FXML
    private VBox messageList;
    @FXML
    private TextField outgoingMessage;

    @FXML
    private void initialize() {

        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
    }

    public void sendButtonCliked(ActionEvent actionEvent) {
        Label message = new Label(outgoingMessage.getText());
        messageList.getChildren().add(message);
        outgoingMessage.clear();
    }
}
