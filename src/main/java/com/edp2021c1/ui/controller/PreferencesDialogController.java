package com.edp2021c1.ui.controller;

import com.edp2021c1.Main;
import com.edp2021c1.core.SeatConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of {@code assets/fxml/dialog/PreferencesDialog.fxml}
 */
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
    private CheckBox luckyOption;

    @FXML
    void applySeatConfig(ActionEvent event) {
        SeatConfig c = new SeatConfig();
        c.row_count = rowCountInput.getText();
        c.column_count = columnCountInput.getText();
        c.random_between_rows = rbrInput.getText();
        c.last_row_pos_can_be_choosed = lastRowPosInput.getText();
        c.person_sort_by_height = nameListInput.getText();
        c.group_leader_list = groupLeaderListInput.getText();
        c.separate_list = separateListInput.getText();
        c.lucky_option = luckyOption.isSelected();
        if (!Main.reloadConfig().equals(c)) {
            Main.saveConfig(c);
            MainWindowController.configIsChanged = true;
        }
    }

    @FXML
    void loadConfigFromFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("加载配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));
        File f = fc.showOpenDialog(stage);
        if (f == null) {
            return;
        }

        SeatConfig seatConfig;
        try {
            seatConfig = SeatConfig.fromJsonFile(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        rowCountInput.setText(seatConfig.row_count);
        columnCountInput.setText(seatConfig.column_count);
        rbrInput.setText(seatConfig.random_between_rows);
        lastRowPosInput.setText(seatConfig.last_row_pos_can_be_choosed);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.group_leader_list);
        separateListInput.setText(seatConfig.separate_list);
        luckyOption.setSelected(seatConfig.lucky_option);
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
        stage.getIcons().add(new Image("assets/img/icon.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SeatConfig seatConfig = Main.reloadConfig();
        rowCountInput.setText(seatConfig.row_count);
        columnCountInput.setText(seatConfig.column_count);
        rbrInput.setText(seatConfig.random_between_rows);
        lastRowPosInput.setText(seatConfig.last_row_pos_can_be_choosed);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.group_leader_list);
        separateListInput.setText(seatConfig.separate_list);
        luckyOption.setSelected(seatConfig.lucky_option);
    }

}
