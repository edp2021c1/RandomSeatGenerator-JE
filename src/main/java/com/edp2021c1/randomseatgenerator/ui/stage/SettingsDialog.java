/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

import com.edp2021c1.randomseatgenerator.AppConfig;
import com.edp2021c1.randomseatgenerator.AppSettings;
import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.ui.node.ConfigPane;
import com.edp2021c1.randomseatgenerator.ui.node.IntegerField;
import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.exception.ExceptionHandler;
import com.edp2021c1.randomseatgenerator.util.exception.TranslatableException;
import com.edp2021c1.randomseatgenerator.util.i18n.I18N;
import com.edp2021c1.randomseatgenerator.util.i18n.Language;
import javafx.collections.FXCollections;
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

public final class SettingsDialog extends Stage {

    @Getter
    private static final SettingsDialog settingsDialog = new SettingsDialog();

    private final FileChooser fileChooser;

    private final ConfigPane configPane;

    private SettingsDialog() {
        super();
        FXUtils.decorate(this, StageType.DIALOG);

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        IntegerField rowCountInput = new IntegerField(true, "rowCountInput");

        IntegerField columnCountInput = new IntegerField(true, "columnCountInput");

        IntegerField rbrInput = new IntegerField(true, "shuffledRowCountInput");

        TextField disabledLastRowPosInput = FXUtils.createEmptyTextField("disabledLastRowPositionsInput");

        TextField nameListInput = FXUtils.createEmptyTextField("nameListInput");

        TextField groupLeaderListInput = FXUtils.createEmptyTextField("leaderNameSetInput");

        TextArea separateListInput = FXUtils.createEmptyTextArea("seperatedPairsInput", 165, 56);

        CheckBox findLuckyCheck = FXUtils.createCheckBox("findLucky");

        CheckBox findLeadersCheck = FXUtils.createCheckBox("findLeaders");

        CheckBox darkModeCheck = FXUtils.createCheckBox("darkMode");

        Label               languageLabel     = new Label("    " + I18N.constant("language") + " ");
        ChoiceBox<Language> languageChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Language.values()));
        languageChoiceBox.setValue(Language.getCurrent());

        Button loadConfigBtn = FXUtils.createButton("load", 90, 26);

        Button applyBtn = FXUtils.createButton("apply", 80, 26);
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
                languageLabel,
                languageChoiceBox,
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

        Hyperlink versionLink = FXUtils.createHyperlink(Metadata.VERSION_PAGE_URI, "version", Metadata.VERSION, Metadata.BUILD_DATE);

        Hyperlink gitRepositoryLink = FXUtils.createHyperlink(Metadata.GIT_REPOSITORY_URI, "git", Metadata.GIT_REPOSITORY_URI);

        Hyperlink licenseLink = FXUtils.createHyperlink(Metadata.LICENSE_URI, "license", Metadata.LICENSE_NAME);

        TextArea licenseText = FXUtils.createEmptyTextArea(650, 288);
        licenseText.setText(Metadata.LICENSE);
        licenseText.setEditable(false);
        licenseText.getStyleClass().add("license-text-area");

        VBox bottomRightBox = new VBox(randomSeatGeneratorLabel, versionLink, gitRepositoryLink, licenseLink, licenseText);
        bottomRightBox.setAlignment(Pos.CENTER_LEFT);

        HBox aboutInfoBox = new HBox(iconView, bottomRightBox);

        Button confirmBtn = FXUtils.createButton("confirm", 80, 26);

        Button cancelBtn = FXUtils.createButton("cancel", 80, 26);

        ButtonBar confirm_apply_cancelBar = new ButtonBar();
        confirm_apply_cancelBar.getButtons().addAll(confirmBtn, applyBtn, cancelBtn);
        confirm_apply_cancelBar.setPrefHeight(66);
        confirm_apply_cancelBar.getStyleClass().add("bottom");

        Tab appConfigTab = FXUtils.createTab("general", appConfigBox);
        appConfigTab.setClosable(false);

        Tab aboutInfoTab = FXUtils.createTab("about", aboutInfoBox);
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
        setTitle(Metadata.NAME + " - " + FXUtils.translateTitle("settings"));
        initOwner(PrimaryWindowManager.getPrimaryStage());

        fileChooser = new FileChooser();
        fileChooser.setTitle("importConfig");
        fileChooser.getExtensionFilters().add(FXUtils.extensionFilter("json"));

        fileChooser.setInitialDirectory(Metadata.DATA_DIR.toFile());

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        loadConfigBtn.setOnAction(event -> loadConfig());

        applyBtn.setOnAction(event -> applyConfig());

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
                MessageDialog.showMessage(this, TranslatableException.io(e, "import_failure", e.getMessage()));
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
