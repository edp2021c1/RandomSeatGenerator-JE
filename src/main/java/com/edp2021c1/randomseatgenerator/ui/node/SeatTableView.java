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

public class SeatTableView extends HBox {
    private SeatTable seatTable;
    private int rowCount;
    private int columnCount;

    private SeatTableView() {

        super();
        getStyleClass().add("seat-table-view");
        setAlignment(Pos.CENTER);
    }

    public SeatTableView(final SeatTable seatTable) {
        this();
        setSeatTable(seatTable);
    }

    public SeatTableView(final SeatConfig config) {
        this();

        setEmptySeatTable(config);
    }

    public void setSeatTable(final SeatTable seatTable) {
        this.seatTable = seatTable;

        SeatConfig config = seatTable.getConfig();
        this.rowCount = config.getRowCount();
        this.columnCount = config.getColumnCount();

        refresh();
    }

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
