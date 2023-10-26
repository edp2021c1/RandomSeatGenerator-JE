package com.edp2021c1.core;

import com.alibaba.excel.EasyExcel;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Used to pack some useful data related to a seat table.
 *
 * @author Calboot
 * @since 1.2.0
 */
@Getter
public class Seat {
    /**
     * The seat table stored as a {@code  List}.
     */
    private final List<String> seat;
    /**
     * The config used to generate the seat table.
     */
    private final SeatConfig config;
    /**
     * The seed used to generate the seat table.
     */
    private final long seed;
    /**
     * The lucky person specially chosen.
     *
     * @since 1.2.1
     */
    private final String luckyPerson;

    /**
     * @param seat        {@link #seat}
     * @param config      {@link #config}
     * @param seed        {@link #seed}
     * @param luckyPerson {@link #luckyPerson}
     */
    public Seat(List<String> seat, SeatConfig config, long seed, String luckyPerson) {
        this.seat = seat;
        this.config = config;
        this.seed = seed;
        if (luckyPerson == null) luckyPerson = "";
        this.luckyPerson = luckyPerson;
    }

    /**
     * @param file to export seat table to.
     * @throws IOException if an I/O error occurred.
     */
    public void exportToExcelDocument(File file) throws IOException {
        Objects.requireNonNull(file);
        Date date = new Date();
        if (!file.createNewFile()) {
            file.delete();
            file.createNewFile();
        }
        EasyExcel.write(file, SeatRowData.class).sheet(String.format("座位表-%tF", date)).doWrite(SeatRowData.fromSeat(this));
        file.setReadOnly();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        int rowCount = config.getRowCount(), columnCount = config.getColumnCount();

        str.append("Seed: ");
        str.append(seed);
        str.append("\n");

        str.append("Seat Table:\n");
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (i * columnCount + j >= seat.size()) return str.toString();
                str.append(seat.get(i * columnCount + j));
                str.append("\t");
            }
            str.append("\n");
        }

        if (config.lucky_option) {
            str.append("Lucky Person: ");
            str.append(luckyPerson);
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

}
