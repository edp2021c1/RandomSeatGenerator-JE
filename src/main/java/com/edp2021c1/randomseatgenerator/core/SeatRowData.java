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

package com.edp2021c1.randomseatgenerator.core;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.edp2021c1.randomseatgenerator.core.SeatConfig.MAX_COLUMN_COUNT;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
public class SeatRowData {

    private final String c1;
    private final String c2;
    private final String c3;
    private final String c4;
    private final String c5;
    private final String c6;
    private final String c7;
    private final String c8;
    private final String c9;
    private final String c10;
    private final String c11;
    private final String c12;
    private final String c13;
    private final String c14;
    private final String c15;
    private final String c16;
    private final String c17;
    private final String c18;
    private final String c19;
    private final String c20;

    private SeatRowData() {
        c1 = c2 = c3 = c4 = c5 = c6 = c7 = c8 = c9 = c10 = c11 = c12 = c13 = c14 = c15 = c16 = c17 = c18 = c19 = c20 = "-";
    }

    private SeatRowData(String... c) {
        this();
        if (c.length > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(String.format("Count of people in a row cannot be larger than %d.", MAX_COLUMN_COUNT));
        }
        for (int i = 0, j = c.length; i < j; i++) {
            try {
                Field f = this.getClass().getDeclaredField(String.format("c%d", (i + 1)));
                f.setAccessible(true);
                f.set(this, c[i]);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns a list of {@code SeatRowData} containing data of a seat table.
     *
     * @param seatTable an instance of {@link SeatTable} being transferred.
     * @return a {@code List} storing {@code SeatRowData} transferred from a {@code SeatTable}.
     */
    public static List<SeatRowData> fromSeat(SeatTable seatTable) {
        SeatConfig conf = seatTable.getConfig();
        int rowCount = conf.getRowCount(), columnCount = conf.getColumnCount();
        List<String> s = seatTable.getSeatTable();
        List<SeatRowData> seatRowData = new ArrayList<>(rowCount);
        String[] tmp = new String[columnCount];

        outer:
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (i * columnCount + j >= s.size()) {
                    break outer;
                }
                tmp[j] = s.get(i * columnCount + j);
            }
            seatRowData.add(new SeatRowData(tmp));
        }

        if (seatTable.getConfig().lucky_option) {
            seatRowData.add(new SeatRowData("Lucky Person", seatTable.getLuckyPerson()));
        }
        seatRowData.add(new SeatRowData("Seed", Long.toString(seatTable.getSeed())));
        return seatRowData;
    }

    /**
     * Returns an empty seat table with specified row count and column count.
     * <p>
     * Every seat in the seat table will be "-".
     *
     * @param rowCount    of the empty seat table.
     * @param columnCount of the empty seat table.
     * @return a {@code List} storing {@code SeatRowData} of an empty seat table.
     */
    public static List<SeatRowData> emptySeat(int rowCount, int columnCount) {
        String[] emptyRowData = new String[columnCount];
        Arrays.fill(emptyRowData, SeatTable.EMPTY_SEAT_PLACEHOLDER);

        SeatRowData emptyRow = new SeatRowData(emptyRowData);

        SeatRowData[] list = new SeatRowData[rowCount];
        Arrays.fill(list, emptyRow);

        return Arrays.asList(list);
    }
}
