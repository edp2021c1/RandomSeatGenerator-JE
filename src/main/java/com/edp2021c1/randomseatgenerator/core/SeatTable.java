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

import lombok.Getter;

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
     * Placeholder of an empty seat.
     */
    public static final String EMPTY_SEAT_PLACEHOLDER = "-";
    /**
     * Regular expression of a group leader.
     */
    public static final String GROUP_LEADER_REGEX = "\\*+.+\\*";
    /**
     * Format of a group leader.
     */
    public static final String GROUP_LEADER_FORMAT = "*%s*";
    /**
     * The seat table stored as a {@code  List}.
     */
    private final List<String> seatTable;
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
     * @param seatTable   {@link #seatTable}
     * @param config      {@link #config}
     * @param seed        {@link #seed}
     * @param luckyPerson {@link #luckyPerson}
     */
    public SeatTable(final List<String> seatTable, final SeatConfig config, final String seed, final String luckyPerson) {
        this.seatTable = seatTable;
        this.config = config;
        this.seed = seed;
        this.luckyPerson = luckyPerson;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();

        final int columnCount = config.getColumnCount();

        str.append("Seed: ");
        str.append(seed);
        str.append("\n");

        str.append("Seat Table:\n");
        for (int i = 0; i < seatTable.size(); i++) {
            str.append(seatTable.get(i));
            str.append("\t\t");
            if (i % columnCount == columnCount - 1) {
                str.append("\n");
            }
        }

        if (config.lucky_option && luckyPerson != null) {
            str.append("Lucky Person: ");
            str.append(luckyPerson);
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

}
