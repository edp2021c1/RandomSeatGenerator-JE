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
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * View of seat table.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableView extends HBox {
    private SeatTable seatTable;
    private int rowCount;
    private int columnCount;

    /**
     * Default constructor.
     */
    private SeatTableView() {
        super();
        getStyleClass().add("seat-table-view");
        setAlignment(Pos.CENTER);
    }

    /**
     * Creates a view showing the given {@code SeatTable}.
     *
     * @param seatTable {@code SeatTable} to be shown in this view
     */
    public SeatTableView(final SeatTable seatTable) {
        this();
        setSeatTable(seatTable);
    }

    /**
     * Creates a view showing an empty {@code SeatTable}.
     *
     * @param config used to generate the empty seat table
     */
    public SeatTableView(final SeatConfig config) {
        this();

        setEmptySeatTable(config);
    }

    /**
     * Sets the value of {@code seatTable} and refreshes the
     * view to display the table.
     *
     * @param seatTable to be displayed
     */
    public void setSeatTable(final SeatTable seatTable) {
        this.seatTable = seatTable;

        SeatConfig config = seatTable.getConfig();
        this.rowCount = config.getRowCount();
        this.columnCount = config.getColumnCount();

        refresh();
    }

    /**
     * Sets {@code seatTable} as empty and refreshes the
     * view to display the table.
     *
     * @param config used to generate the empty seat table
     */
    public void setEmptySeatTable(final SeatConfig config) {
        this.seatTable = new SeatGenerator().generateEmptySeat(config);

        this.rowCount = config.getRowCount();
        this.columnCount = config.getColumnCount();

        refresh();
    }

    private void refresh() {
        final VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        final List<SeatRowData> seatRowData = SeatRowData.fromSeat(seatTable);
        SeatTableRow row;
        for (final SeatRowData s : seatRowData) {
            row = new SeatTableRow(s, columnCount);
            row.prefHeightProperty().bind(heightProperty().divide(rowCount));
            box.getChildren().add(row);
        }
        setHgrow(box, Priority.ALWAYS);
        getChildren().clear();
        getChildren().add(box);
    }
}
