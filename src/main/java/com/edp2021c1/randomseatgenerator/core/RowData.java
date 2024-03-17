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
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.Getter;
import lombok.val;

import java.util.AbstractList;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.MAX_COLUMN_COUNT;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.range;

/**
 * Saves a row of a seat table.
 *
 * @since 1.0.1
 */

@Getter
@ColumnWidth(12)
@ExcelIgnoreUnannotated
public class RowData extends AbstractList<String> {

    private static final String[] headerRow = new String[MAX_COLUMN_COUNT];

    static {
        System.arraycopy(
                buildList(range(1, MAX_COLUMN_COUNT + 1), i -> "Column " + i).toArray(new String[0]), 0,
                headerRow, 0,
                MAX_COLUMN_COUNT
        );
    }

    private final String[] cells = new String[MAX_COLUMN_COUNT];
    private final int cellCount;
    @Getter
    private final boolean header;

    /**
     * Instantiates this class.
     */
    private RowData(final boolean header, final String... cells) {
        this.header = header;

        this.cellCount = cells.length;
        if (cellCount > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(
                    "Count of people in a row cannot be larger than " + MAX_COLUMN_COUNT
            );
        }

        System.arraycopy(cells, 0, this.cells, 0, cellCount);
    }

    /**
     * Constructs and returns a row containing the given cells
     *
     * @param cells cell data
     * @return a row containing the given cells
     */
    public static RowData of(final String... cells) {
        return (cells == null ? new RowData(false) : new RowData(false, cells));
    }

    /**
     * Constructs and returns a header row.
     *
     * @param columnCount count of column
     * @return a header row
     */
    public static RowData headerRow(final int columnCount) {
        if (columnCount > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(
                    "Count of people in a row cannot be larger than " + MAX_COLUMN_COUNT
            );
        }
        val v = new String[columnCount];
        System.arraycopy(headerRow, 0, v, 0, columnCount);
        return new RowData(true, v);
    }

    /**
     * Returns name on the given index
     *
     * @param index column index
     * @return name on the given index
     */
    @Override
    public String get(final int index) {
        return cells[index];
    }

    @Override
    public int size() {
        return MAX_COLUMN_COUNT;
    }
}
