package com.edp2021c1.ui.controller;

import com.edp2021c1.util.LibData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class LibListDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button closeBtn1;

    @FXML
    private ImageView icon;

    @FXML
    private Stage stage;

    @FXML
    private TableView<LibData> table;

    @FXML
    void closeDialog(ActionEvent event) {
        stage.close();
    }

    @FXML
    void initialize() {
        icon.setImage(new Image("assets/img/icon.png"));

        stage.initStyle(StageStyle.UNDECORATED);

        ObservableList<LibData> data = FXCollections.observableArrayList(new LibData("EasyExcel 3.3.2", "Apache 2.0"),
                new LibData("Gson 2.10.1", "Apache 2.0"),
                new LibData("JavaFX 20.0.1", "GNU GPLv3.0"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<LibData, String> libCol = new TableColumn<>("Lib");
        libCol.setCellValueFactory(new PropertyValueFactory<>("lib"));
        TableColumn<LibData, String> licenseCol = new TableColumn<>("License");
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("license"));

        table.setEditable(false);
        table.setItems(data);
        table.getColumns().addAll(libCol, licenseCol);


    }

}
