package com.example;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {


    Image trollFace;

    StringProperty dateTimeProperty;
    DoubleProperty rotationProperty;
    ObjectProperty<Image> VboxLeftPic;

    public HelloModel(){
        dateTimeProperty = new SimpleStringProperty();
        rotationProperty = new SimpleDoubleProperty();
        trollFace = new Image(getClass().getResource("/troll-face.png").toExternalForm());

        VboxLeftPic = new SimpleObjectProperty<Image>(trollFace);
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

    public Image getVboxLeftPic() {
        return VboxLeftPic.get();
    }

    public ObjectProperty<Image> vboxLeftPicProperty() {
        return VboxLeftPic;
    }

    public void setVboxLeftPic(Image vboxLeftPic) {
        this.VboxLeftPic.set(vboxLeftPic);
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
