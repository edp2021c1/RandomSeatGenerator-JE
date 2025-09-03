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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.edp2021c1.randomseatgenerator.util.DataUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.PathWrapper;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.Getter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Used to pack some useful data related to a seat table.
 *
 * @author Calboot
 * @since 1.2.0
 */
@Getter
public class SeatTable {

    private static final long MAX_GENERATING_TIME_SECONDS = 3L;

    private static final ExcelWriterBuilder excelWriterBuilder = EasyExcel.write().head(RowData.class);

    /**
     * Default exporting directory.
     */
    public static final PathWrapper DEFAULT_EXPORTING_DIR = PathWrapper.wrap(Metadata.USER_HOME, "Seat Tables");

    /**
     * Placeholder of an empty seat.
     */
    public static final String EMPTY_SEAT_PLACEHOLDER = "-";

    /**
     * Regular expression of a group leader.
     */
    public static final Predicate<String> groupLeaderRegexPredicate = Pattern.compile("\\*.*\\*").asMatchPredicate();

    /**
     * Format of a group leader.
     */
    public static final String groupLeaderFormat = "*%s*";

    /**
     * Generate a seat table using the specified config and the seed.
     *
     * @param config used to generate the seat table
     * @param seed   used to generate the seat table
     *
     * @return an instance of {@code SeatTable}
     *
     * @throws NullPointerException   if the config is null
     * @throws IllegalConfigException if the config has an illegal format, or if it
     *                                costs too much time to generate the seat table
     */
    public static SeatTable generate(final SeatConfig config, final String seed) {
        return generate(config, seed, SeatTableGenerator.defaultGenerator);
    }

    /**
     * Generate a seat table using the specified config and the seed.
     *
     * @param config    used to generate the seat table
     * @param seed      used to generate the seat table
     * @param generator used to generate the seat table
     *
     * @return an instance of {@code SeatTable}
     *
     * @throws NullPointerException   if the config is null.
     * @throws IllegalConfigException if the config has an illegal format, or if it
     *                                costs too much time to generate the seat table
     */
    public static SeatTable generate(final SeatConfig config, final String seed, final SeatTableGenerator generator) {
        try {
            return RuntimeUtils.runWithTimeout(() -> generator.generate(config, seed), MAX_GENERATING_TIME_SECONDS, TimeUnit.SECONDS);
        } catch (final Throwable e) {
            Throwable e1 = e;
            if (e1 instanceof ExecutionException) {
                e1 = e1.getCause();
            }
            if (e1 instanceof RuntimeException) {
                throw (RuntimeException) e1;
            }
            if (e1 instanceof TimeoutException) {
                IllegalConfigException ex  = new IllegalConfigException("Seat table generating timeout, please check your config or use another seed");
                IllegalConfigException exx = new IllegalConfigException("Seed: " + seed);
                throw new IllegalConfigException(List.of(ex, exx));
            }
            throw new RuntimeException(e1);
        }
    }

    /**
     * Generates an empty seat table.
     *
     * @param config used to generate the empty seat table
     *
     * @return an empty seat table
     */
    public static SeatTable generateEmpty(final SeatConfig config) {
        return generate(config, null, SeatTableGenerator.emptyGenerator);
    }

    /**
     * The seat table stored as a {@code  List}.
     */
    private final List<String> table;

    /**
     * The config used to generate the seat table.
     */
    private final SeatConfig config;

    /**
     * The seed used to generate the seat table.
     */
    private final String seed;

    /**
     * The lucky person specially chosen.
     */
    private final String luckyPerson;

    /**
     * Creates an instance.
     *
     * @param table       {@link #table}
     * @param config      {@link #config}
     * @param seed        {@link #seed}
     * @param luckyPerson {@link #luckyPerson}
     */
    public SeatTable(final List<String> table, final SeatConfig config, final String seed, final String luckyPerson) {
        this.table = table;
        this.config = config;
        this.seed = seed == null ? "$null$" : seed.isEmpty() ? "$empty_string$" : seed;
        this.luckyPerson = config.lucky() ? luckyPerson : null;
    }

    /**
     * Returns a list of {@code RowData} containing data of this.
     *
     * @return a {@code List} storing {@code RowData} transferred from this
     */
    public List<RowData> toRowData() {
        int                 columnCount = config.columnCount();
        LinkedList<RowData> rows        = new LinkedList<RowData>();

        rows.add(RowData.header(columnCount));
        rows.addAll(DataUtils.split(table, columnCount).map(RowData::of).toList());

        if (config.lucky()) {
            rows.add(RowData.of("Lucky Person", luckyPerson));
        }

        rows.add(RowData.of("Seed", seed));
        return rows;
    }

    /**
     * Exports this instance to a chart, either an Excel document (*.xlsx/*.xls), or an CSV file (.csv).
     *
     * @param filePath path of file to export to
     * @param writable if exports to a writable file
     *
     * @throws IOException if an I/O error occurs
     */
    public void exportToChart(final Path filePath, final boolean writable) throws IOException {
        if (filePath == null) {
            exportToChart(DEFAULT_EXPORTING_DIR.resolve("%tF.xlsx".formatted(new Date())), writable);
            return;
        }
        if (filePath.endsWith(".csv")) {
            PathWrapper.wrap(filePath).writeString(toString(",", System.lineSeparator()));
            return;
        }
        try {
            PathWrapper.wrap(filePath).moveToTrash().getParent().replaceWithDirectory();
            File f = filePath.toFile();
            excelWriterBuilder
                    .file(f)
                    .sheet("座位表-%tF".formatted(new Date()))
                    .doWrite(toRowData());
            if (!(writable || f.setReadOnly())) {
                throw new IOException("Failed to set output file \"%s\" to read-only".formatted(f));
            }
        } catch (final Throwable e) {
            throw new IOException("Failed to save seat table to \"%s\"".formatted(filePath), e);
        }
    }

    @Override
    public String toString() {
        return toString(System.lineSeparator());
    }

    /**
     * Joins the cells together into a string, separated by the given separator.
     *
     * @param lineSeparator between each row
     *
     * @return the result string
     */
    public String toString(final String lineSeparator) {
        return String.join(lineSeparator, toRowData().stream().map(RowData::toString).toList());
    }

    /**
     * Joins the cells and rows together into a string and returns the result.
     *
     * @param seatSeparator separator between each seat
     * @param rowSeparator  separator between each row
     *
     * @return the result string
     *
     * @see RowData#toString(String)
     */
    public String toString(final String seatSeparator, final String rowSeparator) {
        return String.join(rowSeparator, toRowData().stream().map(rowData -> rowData.toString(seatSeparator)).toList());
    }

}
