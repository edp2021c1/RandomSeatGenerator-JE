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

import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;

/**
 * Row of {@code SeatTableView}.
 *
 * @author Calboot
 * @since 1.4.0
 */
class SeatTableRow extends HBox {
    /**
     * Creates a row with the given data.
     *
     * @param seatRowData of this row
     * @param columnCount of this row
     */
    public SeatTableRow(final SeatRowData seatRowData, final int columnCount) {
        VBox.setVgrow(this, Priority.ALWAYS);
        setAlignment(Pos.CENTER);

        final int cellCount = Math.max(columnCount, 2);
        final SeatTableCell[] cells = new SeatTableCell[cellCount];
        for (int i = 0; i < cellCount; i++) {
            try {
                final Field f = SeatRowData.getFields()[i];
                cells[i] = new SeatTableCell(f.get(seatRowData));
                cells[i].prefHeightProperty().bind(heightProperty());
                cells[i].prefWidthProperty().bind(widthProperty().divide(columnCount));
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        getChildren().addAll(cells);
    }
}
