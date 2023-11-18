/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.ui.controller;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.MetaData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of {@code assets/fxml/dialog/PreferencesDialog.fxml}
 *
 * @author Calboot
 * @since 1.2.0
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
    private TextField disabledLastRowPosInput;

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
    private Button applyBtn;

    @FXML
    private ImageView iconView;

    @FXML
    void applySeatConfig(ActionEvent event) {
        SeatConfig seatConfig = new SeatConfig();
        seatConfig.row_count = rowCountInput.getText();
        seatConfig.column_count = columnCountInput.getText();
        seatConfig.random_between_rows = rbrInput.getText();
        seatConfig.last_row_pos_cannot_be_chosen = disabledLastRowPosInput.getText();
        seatConfig.person_sort_by_height = nameListInput.getText();
        seatConfig.group_leader_list = groupLeaderListInput.getText();
        seatConfig.separate_list = separateListInput.getText();
        seatConfig.lucky_option = luckyOption.isSelected();

        if (ConfigUtils.reloadConfig().equals(seatConfig)) {
            return;
        }
        try {
            ConfigUtils.saveConfig(seatConfig);
        } catch (IllegalConfigException e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            return;
        }

        MainWindowController.configIsChanged = true;
        applyBtn.setDisable(true);
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
            seatConfig = ConfigUtils.fromJsonFile(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to load seat config from file.", e);
        }

        if (seatConfig != null) {
            initConfigPane(seatConfig);
        }
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
        stage.getIcons().add(new Image(MetaData.ICON_URL));
        stage.setTitle("RandomSeatGenerator - 首选项");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getScene().getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);

        iconView.setImage(new Image(MetaData.ICON_URL));

        initConfigPane(ConfigUtils.reloadConfig());

        rowCountInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().row_count.equals(newValue)));
        columnCountInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().column_count.equals(newValue)));
        rbrInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().random_between_rows.equals(newValue)));
        disabledLastRowPosInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().last_row_pos_cannot_be_chosen.equals(newValue)));
        nameListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().person_sort_by_height.equals(newValue)));
        groupLeaderListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().group_leader_list.equals(newValue)));
        separateListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(ConfigUtils.reloadConfig().separate_list.equals(newValue)));
        luckyOption.selectedProperty().addListener((observable, oldValue, newValue) ->
                applyBtn.setDisable(newValue == ConfigUtils.reloadConfig().lucky_option));
    }

    void initConfigPane(SeatConfig seatConfig) {
        rowCountInput.setText(seatConfig.row_count);
        columnCountInput.setText(seatConfig.column_count);
        rbrInput.setText(seatConfig.random_between_rows);
        disabledLastRowPosInput.setText(seatConfig.last_row_pos_cannot_be_chosen);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.group_leader_list);
        separateListInput.setText(seatConfig.separate_list);
        luckyOption.setSelected(seatConfig.lucky_option);
    }

}
