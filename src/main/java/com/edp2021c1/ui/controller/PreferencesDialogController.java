package com.edp2021c1.ui.controller;

import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferencesDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Stage stage;

    @FXML
    void initialize() {
        stage.initModality(Modality.APPLICATION_MODAL);

    }

}
