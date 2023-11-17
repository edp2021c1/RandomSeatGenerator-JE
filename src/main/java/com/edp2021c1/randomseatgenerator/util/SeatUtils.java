package com.edp2021c1.randomseatgenerator.util;

import com.alibaba.excel.EasyExcel;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import com.edp2021c1.randomseatgenerator.core.SeatTable;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * Contains methods related to {@link SeatTable}.
 */
public class SeatUtils {
    /**
     * Exports this instance to an Excel form file (.xlsx).
     *
     * @param seatTable to export to Excel document.
     * @param file      to export seat table to.
     * @throws IOException if failed to save seat table to Excel document.
     */
    public static void exportToExcelDocument(SeatTable seatTable, File file) throws IOException {
        Objects.requireNonNull(file);
        Date date = new Date();
        if (!file.createNewFile()) {
            if (!(file.delete() & file.createNewFile())) {
                throw new IOException("Failed to save seat table to Excel document.");
            }
        }
        EasyExcel.write(file, SeatRowData.class).sheet(String.format("座位表-%tF", date)).doWrite(SeatRowData.fromSeat(seatTable));
        if (!file.setReadOnly()) {
            throw new IOException("Failed to save seat table to Excel document.");
        }
    }
}
