package com.edp2021c1.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Deprecated
public class AboutDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView img;

    @FXML
    private Stage stage;

    @FXML
    private Button closeBtn;

    @FXML
    private Button libBtn;

    @FXML
    void closeDialog(ActionEvent event) {
        stage.close();
    }

    @FXML
    void openLibList(ActionEvent event) throws IOException {
        Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/dialog/LibListDialog.fxml")));
        s.initOwner(stage);
        s.show();
    }

    @FXML
    void initialize() {
        stage.initModality(Modality.APPLICATION_MODAL);

        img.setImage(new Image("assets/img/icon.png"));
    }

}