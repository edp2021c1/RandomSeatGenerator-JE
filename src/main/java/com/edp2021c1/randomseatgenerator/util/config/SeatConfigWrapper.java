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
import com.edp2021c1.randomseatgenerator.core.NamePair;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.NonNull;
import lombok.val;

import java.util.*;

import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.groupLeaderRegexPredicate;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;

/**
 * Stores the config of the application.
 *
 * @author Calboot
 * @see SeatConfig
 * @see JSONObject
 * @since 1.5.1
 */
public class SeatConfigWrapper implements Map<String, Object>, SeatConfig {

    /**
     * Key of {@code rowCount}.
     *
     * @see SeatConfig#rowCount()
     */
    private static final String KEY_ROW_COUNT = "row_count";

    /**
     * Key of {@code columnCount}.
     *
     * @see SeatConfig#columnCount()
     */
    private static final String KEY_COLUMN_COUNT = "column_count";

    /**
     * Key of {@code randomBetweenRows}.
     *
     * @see SeatConfig#randomBetweenRows()
     */
    private static final String KEY_RANDOM_BETWEEN_ROWS = "random_between_rows";

    /**
     * Key of {@code disabledLastRowPos}.
     *
     * @see SeatConfig#disabledLastRowPos()
     */
    private static final String KEY_DISABLED_LAST_ROW_POS = "last_row_pos_cannot_be_chosen";

    /**
     * Key of {@code names}.
     *
     * @see SeatConfig#names()
     */
    private static final String KEY_NAMES = "person_sort_by_height";

    /**
     * Key of {@code groupLeaders}.
     *
     * @see SeatConfig#groupLeaders()
     */
    private static final String KEY_GROUP_LEADERS = "group_leader_list";

    /**
     * Key of {@code separatedPairs}.
     *
     * @see SeatConfig#separatedPairs()
     */
    private static final String KEY_SEPARATED_PAIRS = "separate_list";

    /**
     * Key of {@code lucky}.
     *
     * @see SeatConfig#lucky()
     */
    private static final String KEY_LUCKY = "lucky_option";

    private final JSONObject config = new JSONObject(8);

    /**
     * Constructs an empty instance.
     */
    public SeatConfigWrapper() {
    }

    /**
     * Constructs an instance.
     *
     * @param map whose mappings are to be placed in this map
     */
    public SeatConfigWrapper(final Map<String, ?> map) {
        putAll(map);
    }

    /**
     * Sets the value of {@link #KEY_LUCKY}
     *
     * @param value to be set
     */
    public void setLucky(final Boolean value) {
        put(KEY_LUCKY, value);
    }

    @Override
    public int randomBetweenRows() throws IllegalConfigException {
        val randomBetweenRows = getRandomBetweenRows();
        if (randomBetweenRows == null || randomBetweenRows == 0) {
            return rowCount();
        }
        if (randomBetweenRows < 0) {
            throw new IllegalConfigException("Random between rows cannot be less than 0");
        }
        return randomBetweenRows;
    }

    /**
     * Returns raw random between row count value.
     *
     * @return raw random between row count value
     */
    public Integer getRandomBetweenRows() {
        return getInteger(KEY_RANDOM_BETWEEN_ROWS);
    }

    /**
     * Sets raw random between row count value.
     *
     * @param value raw value to be set
     */
    public void setRandomBetweenRows(final Integer value) {
        put(KEY_RANDOM_BETWEEN_ROWS, value);
    }

    /**
     * Returns the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Integer} value is to be returned
     *
     * @return the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key
     *
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Integer getInteger(final String key) {
        try {
            return config.getInteger(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to Integer");
        }
    }

    @Override
    public boolean lucky() {
        return Objects.requireNonNullElse(getBoolean(KEY_LUCKY), true);
    }

    @Override
    public SeatConfigWrapper checkAndReturn() throws IllegalConfigException {
        check();
        return this;
    }

    @Override
    public int rowCount() throws IllegalConfigException {
        val rowCount = getRowCount();
        if (rowCount == null || rowCount == 0) {
            throw new IllegalConfigException("Row count cannot be equal to or less than 0");
        }
        return rowCount;
    }

    /**
     * Returns raw row count value.
     *
     * @return raw row count value
     */
    public Integer getRowCount() {
        return getInteger(KEY_ROW_COUNT);
    }

    /**
     * Sets raw row count value.
     *
     * @param value raw value to be set
     */
    public void setRowCount(final Integer value) {
        put(KEY_ROW_COUNT, value);
    }

    @Override
    public int columnCount() throws IllegalConfigException {
        val columnCount = getColumnCount();
        if (columnCount == null || columnCount == 0) {
            throw new IllegalConfigException("Column count cannot be equal to or less than 0");
        }
        return columnCount;
    }

    /**
     * Returns raw column count value.
     *
     * @return raw column count value
     */
    public Integer getColumnCount() {
        return getInteger(KEY_COLUMN_COUNT);
    }

    /**
     * Sets raw column count value.
     *
     * @param value raw value to be set
     */
    public void setColumnCount(final Integer value) {
        put(KEY_COLUMN_COUNT, value);
    }

    @Override
    public List<Integer> disabledLastRowPos() throws IllegalConfigException {
        val disabledLastRowPos = getDisabledLastRowPos();
        if (disabledLastRowPos == null || disabledLastRowPos.isBlank()) {
            return new ArrayList<>();
        }
        if (!Strings.integerListPatternPredicate.test(disabledLastRowPos)) {
            throw new IllegalConfigException("Invalid disabled last row positions: " + disabledLastRowPos);
        }
        return buildList(
                Arrays.asList(disabledLastRowPos.split(" ")),
                Integer::parseUnsignedInt
        );
    }

    /**
     * Returns raw disabled last row position list value.
     *
     * @return raw disabled last row positions value
     */
    public String getDisabledLastRowPos() {
        return getString(KEY_DISABLED_LAST_ROW_POS);
    }

    /**
     * Sets raw disabled last row position list value.
     *
     * @param value raw value to be set
     */
    public void setDisabledLastRowPos(final String value) {
        put(KEY_DISABLED_LAST_ROW_POS, value);
    }

    /**
     * Returns the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code String} value is to be returned
     *
     * @return the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key
     *
     * @throws IllegalConfigException if the key exists, and the value of the key cannot be cast to {@code String}
     */
    public String getString(final String key) {
        try {
            return config.getString(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to String");
        }
    }

    @Override
    public List<String> names() throws IllegalConfigException {
        val names = getNames();
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
        if (l.stream().anyMatch(groupLeaderRegexPredicate)) {
            throw new IllegalConfigException(
                    "Name list must not contain names matching the format of a group leader"
            );
        }
        return l;
    }

    /**
     * Returns raw name list value.
     *
     * @return raw name list value
     */
    public String getNames() {
        return getString(KEY_NAMES);
    }

    /**
     * Sets raw name list value.
     *
     * @param value raw value to be set
     */
    public void setNames(final String value) {
        put(KEY_NAMES, value);
    }

    @Override
    public List<String> groupLeaders() throws IllegalConfigException {
        val groupLeaders = getGroupLeaders();
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

    /**
     * Returns raw group leader list value.
     *
     * @return raw group leader list value
     */
    public String getGroupLeaders() {
        return getString(KEY_GROUP_LEADERS);
    }

    /**
     * Sets raw group leader list value.
     *
     * @param value raw value to be set
     */
    public void setGroupLeaders(final String value) {
        put(KEY_GROUP_LEADERS, value);
    }

    @Override
    public List<NamePair> separatedPairs() throws IllegalConfigException {
        val separatedPairs = getSeparatedPairs();
        if (separatedPairs == null) {
            throw new IllegalConfigException("Separated list cannot be null");
        }
        return buildList(separatedPairs.lines().filter(s -> s != null && !s.isBlank()).toList(), NamePair::new);
    }

    /**
     * Returns raw separated pair list value.
     *
     * @return raw separated pair list value
     */
    public String getSeparatedPairs() {
        return getString(KEY_SEPARATED_PAIRS);
    }

    /**
     * Sets raw separated pair list value.
     *
     * @param value raw value to be set
     */
    public void setSeparatedPairs(final String value) {
        put(KEY_SEPARATED_PAIRS, value);
    }

    /**
     * Returns the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Boolean} value is to be returned
     *
     * @return the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key
     *
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Boolean}
     */
    public Boolean getBoolean(final String key) {
        try {
            return config.getBoolean(key);
        } catch (final JSONException e) {
            throw new IllegalConfigException("Cannot cast to Boolean");
        }
    }

    /**
     * Transfers {@code this} into a json string.
     *
     * @return json string
     */
    public String toJsonString() {
        return config.toString(PrettyFormat);
    }

    /**
     * Copies all the mappings from the specified map to this instance, and returns {@code this}.
     *
     * @param map mappings to be stored in this map
     *
     * @return {@code this}
     */
    public SeatConfigWrapper putAllAndReturn(final Map<? extends String, ?> map) {
        putAll(map);
        return this;
    }

    /**
     * Returns a copy of {@code this}.
     *
     * @return a copy of {@code this}
     */
    public SeatConfigWrapper cloneThis() {
        return new SeatConfigWrapper(config);
    }

    @Override
    public int size() {
        return config.size();
    }

    @Override
    public boolean isEmpty() {
        return config.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return config.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return config.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        return config.get(key);
    }

    @Override
    public Object put(final String key, final Object value) {
        switch (key) {
            case null -> throw new UnsupportedOperationException("Null cannot be used as a key in a config");
            case KEY_ROW_COUNT,
                 KEY_COLUMN_COUNT,
                 KEY_RANDOM_BETWEEN_ROWS,
                 KEY_DISABLED_LAST_ROW_POS,
                 KEY_NAMES,
                 KEY_GROUP_LEADERS,
                 KEY_SEPARATED_PAIRS,
                 KEY_LUCKY -> {
                if (value == null) {
                    return remove(key);
                }
                return config.put(key, value);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public Object remove(final Object key) {
        return config.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ?> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        config.clear();
    }

    @Override
    @NonNull
    public Set<String> keySet() {
        return config.keySet();
    }

    @Override
    @NonNull
    public Collection<Object> values() {
        return config.values();
    }

    @Override
    @NonNull
    public Set<Entry<String, Object>> entrySet() {
        return config.entrySet();
    }

}
