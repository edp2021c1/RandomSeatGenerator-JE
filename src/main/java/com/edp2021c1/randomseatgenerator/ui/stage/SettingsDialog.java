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

package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.ui.node.ConfigPane;
import com.edp2021c1.randomseatgenerator.ui.util.UIFactory;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.*;

/**
 * Settings dialog of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class SettingsDialog extends Stage {
    private final ConfigPane configPane;
    private final TextField rowCountInput;
    private final TextField columnCountInput;
    private final TextField rbrInput;
    private final TextField disabledLastRowPosInput;
    private final TextField nameListInput;
    private final TextField groupLeaderListInput;
    private final TextArea separateListInput;
    private final CheckBox luckyOptionCheck;
    private final CheckBox exportWritableCheck;
    private final Button loadConfigBtn;
    private final Button applyBtn;
    private final FileChooser fc;
    private final URI GIT_REPOSITORY_URI;
    private File importDir = ConfigUtils.getConfigPath().getParent().toFile();
    private File importFile;
    private AppConfig config;

    {
        try {
            GIT_REPOSITORY_URI = new URI(MetaData.GIT_REPOSITORY_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an instance.
     *
     * @param owner of the dialog.
     */
    public SettingsDialog(MainWindow owner) {
        String s = ConfigUtils.reloadConfig().last_import_dir;
        if (s != null) {
            importDir = new File(s);
        }

        final Scene scene;
        final VBox mainBox;
        final VBox topBox;
        final Label seatConfigBoxTitleLabel;
        final HBox apply_loadConfigBox;
        final Separator separator;
        final VBox bottomBox;
        final Label aboutInfoBoxTitleLabel;
        final HBox moreBottomBox;
        final ImageView iconView;
        final VBox bottomRightBox;
        final Label randomSeatGeneratorLabel;
        final Label versionLabel;
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
        applyBtn.setDisable(true);

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

        apply_loadConfigBox = createHBox(1212, 45, loadConfigBtn, applyBtn);

        topBox = createVBox(1212, 300, seatConfigBoxTitleLabel, configPane, apply_loadConfigBox);
        topBox.getStyleClass().add("top");

        separator = new Separator();

        aboutInfoBoxTitleLabel = createLabel("关于", 1212, 15);

        iconView = createImageView(MetaData.ICON_URL, 275, 275);

        randomSeatGeneratorLabel = createLabel("RandomSeatGenerator", 273, 32);
        randomSeatGeneratorLabel.getStyleClass().add("app-name-label");

        versionLabel = new Label("版本:       " + MetaData.VERSION);

        gitRepositoryUrlLabel = new Label("官方仓库: " + MetaData.GIT_REPOSITORY_URL);

        licenseText = createTextArea(null, 937, 282);
        licenseText.setText(MetaData.LICENSE_INFO);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        bottomRightBox = createVBox(958, 287, randomSeatGeneratorLabel, versionLabel, gitRepositoryUrlLabel, licenseText);
        bottomRightBox.setAlignment(Pos.CENTER_LEFT);

        moreBottomBox = createHBox(1212, 408, iconView, bottomRightBox);

        confirmBtn = createButton("确定", 80, 26);

        cancelBtn = createButton("取消", 80, 26);

        buttonBar = createButtonBar(1212, 46, confirmBtn, cancelBtn);

        bottomBox = createVBox(1212, 523, aboutInfoBoxTitleLabel, moreBottomBox, buttonBar);
        bottomBox.getStyleClass().add("bottom");

        mainBox = createVBox(1232, 740, topBox, separator, bottomBox);
        mainBox.getStyleClass().add("main");

        scene = new Scene(mainBox);

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

        setScene(scene);
        setTitle("Random Seat Generator - 设置");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        UIFactory.decorate(this, WindowType.DIALOG);
        initConfigPane(ConfigUtils.reloadConfig(), configPane);

        fc = new FileChooser();
        fc.setTitle("加载配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));


        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        loadConfigBtn.setOnAction(event -> {
            try {
                fc.setInitialDirectory(importDir);
                importFile = fc.showOpenDialog(SettingsDialog.this);
                if (importFile == null) {
                    return;
                }

                try {
                    config = ConfigUtils.fromJson(Paths.get(importFile.getAbsolutePath()));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to load seat config from file.", e);
                }

                if (config != null) {
                    initConfigPane(config, configPane);
                }

                importDir = importFile.getParentFile();
                config = new AppConfig();
                config.last_import_dir = importDir.toString();
                ConfigUtils.saveConfig(config);
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        applyBtn.setOnAction(event -> {
            try {
                config = new AppConfig();
                config.row_count = rowCountInput.getText();
                config.column_count = columnCountInput.getText();
                config.random_between_rows = rbrInput.getText();
                config.last_row_pos_cannot_be_chosen = disabledLastRowPosInput.getText();
                config.person_sort_by_height = nameListInput.getText();
                config.group_leader_list = groupLeaderListInput.getText();
                config.separate_list = separateListInput.getText();
                config.lucky_option = luckyOptionCheck.isSelected();
                config.export_writable = exportWritableCheck.isSelected();

                try {
                    config.checkFormat();
                } catch (final IllegalConfigException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                ConfigUtils.saveConfig(config);

                owner.onConfigChanged();
                applyBtn.setDisable(true);
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        gitRepositoryUrlLabel.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(GIT_REPOSITORY_URI);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        confirmBtn.setOnAction(event -> {
            applyBtn.fire();
            close();
        });
        confirmBtn.setDefaultButton(true);

        cancelBtn.setOnAction(event -> close());
        cancelBtn.setCancelButton(true);

        if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> close();
                    case O -> loadConfigBtn.fire();
                    case S -> applyBtn.fire();
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case O -> loadConfigBtn.fire();
                    case S -> applyBtn.fire();
                }
            });
        }
    }
}
