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

package com.edp2021c1.randomseatgenerator.util;

import com.alibaba.excel.EasyExcel;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import com.edp2021c1.randomseatgenerator.core.SeatTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

/**
 * Methods related to {@link SeatTable}.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class SeatTables {

    /**
     * Default exporting directory.
     */
    public static final Path DEFAULT_EXPORTING_DIR = Path.of(Metadata.USER_HOME, "Seat Tables");

    /**
     * Don't let anyone else instantiate this class.
     */
    private SeatTables() {
    }

    /**
     * Exports this instance to an Excel document (.xlsx).
     *
     * @param seatTable to export to Excel document
     * @param filePath  path of file to export seat table to
     * @param writable  if export seat table to a writable file
     * @throws IOException if an I/O error occurs
     */
    public static void exportToExcelDocument(final SeatTable seatTable, final Path filePath, final boolean writable) throws IOException {
        try {
            Objects.requireNonNull(filePath);
            Utils.delete(filePath);
            EasyExcel.write(filePath.toFile(), SeatRowData.class)
                    .sheet("座位表-%tF".formatted(new Date()))
                    .excludeColumnIndexes(CollectionUtils.range(Math.max(seatTable.getConfig().getColumnCount(), 2), SeatConfig.MAX_COLUMN_COUNT))
                    .doWrite(SeatRowData.fromSeat(seatTable));
            if (!(writable || filePath.toFile().setReadOnly())) {
                throw new RuntimeException("Failed to save seat table to " + filePath);
            }
        } catch (final IOException e) {
            throw new IOException("Failed to save seat table to " + filePath, e);
        }
    }

}
