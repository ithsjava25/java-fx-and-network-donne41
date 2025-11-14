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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.*;

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
        setupBindings();
    }

    /**
     * Applies the default background image to the root layout container.
     * <p>
     * This method loads the predefined background image resource
     * ({@code /natureBackground.jpg}), creates a {@link BackgroundImage}
     * with proportional scaling.
     **/
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

    /**
     * Applies the image to the root layout container.
     * @param image is proportional with {@link #backgroundSize} set in {@link #setupBackground()}
     */
    private void setNewBackground(Image image) {
        backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        root.setBackground(background);
    }

    /**
     * Sets binding for message scrollPane to auto scroll when messages are more than screen size.
     * Visual que for what chatRoom is currently connected to.
     */
    private void setupBindings() {
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

    /**
     * Starts fileChooser to send image to {@link #setNewBackground(Image)}
     * and sets that as new background.
     */
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

    /**
     * Applies the default css to the root container.
     * <p>
     * sets the current css file in {@code /css/style.css} as stylesheet
     */
    public void setTheme() {
        URL themeCss = getClass().getResource("/css/style.css");
        if (themeCss != null) {
            root.getStylesheets().add(themeCss.toExternalForm());
        }
    }

    /**
     * Applies a new css file to the root container.
     */
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
     * Send messages to NtfyConnection server.
     * Will not send if empty text field.
     *<p>
     * Adds programs name in the beginning of the message to acknowledge who sent it.
     * @param actionEvent Enter button
     */
    public void enterButtonSend(ActionEvent actionEvent) {
        String text = outgoingMessage.getText().trim();
        if (text.isEmpty()) {
            outgoingMessage.clear();
            return;
        }
        model.sendMessage("chatAholic: " + text);
        outgoingMessage.requestFocus();

    }

    /**
     * Applies the alignment of msgContainer to the right
     * @param msgContainer from {@link #makeImageMessageBox}
     */
    private void presentMessageSent(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    /**
     * Applies the alignment of msgContainer to the left
     * @param msgContainer from {@link #makeImageMessageBox}
     */
    private void presentMessageReceived(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    /**
     * Presents messages by making new chatboxes from base of Borderpane.
     * Messages are presented in the center of the borderPane while
     * the sender and timestamp are presented in the topbox.
     * @param message are set in the center.
     * @param isSent ifSent is true program name is filtered out.
     * @param timeStamp parsed as Locale of Europe/Stockholm.
     */
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

    /**
     * Present images by making new TiledPane
     * @param message is default set to You sent or received file with file name.
     * @param isSent is true sets the pane alignment to the right.
     * @param timeStamp parsed as Locale of Europe/Stockholm.
     */
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
