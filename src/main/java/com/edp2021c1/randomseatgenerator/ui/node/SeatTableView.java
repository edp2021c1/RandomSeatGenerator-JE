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

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.LinkedList;

/**
 * View of seat table.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableView extends VBox {

    private static final String DEFAULT_STYLE_CLASS = "seat-table-view";

    private final IntegerProperty rowCount = new SimpleIntegerProperty(this, "rowCount");

    private final IntegerProperty columnCount = new SimpleIntegerProperty(this, "columnCount");

    private final ObjectProperty<SeatTable> seatTable = new SimpleObjectProperty<>(this, "seatTable");

    /**
     * Creates a view showing an empty {@code SeatTable}.
     *
     * @param config used to generate the empty seat table
     */
    public SeatTableView(final SeatConfig config) {
        super();
        setAlignment(Pos.CENTER);
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        seatTable.subscribe(newValue -> {
            if (newValue == null) {
                return;
            }

            rowCount.set(newValue.getConfig().rowCount());
            columnCount.set(newValue.getConfig().columnCount());

            val list = new LinkedList<SeatTableRow>();
            for (val strings : newValue.toRowData()) {
                val seatTableRow = new SeatTableRow(strings, columnCount.get());
                seatTableRow.prefHeightProperty().bind(heightProperty().divide(rowCount));
                list.add(seatTableRow);
            }
            super.getChildren().setAll(list);
        });

        minHeightProperty().bind(rowCount.multiply(60));
        minWidthProperty().bind(columnCount.multiply(120));

        setEmptySeatTable(config);
    }

    /**
     * Sets {@code seatTable} as empty and refreshes the
     * view to display the table.
     *
     * @param config used to generate the empty seat table
     */
    public void setEmptySeatTable(final SeatConfig config) {
        seatTable.set(SeatTable.generateEmpty(config));
    }

    /**
     * Returns the property of the table shown.
     *
     * @return {@link #seatTable}
     */
    public ObjectProperty<SeatTable> seatTableProperty() {
        return seatTable;
    }

}
