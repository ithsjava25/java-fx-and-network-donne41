package com.example;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private StringProperty dateTimeProperty;
    private DoubleProperty rotationProperty;
    private ObjectProperty<Image> imageProperty;

    public HelloModel(){
        dateTimeProperty = new SimpleStringProperty();
        rotationProperty = new SimpleDoubleProperty();
        imageProperty = new SimpleObjectProperty<>();

        setImageProperty(new Image("/troll-face.png"));
    }
    public void setDateTime(String dateTime){
        dateTimeProperty.set(dateTime);
    }
    public String getDateTime(){
        return dateTimeProperty.get();
    }
    public StringProperty dateTimeProperty(){
        return dateTimeProperty;
    }
    public void setRotation(double degrees){
        rotationProperty.set(degrees);
    }
    public double getRotation(){
        return rotationProperty.get();
    }
    public DoubleProperty rotationProperty(){
        return rotationProperty;
    }

    public Image getImageProperty() {
        return imageProperty.get();
    }

    public ObjectProperty<Image> imagePropertyProperty() {
        return imageProperty;
    }

    public void setImageProperty(Image imageProperty) {
        this.imageProperty.set(imageProperty);
    }

    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
    }
}
