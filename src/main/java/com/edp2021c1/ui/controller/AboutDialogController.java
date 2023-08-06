package com.edp2021c1.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView img;

    @FXML
    private ImageView icon;

    @FXML
    private Stage aboutDialog;

    @FXML
    private Button closeBtn;

    @FXML
    private Button licenseBtn;

    @FXML
    void closeDialog(ActionEvent event) {
        aboutDialog.close();
    }

    @FXML
    void openLicense(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert img != null : "fx:id=\"img\" was not injected: check your FXML file 'AboutDialog.fxml'.";

        aboutDialog.initStyle(StageStyle.UNDECORATED);

        img.setImage(new Image("assets/img/icon.png"));
        icon.setImage(new Image("assets/img/icon.png"));
    }

}