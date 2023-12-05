package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;

class SeatTableRow extends HBox {
    public SeatTableRow(final SeatRowData seatRowData, final int columnCount) {
        VBox.setVgrow(this, Priority.ALWAYS);
        setAlignment(Pos.CENTER);

        final int cellCount = Math.max(columnCount, 2);
        final SeatTableCell[] cells = new SeatTableCell[cellCount];
        for (int i = 0; i < cellCount; i++) {
            try {
                Field f = SeatRowData.class.getDeclaredField("c" + (i + 1));
                f.setAccessible(true);
                cells[i] = new SeatTableCell(f.get(seatRowData));
                cells[i].prefHeightProperty().bind(heightProperty());
                cells[i].prefWidthProperty().bind(widthProperty().divide(columnCount));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        getChildren().addAll(cells);
    }
}
