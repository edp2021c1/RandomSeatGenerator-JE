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

package com.edp2021c1.randomseatgenerator.ui.control;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.createHBox;

/**
 * Pane containing inputs of {@code SeatConfig}.
 *
 * @author Calboot
 * @since 1.3.4
 */
@Getter
public class SeatConfigPane extends VBox {
    private final TextField rowCountInput;

    private final TextField columnCountInput;

    private final TextField rbrInput;

    private final TextField disabledLastRowPosInput;

    private final TextField nameListInput;

    private final TextField groupLeaderListInput;

    private final TextArea separateListInput;

    private final CheckBox luckyOptionCheck;

    public SeatConfigPane(TextField rowCountInput,
                          TextField columnCountInput,
                          TextField rbrInput,
                          TextField disabledLastRowPosInput,
                          TextField nameListInput,
                          TextField groupLeaderListInput,
                          TextArea separateListInput,
                          CheckBox luckyOptionCheck) {
        super();

        this.rowCountInput = rowCountInput;
        this.columnCountInput = columnCountInput;
        this.rbrInput = rbrInput;
        this.disabledLastRowPosInput = disabledLastRowPosInput;
        this.nameListInput = nameListInput;
        this.groupLeaderListInput = groupLeaderListInput;
        this.separateListInput = separateListInput;
        this.luckyOptionCheck = luckyOptionCheck;

        HBox box1 = createHBox(1212, 60, rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        HBox box2 = createHBox(1212, 69, nameListInput, groupLeaderListInput, separateListInput, luckyOptionCheck);
        getChildren().addAll(box1, box2);
    }

}
