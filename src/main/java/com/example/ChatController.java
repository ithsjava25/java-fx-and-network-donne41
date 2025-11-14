package com.example;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.*;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class ChatController {

    private final ChatModel model = new ChatModel(new NtfyConnectionImpl());

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
    @FXML
    private Button sendImage;
    @FXML
    private Button settingsButton;


    MenuItem settingsBackground;
    MenuItem settingsTheme;
    TextField chatRoomInput;
    BackgroundSize backgroundSize;
    LocalDateTime systemTimeSent = LocalDateTime.now();

    @FXML
    private void initialize() {
        setupSettingsButton();
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

    private void setNewBackground(Image image) {
        backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        root.setBackground(background);
    }

    private void setupBindinger() {
        messageArea.vvalueProperty().bind(messageList.heightProperty());
        currentTopic.textProperty().bind(model.newTopicProperty());

    }


    private void setupSettingsButton() {
        ContextMenu contextMenu = new ContextMenu();
        settingsBackground = new MenuItem("Background");
        settingsTheme = new MenuItem("Theme");
        chatRoomInput = new TextField("Set new chatroom topic");
        CustomMenuItem settingsChatRoom = new CustomMenuItem(chatRoomInput, false);
        contextMenu.getItems().addAll(settingsBackground, settingsTheme, settingsChatRoom);
        settingsButton.setOnMouseClicked(event -> {
            System.out.println("settings img press!");
            contextMenu.show(settingsButton, event.getScreenX(), event.getScreenY());
            chatRoomInput.clear();
            chatRoomInput.setText("Set new chatroom topic");
        });


    }


    private void setupListerners() {
        model.getMessages().addListener((ListChangeListener.Change<? extends messageDTO> c) -> {
            Platform.runLater(() -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        var addedMessage = c.getAddedSubList().getFirst();
                        var parsedTime = LocalDateTime.ofInstant(addedMessage.time(), ZoneId.of("Europe/Stockholm"));
                        long secondsDiff = Math.abs(Duration.between(systemTimeSent, parsedTime).getSeconds());
                        if (addedMessage.attachment() != null) {
                                 if(secondsDiff < 4) {
                                makeImageMessageBox(addedMessage, true, parsedTime);
                            } else {
                                makeImageMessageBox(addedMessage, false, parsedTime);
                            }
                        } else {
                            if (addedMessage.message().matches("^chatAholic.*")) {
                                makeNewMessageBox(addedMessage, true, parsedTime);
                            } else {
                                makeNewMessageBox(addedMessage, false, parsedTime);
                            }
                        }
                    }
                }
            });
        });
        chatRoomInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("^(?!/).*[\\s\\W]+.*")) {
                chatRoomInput.setStyle("-fx-border-color: red;");
            } else {
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
            if (!newTopic.isEmpty()) {
                try {
                    model.setNewTopic(newTopic);
                }catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        outgoingMessage.setOnMouseClicked(event -> {
            outgoingMessage.clear();
        });
        outgoingMessage.setOnDragDropped(this::sendImageDropped);
        outgoingMessage.setOnDragOver(event -> {
            if (event.getGestureSource() != outgoingMessage && event.getDragboard().hasFiles()) {
                boolean hasImage = event.getDragboard().getFiles().stream()
                        .anyMatch(file -> file.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)$"));
                if (hasImage) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

    }

    private void setBackgroundImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Resource File");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image files png,jpg,bmp", "*.jpg", "*.png", "*.bmp")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            setNewBackground(new Image(file.toURI().toString()));
        }
    }

    public void setTheme() {
        URL themeCss = getClass().getResource("/css/style.css");
        if (themeCss != null) {
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
     * Will not send if empty textfield.
     *
     * @param actionEvent Enter button
     */
    public void enterButtonSend(ActionEvent actionEvent) {
        String text = outgoingMessage.getText().trim();
        if (text.isEmpty()) {
            outgoingMessage.clear();
            return;
        }
        if(text.equals("getTopic")){
            System.out.println(model.getTopic());
        }
        model.sendMessage("chatAholic: " + text);
        outgoingMessage.requestFocus();

    }

    private void presentMessageSent(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    private void presentMessageReceived(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    private void makeNewMessageBox(messageDTO message, boolean isSent, LocalDateTime timeStamp) {
        HBox msgContainer = new HBox();
        msgContainer.setId("msgContainer");
        BorderPane messageBox = new BorderPane();
        VBox messageContent = new VBox();
        messageBox.setCenter(messageContent);
        HBox timeStampBox = new HBox(new Text((timeStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        Region spacer = new Region();
        spacer.setPadding(new Insets(2,2,2,2));
        HBox topBox = new HBox();
        topBox.setId("messageLabel");
        messageBox.setId("messageBox");
        if (isSent) {
            String textMsg = message.message();
            String sender = textMsg.substring(0, 10);
            String text = textMsg.substring(11);
            messageContent.getChildren().add(new Text(text));
            HBox senderTextBox = new HBox(new VBox(new Text(sender)));
            topBox.getChildren().addAll(senderTextBox, spacer, timeStampBox);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            messageBox.setTop(topBox);
            msgContainer.getChildren().add(messageBox);
            presentMessageSent(msgContainer);
        } else {
            messageContent.getChildren().add(new Text(message.message()));
            HBox senderTextBox = new HBox(new VBox(new Text("Web User: ")));
            topBox.getChildren().addAll(timeStampBox, spacer, senderTextBox);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            messageBox.setTop(topBox);
            msgContainer.getChildren().add(messageBox);
            presentMessageReceived(msgContainer);
        }

    }

    public void sendImageDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasImage() || dragboard.hasFiles()) {
            try {
                model.sendImage(dragboard.getFiles().getFirst().toPath());
                systemTimeSent = LocalDateTime.now();
            } catch (Exception e) {
                System.out.println("Error sending file: " + e.getMessage());
            }
        }
        event.consume();
    }


    public void sendImage(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload Image");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter(
                        "Image files", "*.jpg", "*.png", "*.gif", "*.bmp"
                )
        );
        File chosenFile = chooser.showOpenDialog(null);
        if (chosenFile != null) {
            systemTimeSent = LocalDateTime.now();
            model.sendImage(chosenFile.toPath());

        }
    }

    public void presentImageReceived(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
    }

    public void presentImageSent(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
    }

    public void makeImageMessageBox(messageDTO message, boolean isSent, LocalDateTime timeStamp) {
        String messUrl = message.attachment().url().toString();
        String attachmentName = message.attachment().name();
        HBox msgContainer = new HBox();
        msgContainer.setId("msgContainer");
        ImageView incImage = new ImageView(new Image(messUrl));
        incImage.setPreserveRatio(true);
        incImage.setFitHeight(720);
        incImage.setFitWidth(720);
        AnchorPane imagePlace = new AnchorPane(incImage);
        if (isSent) {
            TitledPane messageBox = new TitledPane("You sent file: " + attachmentName + " " + (timeStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))), imagePlace);
            msgContainer.getChildren().add(messageBox);
            presentImageSent(msgContainer);

        } else {
            TitledPane messageBox = new TitledPane("You received file: " + attachmentName + " " + (timeStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))), imagePlace);
            presentImageReceived(msgContainer);
            msgContainer.getChildren().add(messageBox);

        }
    }

}
