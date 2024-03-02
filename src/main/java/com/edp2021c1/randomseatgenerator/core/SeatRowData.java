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

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.range;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
@ExcelIgnoreUnannotated
public class SeatRowData {

    private static final List<Field> fields;

    static {
        final Class<SeatRowData> clazz = SeatRowData.class;
        fields = buildList(range(1, SeatTable.MAX_COLUMN_COUNT + 1), i -> {
            final Field field;
            try {
                field = clazz.getDeclaredField("c" + i);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            return field;
        });
    }

    @ExcelProperty(value = "Column 1")
    private final String c1 = null;
    @ExcelProperty(value = "Column 2")
    private final String c2 = null;
    @ExcelProperty(value = "Column 3")
    private final String c3 = null;
    @ExcelProperty(value = "Column 4")
    private final String c4 = null;
    @ExcelProperty(value = "Column 5")
    private final String c5 = null;
    @ExcelProperty(value = "Column 6")
    private final String c6 = null;
    @ExcelProperty(value = "Column 7")
    private final String c7 = null;
    @ExcelProperty(value = "Column 8")
    private final String c8 = null;
    @ExcelProperty(value = "Column 9")
    private final String c9 = null;
    @ExcelProperty(value = "Column 10")
    private final String c10 = null;
    @ExcelProperty(value = "Column 11")
    private final String c11 = null;
    @ExcelProperty(value = "Column 12")
    private final String c12 = null;
    @ExcelProperty(value = "Column 13")
    private final String c13 = null;
    @ExcelProperty(value = "Column 14")
    private final String c14 = null;
    @ExcelProperty(value = "Column 15")
    private final String c15 = null;
    @ExcelProperty(value = "Column 16")
    private final String c16 = null;
    @ExcelProperty(value = "Column 17")
    private final String c17 = null;
    @ExcelProperty(value = "Column 18")
    private final String c18 = null;
    @ExcelProperty(value = "Column 19")
    private final String c19 = null;
    @ExcelProperty(value = "Column 20")
    private final String c20 = null;

    private final String[] data = new String[20];

    /**
     * Instantiates this class with the given names.
     *
     * @param names name data
     */
    SeatRowData(final String... names) {
        final int len = names.length;
        if (len > SeatTable.MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(
                    "Count of people in a row cannot be larger than " + SeatTable.MAX_COLUMN_COUNT
            );
        }
        try {
            for (int i = 0; i < len; i++) {
                fields.get(i).set(this, names[i]);
            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        System.arraycopy(names, 0, data, 0, len);
    }

    /**
     * Returns name on the given index
     *
     * @param columnIndex column index
     * @return name on the given index
     */
    public String getName(final int columnIndex) {
        if (columnIndex >= SeatTable.MAX_COLUMN_COUNT) {
            throw new IndexOutOfBoundsException();
        }
        return data[columnIndex];
    }

}
