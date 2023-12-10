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

import com.edp2021c1.randomseatgenerator.util.AppConfig;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.createHBox;

/**
 * Config pane.
 * <p>
 * Method {@link #getConfig()} needs to be overridden because
 * the source of config might not be specific.
 *
 * @author Calboot
 * @since 1.3.4
 */
@Getter
public abstract class ConfigPane extends VBox {
    private final TextField rowCountInput;

    private final TextField columnCountInput;

    private final TextField rbrInput;

    private final TextField disabledLastRowPosInput;

    private final TextField nameListInput;

    private final TextField groupLeaderListInput;

    private final TextArea separateListInput;

    private final CheckBox luckyOptionCheck;

    private final CheckBox exportWritableCheck;

    /**
     * @param rowCountInput            input of {@code row_count}
     * @param columnCountInput         input of {@code column_count}
     * @param rbrInput                 input of {@code random_between_rows}
     * @param disabledLastRowPosInput  input of {@code last_row_pos_cannot_be_chosen}
     * @param nameListInput            input of {@code person_sort_by_height}
     * @param groupLeaderListInput     input of {@code group_leader_list}
     * @param separateListInput        input of {@code separate_list}
     * @param luckyOptionCheck         input of {@code lucky_option}
     * @param exportWritableCheck      input of {@code export_writable}
     * @param applyBtnDisabledProperty property of whether the current global config is equal to the config in the pane,
     *                                 usually decides whether the apply button is disabled. Ignored if is null.
     */
    public ConfigPane(TextField rowCountInput,
                      TextField columnCountInput,
                      TextField rbrInput,
                      TextField disabledLastRowPosInput,
                      TextField nameListInput,
                      TextField groupLeaderListInput,
                      TextArea separateListInput,
                      CheckBox luckyOptionCheck,
                      CheckBox exportWritableCheck,
                      BooleanProperty applyBtnDisabledProperty) {
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

        HBox box1 = createHBox(1212, 60, rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        HBox box2 = createHBox(1212, 70, nameListInput, groupLeaderListInput, separateListInput, luckyOptionCheck);
        HBox box3 = createHBox(1212, 60, exportWritableCheck);
        getChildren().addAll(box1, box2, box3);

        if (applyBtnDisabledProperty == null) {
            return;
        }
        rowCountInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().row_count.equals(newValue)));
        columnCountInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().column_count.equals(newValue)));
        rbrInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().random_between_rows.equals(newValue)));
        disabledLastRowPosInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().last_row_pos_cannot_be_chosen.equals(newValue)));
        nameListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().person_sort_by_height.equals(newValue)));
        groupLeaderListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().group_leader_list.equals(newValue)));
        separateListInput.textProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(getConfig().separate_list.equals(newValue)));
        luckyOptionCheck.selectedProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(newValue == getConfig().lucky_option));
        exportWritableCheck.selectedProperty().addListener((observable, oldValue, newValue) ->
                applyBtnDisabledProperty.set(newValue == getConfig().export_writable));
    }

    /**
     * @return config loaded from source.
     */
    protected abstract AppConfig getConfig();

    /**
     * Resets the pane with the given config.
     *
     * @param config to set to the pane.
     */
    public void reset(AppConfig config) {
        rowCountInput.setText(config.row_count);
        columnCountInput.setText(config.column_count);
        rbrInput.setText(config.random_between_rows);
        disabledLastRowPosInput.setText(config.last_row_pos_cannot_be_chosen);
        nameListInput.setText(config.person_sort_by_height);
        groupLeaderListInput.setText(config.group_leader_list);
        separateListInput.setText(config.separate_list);
        luckyOptionCheck.setSelected(config.lucky_option);
        exportWritableCheck.setSelected(config.export_writable);
    }

}
