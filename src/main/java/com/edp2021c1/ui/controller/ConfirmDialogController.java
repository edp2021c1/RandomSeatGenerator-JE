package com.edp2021c1.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfirmDialogController {
    @FXML
    public ImageView icon;
    @FXML
    public Button closeBtn;
    @FXML
    public Stage stage;
    @FXML
    public Button confirmBtn;
    @FXML
    public ImageView img;

    @FXML
    public void closeDialog(ActionEvent actionEvent) {
        stage.close();
    }

    @FXML
    void initialize() {
        icon.setImage(new Image("assets/img/icon.png"));
        img.setImage(new Image("assets/img/icon.png"));

        stage.initStyle(StageStyle.UNDECORATED);
    }
}
