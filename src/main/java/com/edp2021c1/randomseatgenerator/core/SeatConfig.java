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

import java.util.List;

/**
 * Stores config used to generate seat table.
 *
 * @author Calboot
 * @since 1.5.0
 */
public interface SeatConfig {

    /**
     * Returns row count.
     *
     * @return {@code  row_count}
     * @throws IllegalConfigException if row count is null or not positive
     */
    int getRowCount() throws IllegalConfigException;

    /**
     * Returns column count.
     *
     * @return column count
     * @throws IllegalConfigException if column count is null or not positive
     */
    int getColumnCount() throws IllegalConfigException;

    /**
     * Returns random between rows, row count if is null or not positive.
     *
     * @return {@code  random_between_rows}
     * @throws IllegalConfigException if row count is null, or not positive
     */
    int getRandomBetweenRows() throws IllegalConfigException;

    /**
     * Returns disabled last row positions as a list of {@link Integer}.
     *
     * @return disabled last row positions as a list of {@link Integer}
     * @throws IllegalConfigException if failed to parse the value into a list of integers
     */
    List<Integer> getDisabledLastRowPos() throws IllegalConfigException;

    /**
     * Returns names as a list of string
     *
     * @return names as a list of string
     * @throws IllegalConfigException if name list is null, contains {@link SeatTable#EMPTY_SEAT_PLACEHOLDER},
     *                                or contains names matching {@link SeatTable#groupLeaderRegex}
     */
    List<String> getNames() throws IllegalConfigException;

    /**
     * Returns group leaders as a list of string.
     *
     * @return group leaders as a list of string
     * @throws IllegalConfigException if group leader list is null, or contains {@link SeatTable#EMPTY_SEAT_PLACEHOLDER}
     */
    List<String> getGroupLeaders() throws IllegalConfigException;

    /**
     * Returns separated pairs as a list of {@link SeparatedPair}.
     *
     * @return separated pairs as a list of {@code SeparatedPair}.
     * @throws IllegalConfigException if {@code separate_list} contains one or more invalid pairs.
     * @see SeparatedPair
     */
    List<SeparatedPair> getSeparatedPairs() throws IllegalConfigException;

    /**
     * Returns whether lucky option is on, false if lucky is null.
     *
     * @return whether lucky option is on
     */
    boolean isLucky();

    /**
     * Checks the format of this instance.
     *
     * @throws IllegalConfigException if this instance has an illegal format
     */
    void check() throws IllegalConfigException;

    /**
     * Checks format and returns {@code this}.
     *
     * @return this
     * @throws IllegalConfigException if this instance has an illegal format
     * @see #check()
     */
    default SeatConfig checkAndReturn() throws IllegalConfigException {
        check();
        return this;
    }
}
