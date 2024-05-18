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

import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.val;

import java.util.List;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;

/**
 * This class packs the names of a pair of people,
 * and provides a method to check if they are separated in a certain seat table.
 *
 * @author Calboot
 * @since 1.0.0
 */
public class NamePair {

    private final String first;

    private final String last;

    /**
     * Create an instance from a {@code String}.
     * <p>
     * The format of the {@code String} should be "name_1 name_2".
     * <p>
     * Note that every character after the first space will be taken as the second name.
     *
     * @param s a {@code String} contains the names of the two people separated, divided by a {@code space}
     *
     * @throws IllegalConfigException if the {@code String} contains only one name
     */
    public NamePair(final String s) throws IllegalConfigException {
        val t = s.split(" ", 2);
        if (t.length < 2) {
            throw new IllegalConfigException("Invalid separate pair: \"%s\"".formatted(s));
        }
        if (EMPTY_SEAT_PLACEHOLDER.equals(t[0]) || EMPTY_SEAT_PLACEHOLDER.equals(t[1])) {
            throw new IllegalConfigException(
                    "Separated name list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER));
        }
        if (Objects.equals(t[0], t[1])) {
            throw new IllegalConfigException("Two names in one separate pair cannot be the same");
        }
        first = t[0];
        last = t[1];
    }

    /**
     * Check if {@code first} and {@code last} are separated in the specified seat table.
     *
     * @param seat        the seat table checked.
     * @param columnCount count of columns of the seat table.
     *
     * @return if {@code name_1} and {@code name_2} are separated in the seat table.
     */
    public boolean checkSeperated(final List<String> seat, final int columnCount) {
        if (seat == null || !(seat.contains(first) && seat.contains(last))) {
            return true;
        }
        val i = Math.abs(seat.indexOf(first) - seat.indexOf(last));
        if (i == 1) {
            return false;
        }
        switch (columnCount) {
            case 1 -> {
                return true;
            }
            case 2 -> {
                return i != 2 && i != 3;
            }
            default -> {
                return i != columnCount && i != columnCount - 1 && i != columnCount + 1;
            }
        }
    }

}
