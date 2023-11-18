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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Stores config used to generate a seat table.
 *
 * @author Calboot
 * @since 1.2.0
 */
public final class SeatConfig {
    /**
     * Max count of column in a {@code SeatRowData}.
     */
    public static final int MAX_COLUMN_COUNT = 20;
    /**
     * Row count (int).
     */
    public String row_count;
    /**
     * Column count (int).
     * Cannot be larger than {@link #MAX_COLUMN_COUNT}.
     *
     * @see #MAX_COLUMN_COUNT
     */
    public String column_count;
    /**
     * Count of the rows rotated randomly together as group (int).
     */
    public String random_between_rows;
    /**
     * Positions in the last row that cannot be chosen, in case something blocks the last row ({@code space} between two numbers).
     */
    public String last_row_pos_cannot_be_chosen;
    /**
     * Name list sorted by height ({@code space} between two people).
     */
    public String person_sort_by_height;
    /**
     * A list of people who can be a leader of a column ({@code space} between two people).
     */
    public String group_leader_list;
    /**
     * A list of people pairs separated (a pair of names each line, and {@code space} between two names of a pair).
     */
    public String separate_list;
    /**
     * Whether there will be a lucky person specially chosen from the last rows.
     */
    public boolean lucky_option;

    /**
     * Nothing to say…
     */
    public SeatConfig() {
        super();
    }

    /**
     * Returns {@link #row_count} as an integer.
     *
     * @return {@code  row_count} as an integer.
     * @throws IllegalConfigException if {@code row_count} cannot be parsed into an unsigned integer.
     * @see #row_count
     */
    public int getRowCount() throws IllegalConfigException {
        int r;
        try {
            r = Integer.parseUnsignedInt(row_count);
        } catch (NumberFormatException e) {
            throw new IllegalConfigException(String.format("Invalid row_count: %s.", row_count), e);
        }
        return r;
    }

    /**
     * Returns {@link #column_count} as an integer.
     *
     * @return {@code  column_count} as an integer.
     * @throws IllegalConfigException if {@code column_count} cannot be parsed into an unsigned integer
     *                                    or is larger than {@link #MAX_COLUMN_COUNT}
     * @see #column_count
     * @see #MAX_COLUMN_COUNT
     */
    public int getColumnCount() throws IllegalConfigException {
        int c;
        try {
            c = Integer.parseUnsignedInt(column_count);
        } catch (NumberFormatException e) {
            throw new IllegalConfigException(String.format("Invalid column_count: %s.", column_count), e);
        }
        if (c > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException(String.format("Column count cannot be larger than %d.", MAX_COLUMN_COUNT));
        }
        return c;
    }

    /**
     * Returns {@link #random_between_rows} as an integer.
     *
     * @return {@code  random_between_rows} as an integer.
     * @throws IllegalConfigException if {@code random_between_rows} cannot be parsed into an unsigned integer.
     * @see #random_between_rows
     */
    public int getRandomBetweenRows() throws IllegalConfigException {
        if (random_between_rows.isBlank()) {
            return getRowCount();
        }
        int r;
        try {
            r = Integer.parseUnsignedInt(random_between_rows);
        } catch (NumberFormatException e) {
            throw new IllegalConfigException(String.format("Invalid random_between_rows: %s.", random_between_rows), e);
        }
        return r;
    }

    /**
     * Returns {@link #last_row_pos_cannot_be_chosen} as a list of {@code int}.
     *
     * @return {@code  last_row_pos_cannot_be_choosed} as a list of {@code int}.
     * @throws IllegalConfigException if failed to parse {@code last_row_pos_cannot_be_choosed}.
     * @see #last_row_pos_cannot_be_chosen
     */
    public List<Integer> getNotAllowedLastRowPos() throws IllegalConfigException {
        if (last_row_pos_cannot_be_chosen.isBlank()) {
            return new ArrayList<>();
        }
        String[] t = last_row_pos_cannot_be_chosen.split(" ");
        List<Integer> i = new ArrayList<>(t.length);
        try {
            for (String s : t) {
                i.add(Integer.parseUnsignedInt(s));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalConfigException(String.format("Invalid last row positions: %s.", last_row_pos_cannot_be_chosen), e);
        }
        return i;
    }

    /**
     * Returns {@link #person_sort_by_height} as a list of {@code String}.
     *
     * @return {@code  person_sort_by_height} as a list of {@code String}.
     * @see #person_sort_by_height
     */
    public List<String> getNameList() {
        return Arrays.asList(person_sort_by_height.split(" "));
    }

    /**
     * Returns {@link #group_leader_list} as a list of {@code String}.
     *
     * @return {@code  group_leader_list} as a list of {@code String}.
     * @see #group_leader_list
     */
    public List<String> getGroupLeaderList() {
        return Arrays.asList(group_leader_list.split(" "));
    }

    /**
     * Returns {@link #separate_list} as a list of {@code Separate}.
     *
     * @return {@code  separate_list} as a list of {@code Separate}.
     * @throws IllegalConfigException if {@code separate_list} contains one or more invalid pairs.
     * @see #separate_list
     */
    public List<Separate> getSeparatedList() throws IllegalConfigException {
        String[] t = separate_list.split("\n");
        ArrayList<Separate> s = new ArrayList<>(t.length);

        for (String m : t) {
            if (!m.isBlank()) {
                s.add(new Separate(m));
            }
        }

        return s;
    }

    /**
     * Check if another instance equals to this one.
     *
     * @param another another {@code SeatConfig} to compare with.
     * @return if these two instances are equal.
     */
    public boolean equals(SeatConfig another) {
        return Objects.equals(row_count, another.row_count)
                && Objects.equals(column_count, another.column_count)
                && Objects.equals(random_between_rows, another.random_between_rows)
                && Objects.equals(last_row_pos_cannot_be_chosen, another.last_row_pos_cannot_be_chosen)
                && Objects.equals(person_sort_by_height, another.person_sort_by_height)
                && Objects.equals(group_leader_list, another.group_leader_list)
                && Objects.equals(separate_list, another.separate_list)
                && Objects.equals(lucky_option, another.lucky_option);
    }

    /**
     * Check format.
     *
     * @throws IllegalConfigException if this instance has an illegal format.
     */
    public void checkFormat() throws IllegalConfigException {
        getRowCount();
        getColumnCount();
        getRandomBetweenRows();
        getNotAllowedLastRowPos();
        getSeparatedList();
    }
}
