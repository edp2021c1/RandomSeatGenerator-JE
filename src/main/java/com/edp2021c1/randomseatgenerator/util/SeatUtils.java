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

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import org.apache.commons.io.file.PathUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Path;

public final class SeatUtils {

    private static void exportToMsChart(SeatTable table, Path path, Workbook workbook) throws IOException {
        Sheet sheet = workbook.createSheet("Seat Table");
        int   i     = 0;
        Row   head  = sheet.createRow(i);
        for (int j = 0; j < table.columnCount; j++) {
            head.createCell(j).setCellValue("Column " + (i + 1));
        }
        i++;
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (; i <= table.rowCount; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < table.columnCount; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(table.get(i - 1, j));
                if (table.isLeader(i - 1, j)) {
                    cell.setCellStyle(style);
                }
            }
        }
        if (table.hasLuckyPerson()) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("LuckyPerson");
            row.createCell(1).setCellValue(table.getLuckyPerson());
            i++;
        }
        Row seedRow = sheet.createRow(i);
        seedRow.createCell(0).setCellValue("Seed");
        seedRow.createCell(1).setCellValue(table.getSeed());
        workbook.write(PathUtils.newOutputStream(path, false));
    }

    public static SeatTable generateEmpty(SeatConfig config) {
        return new SeatGenerator(config).generateEmpty();
    }

    public static void exportToXlsx(SeatTable table, Path path) throws IOException {
        exportToMsChart(table, path, new XSSFWorkbook());
    }

    public static void exportToXls(SeatTable table, Path path) throws IOException {
        exportToMsChart(table, path, new HSSFWorkbook());
    }

    public static void exportToCsv(SeatTable table, Path path) throws IOException {
        IOUtils.writeFile(path, table.toString().replace("[", "").replace("]", ""));
    }

    public static void export(SeatTable table, Path path) throws IOException {
        String s = path.toString();
        if (s.endsWith(".xlsx")) {
            exportToXlsx(table, path);
        } else if (s.endsWith(".xls")) {
            exportToXls(table, path);
        } else {
            exportToCsv(table, path);
        }
    }

}
