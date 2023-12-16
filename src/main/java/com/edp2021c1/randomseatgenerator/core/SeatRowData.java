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

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edp2021c1.randomseatgenerator.core.SeatConfig.MAX_COLUMN_COUNT;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
public class SeatRowData {

    @ExcelIgnore
    private static final Map<Integer, Field> fields = new HashMap<>(MAX_COLUMN_COUNT);

    static {
        try {
            for (int i = 0; i < MAX_COLUMN_COUNT; i++) {
                final Field field = SeatRowData.class.getDeclaredField("c" + (i + 1));
                field.setAccessible(true);
                fields.put(i, field);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final String c1 = null;
    private final String c2 = null;
    private final String c3 = null;
    private final String c4 = null;
    private final String c5 = null;
    private final String c6 = null;
    private final String c7 = null;
    private final String c8 = null;
    private final String c9 = null;
    private final String c10 = null;
    private final String c11 = null;
    private final String c12 = null;
    private final String c13 = null;
    private final String c14 = null;
    private final String c15 = null;
    private final String c16 = null;
    private final String c17 = null;
    private final String c18 = null;
    private final String c19 = null;
    private final String c20 = null;

    private SeatRowData(final String... c) {
        final int len = c.length;
        if (len > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(
                    "Count of people in a row cannot be larger than " + MAX_COLUMN_COUNT
            );
        }
        for (int i = 0; i < len; i++) {
            try {
                final Field f = fields.get(i);
                f.set(this, c[i]);
            } catch (final ReflectiveOperationException e) {
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
    public static List<SeatRowData> fromSeat(final SeatTable seatTable) {
        final SeatConfig conf = seatTable.getConfig();
        final int columnCount = conf.getColumnCount();
        final List<String> s = seatTable.getSeatTable();
        final List<SeatRowData> seatRowData = new ArrayList<>();
        final String[] tmp = new String[columnCount];

        for (int i = 0, j = 0; i < s.size(); i++, j = i % columnCount) {
            tmp[j] = s.get(i);
            if (j == columnCount - 1) {
                seatRowData.add(new SeatRowData(tmp));
            }
        }

        if (seatTable.getConfig().lucky_option) {
            seatRowData.add(new SeatRowData("Lucky Person", seatTable.getLuckyPerson()));
        }

        final String seed = seatTable.getSeed();
        if (seed.isEmpty()) {
            seatRowData.add(new SeatRowData("Seed", "empty_string"));
            return seatRowData;
        }
        seatRowData.add(new SeatRowData("Seed", seatTable.getSeed()));
        return seatRowData;
    }

}
