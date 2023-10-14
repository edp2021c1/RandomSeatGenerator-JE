package com.edp2021c1.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX application intro.
 */
public class App extends Application {
    public void start(Stage primaryStage) throws IOException {
        Stage stage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/MainWindow.fxml")));
        stage.show();
    }
}
