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
import lombok.Getter;

import java.util.AbstractList;
import java.util.Arrays;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.range;

/**
 * Saves a row of a seat table.
 *
 * @author Calboot
 * @since 1.0.1
 */
@Getter
@ExcelIgnoreUnannotated
public class RowData extends AbstractList<String> {

    private final String[] cells;
    private final int cellCount;

    @Getter
    private final boolean header;

    /**
     * Instantiates this class.
     */
    private RowData(final boolean header, final String... cells) {
        this.header = header;
        cellCount = cells.length;
        this.cells = Arrays.copyOf(cells, cellCount);
    }

    /**
     * Constructs and returns a row containing the given cells
     *
     * @param cells cell data
     * @return a row containing the given cells
     */
    static RowData of(final String... cells) {
        return (cells == null ? new RowData(false) : new RowData(false, cells));
    }

    /**
     * Constructs and returns a header row.
     *
     * @param columnCount count of column
     * @return a header row
     */
    static RowData header(final int columnCount) {
        return new RowData(true, buildList(range(1, columnCount + 1), i -> "Column " + i).toArray(new String[columnCount]));
    }

    /**
     * Returns name on the given index
     *
     * @param index column index, possibly larger than {@link #cellCount}
     * @return name on the given index
     */
    @Override
    public String get(final int index) {
        return index > cellCount - 1 ? null : cells[index];
    }

    @Override
    public int size() {
        return cellCount;
    }

    @Override
    @SuppressWarnings("all")
    public Object[] toArray() {
        return Arrays.copyOf(cells, cellCount);
    }
}
