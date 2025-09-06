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

import com.edp2021c1.randomseatgenerator.v2.seat.SeatConfig;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatTable;
import com.edp2021c1.randomseatgenerator.v2.util.SeatUtils;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * View of seat table.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableView extends VBox {

    private static final String DEFAULT_STYLE_CLASS = "seat-table-view";

    private final IntegerProperty rowCount;

    private final IntegerProperty columnCount;

    private final ObjectProperty<SeatTable> seatTable;

    /**
     * Creates a view showing an empty {@code SeatTable}.
     *
     * @param config used to generate the empty seat table
     */
    public SeatTableView(SeatConfig config) {
        super();
        setAlignment(Pos.CENTER);
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        rowCount = new SimpleIntegerProperty(this, "rowCount");
        columnCount = new SimpleIntegerProperty(this, "columnCount");
        seatTable = new SimpleObjectProperty<>(this, "seatTable");

        seatTable.subscribe(newValue -> {
            if (newValue == null) {
                return;
            }

            rowCount.set(newValue.getRowCount() + (newValue.hasLuckyPerson() ? 3 : 2));
            columnCount.set(Math.max(newValue.getColumnCount(), 2));

            DoubleBinding            height = heightProperty().divide(rowCount);
            LinkedList<SeatTableRow> list   = new LinkedList<>();
            list.add(SeatTableRow.createHeader(columnCount.get(), height));
            for (int i = 0; i < newValue.getRowCount(); i++) {
                list.add(new SeatTableRow(newValue.getRow(i), columnCount.get(), newValue.getLeadersOfRow(i), height));
            }
            if (newValue.hasLuckyPerson()) {
                list.add(new SeatTableRow(Arrays.asList("Lucky Person", newValue.getLuckyPerson()), columnCount.get(), height));
            }
            list.add(new SeatTableRow(Arrays.asList("Seed", newValue.getSeed()), columnCount.get(), height));
            super.getChildren().setAll(list);
        });

        minHeightProperty().bind(rowCount.multiply(80));
        minWidthProperty().bind(columnCount.multiply(120));

        setEmptySeatTable(config);
    }

    /**
     * Sets {@code seatTable} as empty and refreshes the
     * view to display the table.
     *
     * @param config used to generate the empty seat table
     */
    public void setEmptySeatTable(SeatConfig config) {
        seatTable.set(SeatUtils.generateEmpty(config));
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
