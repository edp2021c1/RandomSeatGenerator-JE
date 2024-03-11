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

import com.edp2021c1.randomseatgenerator.ui.UIUtils;
import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfig;
import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfigHolder;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.util.config.JSONAppConfig.*;

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

    private final JSONAppConfigHolder source;

    private final JSONAppConfig content;

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
                      final JSONAppConfigHolder configSource) {
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
        this.content = source.get();

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

        if (applyBtnDisabledProperty == null) {
            return;
        }
        rowCountInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_ROW_COUNT, newValue.intValue());
            applyBtnDisabledProperty.set(checkEquals());
        });
        columnCountInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_COLUMN_COUNT, newValue.intValue());
            applyBtnDisabledProperty.set(checkEquals());
        });
        rbrInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_RANDOM_BETWEEN_ROWS, newValue.intValue());
            applyBtnDisabledProperty.set(checkEquals());
        });
        disabledLastRowPosInput.textProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_DISABLED_LAST_ROW_POS, newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        nameListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_NAMES, newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        groupLeaderListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_GROUP_LEADERS, newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        separateListInput.textProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_SEPARATED_PAIRS, newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        luckyOptionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            content.put(KEY_LUCKY, newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        exportWritableCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            content.put("export.writable", newValue);
            applyBtnDisabledProperty.set(checkEquals());
        });
        darkModeCheck.selectedProperty().bindBidirectional(UIUtils.globalDarkModeProperty());
    }

    /**
     * Returns a copy of the current config
     *
     * @return a copy of {@link #content}
     */
    public JSONAppConfig getContent() {
        return content.cloneThis();
    }

    /**
     * Resets the pane with the given config.
     *
     * @param config to set to the pane.
     */
    public void setContent(final JSONAppConfig config) {
        if (config == null) {
            return;
        }
        rowCountInput.setValue(config.getRowCount());
        columnCountInput.setValue(config.getColumnCount());
        rbrInput.setValue(config.getRandomBetweenRows());
        disabledLastRowPosInput.setText(config.getString(KEY_DISABLED_LAST_ROW_POS));
        nameListInput.setText(config.getString(KEY_NAMES));
        groupLeaderListInput.setText(config.getString(KEY_GROUP_LEADERS));
        separateListInput.setText(config.getString(KEY_SEPARATED_PAIRS));
        luckyOptionCheck.setSelected(config.isLucky());
        exportWritableCheck.setSelected(Boolean.TRUE.equals(config.getBoolean("export.writable")));
        darkModeCheck.setSelected(!Boolean.FALSE.equals(config.getBoolean("appearance.style.dark")));
    }

    private boolean checkEquals() {
        return Objects.equals(content, source.get());
    }

}
