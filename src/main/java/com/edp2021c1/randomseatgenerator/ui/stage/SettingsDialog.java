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

import com.edp2021c1.randomseatgenerator.ui.UIUtils;
import com.edp2021c1.randomseatgenerator.ui.node.ConfigPane;
import com.edp2021c1.randomseatgenerator.ui.node.IntegerField;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfigHolder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
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
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.ui.UIUtils.*;
import static com.edp2021c1.randomseatgenerator.util.Metadata.*;

/**
 * Settings dialog of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class SettingsDialog extends Stage {
    @Getter
    private static final SettingsDialog settingsDialog = new SettingsDialog();
    private final ObjectProperty<File> importDir;
    private final JSONAppConfigHolder cfHolder;

    /**
     * Creates an instance.
     */
    private SettingsDialog() {
        super();

        cfHolder = JSONAppConfigHolder.global();

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        val rowCountInput = new IntegerField(true, "行数");

        val columnCountInput = new IntegerField(true, "列数");

        val rbrInput = new IntegerField(true, "随机轮换的行数");

        val disabledLastRowPosInput = createEmptyTextField("最后一排不可选位置");
        disabledLastRowPosInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            if (newValue == null || !Strings.integerListPattern.matcher(newValue).matches()) {
                disabledLastRowPosInput.setText(oldValue);
            }
        });

        val nameListInput = createEmptyTextField("名单 (按身高排序)");

        val groupLeaderListInput = createEmptyTextField("组长列表");

        val separateListInput = createTextArea("拆分列表", 165, 56);

        val luckyOptionCheck = new CheckBox("随机挑选左护法");

        val exportWritableCheck = new CheckBox("导出为可写");

        val darkModeCheck = new CheckBox("深色模式");

        val loadConfigBtn = createButton("从文件加载", 90, 26);

        val applyBtn = createButton("应用", 80, 26);
        applyBtn.setDisable(true);
        BooleanProperty applyBtnDisabledProperty = applyBtn.disableProperty();

        val configPane = new ConfigPane(
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
                cfHolder
        );

        val loadConfigBtnBox = new HBox(loadConfigBtn);
        loadConfigBtnBox.setPrefHeight(45);
        loadConfigBtnBox.setAlignment(Pos.CENTER);

        val appConfigBox = new VBox(configPane, loadConfigBtnBox);

        val iconView = new ImageView(getIcon());
        iconView.setFitWidth(275);
        iconView.setFitHeight(275);

        val randomSeatGeneratorLabel = new Label(NAME);
        randomSeatGeneratorLabel.setPrefHeight(32);
        randomSeatGeneratorLabel.getStyleClass().add("app-name-label");

        val versionLink = new Hyperlink("版本:        " + VERSION);

        val gitRepositoryLink = new Hyperlink("Git仓库:   " + GIT_REPOSITORY_URI);

        val licenseLink = new Hyperlink("许可证:    %s(%s)".formatted(LICENSE_NAME, LICENSE_URI));

        val licenseText = createTextArea(null, 650, 288);
        licenseText.setText(LICENSE_INFO);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        val bottomRightBox = new VBox(randomSeatGeneratorLabel, versionLink, gitRepositoryLink, licenseLink, licenseText);
        bottomRightBox.setAlignment(Pos.CENTER_LEFT);

        val aboutInfoBox = new HBox(iconView, bottomRightBox);

        val confirmBtn = createButton("确定", 80, 26);

        val cancelBtn = createButton("取消", 80, 26);

        val confirm_apply_cancelBar = new ButtonBar();
        confirm_apply_cancelBar.getButtons().addAll(confirmBtn, applyBtn, cancelBtn);
        confirm_apply_cancelBar.setPrefHeight(66);
        confirm_apply_cancelBar.getStyleClass().add("bottom");

        val appConfigTab = new Tab("常规", appConfigBox);
        appConfigTab.setClosable(false);

        val aboutInfoTab = new Tab("关于", aboutInfoBox);
        aboutInfoTab.setClosable(false);

        val topPane = new TabPane(appConfigTab, aboutInfoTab);

        val mainBox = new VBox(topPane, confirm_apply_cancelBar);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5),
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
        setTitle(NAME + " - 设置");
        initOwner(getMainWindow());
        decorate(this, StageType.DIALOG);

        val fc = new FileChooser();
        fc.setTitle("加载配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));

        importDir = fc.initialDirectoryProperty();

        val config = cfHolder.get();
        importDir.set(config.getString("import.dir.previous") == null ? cfHolder.getConfigPath().getParent().toFile() : new File(config.getString("import.dir.previous")));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        loadConfigBtn.setOnAction(event -> {
            try {
                var tmp = importDir.get();
                if (tmp != null) {
                    while (!tmp.isDirectory()) {
                        tmp = tmp.getParentFile();
                    }
                    importDir.set(tmp);
                }

                val importFile = fc.showOpenDialog(this);
                if (importFile == null) {
                    return;
                }

                try {
                    val temp = JSONAppConfigHolder.createHolder(importFile.toPath(), false);
                    configPane.setContent(temp.get());
                    temp.close();
                } catch (final IOException e) {
                    Logging.warning("Failed to import config");
                    Logging.warning(Strings.getStackTrace(e));
                    MessageDialog.showMessage(this, "导入设置失败");
                }

                importDir.set(importFile.getParentFile());
                cfHolder.put("import.dir.previous", importDir.get().toString());
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });

        applyBtn.setOnAction(event -> {
            try {
                cfHolder.putAll(configPane.getContent().checkAndReturn());
                UIUtils.getMainWindow().configChanged();
                applyBtn.setDisable(true);
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });

        versionLink.setOnAction(event -> DesktopUtils.browseIfSupported(VERSION_PAGE_URI));

        gitRepositoryLink.setOnAction(event -> DesktopUtils.browseIfSupported(GIT_REPOSITORY_URI));

        licenseLink.setOnAction(event -> DesktopUtils.browseIfSupported(LICENSE_URI));

        confirmBtn.setOnAction(event -> {
            applyBtn.fire();
            close();
        });
        confirmBtn.setDefaultButton(true);

        cancelBtn.setOnAction(event -> close());
        cancelBtn.setCancelButton(true);

        if (OperatingSystem.getCurrent().isMac()) {
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

        setOnShown(event -> configPane.setContent(cfHolder.get()));
    }
}
