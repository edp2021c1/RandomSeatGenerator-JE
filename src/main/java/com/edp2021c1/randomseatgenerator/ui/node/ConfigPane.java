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

package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.v2.AppConfig;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Objects;

/**
 * Config pane.
 *
 * @author Calboot
 * @since 1.3.4
 */
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

    @Getter
    private final AppConfig content;

    private final BooleanProperty applyButtonDisabledProperty;

    /**
     * Constructs an instance.
     *
     * @param rowCountInput            input of row count
     * @param columnCountInput         input of column count
     * @param rbrInput                 input of random between rows
     * @param disabledLastRowPosInput  input of unavailable last row positions
     * @param nameListInput            input of names
     * @param groupLeaderListInput     input of group leaders
     * @param separateListInput        input of separated pairs
     * @param findLuckyCheck           input of lucky option
     * @param findLeadersCheck         input of findLeaders
     * @param darkModeCheck            input of dark mode
     * @param applyBtnDisabledProperty property of whether the current globalHolder config is equal to the config in the pane,
     *                                 usually decides whether the apply button is disabled, ignored if is null
     */
    public ConfigPane(
            final IntegerField rowCountInput,
            final IntegerField columnCountInput,
            final IntegerField rbrInput,
            final TextField disabledLastRowPosInput,
            final TextField nameListInput,
            final TextField groupLeaderListInput,
            final TextArea separateListInput,
            final CheckBox findLuckyCheck,
            final CheckBox findLeadersCheck,
            final CheckBox darkModeCheck,
            final BooleanProperty applyBtnDisabledProperty
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
        applyButtonDisabledProperty = applyBtnDisabledProperty;

        content = AppSettings.config.copy();

        HBox box1 = new HBox(rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        box1.setPrefHeight(60);
        box1.setAlignment(Pos.CENTER);
        HBox box2 = new HBox(nameListInput, groupLeaderListInput, separateListInput, findLeadersCheck, findLuckyCheck);
        box2.setPrefHeight(60);
        box2.setAlignment(Pos.CENTER);
        HBox box3 = new HBox(darkModeCheck);
        box3.setPrefHeight(60);
        box3.setAlignment(Pos.CENTER);
        getChildren().addAll(box1, box2, box3);

        darkModeProperty.bindBidirectional(FXUtils.globalDarkModeProperty());

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
    }

    private boolean checkEquals() {
        return Objects.equals(content, AppSettings.config);
    }

    /**
     * Refreshes the state of {@link #applyButtonDisabledProperty}.
     */
    public void refreshState() {
        applyButtonDisabledProperty.set(checkEquals());
    }

    /**
     * Resets the pane with the given config.
     *
     * @param config to set to the pane.
     */
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
    }

}
