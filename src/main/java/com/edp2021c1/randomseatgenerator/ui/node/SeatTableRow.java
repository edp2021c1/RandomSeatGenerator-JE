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

import com.edp2021c1.randomseatgenerator.core.RowData;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.val;

/**
 * Row of {@code SeatTableView}.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableRow extends HBox {

    private static final String DEFAULT_STYLE_CLASS = "seat-table-row";

    private static final PseudoClass PSEUDO_CLASS_HEADER
            = PseudoClass.getPseudoClass("header");

    /**
     * Creates a row with the given data.
     *
     * @param rowData     of this row
     * @param columnCount of this row
     */
    public SeatTableRow(final RowData rowData, final int columnCount) {
        super();
        VBox.setVgrow(this, Priority.ALWAYS);
        setAlignment(Pos.CENTER);

        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded() && c.getAddedSubList().parallelStream().anyMatch(n -> !(n instanceof SeatTableCell))) {
                    throw new UnsupportedOperationException("Cannot add a non-cell child");
                }
            }
        });

        getChildren().clear();

        val cellCount = Math.max(2, columnCount);
        for (var i = 0; i < cellCount; i++) {
            val cell = new SeatTableCell(rowData.get(i));
            cell.prefHeightProperty().bind(heightProperty());
            cell.prefWidthProperty().bind(widthProperty().divide(columnCount));
            getChildren().add(cell);
        }

        pseudoClassStateChanged(PSEUDO_CLASS_HEADER, rowData.isHeader());

        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

}
