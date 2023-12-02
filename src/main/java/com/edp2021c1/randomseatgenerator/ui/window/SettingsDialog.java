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

package com.edp2021c1.randomseatgenerator.ui.window;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.ui.control.ConfigPane;
import com.edp2021c1.randomseatgenerator.util.AppConfig;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.MetaData;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.*;

/**
 * Settings dialog of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class SettingsDialog extends Stage {
    /**
     * Creates an instance.
     *
     * @param owner of the dialog.
     */
    public SettingsDialog(MainWindow owner) {
        final Scene scene;
        final VBox mainBox;
        final VBox topBox;
        final Label seatConfigBoxTitleLabel;
        final ConfigPane configPane;
        final TextField rowCountInput;
        final TextField columnCountInput;
        final TextField rbrInput;
        final TextField disabledLastRowPosInput;
        final TextField nameListInput;
        final TextField groupLeaderListInput;
        final TextArea separateListInput;
        final CheckBox luckyOptionCheck;
        final CheckBox exportWritableCheck;
        final HBox box3;
        final Button loadConfigBtn;
        final Button applyBtn;
        final VBox bottomBox;
        final Label aboutInfoBoxTitleLabel;
        final HBox box4;
        final ImageView iconView;
        final VBox box5;
        final Label randomSeatGeneratorLabel;
        final Label gitRepositoryUrlLabel;
        final TextArea licenseText;
        final ButtonBar buttonBar;
        final Button confirmBtn;
        final Button cancelBtn;

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        seatConfigBoxTitleLabel = createLabel("生成和导出", 1212, 30);
        rowCountInput = createTextField("行数");
        columnCountInput = createTextField("列数");
        rbrInput = createTextField("随机轮换的行数");
        disabledLastRowPosInput = createTextField("最后一排不可选位置");

        nameListInput = createTextField("名单 (按身高排序)");
        groupLeaderListInput = createTextField("组长列表");
        separateListInput = createTextArea("拆分列表", 165, 56);
        luckyOptionCheck = new CheckBox("随机挑选一名幸运儿");

        exportWritableCheck = new CheckBox("导出为可写");

        loadConfigBtn = createButton("从文件加载", 80, 26);
        applyBtn = createButton("应用", 80, 26);

        configPane = new ConfigPane(
                rowCountInput,
                columnCountInput,
                rbrInput,
                disabledLastRowPosInput,
                nameListInput,
                groupLeaderListInput,
                separateListInput,
                luckyOptionCheck,
                exportWritableCheck,
                applyBtn
        );

        box3 = createHBox(1212, 45, loadConfigBtn, applyBtn);

        topBox = createVBox(1212, 300, seatConfigBoxTitleLabel, configPane, box3);

        aboutInfoBoxTitleLabel = createLabel("关于", 1212, 15);
        iconView = createImageView(MetaData.ICON_URL, 275, 275);
        randomSeatGeneratorLabel = createLabel("RandomSeatGenerator", 273, 32);
        gitRepositoryUrlLabel = createLabel("官方仓库    https://github.com/edp2021c1/RandomSeatGenerator-JE.git", 452, 23);
        licenseText = createTextArea(null, 937, 282);

        box5 = createVBox(958, 264, randomSeatGeneratorLabel, gitRepositoryUrlLabel, licenseText);

        box4 = createHBox(1212, 385, iconView, box5);

        confirmBtn = createButton("确定", 80, 26);
        cancelBtn = createButton("取消", 80, 26);

        buttonBar = createButtonBar(1212, 46, confirmBtn, cancelBtn);

        bottomBox = createVBox(1212, 500, aboutInfoBoxTitleLabel, box4, buttonBar);

        mainBox = createVBox(1232, 720, topBox, bottomBox);

        scene = new Scene(mainBox);
        scene.getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);

        setMargins(DEFAULT_MARGIN,
                rowCountInput,
                columnCountInput,
                rbrInput,
                disabledLastRowPosInput,
                nameListInput,
                groupLeaderListInput,
                separateListInput,
                loadConfigBtn,
                applyBtn,
                confirmBtn,
                cancelBtn
        );
        setPaddings(DEFAULT_PADDING, topBox, bottomBox);
        applyBtn.setDisable(true);
        randomSeatGeneratorLabel.getStyleClass().add("app-name-label");
        licenseText.setText(MetaData.LICENSE_INFO);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        setScene(scene);
        getIcons().add(new Image(MetaData.ICON_URL));
        setTitle("Random Seat Generator - 设置");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        initConfigPane(ConfigUtils.reloadConfig(), configPane);


        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        loadConfigBtn.setOnAction(event -> {
            try {
                final FileChooser fc = new FileChooser();
                fc.setTitle("加载配置文件");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));
                final File f = fc.showOpenDialog(SettingsDialog.this);
                if (f == null) {
                    return;
                }

                final AppConfig seatConfig;
                try {
                    seatConfig = ConfigUtils.fromJson(Paths.get(f.getAbsolutePath()));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to load seat config from file.", e);
                }

                if (seatConfig != null) {
                    initConfigPane(seatConfig, configPane);
                }
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        applyBtn.setOnAction(event -> {
            try {
                final AppConfig config = new AppConfig();
                config.row_count = rowCountInput.getText();
                config.column_count = columnCountInput.getText();
                config.random_between_rows = rbrInput.getText();
                config.last_row_pos_cannot_be_chosen = disabledLastRowPosInput.getText();
                config.person_sort_by_height = nameListInput.getText();
                config.group_leader_list = groupLeaderListInput.getText();
                config.separate_list = separateListInput.getText();
                config.lucky_option = luckyOptionCheck.isSelected();
                config.export_writable = exportWritableCheck.isSelected();

                if (ConfigUtils.reloadConfig().equals(config)) {
                    return;
                }
                try {
                    ConfigUtils.saveConfig(config);
                } catch (final IllegalConfigException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                    return;
                }

                owner.onConfigChanged();
                applyBtn.setDisable(true);
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        confirmBtn.setOnAction(event -> {
            applyBtn.fire();
            cancelBtn.fire();
        });
        confirmBtn.setDefaultButton(true);

        cancelBtn.setOnAction(event -> {
            try {
                SettingsDialog.this.close();
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });
        cancelBtn.setCancelButton(true);
    }
}
