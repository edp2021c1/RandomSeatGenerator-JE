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
import com.edp2021c1.randomseatgenerator.util.config.CachedMapSeatConfig;
import com.edp2021c1.randomseatgenerator.util.config.SeatConfigHolder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.val;

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

    private final IntegerProperty randomBetweenRowsProperty;

    private final StringProperty disabledLastRowPosProperty;

    private final StringProperty nameListProperty;

    private final StringProperty groupLeaderListProperty;

    private final StringProperty separateListProperty;

    private final BooleanProperty luckyOptionProperty;

    private final BooleanProperty exportWritableProperty;

    private final BooleanProperty darkModeProperty;

    private final SeatConfigHolder source;

    private final CachedMapSeatConfig content;

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
     * @param luckyOptionCheck         input of lucky option
     * @param exportWritableCheck      input of whether the seat table will be exported to a writable chart
     * @param darkModeCheck            input of dark mode
     * @param applyBtnDisabledProperty property of whether the current globalHolder config is equal to the config in the pane,
     *                                 usually decides whether the apply button is disabled, ignored if is null
     * @param configSource             holder of the config
     */
    public ConfigPane(
            final IntegerField rowCountInput,
            final IntegerField columnCountInput,
            final IntegerField rbrInput,
            final TextField disabledLastRowPosInput,
            final TextField nameListInput,
            final TextField groupLeaderListInput,
            final TextArea separateListInput,
            final CheckBox luckyOptionCheck,
            final CheckBox exportWritableCheck,
            final CheckBox darkModeCheck,
            final BooleanProperty applyBtnDisabledProperty,
            final SeatConfigHolder configSource
    ) {
        super();

        rowCountProperty = rowCountInput.valueProperty();
        columnCountProperty = columnCountInput.valueProperty();
        randomBetweenRowsProperty = rbrInput.valueProperty();
        disabledLastRowPosProperty = disabledLastRowPosInput.textProperty();
        nameListProperty = nameListInput.textProperty();
        groupLeaderListProperty = groupLeaderListInput.textProperty();
        separateListProperty = separateListInput.textProperty();
        luckyOptionProperty = luckyOptionCheck.selectedProperty();
        exportWritableProperty = exportWritableCheck.selectedProperty();
        darkModeProperty = darkModeCheck.selectedProperty();
        applyButtonDisabledProperty = applyBtnDisabledProperty;

        source = configSource;
        content = source.getClone();

        val box1 = new HBox(rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        box1.setPrefHeight(60);
        box1.setAlignment(Pos.CENTER);
        val box2 = new HBox(nameListInput, groupLeaderListInput, separateListInput, luckyOptionCheck);
        box2.setPrefHeight(60);
        box2.setAlignment(Pos.CENTER);
        val box3 = new HBox(exportWritableCheck, darkModeCheck);
        box3.setPrefHeight(60);
        box3.setAlignment(Pos.CENTER);
        getChildren().addAll(box1, box2, box3);

        exportWritableProperty.bindBidirectional(FXUtils.exportWritableProperty());
        darkModeProperty.bindBidirectional(FXUtils.globalDarkModeProperty());

        if (applyBtnDisabledProperty == null) {
            return;
        }
        rowCountProperty.subscribe(newValue -> {
            content.setRowCount(newValue.intValue());
            refreshState();
        });
        columnCountProperty.subscribe(newValue -> {
            content.setColumnCount(newValue.intValue());
            refreshState();
        });
        randomBetweenRowsProperty.subscribe(newValue -> {
            content.setRandomBetweenRows(newValue.intValue());
            refreshState();
        });
        disabledLastRowPosProperty.subscribe(newValue -> {
            content.setDisabledLastRowPos(newValue);
            refreshState();
        });
        nameListProperty.subscribe(newValue -> {
            content.setNames(newValue);
            refreshState();
        });
        groupLeaderListProperty.subscribe(newValue -> {
            content.setGroupLeaders(newValue);
            refreshState();
        });
        separateListProperty.subscribe(newValue -> {
            content.setSeparatedPairs(newValue);
            refreshState();
        });
        luckyOptionProperty.subscribe(newValue -> {
            content.setLucky(newValue);
            refreshState();
        });
    }

    private boolean checkEquals() {
        return Objects.equals(content, source.getClone());
    }

    /**
     * Refreshes the state of {@link #applyButtonDisabledProperty}.
     */
    public void refreshState() {
        applyButtonDisabledProperty.set(checkEquals());
    }

    /**
     * Returns a copy of the current config
     *
     * @return a copy of {@link #content}
     */
    public CachedMapSeatConfig getContent() {
        return content.cloneThis();
    }

    /**
     * Resets the pane with the given config.
     *
     * @param config to set to the pane.
     */
    public void setContent(final CachedMapSeatConfig config) {
        if (config == null) {
            return;
        }
        rowCountProperty.setValue(config.getRowCount());
        columnCountProperty.setValue(config.getColumnCount());
        randomBetweenRowsProperty.setValue(config.getRandomBetweenRows());
        disabledLastRowPosProperty.set(config.getDisabledLastRowPos());
        nameListProperty.set(config.getNames());
        groupLeaderListProperty.set(config.getGroupLeaders());
        separateListProperty.set(config.getSeparatedPairs());
        luckyOptionProperty.set(config.getLucky());
        exportWritableProperty.set(FXUtils.exportWritableProperty().get());
        darkModeProperty.set(FXUtils.globalDarkModeProperty().get());
    }

}
