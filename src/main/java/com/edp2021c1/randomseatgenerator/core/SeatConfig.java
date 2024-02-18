package com.edp2021c1.randomseatgenerator.core;

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
     * @throws IllegalConfigException if row count is null or not larger than 0
     */
    int getRowCount() throws IllegalConfigException;

    /**
     * Returns column count.
     *
     * @return column count
     * @throws IllegalConfigException if column count is null or not larger than 0,
     *                                or larger than {@link SeatTable#MAX_COLUMN_COUNT}
     * @see SeatTable#MAX_COLUMN_COUNT
     */
    int getColumnCount() throws IllegalConfigException;

    /**
     * Returns random between rows, row count if is null or not larger than 0.
     *
     * @return {@code  random_between_rows}
     * @throws IllegalConfigException if row count is null, or not larger than zero
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
     * Returns whether lucky option is on false if lucky is null.
     *
     * @return whether lucky option is on
     */
    Boolean isLucky();
}
