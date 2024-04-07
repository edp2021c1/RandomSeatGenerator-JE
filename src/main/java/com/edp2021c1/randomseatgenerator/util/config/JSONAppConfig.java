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

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeparatedPair;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.val;

import java.util.*;

import static com.alibaba.fastjson2.JSONWriter.Feature.MapSortField;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.*;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;

/**
 * Stores the config of the application.
 *
 * @author Calboot
 * @see SeatConfig
 * @see JSONObject
 * @since 1.5.1
 */
public class JSONAppConfig extends JSONObject implements SeatConfig {

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
    private static final int DEFAULT_INITIAL_CAPACITY = 14;

    /**
     * Constructs an empty instance.
     */
    public JSONAppConfig() {
        super(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs an instance with the specified initial capacity.
     *
     * @param initialCapacity initial size of the map
     */
    public JSONAppConfig(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an instance.
     *
     * @param map whose mappings are to be placed in this map
     */
    public JSONAppConfig(final Map<String, ?> map) {
        super(Math.max(DEFAULT_INITIAL_CAPACITY, map.size()));
        putAll(map);
    }

    @Override
    public int getRowCount() throws IllegalConfigException {
        val rowCount = getInteger(KEY_ROW_COUNT);
        if (rowCount == null || rowCount == 0) {
            throw new IllegalConfigException("Row count cannot be equal to or less than 0");
        }
        return rowCount;
    }

    @Override
    public int getColumnCount() throws IllegalConfigException {
        val columnCount = getInteger(KEY_COLUMN_COUNT);
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
        val randomBetweenRows = getInteger(KEY_RANDOM_BETWEEN_ROWS);
        if (randomBetweenRows == null || randomBetweenRows == 0) {
            return getRowCount();
        }
        if (randomBetweenRows < 0) {
            throw new IllegalConfigException("Random between rows cannot be less than 0");
        }
        return randomBetweenRows;
    }

    @Override
    public List<Integer> getDisabledLastRowPos() throws IllegalConfigException {
        val disabledLastRowPos = getString(KEY_DISABLED_LAST_ROW_POS);
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
        val names = getString(KEY_NAMES);
        if (names == null) {
            throw new IllegalConfigException("Name list cannot be null");
        }
        val l = Arrays.asList(names.split(" "));
        if (l.contains(EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Name list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER)
            );
        }
        l.removeAll(List.of(""));
        if (l.stream().anyMatch(groupLeaderRegex.asMatchPredicate())) {
            throw new IllegalConfigException(
                    "Name list must not contain names matching the format of a group leader"
            );
        }
        return l;
    }

    @Override
    public List<String> getGroupLeaders() throws IllegalConfigException {
        val groupLeaders = getString(KEY_GROUP_LEADERS);
        if (groupLeaders == null) {
            throw new IllegalConfigException("Group leader list cannot be null");
        }
        val l = Arrays.asList(groupLeaders.split(" "));
        if (l.contains(EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Group leader list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER)
            );
        }
        return l;
    }

    @Override
    public List<SeparatedPair> getSeparatedPairs() throws IllegalConfigException {
        val separatedPairs = getString(KEY_SEPARATED_PAIRS);
        if (separatedPairs == null) {
            throw new IllegalConfigException("Separated list cannot be null");
        }
        return buildList(separatedPairs.lines().filter(s -> s != null && !s.isBlank()).toList(), SeparatedPair::new);
    }

    @Override
    public boolean isLucky() {
        return Objects.requireNonNullElse(getBoolean(KEY_LUCKY), true);
    }

    @Override
    public void check() throws IllegalConfigException {
        val causes = new ArrayList<IllegalConfigException>();
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
     * Returns the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code String} value is to be returned
     * @return the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key cannot be cast to {@code String}
     */
    public String getString(final String key) {
        try {
            return super.getString(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to String");
        }
    }

    /**
     * Returns the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Double} value is to be returned
     * @return the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key cannot be cast to {@code String}
     */
    public Double getDouble(final String key) {
        try {
            return super.getDouble(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to Double");
        }
    }

    /**
     * Returns the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Integer} value is to be returned
     * @return the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Integer getInteger(final String key) {
        try {
            return super.getInteger(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to Integer");
        }
    }

    /**
     * Returns the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Boolean} value is to be returned
     * @return the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Boolean}
     */
    public Boolean getBoolean(final String key) {
        try {
            return super.getBoolean(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to Boolean");
        }
    }

    /**
     * Transfers {@code this} into a json string.
     *
     * @return json string
     */
    @Override
    public String toString() {
        return toString(PrettyFormat, MapSortField);
    }

    @Override
    public Object put(final String key, final Object value) {
        if (key == null) {
            throw new UnsupportedOperationException("Null cannot be used as a key in a config");
        }
        if (value == null) {
            return remove(key);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ?> map) {
        map.forEach(this::put);
    }

    /**
     * Puts the mapping of the given key and value, and then returns {@code this}.
     *
     * @param key   to put
     * @param value to put
     * @return {@code this}
     */
    public JSONAppConfig putAndReturn(final String key, final Object value) {
        put(key, value);
        return this;
    }

    /**
     * Copies all the mappings from the specified map to this instance, and returns {@code this}.
     *
     * @param map mappings to be stored in this map
     * @return {@code this}
     */
    public JSONAppConfig putAllAndReturn(final Map<? extends String, ?> map) {
        putAll(map);
        return this;
    }

    /**
     * Parses and puts the {@link JSONObject} from the string.
     *
     * @param jsonString to parse and put
     * @return {@code this}
     */
    public JSONAppConfig putJsonAndReturn(final String jsonString) {
        return putAllAndReturn(parseObject(jsonString));
    }

    /**
     * Returns a copy of {@code this}.
     *
     * @return a copy of {@code this}
     */
    public JSONAppConfig cloneThis() {
        return new JSONAppConfig(this);
    }

}
