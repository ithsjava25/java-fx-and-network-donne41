package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private Label messageLabel;
    public Label dateAndTimeLable;
    public Button upDateButton;
    public VBox smallVboxContent;
    private double rotation = 0;

    private Timeline timeline;

    public HelloModel getModel(){
        return model;
    }

    @FXML
    private void initialize() {
        dateAndTimeLable.textProperty().bind(model.dateTimeProperty());
        //A[ dateAndTimeLable.textProperty() ].bind(B[ model.dateTimeProperty() ])
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
//        if(dateAndTimeLable != null) {
//            dateAndTimeLable.setText(LocalDateTime.now()
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(2), _ ->{
            model.setDateTime(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void updateButtonAction(ActionEvent actionEvent) {
        model.setDateTime(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        rotation += 27;
        smallVboxContent.setRotate(rotation);
    }
}
