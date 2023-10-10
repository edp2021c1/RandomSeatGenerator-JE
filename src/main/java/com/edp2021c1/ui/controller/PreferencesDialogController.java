package com.edp2021c1.ui.controller;

import com.edp2021c1.Main;
import com.edp2021c1.core.SeatConfig;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class PreferencesDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Stage stage;

    @FXML
    private TextField columnCountInput;

    @FXML
    private TextField groupLeaderListInput;

    @FXML
    private TextField lastRowPosInput;

    @FXML
    private TextField nameListInput;

    @FXML
    private TextField rbrInput;

    @FXML
    private TextField rowCountInput;

    @FXML
    private TextArea separateListInput;

    @FXML
    void applySeatConfig(ActionEvent event) {
        SeatConfig c = new SeatConfig();
        c.rows = rowCountInput.getText();
        c.columns = columnCountInput.getText();
        c.random_between_rows = rbrInput.getText();
        c.last_row_pos_can_be_choosed = lastRowPosInput.getText();
        c.person_sort_by_height = nameListInput.getText();
        c.zz = groupLeaderListInput.getText();
        c.separate = separateListInput.getText();
        Main.seatConfig = c;
    }

    @FXML
    void loadConfigFromFile(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("加载配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));
        File f = fc.showOpenDialog(stage);
        if (f == null) {
            return;
        }

        FileInputStream inputStream = new FileInputStream(f);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String str = new String(bytes, StandardCharsets.UTF_8);
        SeatConfig seatConfig = new Gson().fromJson(str, SeatConfig.class);

        rowCountInput.setText(seatConfig.rows);
        columnCountInput.setText(seatConfig.columns);
        rbrInput.setText(seatConfig.random_between_rows);
        lastRowPosInput.setText(seatConfig.last_row_pos_can_be_choosed);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.zz);
        separateListInput.setText(seatConfig.separate);
    }

    @FXML
    void confirm(ActionEvent event) {
        applySeatConfig(null);
        stage.close();
    }

    @FXML
    void cancel(ActionEvent event) {
        stage.close();
    }

    @FXML
    void initialize() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SeatConfig seatConfig = Main.seatConfig;
        rowCountInput.setText(seatConfig.rows);
        columnCountInput.setText(seatConfig.columns);
        rbrInput.setText(seatConfig.random_between_rows);
        lastRowPosInput.setText(seatConfig.last_row_pos_can_be_choosed);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.zz);
        separateListInput.setText(seatConfig.separate);
    }

}
