package com.example;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.*;
import java.io.File;
import java.net.URL;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnectionImpl());

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
    private Text currentTopic;
    @FXML
    private Circle connectionStatus;
    @FXML
    private ImageView settingsImage;
    @FXML
    private HBox topHbox;
    private MenuButton settingsButton;
    MenuItem settingsBackground;
    MenuItem settingsTheme;
    TextField chatRoomInput;
    BackgroundSize backgroundSize;

    @FXML
    private void initialize() {
        setupMenuImage();
        setupBackground();
        setTheme();
        setupListerners();
        setupBindinger();
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
        currentTopic.textProperty().bind(model.newTopicProperty());

    }


    private void setupMenuImage(){
        ImageView settingsDots = new ImageView(new Image(getClass()
                .getResource("/threeDotsSettings.png").toExternalForm()));
        settingsDots.setFitHeight(30);
        settingsDots.setFitWidth(10);
        ContextMenu contextMenu = new ContextMenu();
        settingsBackground = new MenuItem("Background");
        settingsTheme = new MenuItem("Theme");
        chatRoomInput = new TextField("Set new chatroom topic");
        CustomMenuItem settingsChatRoom = new CustomMenuItem(chatRoomInput, false);
        contextMenu.getItems().addAll(settingsBackground, settingsTheme, settingsChatRoom);
        settingsImage = settingsDots;
        topHbox.getChildren().add(settingsDots);
        settingsDots.setOnMouseClicked(event -> {
            contextMenu.show(settingsImage, event.getScreenX(), event.getScreenY());
            chatRoomInput.clear();
            chatRoomInput.setText("Set new chatroom topic");
        });


    }


    private void setupListerners() {
        model.getMessages().addListener((ListChangeListener.Change<? extends transfereMessageDTO> c) -> {
            Platform.runLater(() -> {
                System.out.println("Message list change detected!");
                while(c.next()){
                    if(c.wasAdded()){
                        var addedMessage =  c.getAddedSubList().getFirst();
                        if(addedMessage.message().matches("^chatAholic.*")){
                            makeNewSentMsg(addedMessage);
                        }else {
                            makeNewIncomingMsg(addedMessage);
                        }
                    }
                }
            });
        });
        chatRoomInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches(".*\\s+.*")){
                chatRoomInput.setStyle("-fx-border-color: red;");
            }else{
                chatRoomInput.setStyle("-fx-border-color: green;");
            }
        });
        settingsBackground.setOnAction(e -> {
            setBackgroundImage();
        });

        settingsTheme.setOnAction(e -> {
            changeTheme();
        });
        chatRoomInput.setOnMouseClicked(event -> {
            chatRoomInput.clear();

        });
        chatRoomInput.setOnAction(e -> {
            String newTopic = chatRoomInput.getText();
            if(!newTopic.isEmpty() && !newTopic.matches(".*\\s+.*")){
                model.setNewTopic(newTopic);
            }
        });
        outgoingMessage.setOnMouseClicked(event -> {
            outgoingMessage.clear();
        });

    }

private void setBackgroundImage() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Resource File");
    chooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image files png,jpg,bmp", "*.jpg", "*.png", "*.bmp")
    );
    File file = chooser.showOpenDialog(null);
    if(file != null){
        setNewBackground(new Image(file.toURI().toString()));
        }
    }
    public void setTheme(){
        URL themeCss = getClass().getResource("/css/style.css");
        System.out.println(themeCss);
        if(themeCss != null) {
            root.getStylesheets().add(themeCss.toExternalForm());
        }
    }
    public void changeTheme() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload new Stylesheet");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("Stylesheet", "*.css"));
        File chosenFile = chooser.showOpenDialog(null);
        if (chosenFile != null) {
            String cssPath = chosenFile.toURI().toString();
            root.getStylesheets().clear();
            root.getStylesheets().add(cssPath);
        } else {
            System.out.println("No file selected");
        }
    }


/**
 * Send messenges. Suppost to be on the right side.
 * Wont send if empty textfield.
 * @param actionEvent Enter button
 */
public void enterButtonSend(ActionEvent actionEvent) {
    String text = outgoingMessage.getText().trim();
    System.out.println("Enter press from messageField!");
    if (text.isEmpty()) {
        outgoingMessage.clear();
        return;
    }
    model.sendMessage("chatAholic: " + text);
    outgoingMessage.requestFocus();

}
private void makeNewIncomingMsg(transfereMessageDTO message) {
//    String textMsg = message.message();
//    String sender = textMsg.substring(0,9);
//    String text = textMsg.substring(10);
    HBox msgContainer = new HBox();
    msgContainer.setId("msgContainer");
    BorderPane messageBox = new BorderPane(new ScrollPane(new Text(message.message())));
    messageBox.getCenter().setId("messagetextId");
    HBox timeStampBox = new HBox(new VBox(message.time()));
    HBox senderTextBox = new HBox(new VBox(new Text("Web User: ")));
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

private void makeNewSentMsg(transfereMessageDTO message) {
    String textMsg = message.message();
    String sender = textMsg.substring(0,10);
    String text = textMsg.substring(11);
    HBox msgContainer = new HBox();
    BorderPane messageBox = new BorderPane(new ScrollPane(new Text(text)));
    messageBox.getCenter().setId("messagetextId");
    HBox timeStampBox = new HBox(new VBox(message.time()));
    HBox senderTextBox = new HBox(new VBox(new Text(sender)));
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

    //TODO Drag and drop image.
    // make new image messagebox.
    // Blinka vid mottaget meddelande.
    public void sendImage(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        System.out.println(dragboard);
        if (dragboard.hasFiles()) {
            System.out.println(dragboard.getImage());

        }
    }

}
