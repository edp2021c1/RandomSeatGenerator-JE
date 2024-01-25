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
import com.edp2021c1.randomseatgenerator.util.CollectionUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.Utils;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used to pack some useful data related to a seat table.
 *
 * @author Calboot
 * @since 1.2.0
 */
@Getter
public class SeatTable {

    /**
     * Default exporting directory.
     */
    public static final Path DEFAULT_EXPORTING_DIR = Path.of(Metadata.USER_HOME, "Seat Tables");
    /**
     * Placeholder of an empty seat.
     */
    public static final String emptySeatPlaceholder = "-";
    /**
     * Regular expression of a group leader.
     */
    public static final String groupLeaderRegex = "\\*+.+\\*";
    /**
     * Format of a group leader.
     */
    public static final String groupLeaderFormat = "*%s*";
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
        this.seed = seed;
        this.luckyPerson = luckyPerson;
    }

    /**
     * Returns a list of {@code SeatRowData} containing data of this.
     *
     * @return a {@code List} storing {@code SeatRowData} transferred from this.
     */
    public List<SeatRowData> toRowData() {
        final int columnCount = config.getColumnCount();
        final List<SeatRowData> seatRowData = new ArrayList<>(config.getRowCount());
        final String[] tmp = new String[columnCount];

        final int size = table.size();
        for (int i = 0, j = 0; i < size; i++, j = i % columnCount) {
            tmp[j] = table.get(i);
            if (j == columnCount - 1) {
                seatRowData.add(new SeatRowData(tmp));
            }
        }

        if (config.isLucky()) {
            seatRowData.add(new SeatRowData("Lucky Person", luckyPerson));
        }

        seatRowData.add(new SeatRowData("Seed", seed.isEmpty() ? "empty_string" : seed));
        return seatRowData;
    }

    @Override
    public String toString() {
        final int columnCount = config.getColumnCount();

        final StringBuilder str = new StringBuilder("Seed: ").append(seed).append("\nSeat Table:\n");

        for (int i = 0; i < table.size(); i++) {
            if (i % columnCount == 0) {
                str.append("\n");
            }
            str.append(table.get(i)).append("\t\t");
        }

        if (config.isLucky()) {
            str.append("\nLucky Person: ").append(luckyPerson);
        }

        return str.toString();
    }

    /**
     * Exports this instance to an Excel document (.xlsx).
     *
     * @param filePath path of file to export to
     * @param writable if exports to a writable file
     * @throws IOException if an I/O error occurs
     */
    public void exportToExcelDocument(final Path filePath, final boolean writable) throws IOException {
        try {
            Utils.delete(filePath);
            EasyExcel.write(filePath.toFile(), SeatRowData.class)
                    .sheet("座位表-%tF".formatted(new Date()))
                    .excludeColumnIndexes(CollectionUtils.range(Math.max(config.getColumnCount(), 2), SeatConfig.MAX_COLUMN_COUNT))
                    .doWrite(toRowData());
            if (!(writable || filePath.toFile().setReadOnly())) {
                throw new RuntimeException("Failed to save seat table to " + filePath);
            }
        } catch (final IOException e) {
            throw new IOException("Failed to save seat table to " + filePath, e);
        }
    }

}
