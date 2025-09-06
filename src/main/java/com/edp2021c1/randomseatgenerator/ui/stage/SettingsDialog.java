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

import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.ui.node.ConfigPane;
import com.edp2021c1.randomseatgenerator.ui.node.FormatableTextField;
import com.edp2021c1.randomseatgenerator.ui.node.IntegerField;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Notice;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.v2.AppConfig;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;
import com.edp2021c1.randomseatgenerator.v2.util.IOUtils;
import com.edp2021c1.randomseatgenerator.v2.util.Metadata;
import com.edp2021c1.randomseatgenerator.v2.util.exception.ExceptionHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

/**
 * Settings dialog of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public final class SettingsDialog extends Stage {

    @Getter
    private static final SettingsDialog settingsDialog = new SettingsDialog();

    private final FileChooser fileChooser;

    private final ConfigPane configPane;

    /**
     * Creates an instance.
     */
    private SettingsDialog() {
        super();
        FXUtils.decorate(this, StageType.DIALOG);

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        IntegerField rowCountInput = new IntegerField(true, "行数");

        IntegerField columnCountInput = new IntegerField(true, "列数");

        IntegerField rbrInput = new IntegerField(true, "随机轮换的行数");

        FormatableTextField disabledLastRowPosInput = FormatableTextField.of((oldValue, newValue) -> {
            if (newValue == null || !Strings.integerListPatternPredicate.test(newValue)) {
                return oldValue;
            }
            return newValue;
        });
        disabledLastRowPosInput.setPromptText("最后一排不可选位置");

        TextField nameListInput = FXUtils.createEmptyTextField("名单 (按身高排序)");

        TextField groupLeaderListInput = FXUtils.createEmptyTextField("组长列表");

        TextArea separateListInput = FXUtils.createEmptyTextArea("拆分列表", 165, 56);

        CheckBox findLuckyCheck = new CheckBox("挑选护法");

        CheckBox findLeadersCheck = new CheckBox("挑选组长");

        CheckBox darkModeCheck = new CheckBox("深色模式");

        Button loadConfigBtn = FXUtils.createButton("从文件加载", 90, 26);

        Button applyBtn = FXUtils.createButton("应用", 80, 26);
        applyBtn.setDisable(true);

        configPane = new ConfigPane(
                rowCountInput,
                columnCountInput,
                rbrInput,
                disabledLastRowPosInput,
                nameListInput,
                groupLeaderListInput,
                separateListInput,
                findLuckyCheck,
                findLeadersCheck,
                darkModeCheck,
                applyBtn.disableProperty()
        );
        configPane.setContent(AppSettings.config);

        HBox loadConfigBtnBox = new HBox(loadConfigBtn);
        loadConfigBtnBox.setPrefHeight(45);
        loadConfigBtnBox.setAlignment(Pos.CENTER);

        VBox appConfigBox = new VBox(configPane, loadConfigBtnBox);

        ImageView iconView = new ImageView(FXUtils.getIcon());
        iconView.setFitWidth(275);
        iconView.setFitHeight(275);

        Label randomSeatGeneratorLabel = new Label(Metadata.NAME);
        randomSeatGeneratorLabel.setPrefHeight(32);
        randomSeatGeneratorLabel.getStyleClass().add("app-name-label");

        Hyperlink versionLink = new Hyperlink("版本:        " + Metadata.VERSION);

        Hyperlink gitRepositoryLink = new Hyperlink("Git仓库:   " + Metadata.GIT_REPOSITORY_URI);

        Hyperlink licenseLink = new Hyperlink("许可证:    %s(%s)".formatted(Metadata.LICENSE_NAME, Metadata.LICENSE_URI));

        TextArea licenseText = FXUtils.createEmptyTextArea(null, 650, 288);
        licenseText.setText(Metadata.LICENSE_INFO);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        VBox bottomRightBox = new VBox(randomSeatGeneratorLabel, versionLink, gitRepositoryLink, licenseLink, licenseText);
        bottomRightBox.setAlignment(Pos.CENTER_LEFT);

        HBox aboutInfoBox = new HBox(iconView, bottomRightBox);

        Button confirmBtn = FXUtils.createButton("确定", 80, 26);

        Button cancelBtn = FXUtils.createButton("取消", 80, 26);

        ButtonBar confirm_apply_cancelBar = new ButtonBar();
        confirm_apply_cancelBar.getButtons().addAll(confirmBtn, applyBtn, cancelBtn);
        confirm_apply_cancelBar.setPrefHeight(66);
        confirm_apply_cancelBar.getStyleClass().add("bottom");

        Tab appConfigTab = new Tab("常规", appConfigBox);
        appConfigTab.setClosable(false);

        Tab aboutInfoTab = new Tab("关于", aboutInfoBox);
        aboutInfoTab.setClosable(false);

        TabPane topPane = new TabPane(appConfigTab, aboutInfoTab);

        VBox mainBox = new VBox(topPane, confirm_apply_cancelBar);
        mainBox.getStyleClass().add("main");

        FXUtils.setInsets(new Insets(5),
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

        setScene(new Scene(mainBox));
        setTitle(Metadata.NAME + " - 设置");
        initOwner(PrimaryWindowManager.getPrimaryStage());

        fileChooser = new FileChooser();
        fileChooser.setTitle("加载配置文件");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));

        fileChooser.setInitialDirectory(new File(Metadata.USER_HOME));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        loadConfigBtn.setOnAction(event -> loadConfig());

        applyBtn.setOnAction(event -> applyConfig());

        versionLink.setOnAction(event -> DesktopUtils.browseIfSupported(Metadata.VERSION_PAGE_URI));

        gitRepositoryLink.setOnAction(event -> DesktopUtils.browseIfSupported(Metadata.GIT_REPOSITORY_URI));

        licenseLink.setOnAction(event -> DesktopUtils.browseIfSupported(Metadata.LICENSE_URI));

        confirmBtn.setOnAction(event -> confirmConfig());
        confirmBtn.setDefaultButton(true);

        cancelBtn.setOnAction(event -> close());
        cancelBtn.setCancelButton(true);

        if (OperatingSystem.MAC == OperatingSystem.getCurrent()) {
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

    private void loadConfig() {
        try {
            File tmp = fileChooser.getInitialDirectory();
            if (tmp != null) {
                fileChooser.setInitialDirectory(IOUtils.getClosestDirectory(tmp));
            }

            File importFile = fileChooser.showOpenDialog(this);
            if (importFile == null) {
                return;
            }

            try {
                configPane.setContent(AppConfig.loadFromPath(importFile.toPath()));
            } catch (IOException e) {
                LOGGER.warn("Failed to import config", e);
                MessageDialog.showMessage(this, Notice.of("导入设置失败"));
            }

            fileChooser.setInitialDirectory(importFile.getParentFile());
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    private void confirmConfig() {
        applyConfig();
        close();
    }

    private void applyConfig() {
        try {
            AppSettings.config = configPane.getContent().copy();
            AppSettings.saveConfig();
            configPane.refreshState();
            PrimaryWindowManager.configChanged();
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

}
