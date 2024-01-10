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
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;
import com.edp2021c1.randomseatgenerator.util.ui.UIFactory;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.edp2021c1.randomseatgenerator.util.Metadata.*;
import static com.edp2021c1.randomseatgenerator.util.ui.UIFactory.*;

/**
 * Settings dialog of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class SettingsDialog extends Stage {
    private File importDir = ConfigHolder.globalHolder().getConfigPath().getParent().toFile();
    private File importFile;
    private RawAppConfig config;

    /**
     * Creates an instance.
     *
     * @param owner of the dialog.
     */
    public SettingsDialog(MainWindow owner) {
        String s = ConfigHolder.globalHolder().get().last_import_dir;
        if (s != null) {
            importDir = new File(s);
        }

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        final TextField rowCountInput = createTextField("行数");

        final TextField columnCountInput = createTextField("列数");

        final TextField rbrInput = createTextField("随机轮换的行数");

        final TextField disabledLastRowPosInput = createTextField("最后一排不可选位置");

        final TextField nameListInput = createTextField("名单 (按身高排序)");

        final TextField groupLeaderListInput = createTextField("组长列表");

        final TextArea separateListInput = createTextArea("拆分列表", 165, 56);

        final CheckBox luckyOptionCheck = new CheckBox("随机挑选一名幸运儿");

        final CheckBox exportWritableCheck = new CheckBox("导出为可写");

        final CheckBox darkModeCheck = new CheckBox("深色模式");

        final Button loadConfigBtn = createButton("从文件加载", 90, 26);

        final Button applyBtn = createButton("应用", 80, 26);
        applyBtn.setDisable(true);
        BooleanProperty applyBtnDisabledProperty = applyBtn.disableProperty();

        final ConfigPane configPane = new ConfigPane(
                rowCountInput,
                columnCountInput,
                rbrInput,
                disabledLastRowPosInput,
                nameListInput,
                groupLeaderListInput,
                separateListInput,
                luckyOptionCheck,
                exportWritableCheck,
                darkModeCheck,
                applyBtnDisabledProperty,
                ConfigHolder.globalHolder()
        );

        final HBox loadConfigBtnBox = new HBox(loadConfigBtn);
        loadConfigBtnBox.setPrefHeight(45);
        loadConfigBtnBox.setAlignment(Pos.CENTER);

        final VBox appConfigBox = new VBox(configPane, loadConfigBtnBox);

        final ImageView iconView = createImageView(ICON_URL, 275, 275);

        final Label randomSeatGeneratorLabel = new Label(NAME);
        randomSeatGeneratorLabel.setPrefHeight(32);
        randomSeatGeneratorLabel.getStyleClass().add("app-name-label");

        final Label versionLabel = new Label("版本:       " + VERSION);

        final Label gitRepositoryUrlLabel = new Label("Git仓库:   " + GIT_REPOSITORY_URL);

        final Label licenseLabel = new Label("许可证:    %s(%s)".formatted(LICENSE_NAME, LICENSE_URL));

        final TextArea licenseText = createTextArea(null, 960, 288);
        licenseText.setText(LICENSE_INFO);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        final VBox bottomRightBox = new VBox(randomSeatGeneratorLabel, versionLabel, gitRepositoryUrlLabel, licenseLabel, licenseText);
        bottomRightBox.setAlignment(Pos.CENTER_LEFT);

        final HBox aboutInfoBox = new HBox(iconView, bottomRightBox);

        final Button confirmBtn = createButton("确定", 80, 26);

        final Button cancelBtn = createButton("取消", 80, 26);

        final ButtonBar confirm_apply_cancelBar = new ButtonBar();
        confirm_apply_cancelBar.getButtons().addAll(confirmBtn, applyBtn, cancelBtn);
        confirm_apply_cancelBar.setPrefHeight(66);
        confirm_apply_cancelBar.getStyleClass().add("bottom");

        final Tab appConfigTab = new Tab("常规", appConfigBox);
        appConfigTab.setClosable(false);

        final Tab aboutInfoTab = new Tab("关于", aboutInfoBox);
        aboutInfoTab.setClosable(false);

        final TabPane topPane = new TabPane(appConfigTab, aboutInfoTab);

        final VBox mainBox = new VBox(topPane, confirm_apply_cancelBar);
        mainBox.getStyleClass().add("main");

        final Scene scene = new Scene(mainBox);

        setMargins(new Insets(5),
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
        setTitle(NAME + " - 设置");
        initOwner(owner);
        UIFactory.decorate(this, StageType.DIALOG);

        final FileChooser fc = new FileChooser();
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
                    config = RawAppConfig.fromJson(Paths.get(importFile.getAbsolutePath()));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to load seat config from file", e);
                }

                configPane.reset(config);

                importDir = importFile.getParentFile();
                config = new RawAppConfig();
                config.last_import_dir = importDir.toString();
                ConfigHolder.globalHolder().set(config);
            } catch (final Throwable e) {
                CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
            }
        });

        applyBtn.setOnAction(event -> {
            try {
                config = configPane.getCurrent();

                try {
                    config.checkFormat();
                } catch (final IllegalConfigException e) {
                    CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                ConfigHolder.globalHolder().set(config);

                owner.onConfigChanged();
                applyBtn.setDisable(true);
            } catch (final Throwable e) {
                CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
            }
        });

        gitRepositoryUrlLabel.setOnMouseClicked(event -> DesktopUtils.browseIfSupported(GIT_REPOSITORY_URI));

        licenseLabel.setOnMouseClicked(event -> DesktopUtils.browseIfSupported(LICENSE_URI));

        confirmBtn.setOnAction(event -> {
            applyBtn.fire();
            close();
        });
        confirmBtn.setDefaultButton(true);

        cancelBtn.setOnAction(event -> close());
        cancelBtn.setCancelButton(true);

        if (OperatingSystem.getCurrent() == OperatingSystem.MAC) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case Q -> System.exit(0);
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

        setOnShown(event -> configPane.reset(ConfigHolder.globalHolder().get()));
    }
}
