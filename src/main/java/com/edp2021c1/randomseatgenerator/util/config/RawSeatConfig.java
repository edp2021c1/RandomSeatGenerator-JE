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

package com.edp2021c1.randomseatgenerator.util.config;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.core.SeparatedPair;

import java.util.ArrayList;
import java.util.Arrays;

import static com.edp2021c1.randomseatgenerator.core.SeatConfig.MAX_COLUMN_COUNT;

/**
 * Stores raw {@link  SeatConfig}.
 *
 * @author Calboot
 * @see SeatConfig
 * @since 1.4.8
 */
public class RawSeatConfig {
    /**
     * Row count (int).
     */
    public String row_count;
    /**
     * Column count (int).
     * Cannot be larger than {@link SeatConfig#MAX_COLUMN_COUNT}.
     *
     * @see SeatConfig#MAX_COLUMN_COUNT
     */
    public String column_count;
    /**
     * Count of the rows rotated randomly together as group (int).
     */
    public String random_between_rows;
    /**
     * Positions in the last row that cannot be chosen, in case something blocks
     * the last row ({@code space} between two numbers).
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
    public Boolean lucky_option;

    /**
     * Returns {@link #row_count} as an integer.
     *
     * @return {@code  row_count} as an integer.
     * @throws IllegalConfigException if {@code row_count} cannot be parsed into an unsigned integer.
     * @see #row_count
     */
    public int getRowCount() throws IllegalConfigException {
        final int r;
        try {
            r = Integer.parseUnsignedInt(row_count);
        } catch (final NumberFormatException e) {
            throw new IllegalConfigException("Invalid row_count: " + row_count);
        }
        if (r == 0) {
            throw new IllegalConfigException("Row count cannot be zero");
        }
        return r;
    }

    /**
     * Returns {@link #column_count} as an integer.
     *
     * @return {@code  column_count} as an integer.
     * @throws IllegalConfigException if {@code column_count} cannot be parsed into an unsigned integer
     *                                or is larger than {@link SeatConfig#MAX_COLUMN_COUNT}
     * @see #column_count
     * @see SeatConfig#MAX_COLUMN_COUNT
     */
    public int getColumnCount() throws IllegalConfigException {
        final int c;
        try {
            c = Integer.parseUnsignedInt(column_count);
        } catch (final NumberFormatException e) {
            throw new IllegalConfigException("Invalid column_count: " + column_count);
        }
        if (c == 0) {
            throw new IllegalConfigException("Column count cannot be zero");
        }
        if (c > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException("Column count cannot be larger than " + MAX_COLUMN_COUNT);
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
        final int r;
        try {
            r = Integer.parseUnsignedInt(random_between_rows);
        } catch (final NumberFormatException e) {
            throw new IllegalConfigException("Invalid random_between_rows: " + random_between_rows);
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
    public ArrayList<Integer> getNotAllowedLastRowPos() throws IllegalConfigException {
        if (last_row_pos_cannot_be_chosen.isBlank()) {
            return new ArrayList<>();
        }
        final String[] t = last_row_pos_cannot_be_chosen.split(" ");
        final ArrayList<Integer> i = new ArrayList<>(t.length);
        try {
            for (final String s : t) {
                i.add(Integer.parseUnsignedInt(s));
            }
        } catch (final IllegalArgumentException e) {
            throw new IllegalConfigException(
                    "Invalid last row positions: " + last_row_pos_cannot_be_chosen
            );
        }
        return i;
    }

    /**
     * Returns {@link #person_sort_by_height} as a list of {@code String}.
     *
     * @return {@code  person_sort_by_height} as a list of {@code String}.
     * @see #person_sort_by_height
     */
    public ArrayList<String> getNameList() {
        final ArrayList<String> l = new ArrayList<>(Arrays.asList(person_sort_by_height.split(" ")));
        if (l.contains(SeatTable.EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Name list must not contain empty seat place holder \"%s\"".formatted(SeatTable.EMPTY_SEAT_PLACEHOLDER)
            );
        }
        if (l.contains("")) {
            throw new IllegalConfigException("Name list must not contain empty name");
        }
        for (final String s : l) {
            if (s.matches(SeatTable.GROUP_LEADER_REGEX)) {
                throw new IllegalConfigException("Name list must not contain names in the format of a group leader");
            }
        }
        return l;
    }

    /**
     * Returns {@link #group_leader_list} as a list of {@code String}.
     *
     * @return {@code  group_leader_list} as a list of {@code String}.
     * @see #group_leader_list
     */
    public ArrayList<String> getGroupLeaderList() {
        final ArrayList<String> l = new ArrayList<>(Arrays.asList(group_leader_list.split(" ")));
        if (l.contains(SeatTable.EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Group leader list must not contain empty seat place holder \"%s\"".formatted(SeatTable.EMPTY_SEAT_PLACEHOLDER)
            );
        }
        return l;
    }

    /**
     * Returns {@link #separate_list} as a list of {@code SeparatedPair}.
     *
     * @return {@code  separate_list} as a list of {@code SeparatedPair}.
     * @throws IllegalConfigException if {@code separate_list} contains one or more invalid pairs.
     * @see #separate_list
     */
    public ArrayList<SeparatedPair> getSeparatedList() throws IllegalConfigException {
        final String[] t = separate_list.split("\n");
        final ArrayList<SeparatedPair> s = new ArrayList<>(t.length);

        for (final String m : t) {
            if (!m.isBlank()) {
                s.add(new SeparatedPair(m));
            }
        }

        return s;
    }

    /**
     * Check format.
     *
     * @throws IllegalConfigException if this instance has an illegal format.
     */
    public void checkFormat() throws IllegalConfigException {
        final StringBuilder str = new StringBuilder();
        try {
            getRowCount();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getColumnCount();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getRandomBetweenRows();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getNotAllowedLastRowPos();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getNameList();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getGroupLeaderList();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        try {
            getSeparatedList();
        } catch (IllegalConfigException e) {
            str.append(e.getMessage());
            str.append("\n");
        }
        if (lucky_option == null) {
            str.append("Lucky option cannot be null\n");
        }
        if (!str.isEmpty()) {
            str.deleteCharAt(str.length() - 1);
            throw new IllegalConfigException(str.toString());
        }
    }

    /**
     * Returns the {@link SeatConfig} stored.
     *
     * @return the config stored
     */
    public SeatConfig getContent() {
        checkFormat();

        final SeatConfig config = new SeatConfig();
        config.setRowCount(getRowCount());
        config.setColumnCount(getColumnCount());
        config.setRandomBetweenRows(getRandomBetweenRows());
        config.setDisabledLastRowPos(getNotAllowedLastRowPos());
        config.setNames(getNameList());
        config.setGroupLeaders(getGroupLeaderList());
        config.setSeparatedPairs(getSeparatedList());
        config.setLucky(lucky_option);

        return config;
    }
}
