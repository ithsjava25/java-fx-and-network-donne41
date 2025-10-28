package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();
    public VBox pictureVBox;
    Random random = new Random();
    @FXML
    private Label messageLabel;
    public Label dateAndTimeLable;
    public Button upDateButton;
    public Button backFlipButton;
    public VBox smallVboxContent;
    public BorderPane allContentWindow;
    private double rotation = 0;
    public ImageView pictureView;

    private Timeline timeline;
    private Timeline rotateTime;
    public int rotateSpeed = 20;
    public int rotationDegrees = 5;

    public HelloModel getModel(){
        return model;
    }




    @FXML
    private void initialize() {
        dateAndTimeLable.textProperty().bind(model.dateTimeProperty());
        allContentWindow.rotateProperty().bind(model.rotationProperty());


        //A[ dateAndTimeLable.textProperty() ].bind(B[ model.dateTimeProperty() ])



        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
//        if(dateAndTimeLable != null) {
//            dateAndTimeLable.setText(LocalDateTime.now()
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ ->{
            model.setDateTime(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    public void backFlip(){
        rotateTime = new Timeline(new KeyFrame(Duration.millis(rotateSpeed), event -> {
            model.setRotation(model.getRotation()+rotationDegrees);
        }));
        rotateTime.setCycleCount(360/rotationDegrees);
        rotateTime.play();
    }
    public void doAFlip(ActionEvent actionEvent) {
        backFlip();
    }
    public void updateButtonAction(ActionEvent actionEvent) {
        model.setRotation(0.0);
    }
    public void chaosButtonAction(ActionEvent actionEvent) {
        model.setRotation(random.nextDouble(1,361));
    }
}
