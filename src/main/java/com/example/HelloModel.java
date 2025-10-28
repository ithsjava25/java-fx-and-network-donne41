package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.*;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    Image noSmash;
    Image smash1;
    Image smash2;
    Image smash3;
    Image smash4;

    ObjectProperty<Image> left;
    ObjectProperty<Image> middle;
    ObjectProperty<Image> right;

    private IntegerProperty points;

    public HelloModel(){
        noSmash = new Image(getClass().getResource("/lantern.png").toExternalForm());
        smash1 = new Image(getClass().getResource("/lantern1.png").toExternalForm());
        smash2 = new Image(getClass().getResource("/lantern2.png").toExternalForm());
        smash3 = new Image(getClass().getResource("/lantern3.png").toExternalForm());
        smash4 = new Image(getClass().getResource("/lantern4.png").toExternalForm());
        left = new SimpleObjectProperty<Image>(noSmash);
        middle = new SimpleObjectProperty<Image>(noSmash);
        right = new SimpleObjectProperty<Image>(noSmash);

        points = new SimpleIntegerProperty(0);
    }

    public Image getLeft() {
        return left.get();
    }

    public ObjectProperty<Image> leftProperty() {
        return left;
    }

    public void setLeft(Image left) {
        this.left.set(left);
    }

    public Image getMiddle() {
        return middle.get();
    }

    public ObjectProperty<Image> middleProperty() {
        return middle;
    }

    public void setMiddle(Image middle) {
        this.middle.set(middle);
    }

    public Image getRight() {
        return right.get();
    }

    public ObjectProperty<Image> rightProperty() {
        return right;
    }

    public void setRight(Image right) {
        this.right.set(right);
    }

    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public Image getNoSmash(){
        return noSmash;
    }
    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Welcome to halloweenSmashGame " + javafxVersion + ", running on Java " + javaVersion + ".";
    }

    public void smash(int lantern) {
    //if model is with face, get point and return to no face model.
        if(lantern == 0 && getLeft() != noSmash){
            setPoints(getPoints()+1);
            setLeft(noSmash);
        }if(lantern == 1 && getMiddle() != noSmash){
            setPoints(getPoints()+1);
            setMiddle(noSmash);
        }if(lantern == 2 && getRight() != noSmash){
            setPoints(getPoints()+1);
            setRight(noSmash);
        }
    }

    public void changeImage(int lantern, int imageId){
        switch(lantern){
            case 0 -> setLeft( getImage(imageId));
            case 1 -> setMiddle( getImage(imageId));
            case 2 -> setRight( getImage(imageId));
        }
    }

    private Image getImage(int imageId) {
        return switch(imageId){
            case 0 -> smash1;
            case 1 -> smash2;
            case 2 -> smash3;
            case 3 -> smash4;
            default -> noSmash;
        };
    }
}
