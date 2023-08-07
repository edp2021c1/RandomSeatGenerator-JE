package com.edp2021c1.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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
    private Button closeBtn1;

    @FXML
    private Button libBtn;

    @FXML
    void closeDialog(ActionEvent event) {
        aboutDialog.close();
    }

    @FXML
    void openLibList(ActionEvent event) throws IOException {
        Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/fxml/dialog/LibListDialog.fxml")));
        s.initOwner(aboutDialog);
        s.show();
    }

    @FXML
    void initialize() {
        assert aboutDialog != null : "fx:id=\"aboutDialog\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert closeBtn != null : "fx:id=\"closeBtn\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert closeBtn1 != null : "fx:id=\"closeBtn1\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert icon != null : "fx:id=\"icon\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert img != null : "fx:id=\"img\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert libBtn != null : "fx:id=\"licenseBtn\" was not injected: check your FXML file 'AboutDialog.fxml'.";

        aboutDialog.initStyle(StageStyle.UNDECORATED);

        img.setImage(new Image("assets/img/icon.png"));
        icon.setImage(new Image("assets/img/icon.png"));
    }

}