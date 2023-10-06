package com.edp2021c1.data;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
public class SeatRowData_Old {
    @ExcelIgnore
    private static final SeatRowData_Old emptyRow = new SeatRowData_Old();
    @ExcelIgnore
    public static final ArrayList<SeatRowData_Old> emptySeat = new ArrayList<>(Arrays.asList(emptyRow, emptyRow, emptyRow, emptyRow, emptyRow, emptyRow, emptyRow));
    private final String c1, c2, c3, c4, c5, c6, c7;

    public SeatRowData_Old() {
        c1 = c2 = c3 = c4 = c5 = c6 = c7 = "-";
    }

    public SeatRowData_Old(String c1, String c2, String c3, String c4, String c5, String c6, String c7) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        this.c5 = c5;
        this.c6 = c6;
        this.c7 = c7;
    }

    public static ArrayList<SeatRowData_Old> fromSeat(ArrayList<String> seat, long seed) {
        ArrayList<SeatRowData_Old> s = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            s.add(new SeatRowData_Old(seat.get(i * 7), seat.get(i * 7 + 1), seat.get(i * 7 + 2), seat.get(i * 7 + 3), seat.get(i * 7 + 4), seat.get(i * 7 + 5), seat.get(i * 7 + 6)));
        }
        s.add(new SeatRowData_Old("Seed:", Long.toString(seed), "", "", "", "", ""));

        return s;
    }
}
