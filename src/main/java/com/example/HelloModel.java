package com.example;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.image.*;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    Image background;
    Label message;

    ObjectProperty<Label> messageProp; //might do send and recived

    public HelloModel(){
        messageProp = new SimpleObjectProperty<Label>();

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
