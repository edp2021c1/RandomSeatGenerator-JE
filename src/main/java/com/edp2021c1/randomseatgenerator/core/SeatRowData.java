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
import java.util.List;

import static com.edp2021c1.randomseatgenerator.core.SeatConfig.MAX_COLUMN_COUNT;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.range;

/**
 * An instance of this class saves a row of a seat table. This class also provides a method to
 * turn a seat table into an {@code ArrayList} of this class.
 *
 * @since 1.0.1
 */

@Getter
public class SeatRowData {

    @ExcelIgnore
    @Getter
    private static final List<Field> fields;

    static {
        final Class<SeatRowData> clazz = SeatRowData.class;
        fields = buildList(range(1, MAX_COLUMN_COUNT + 1), i -> {
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

    public SeatRowData(final String... c) {
        final int len = c.length;
        if (len > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(
                    "Count of people in a row cannot be larger than " + MAX_COLUMN_COUNT
            );
        }
        for (int i = 0; i < len; i++) {
            try {
                fields.get(i).set(this, c[i]);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
