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
import com.edp2021c1.randomseatgenerator.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.modifiableListOf;

/**
 * Stores raw {@link SeatConfig}.
 *
 * @author Calboot
 * @see SeatConfig
 * @since 1.4.8
 */
public class RawSeatConfig {

    /**
     * Row count (int).
     */
    public Integer row_count;
    /**
     * Column count (int).
     * Cannot be larger than {@link SeatTable#MAX_COLUMN_COUNT}.
     *
     * @see SeatTable#MAX_COLUMN_COUNT
     */
    public Integer column_count;
    /**
     * Count of the rows rotated randomly together as group (int).
     */
    public Integer random_between_rows;
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
     * Constructs an instance.
     */
    public RawSeatConfig() {
    }

    /**
     * Returns {@link #row_count}.
     *
     * @return {@code  row_count}
     * @throws IllegalConfigException if {@code row_count} is null or not larger than 0.
     * @see #row_count
     */
    public int getRowCount() throws IllegalConfigException {
        return row_count;
    }

    private void checkRowCount() throws IllegalConfigException {
        if (row_count == null || row_count <= 0) {
            throw new IllegalConfigException("Row count cannot be equal to or less than 0");
        }
    }

    /**
     * Returns {@link #column_count} as an integer.
     *
     * @return {@code  column_count} as an integer.
     * @throws IllegalConfigException if {@code column_count} is null or not larger than 0,
     *                                or larger than {@link SeatTable#MAX_COLUMN_COUNT}
     * @see #column_count
     * @see SeatTable#MAX_COLUMN_COUNT
     */
    public int getColumnCount() throws IllegalConfigException {
        checkColumnCount();
        return column_count;
    }

    private void checkColumnCount() throws IllegalConfigException {
        if (column_count == null || column_count <= 0) {
            throw new IllegalConfigException("Column count cannot be null or equal to/less than 0");
        }
        if (column_count > SeatTable.MAX_COLUMN_COUNT) {
            throw new IllegalConfigException("Column count cannot be larger than " + SeatTable.MAX_COLUMN_COUNT);
        }
    }

    /**
     * Returns {@link #random_between_rows}, {@link #column_count} if is null or not larger than 0.
     *
     * @return {@code  random_between_rows}
     * @throws IllegalConfigException if {@code column_count} is null or not larger than zero,
     *                                *                                or larger than {@link SeatTable#MAX_COLUMN_COUNT}
     * @see #random_between_rows
     */
    public int getRandomBetweenRows() throws IllegalConfigException {
        if (random_between_rows == null || random_between_rows <= 0) {
            return getRowCount();
        }

        return random_between_rows;
    }

    /**
     * Returns {@link #last_row_pos_cannot_be_chosen} as a list of {@link Integer}.
     *
     * @return {@code  last_row_pos_cannot_be_chosen} as a list of {@link Integer}.
     * @throws IllegalConfigException if failed to parse {@code last_row_pos_cannot_be_chosen}.
     * @see #last_row_pos_cannot_be_chosen
     */
    public List<Integer> getDisabledLastRowPos() throws IllegalConfigException {
        if (last_row_pos_cannot_be_chosen == null || last_row_pos_cannot_be_chosen.isBlank()) {
            return new ArrayList<>();
        }
        checkDisabledLastRowPos();
        return buildList(
                Arrays.asList(last_row_pos_cannot_be_chosen.split(" ")),
                Integer::parseUnsignedInt
        );
    }

    private void checkDisabledLastRowPos() throws IllegalConfigException {
        if (!Strings.integerListPattern.matcher(last_row_pos_cannot_be_chosen).matches()) {
            throw new IllegalConfigException("Invalid last row positions: " + last_row_pos_cannot_be_chosen);
        }
    }

    /**
     * Returns {@link #person_sort_by_height} as a list of {@code String}.
     *
     * @return {@code  person_sort_by_height} as a list of {@code String}.
     * @see #person_sort_by_height
     */
    public List<String> getNameList() throws IllegalConfigException {
        final List<String> l = modifiableListOf(person_sort_by_height.split(" "));
        if (l.contains(SeatTable.emptySeatPlaceholder)) {
            throw new IllegalConfigException(
                    "Name list must not contain empty seat place holder \"%s\"".formatted(SeatTable.emptySeatPlaceholder)
            );
        }
        l.removeAll(List.of(""));
        for (final String s : l) {
            if (SeatTable.groupLeaderRegex.matcher(s).matches()) {
                throw new IllegalConfigException(
                        "Name list must not contain names matching the format of a group leader"
                );
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
    public List<String> getGroupLeaderList() throws IllegalConfigException {
        final List<String> l = modifiableListOf(group_leader_list.split(" "));
        if (l.contains(SeatTable.emptySeatPlaceholder)) {
            throw new IllegalConfigException(
                    "Group leader list must not contain empty seat place holder \"%s\"".formatted(SeatTable.emptySeatPlaceholder)
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
    public List<SeparatedPair> getSeparatedList() throws IllegalConfigException {
        final List<String> t = Arrays.asList(separate_list.split("\n"));
        final List<SeparatedPair> s = new ArrayList<>(t.size());

        t.forEach(m -> {
            if (!m.isBlank()) {
                s.add(new SeparatedPair(m));
            }
        });

        return s;
    }

    /**
     * Check format.
     *
     * @throws IllegalConfigException if this instance has an illegal format.
     */
    public void checkFormat() throws IllegalConfigException {
        final List<IllegalConfigException> causes = new ArrayList<>();
        try {
            checkRowCount();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            checkColumnCount();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            checkDisabledLastRowPos();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getNameList();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getGroupLeaderList();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getSeparatedList();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        if (lucky_option == null) {
            causes.add(new IllegalConfigException("Lucky option cannot be null"));
        }
        if (!causes.isEmpty()) {
            throw new IllegalConfigException(causes);
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
        config.setDisabledLastRowPos(getDisabledLastRowPos());
        config.setNames(getNameList());
        config.setGroupLeaders(getGroupLeaderList());
        config.setSeparatedPairs(getSeparatedList());
        config.setLucky(lucky_option);

        return config;
    }
}
