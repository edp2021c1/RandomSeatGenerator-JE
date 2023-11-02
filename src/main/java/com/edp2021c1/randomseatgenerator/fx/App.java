package com.edp2021c1.randomseatgenerator.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX application intro.
 */
public class App extends Application {
    public void start(Stage primaryStage) {
        Stage stage;
        try {
            stage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/MainWindow.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}
