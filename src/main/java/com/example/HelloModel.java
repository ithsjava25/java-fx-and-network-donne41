package com.example;

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

    public HelloModel(){
        noSmash = new Image(getClass().getResource("/lantern.png").toExternalForm());
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
