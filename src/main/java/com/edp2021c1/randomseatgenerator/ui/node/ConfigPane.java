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

import com.edp2021c1.randomseatgenerator.ui.UIFactory;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Config pane.
 *
 * @author Calboot
 * @since 1.3.4
 */
public class ConfigPane extends VBox {
    private final IntegerField rowCountInput;

    private final IntegerField columnCountInput;

    private final IntegerField rbrInput;

    private final TextField disabledLastRowPosInput;

    private final TextField nameListInput;

    private final TextField groupLeaderListInput;

    private final TextArea separateListInput;

    private final CheckBox luckyOptionCheck;

    private final CheckBox exportWritableCheck;

    private final CheckBox darkModeCheck;

    private final ConfigHolder source;

    private final RawAppConfig current;

    /**
     * Constructs an instance.
     *
     * @param rowCountInput            input of {@code row_count}
     * @param columnCountInput         input of {@code column_count}
     * @param rbrInput                 input of {@code random_between_rows}
     * @param disabledLastRowPosInput  input of {@code last_row_pos_cannot_be_chosen}
     * @param nameListInput            input of {@code person_sort_by_height}
     * @param groupLeaderListInput     input of {@code group_leader_list}
     * @param separateListInput        input of {@code separate_list}
     * @param luckyOptionCheck         input of {@code lucky_option}
     * @param exportWritableCheck      input of {@code export_writable}
     * @param darkModeCheck            input of {@code dark_mode}
     * @param applyBtnDisabledProperty property of whether the current globalHolder config is equal to the config in the pane,
     *                                 usually decides whether the apply button is disabled. Ignored if is null.
     * @param configSource             holder of the config
     */
    public ConfigPane(final IntegerField rowCountInput,
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
                      final ConfigHolder configSource) {
        super();

        this.rowCountInput = rowCountInput;
        this.columnCountInput = columnCountInput;
        this.rbrInput = rbrInput;
        this.disabledLastRowPosInput = disabledLastRowPosInput;
        this.nameListInput = nameListInput;
        this.groupLeaderListInput = groupLeaderListInput;
        this.separateListInput = separateListInput;
        this.luckyOptionCheck = luckyOptionCheck;
        this.exportWritableCheck = exportWritableCheck;
        this.darkModeCheck = darkModeCheck;

        this.source = configSource;
        this.current = getConfigFromSource();

        final HBox box1 = new HBox(rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        box1.setPrefHeight(60);
        box1.setAlignment(Pos.CENTER);
        final HBox box2 = new HBox(nameListInput, groupLeaderListInput, separateListInput, luckyOptionCheck);
        box2.setPrefHeight(60);
        box2.setAlignment(Pos.CENTER);
        final HBox box3 = new HBox(exportWritableCheck, darkModeCheck);
        box3.setPrefHeight(60);
        box3.setAlignment(Pos.CENTER);
        getChildren().addAll(box1, box2, box3);

        if (applyBtnDisabledProperty == null) {
            return;
        }
        rowCountInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            current.row_count = newValue.intValue();
            applyBtnDisabledProperty.set(checkEquals());
        });
        columnCountInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            current.column_count = newValue.intValue();
            applyBtnDisabledProperty.set(checkEquals());
        });
        rbrInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            current.random_between_rows = newValue.intValue();
            applyBtnDisabledProperty.set(checkEquals());
        });
        disabledLastRowPosInput.textProperty().addListener((observable, oldValue, newValue) -> {
            current.last_row_pos_cannot_be_chosen = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        nameListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            current.person_sort_by_height = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        groupLeaderListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            current.group_leader_list = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        separateListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            current.separate_list = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        luckyOptionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            current.lucky_option = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        exportWritableCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            current.export_writable = newValue;
            applyBtnDisabledProperty.set(checkEquals());
        });
        darkModeCheck.selectedProperty().bindBidirectional(UIFactory.globalDarkModeProperty());
    }

    /**
     * Returns a copy of the current config
     *
     * @return a copy of {@link #current}
     */
    public RawAppConfig getCurrent() {
        return current.clone();
    }

    private boolean checkEquals() {
        final RawAppConfig cf = getConfigFromSource();
        return Objects.equals(current.row_count, cf.row_count)
                && Objects.equals(current.column_count, cf.column_count)
                && Objects.equals(current.random_between_rows, cf.random_between_rows)
                && Objects.equals(current.last_row_pos_cannot_be_chosen, cf.last_row_pos_cannot_be_chosen)
                && Objects.equals(current.person_sort_by_height, cf.person_sort_by_height)
                && Objects.equals(current.group_leader_list, cf.group_leader_list)
                && Objects.equals(current.separate_list, cf.separate_list)
                && Objects.equals(current.lucky_option, cf.lucky_option)
                && Objects.equals(current.export_writable, cf.export_writable);
    }

    /**
     * Returns the config loaded from source.
     *
     * @return config loaded from source.
     */
    protected RawAppConfig getConfigFromSource() {
        return source.get();
    }

    /**
     * Resets the pane with the given config.
     *
     * @param config to set to the pane.
     */
    public void reset(final RawAppConfig config) {
        if (config == null) {
            return;
        }
        rowCountInput.setValue(config.row_count);
        columnCountInput.setValue(config.column_count);
        rbrInput.setValue(config.random_between_rows);
        disabledLastRowPosInput.setText(config.last_row_pos_cannot_be_chosen);
        nameListInput.setText(config.person_sort_by_height);
        groupLeaderListInput.setText(config.group_leader_list);
        separateListInput.setText(config.separate_list);
        luckyOptionCheck.setSelected(config.lucky_option);
        exportWritableCheck.setSelected(config.export_writable);
        darkModeCheck.setSelected(config.dark_mode);
    }

}
