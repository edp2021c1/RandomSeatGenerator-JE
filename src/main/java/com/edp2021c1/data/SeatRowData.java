package com.edp2021c1.data;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.edp2021c1.core.Seat;
import com.edp2021c1.core.SeatConfig;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
public class SeatRowData {
    /**
     * Max count of column in a {@code SeatRowData}.
     */
    @ExcelIgnore
    public static final int MAX_COLUMN_COUNT = 20;

    private String c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20;

    /**
     * @param c array of names in a row of a seat table.
     */
    public SeatRowData(String... c) {
        for (int i = 0, j = c.length; i < j; i++) {
            if (!(i < MAX_COLUMN_COUNT)) {
                throw new RuntimeException(String.format("Column count cannot be larger than %d.", MAX_COLUMN_COUNT));
            }
            try{
                Field f = this.getClass().getDeclaredField(String.format("c%d", (i + 1)));
                f.setAccessible(true);
                f.set(this, c[i]);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param seat an instance of {@link Seat} being transferred.
     * @return a {@code List} storing {@code SeatRowData} transferred from a {@code Seat}.
     */
    public static List<SeatRowData> fromSeat(Seat seat) {
        SeatConfig conf = seat.getConfig();
        int rowCount = conf.getRowCount(), columnCount = conf.getColumnCount();
        List<String> s = seat.getSeat();
        List<SeatRowData> seatRowData = new ArrayList<>(rowCount);
        String[] tmp = new String[columnCount];
        outer:
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (i * columnCount + j >= s.size()) break outer;
                tmp[j] = s.get(i * columnCount + j);
            }
            seatRowData.add(new SeatRowData(tmp));
        }
        seatRowData.add(new SeatRowData("seed", Long.toString(seat.getSeed())));
        if (seat.getConfig().lucky_option) seatRowData.add(new SeatRowData("lucky person", seat.getLuckyPerson()));
        return seatRowData;
    }

    /**
     * @param rowCount    of the empty seat table.
     * @param columnCount of the empty seat table.
     * @return a {@code List} storing {@code SeatRowData} of an empty seat table.
     */
    public static List<SeatRowData> emptySeat(int rowCount, int columnCount) {
        String[] emptyRowData = new String[columnCount];
        Arrays.fill(emptyRowData, "-");

        SeatRowData emptyRow = new SeatRowData(emptyRowData);

        SeatRowData[] list = new SeatRowData[rowCount + 1];
        Arrays.fill(list, emptyRow);

        return Arrays.asList(list);
    }
}
