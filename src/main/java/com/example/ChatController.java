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

    /**
     * Initializes the controller after FXML loading by configuring UI behavior and appearance.
     *
     * Performs setup of the settings button and its menu, applies the background image and CSS theme,
     * and registers UI listeners and property bindings required for runtime interaction.
     */
    @FXML
    private void initialize() {
        setupSettingsButton();
        setupBackground();
        setTheme();
        setupListerners();
        setupBindings();
    }

    /**
     * Set the controller's root background to the default nature image.
     *
     * Loads the bundled resource "/natureBackground.jpg" and applies it to the root
     * container with proportional scaling and centered positioning.
     */
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
     * Sets the root layout's background to the provided image, scaled using the controller's backgroundSize.
     *
     * @param image the image to apply; it will be scaled proportionally using {@link #backgroundSize}
     */
    private void setNewBackground(Image image) {
        backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        root.setBackground(background);
    }

    /**
     * Keep the message view scrolled to the newest message and display the model's current chat topic.
     *
     * Binds the message area's vertical scroll value to the message list height to maintain automatic scrolling,
     * and binds the topic label text to the model's new topic property.
     */
    private void setupBindings() {
        messageArea.vvalueProperty().bind(messageList.heightProperty());
        currentTopic.textProperty().bind(model.newTopicProperty());

    }


    /**
     * Configure the settings button to display a context menu for selecting background, theme,
     * and entering a new chat room topic.
     *
     * <p>Initializes the controller fields {@code settingsBackground}, {@code settingsTheme}, and
     * {@code chatRoomInput}, creates a ContextMenu containing those menu items (the chat room input
     * is wrapped in a {@code CustomMenuItem}), and attaches a mouse-click handler that shows the
     * menu at the click location and resets the chat room input text.</p>
     */
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


    /**
     * Registers all UI and model listeners used by the controller.
     *
     * <p>Wires the message-listener to render newly added messages (text or image) into the chat view,
     * deciding whether a message is presented as "sent" or "received" based on the message content
     * (text prefix or presence of an attachment) and the elapsed time since the controller's last
     * recorded outbound image send time. Also attaches listeners that validate and submit the chat-room
     * topic, clear inputs on click, open background/theme pickers, and handle drag-and-drop and click
     * image-send interactions for the outgoing message field.</p>
     */
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
     * Opens a file chooser to select a JPG, PNG, or BMP image and sets the selected image as the UI background.
     *
     * If no file is selected, the background remains unchanged.
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
     * Apply the default stylesheet to the controller's root container.
     *
     * Loads the resource "/css/style.css" from the classpath and adds it to the root node's stylesheets if present.
     */
    public void setTheme() {
        URL themeCss = getClass().getResource("/css/style.css");
        if (themeCss != null) {
            root.getStylesheets().add(themeCss.toExternalForm());
        }
    }

    /**
     * Opens a file chooser to select a CSS stylesheet and applies the chosen stylesheet to the root container.
     *
     * If a file is selected, existing stylesheets are cleared and the selected CSS is added to the root.
     * If no file is selected, a message "No file selected" is printed to standard output.
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
     * Send the current text in the outgoing message field to the server with the "chatAholic" sender prefix.
     *
     * Trims whitespace and does nothing if the trimmed text is empty. After sending, refocuses the outgoing message field.
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
     * Aligns a sent message container to the right, adds it to the message list, and clears the outgoing message input.
     *
     * @param msgContainer the HBox containing the message to present (will be aligned to the top-right and added to the message list)
     */
    private void presentMessageSent(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    /**
     * Aligns the given message container to the top-left, adds it to the chat message list, and clears the outgoing message field.
     *
     * @param msgContainer the message HBox to display in the chat message list
     */
    private void presentMessageReceived(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
        outgoingMessage.clear();
    }

    /**
     * Creates a chat message UI block and adds it to the message list, aligning sent messages to the right and received messages to the left.
     *
     * The message block displays the message text together with a sender label and a timestamp (formatted HH:mm:ss). When `isSent` is true the sender information is taken from the message payload; when false a generic "Web User" label is shown.
     *
     * @param message  DTO containing the message text and attachment metadata
     * @param isSent   true if the message originated locally (affects alignment and sender label), false if received
     * @param timeStamp  timestamp to display with the message (formatted as HH:mm:ss)
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

    /**
     * Sends the first dragged image or file to the model if the dragboard contains image data or files.
     *
     * @param event the DragEvent whose dragboard contains the dragged image or files; the event is consumed by this handler
     */
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


    /**
     * Opens a file chooser restricted to common image formats and, if the user selects a file,
     * records the send time and sends the image via the model.
     *
     * If no file is selected the method returns without action.
     */
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

    /**
     * Adds an image message container to the chat and aligns it to the left.
     *
     * @param msgContainer the HBox containing the image message; it will be aligned to the top-left and appended to the message list
     */
    public void presentImageReceived(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_LEFT);
        messageList.getChildren().add(msgContainer);
    }

    /**
     * Adds the given image message container to the chat message list aligned to the top-right.
     *
     * @param msgContainer the HBox containing the image message UI to present as sent by the local user
     */
    public void presentImageSent(HBox msgContainer) {
        msgContainer.setAlignment(Pos.TOP_RIGHT);
        messageList.getChildren().add(msgContainer);
    }

    /**
     * Display an image message in the chat with a timestamped title and appropriate alignment.
     *
     * The method extracts the attachment URL and name from the provided message, creates a titled
     * image tile labeled with the attachment name and the given timestamp formatted as HH:mm:ss
     * (Europe/Stockholm), and adds it to the message list aligned to the right for sent messages
     * or to the left for received messages.
     *
     * @param message   the message DTO whose attachment provides the image URL and attachment name
     * @param isSent    true to present the message as sent (right-aligned and labeled "You sent file"), false to present as received (left-aligned and labeled "You received file")
     * @param timeStamp the timestamp to display in the title; formatted as "HH:mm:ss" in the Europe/Stockholm locale
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