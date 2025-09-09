/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

import com.google.common.collect.Sets;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class SeatTableRow extends HBox {

    private static final String DEFAULT_STYLE_CLASS = "seat-table-row";

    private static final PseudoClass PSEUDO_CLASS_HEADER
            = PseudoClass.getPseudoClass("header");

    public static SeatTableRow createHeader(int columnCount, DoubleBinding height) {
        return new SeatTableRow(IntStream.range(0, columnCount).mapToObj(i -> "Column " + (i + 1)).toList(), columnCount, Sets.newHashSet(), height, true);
    }

    public SeatTableRow(List<String> rowData, int columnCount, DoubleBinding height) {
        this(rowData, columnCount, Sets.newHashSet(), height);
    }

    public SeatTableRow(List<String> rowData, int columnCount, Set<Integer> leaders, DoubleBinding height) {
        this(rowData, columnCount, leaders, height, false);
    }

    private SeatTableRow(List<String> rowData, int columnCount, Set<Integer> leaders, DoubleBinding height, boolean header) {
        super();

        VBox.setVgrow(this, Priority.ALWAYS);
        setAlignment(Pos.CENTER);

        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded() && c.getAddedSubList().stream().anyMatch(n -> !(n instanceof SeatTableCell))) {
                    throw new UnsupportedOperationException("Cannot add a non-cell child");
                }
            }
        });

        getChildren().clear();

        for (int i = 0; i < columnCount; i++) {
            SeatTableCell cell = new SeatTableCell(i >= rowData.size() ? null : rowData.get(i), leaders.contains(i));
            cell.prefHeightProperty().bind(heightProperty());
            cell.prefWidthProperty().bind(widthProperty().divide(columnCount));
            getChildren().add(cell);
        }

        pseudoClassStateChanged(PSEUDO_CLASS_HEADER, header);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        prefHeightProperty().bind(height);
    }

}
