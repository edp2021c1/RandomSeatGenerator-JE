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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.emptySeatPlaceholder;

/**
 * This class packs the names of a pair of people separated when generating the seat and
 * provides a method to check if they are separated in a certain seat table.
 *
 * @author Calboot
 * @since 1.0.0
 */
public class SeparatedPair {
    private final String name_1;
    private final String name_2;

    /**
     * Create an instance from a {@code String}.
     * <p>
     * The format of the {@code String} should be "name_1 name_2".
     * <p>
     * Note that every character after the first space will be taken as the second name.
     *
     * @param s a {@code String} contains the names of the two people separated, divided by a {@code space}.
     * @throws IllegalConfigException if the {@code String} contains only one name.
     */
    public SeparatedPair(final String s) throws IllegalConfigException {
        final List<String> t = Arrays.asList(s.split(" ", 2));
        if (t.size() < 2) {
            throw new IllegalConfigException("Invalid separate pair: \"%s\"".formatted(s));
        }
        if (t.contains(emptySeatPlaceholder)) {
            throw new IllegalConfigException(
                    "Separated name list must not contain empty seat place holder \"%s\"".formatted(emptySeatPlaceholder));
        }
        if (Objects.equals(t.get(0), t.get(1))) {
            throw new IllegalConfigException("Two names in one separate pair cannot be the same");
        }
        name_1 = t.getFirst();
        name_2 = t.getLast();
    }

    /**
     * Check if {@code name_1} and {@code name_2} are separated in the specified seat table.
     *
     * @param seat        the seat table checked.
     * @param columnCount count of columns of the seat table.
     * @return if {@code name_1} and {@code name_2} are separated in the seat table.
     */
    public boolean check(final List<String> seat, final int columnCount) {
        if (seat == null || !(seat.contains(name_1) && seat.contains(name_2))) {
            return true;
        }
        return !Arrays.asList(
                1,
                columnCount - 1,
                columnCount,
                columnCount + 1
        ).contains(Math.abs(seat.indexOf(name_1) - seat.indexOf(name_2)));
    }

}
