package com.edp2021c1.randomseatgenerator.v2.util;

import com.edp2021c1.randomseatgenerator.v2.seat.SeatConfig;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatGenerator;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatTable;
import org.apache.commons.io.file.PathUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        for (; i <= table.rowCount; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < table.columnCount; j++) {
                String s;
                if (table.isLeader(i - 1, j)) {
                    s = "{" + table.get(i - 1, j) + "}";
                } else {
                    s = table.get(i - 1, j);
                }
                row.createCell(j).setCellValue(s);
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

    public static SeatTable generate(SeatConfig config, String seed) {
        return new SeatGenerator(config).generate(seed);
    }

    public static SeatTable generateEmpty(SeatConfig config) {
        return new SeatGenerator(config).generateEmpty();
    }

    public static void exportToXlsx(SeatTable table, Path path) throws IOException {
        if (!path.toString().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Unmatched file extension: *.xlsx expected but %s is found".formatted(path.getFileName()));
        }
        exportToMsChart(table, path, new XSSFWorkbook());
    }

    public static void exportToXls(SeatTable table, Path path) throws IOException {
        if (!path.toString().endsWith(".xls")) {
            throw new IllegalArgumentException("Unmatched file extension: *.xls expected but %s is found".formatted(path.getFileName()));
        }
        exportToMsChart(table, path, new HSSFWorkbook());
    }

}
