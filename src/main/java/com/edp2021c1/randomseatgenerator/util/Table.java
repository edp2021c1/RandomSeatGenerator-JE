/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

package com.edp2021c1.randomseatgenerator.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Table<E> extends AbstractCollection<E> implements RandomAccess {

    @Getter
    protected final int rowCount;

    @Getter
    protected final int columnCount;

    protected final int size;

    @Getter
    protected final E[] data;

    @Contract(value = "_, _, null -> fail")
    public Table(int rowCount, int columnCount, E[] e) {
        if (rowCount < 0 || columnCount < 0) {
            throw new IllegalArgumentException();
        }
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.size = rowCount * columnCount;
        this.data = Arrays.copyOf(e, size);
    }

    public List<E> getDataAsList() {
        return Arrays.asList(data);
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return Iterators.forArray(data);
    }

    @Override
    public int size() {
        return size;
    }

    @Nullable
    public E get(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || columnIndex < 0 || rowIndex >= rowCount || columnIndex >= columnCount) {
            return null;
        }
        return data[rowIndex * columnCount + columnIndex];
    }

    public Optional<E> getOptional(int rowIndex, int columnIndex) {
        return Optional.ofNullable(get(rowIndex, columnIndex));
    }

    public Row<E> getRow(int rowIndex) {
        return new Row<>(this, rowIndex);
    }

    public Column<E> getColumn(int columnIndex) {
        return new Column<>(this, columnIndex);
    }

    public Stream<Row<E>> getRows() {
        return IntStream.range(0, rowCount).mapToObj(i -> new Row<>(this, i));
    }

    public Stream<Column<E>> getColumns() {
        return IntStream.range(0, columnCount).mapToObj(i -> new Column<>(this, i));
    }

    public List<E> getNeighbours(int rowIndex, int columnIndex) {
        List<E> list = Lists.newArrayListWithExpectedSize(4);
        getOptional(rowIndex - 1, columnIndex).ifPresent(list::add);
        getOptional(rowIndex + 1, columnIndex).ifPresent(list::add);
        getOptional(rowIndex, columnIndex - 1).ifPresent(list::add);
        getOptional(rowIndex, columnIndex + 1).ifPresent(list::add);
        return list;
    }

    public static class Row<E> extends AbstractList<E> implements RandomAccess {

        private final Table<E> table;

        private final int offset;

        public Row(Table<E> table, int index) {
            this.table = table;
            this.offset = index * table.columnCount;
        }

        @Override
        public E get(int index) {
            return table.data[index + offset];
        }

        @Override
        public int size() {
            return table.columnCount;
        }

    }

    public static class Column<E> extends AbstractList<E> implements RandomAccess {

        private final Table<E> table;

        private final int offset;

        public Column(Table<E> table, int index) {
            this.table = table;
            this.offset = index;
        }

        @Override
        public E get(int index) {
            return table.data[index * table.columnCount + offset];
        }

        @Override
        public int size() {
            return table.rowCount;
        }

    }

}
