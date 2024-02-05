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

import com.edp2021c1.randomseatgenerator.core.SeatTable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;

/**
 * Cell of {@code SeatTableView}.
 *
 * @author Calboot
 * @since 1.4.0
 */
public class SeatTableCell extends Label {
    private static final String DEFAULT_STYLE_CLASS = "seat-table-cell";
    private static final PseudoClass PSEUDO_CLASS_LEADER
            = PseudoClass.getPseudoClass("leader");

    /**
     * Creates a cell showing the given object.
     *
     * @param o {@code Object} to be shown
     */
    public SeatTableCell(final Object o) {
        setAlignment(Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
        setMinSize(120, 60);

        final String s = Objects.toString(o, "");
        setText(s);

        new SimpleBooleanProperty(this, "leader") {
            @Override
            protected void invalidated() {
                pseudoClassStateChanged(PSEUDO_CLASS_LEADER, get());
            }
        }.set(SeatTable.groupLeaderRegex.matcher(s).matches());

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }
}
