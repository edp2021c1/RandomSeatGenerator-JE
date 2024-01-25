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
import com.edp2021c1.randomseatgenerator.core.SeatTableFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * View of seat table.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableView extends VBox {
    private static final String DEFAULT_STYLE_CLASS = "seat-table-view";
    private int rowCount;
    private int columnCount;
    private final ObjectProperty<SeatTable> seatTable = new SimpleObjectProperty<>(this, "seatTable") {
        @Override
        protected void invalidated() {
            if (get() == null) return;

            rowCount = get().getConfig().getRowCount();
            columnCount = get().getConfig().getColumnCount();

            getChildren().clear();
            get().toRowData().forEach(s -> {
                final SeatTableRow row = new SeatTableRow(s, columnCount);
                row.prefHeightProperty().bind(heightProperty().divide(rowCount));
                getChildren().add(row);
            });
        }
    };

    /**
     * Default constructor.
     */
    private SeatTableView() {
        super();
        setAlignment(Pos.CENTER);
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                for (final Node n : c.getAddedSubList()) {
                    if (!(n instanceof SeatTableRow)) {
                        throw new UnsupportedOperationException("Cannot add a non-row child");
                    }
                }
            }
        });
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
        this.seatTable.set(seatTable);
    }

    /**
     * Sets {@code seatTable} as empty and refreshes the
     * view to display the table.
     *
     * @param config used to generate the empty seat table
     */
    public void setEmptySeatTable(final SeatConfig config) {
        this.seatTable.set(SeatTableFactory.generateEmpty(config));
    }

}
