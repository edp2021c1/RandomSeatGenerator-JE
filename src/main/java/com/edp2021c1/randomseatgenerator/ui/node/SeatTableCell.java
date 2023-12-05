package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.core.SeatTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class SeatTableCell extends Label {
    private static final PseudoClass PSEUDO_CLASS_LEADER
            = PseudoClass.getPseudoClass("leader");

    public SeatTableCell(Object o) {
        setAlignment(Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
        setMinSize(40, 30);

        String s;
        if (o == null) {
            s = "";
        } else {
            s = o.toString();
        }
        setText(s);

        BooleanProperty leader = new BooleanPropertyBase(false) {
            @Override
            protected void invalidated() {
                pseudoClassStateChanged(PSEUDO_CLASS_LEADER, get());
            }

            @Override
            public Object getBean() {
                return SeatTableCell.this;
            }

            @Override
            public String getName() {
                return "leader";
            }
        };

        leader.set(s.length() > 1 && s.startsWith(SeatTable.GROUP_LEADER_IDENTIFIER) && s.endsWith(SeatTable.GROUP_LEADER_IDENTIFIER));

        getStyleClass().add("seat-table-cell");
    }
}
