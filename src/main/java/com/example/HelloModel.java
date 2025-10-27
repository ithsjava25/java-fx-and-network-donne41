package com.example;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private StringProperty dateTimeProperty;
    private DoubleProperty rotationProperty;

    public HelloModel(){
        dateTimeProperty = new SimpleStringProperty();
        rotationProperty = new SimpleDoubleProperty();
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
    public DoubleProperty rotationProperty(){
        return rotationProperty;
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
