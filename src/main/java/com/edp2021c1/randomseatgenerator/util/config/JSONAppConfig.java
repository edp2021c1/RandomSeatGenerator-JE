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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeparatedPair;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.Utils;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.*;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.modifiableList;

/**
 * Stores the raw config of the application.
 *
 * @author Calboot
 * @see SeatConfig
 * @since 1.5.1
 */
public class JSONAppConfig extends HashtableConfig implements SeatConfig {

    /**
     * Key of {@code rowCount}.
     *
     * @see SeatConfig#getRowCount()
     */
    public static final String KEY_ROW_COUNT = "row_count";

    /**
     * Key of {@code columnCount}.
     *
     * @see SeatConfig#getColumnCount()
     */
    public static final String KEY_COLUMN_COUNT = "column_count";

    /**
     * Key of {@code randomBetweenRows}.
     *
     * @see SeatConfig#getRandomBetweenRows()
     */
    public static final String KEY_RANDOM_BETWEEN_ROWS = "random_between_rows";

    /**
     * Key of {@code disabledLastRowPos}.
     *
     * @see SeatConfig#getDisabledLastRowPos()
     */
    public static final String KEY_DISABLED_LAST_ROW_POS = "last_row_pos_cannot_be_chosen";

    /**
     * Key of {@code names}.
     *
     * @see SeatConfig#getNames()
     */
    public static final String KEY_NAMES = "person_sort_by_height";

    /**
     * Key of {@code groupLeaders}.
     *
     * @see SeatConfig#getGroupLeaders()
     */
    public static final String KEY_GROUP_LEADERS = "group_leader_list";

    /**
     * Key of {@code separatedPairs}.
     *
     * @see SeatConfig#getSeparatedPairs()
     */
    public static final String KEY_SEPARATED_PAIRS = "separate_list";

    /**
     * Key of {@code lucky}.
     *
     * @see SeatConfig#isLucky()
     */
    public static final String KEY_LUCKY = "lucky_option";

    /**
     * Constructs an empty instance.
     */
    public JSONAppConfig() {
        super(new HashMap<>());
    }

    /**
     * Constructs an instance.
     *
     * @param map whose mappings are to be placed in this map
     * @see HashtableConfig#HashtableConfig(Map)  HashtableConfig
     */
    public JSONAppConfig(Map<String, ?> map) {
        super(map);
    }

    /**
     * Loads an instance from the given json string.
     *
     * @param json to translate the instance from
     * @return the instance loaded from the json string
     */
    public static JSONAppConfig fromJson(final String json) {
        return new JSONAppConfig(JSON.parseObject(json));
    }

    /**
     * Loads an instance from a given json file.
     *
     * @param filePath to load json from
     * @return instance loaded from json
     * @throws IOException if an I/O error occurs
     */
    public static JSONAppConfig fromJson(final Path filePath) throws IOException {
        return fromJson(Files.readString(filePath));
    }

    @Override
    public int getRowCount() throws IllegalConfigException {
        final Integer rowCount = getInteger(KEY_ROW_COUNT);
        if (rowCount == null || rowCount == 0) {
            throw new IllegalConfigException("Row count cannot be equal to or less than 0");
        }
        return rowCount;
    }

    @Override
    public int getColumnCount() throws IllegalConfigException {
        final Integer columnCount = getInteger(KEY_COLUMN_COUNT);
        if (columnCount == null || columnCount == 0) {
            throw new IllegalConfigException("Column count cannot be equal to or less than 0");
        }
        if (columnCount > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException("Column count cannot be larger than " + MAX_COLUMN_COUNT);
        }
        return columnCount;
    }

    @Override
    public int getRandomBetweenRows() throws IllegalConfigException {
        final Integer randomBetweenRows = getInteger(KEY_RANDOM_BETWEEN_ROWS);
        if (randomBetweenRows == null || randomBetweenRows <= 0) {
            return getRowCount();
        }
        return randomBetweenRows;
    }

    @Override
    public List<Integer> getDisabledLastRowPos() throws IllegalConfigException {
        final String disabledLastRowPos = getString(KEY_DISABLED_LAST_ROW_POS);
        if (disabledLastRowPos == null || disabledLastRowPos.isBlank()) {
            return new ArrayList<>();
        }
        if (!Strings.integerListPattern.matcher(disabledLastRowPos).matches()) {
            throw new IllegalConfigException("Invalid disabled last row positions: " + disabledLastRowPos);
        }
        return buildList(
                Arrays.asList(disabledLastRowPos.split(" ")),
                Integer::parseUnsignedInt
        );
    }

    @Override
    public List<String> getNames() throws IllegalConfigException {
        final String names = getString(KEY_NAMES);
        if (names == null) {
            throw new IllegalConfigException("Name list cannot be null");
        }
        final List<String> l = Arrays.asList(names.split(" "));
        if (l.contains(EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Name list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER)
            );
        }
        l.removeAll(List.of(""));
        for (final String s : l) {
            if (groupLeaderRegex.matcher(s).matches()) {
                throw new IllegalConfigException(
                        "Name list must not contain names matching the format of a group leader"
                );
            }
        }
        return modifiableList(l);
    }

    @Override
    public List<String> getGroupLeaders() throws IllegalConfigException {
        final String groupLeaders = getString(KEY_GROUP_LEADERS);
        if (groupLeaders == null) {
            throw new IllegalConfigException("Group leader list cannot be null");
        }
        final List<String> l = Arrays.asList(groupLeaders.split(" "));
        if (l.contains(EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Group leader list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER)
            );
        }
        return modifiableList(l);
    }

    @Override
    public List<SeparatedPair> getSeparatedPairs() throws IllegalConfigException {
        final String separatedPairs = getString(KEY_SEPARATED_PAIRS);
        if (separatedPairs == null) {
            throw new IllegalConfigException("Separated list cannot be null");
        }
        return buildList(separatedPairs.lines().filter(s -> s != null && !s.isBlank()).toList(), SeparatedPair::new);
    }

    @Override
    public boolean isLucky() {
        return Utils.elseIfNull(getBoolean(KEY_LUCKY), true);
    }

    @Override
    public void check() throws IllegalConfigException {
        final List<IllegalConfigException> causes = new ArrayList<>();
        try {
            getRowCount();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getColumnCount();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getDisabledLastRowPos();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getNames();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getGroupLeaders();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getSeparatedPairs();
        } catch (final IllegalConfigException e) {
            causes.add(e);
        }
        if (!causes.isEmpty()) {
            throw new IllegalConfigException(causes);
        }
    }

    @Override
    public JSONAppConfig checkAndReturn() throws IllegalConfigException {
        check();
        return this;
    }

    /**
     * Copies all the mappings from the specified map to this instance, and returns {@code this}.
     *
     * @param other mappings to be stored in this map
     * @return {@code this}
     */
    public JSONAppConfig putAllAndReturn(final Map<String, ?> other) {
        putAll(other);
        return this;
    }

    @Override
    public JSONAppConfig clone() {
        return (JSONAppConfig) super.clone();
    }

    /**
     * Transfers {@code this} into a json string.
     *
     * @return json string
     */
    public String toJson() {
        return JSON.toJSONString(this, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.FieldBased, JSONWriter.Feature.MapSortField);
    }

}
