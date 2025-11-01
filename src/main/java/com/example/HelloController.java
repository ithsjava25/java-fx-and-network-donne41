package com.example;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;

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
    private BorderPane root;
    @FXML
    private BackgroundImage backgroundImage;
    @FXML
    private VBox messageList;
    @FXML
    private ScrollPane messageArea;
    @FXML
    private TextField outgoingMessage;
    @FXML
    private HBox topRightBox;
    private MenuButton settingsButton;
    MenuItem settingsBackground;
    MenuItem settingsFontSize;
    BackgroundSize backgroundSize;

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
        setupMenuButton();
        setupListerners();
        setupBindinger();
        setupBackground();
    }

    private void setupBackground() {
        Image startImage = new Image(getClass().getResource("/natureBackground.jpg").toExternalForm());
        backgroundSize = new BackgroundSize(
                100, 100,
                true, true,
                false, true
        );
        backgroundImage = new BackgroundImage(startImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background startBackground = new Background(backgroundImage);
        root.setBackground(startBackground);
    }
    private void setNewBackground(Image image){
        backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        root.setBackground(background);
    }

    private void setupBindinger() {
        messageArea.vvalueProperty().bind(messageList.heightProperty());

    }

    private void setupMenuButton() {
        settingsBackground = new MenuItem("Background");
        settingsFontSize = new MenuItem("Font size");
        ImageView menuIcon = new ImageView();
        Image menuPic = new Image(getClass().getResource("/attachment.png").toExternalForm());
        menuIcon.setFitHeight(15);
        menuIcon.setFitWidth(15);
        menuIcon.setImage(menuPic);
        settingsButton = new MenuButton("", menuIcon, settingsBackground, settingsFontSize);
        topRightBox.getChildren().add(settingsButton);
    }


    private void setupListerners() {
        model.getMessages().addListener((ListChangeListener.Change<? extends Message> c) -> {
            Platform.runLater(() -> {
                outgoingMessage.requestFocus();
            });
        });
        settingsBackground.setOnAction(e -> {
            setBackgroundImage();
        });

        settingsFontSize.setOnAction(e -> {
            System.out.println("Setting font size might be complicated actually");
        });
    }

//TODO skicka endast bilder vidare till setBackground().
private void setBackgroundImage() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Resource File");
    File file = chooser.showOpenDialog(null);
    System.out.println(file);
}

//    public void addMessageToView(Message message) {
//        Label messageLabel = new Label(message.getMessage());
//    }


/**
 * Simulate recived messenges. Suppost  to be on the left side
 *
 * @param actionEvent Send button.
 */
public void sendButtonCliked(ActionEvent actionEvent) {
    String text = outgoingMessage.getText().trim();
    if (text.isEmpty()) {
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
    if (text.isEmpty()) {
        outgoingMessage.clear();
        return;
    }
    makeNewSentMsg(text);
}

private void makeNewIncomingMsg(String text) {
    model.addMessage(text, "IncomingMsg");
    HBox msgContainer = new HBox();
    msgContainer.setId("msgContainer");
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
