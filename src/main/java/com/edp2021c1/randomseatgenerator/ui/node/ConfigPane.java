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

package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.AppConfig;
import com.edp2021c1.randomseatgenerator.AppSettings;
import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.util.i18n.Language;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Objects;

public class ConfigPane extends VBox {

    private final IntegerProperty rowCountProperty;

    private final IntegerProperty columnCountProperty;

    private final IntegerProperty shuffledRowCountProperty;

    private final StringProperty disabledLastRowPosProperty;

    private final StringProperty nameListProperty;

    private final StringProperty leaderNameSetProperty;

    private final StringProperty separateListProperty;

    private final BooleanProperty findLuckyProperty;

    private final BooleanProperty findLeadersProperty;

    private final BooleanProperty darkModeProperty;

    private final ObjectProperty<Language> languageProperty;

    @Getter
    private final AppConfig content;

    private final BooleanProperty applyButtonDisabledProperty;

    public ConfigPane(
            IntegerField rowCountInput,
            IntegerField columnCountInput,
            IntegerField rbrInput,
            TextField disabledLastRowPosInput,
            TextField nameListInput,
            TextField groupLeaderListInput,
            TextArea separateListInput,
            CheckBox findLuckyCheck,
            CheckBox findLeadersCheck,
            CheckBox darkModeCheck,
            Label languageLabel,
            ChoiceBox<Language> languageChoiceBox,
            BooleanProperty applyBtnDisabledProperty
    ) {
        super();

        rowCountProperty = rowCountInput.valueProperty();
        columnCountProperty = columnCountInput.valueProperty();
        shuffledRowCountProperty = rbrInput.valueProperty();
        disabledLastRowPosProperty = disabledLastRowPosInput.textProperty();
        nameListProperty = nameListInput.textProperty();
        leaderNameSetProperty = groupLeaderListInput.textProperty();
        separateListProperty = separateListInput.textProperty();
        findLuckyProperty = findLuckyCheck.selectedProperty();
        findLeadersProperty = findLeadersCheck.selectedProperty();
        darkModeProperty = darkModeCheck.selectedProperty();
        languageProperty = languageChoiceBox.valueProperty();
        applyButtonDisabledProperty = applyBtnDisabledProperty;

        content = AppSettings.config.copy();

        HBox box1 = new HBox(rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        box1.setPrefHeight(60);
        box1.setAlignment(Pos.CENTER);
        HBox box2 = new HBox(nameListInput, groupLeaderListInput, separateListInput, findLeadersCheck, findLuckyCheck);
        box2.setPrefHeight(60);
        box2.setAlignment(Pos.CENTER);
        HBox box3 = new HBox(darkModeCheck, languageLabel, languageChoiceBox);
        box3.setPrefHeight(60);
        box3.setAlignment(Pos.CENTER);
        getChildren().addAll(box1, box2, box3);

        if (applyBtnDisabledProperty == null) {
            return;
        }
        rowCountProperty.subscribe(newValue -> {
            content.seatConfig.rowCount = newValue.intValue();
            refreshState();
        });
        columnCountProperty.subscribe(newValue -> {
            content.seatConfig.columnCount = newValue.intValue();
            refreshState();
        });
        shuffledRowCountProperty.subscribe(newValue -> {
            content.seatConfig.shuffledRowCount = newValue.intValue();
            refreshState();
        });
        disabledLastRowPosProperty.subscribe(newValue -> {
            content.seatConfig.disabledLastRowPositions = newValue;
            refreshState();
        });
        nameListProperty.subscribe(newValue -> {
            content.seatConfig.nameList = newValue;
            refreshState();
        });
        leaderNameSetProperty.subscribe(newValue -> {
            content.seatConfig.leaderNameSet = newValue;
            refreshState();
        });
        separateListProperty.subscribe(newValue -> {
            content.seatConfig.separatedPairs = newValue;
            refreshState();
        });
        findLuckyProperty.subscribe(newValue -> {
            content.seatConfig.findLucky = newValue;
            refreshState();
        });
        findLeadersProperty.subscribe(newValue -> {
            content.seatConfig.findLeaders = newValue;
            refreshState();
        });
        darkModeProperty.bindBidirectional(FXUtils.globalDarkModeProperty());
        darkModeProperty.subscribe(newValue -> {
            content.darkMode = newValue;
            refreshState();
        });
        languageProperty.subscribe(newValue -> {
            content.language = newValue.code;
            refreshState();
        });
    }

    private boolean checkEquals() {
        return Objects.equals(content, AppSettings.config);
    }

    public void refreshState() {
        applyButtonDisabledProperty.set(checkEquals());
    }

    public void setContent(AppConfig config) {
        if (config == null) {
            return;
        }
        rowCountProperty.setValue(config.seatConfig.rowCount);
        columnCountProperty.setValue(config.seatConfig.columnCount);
        shuffledRowCountProperty.setValue(config.seatConfig.shuffledRowCount);
        disabledLastRowPosProperty.set(config.seatConfig.disabledLastRowPositions);
        nameListProperty.set(config.seatConfig.nameList);
        leaderNameSetProperty.set(config.seatConfig.leaderNameSet);
        separateListProperty.set(config.seatConfig.separatedPairs);
        findLuckyProperty.set(config.seatConfig.findLucky);
        findLeadersProperty.set(config.seatConfig.findLeaders);
        darkModeProperty.set(config.darkMode);
        languageProperty.set(Language.getByCode(config.language));
    }

}
