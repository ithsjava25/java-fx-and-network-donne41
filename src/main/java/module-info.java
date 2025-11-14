module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;
    requires tools.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires javafx.graphics;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.example to javafx.fxml;
    exports com.example;
}