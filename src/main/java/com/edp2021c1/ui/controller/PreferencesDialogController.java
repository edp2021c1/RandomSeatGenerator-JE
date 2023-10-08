package com.edp2021c1.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.edp2021c1.Main;
import com.edp2021c1.core.SeatConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PreferencesDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
        SeatConfig c=new SeatConfig();
        c.rows=rowCountInput.getText();
        c.columns=columnCountInput.getText();
        c.random_between_rows=rbrInput.getText();
        c.last_row_pos_can_be_choosed=lastRowPosInput.getText();
        c.person_sort_by_height=nameListInput.getText();
        c.zz=groupLeaderListInput.getText();
        c.separate=separateListInput.getText();
        Main.seatConfig=c;
    }

    @FXML
    void loadConfigFromFile(ActionEvent event) {

    }

    @FXML
    void initialize() {
        SeatConfig seatConfig=Main.seatConfig;
        rowCountInput.setText(seatConfig.rows);
        columnCountInput.setText(seatConfig.columns);
        rbrInput.setText(seatConfig.random_between_rows);
        lastRowPosInput.setText(seatConfig.last_row_pos_can_be_choosed);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.zz);
        separateListInput.setText(seatConfig.separate);
    }

}
