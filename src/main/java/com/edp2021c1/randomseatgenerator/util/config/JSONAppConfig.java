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
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.alibaba.fastjson2.annotation.JSONField;
import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.core.SeparatedPair;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.Utils;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.*;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.modifiableListOf;

/**
 * Stores the raw config of the application.
 *
 * @author Calboot
 * @see SeatConfig
 * @since 1.4.8
 */
@EqualsAndHashCode(doNotUseGetters = true)
public class JSONAppConfig implements Cloneable, SeatConfig {
    private static final Field[] fields = JSONAppConfig.class.getFields();

    /**
     * Row count (int).
     */
    @JSONField(name = "row_count")
    public Integer rowCount;
    /**
     * Column count (int).
     * Cannot be larger than {@link SeatTable#MAX_COLUMN_COUNT}.
     *
     * @see SeatTable#MAX_COLUMN_COUNT
     */
    @JSONField(name = "column_count")
    public Integer columnCount;
    /**
     * Count of the rows rotated randomly together as group (int).
     */
    @JSONField(name = "random_between_rows")
    public Integer randomBetweenRows;
    /**
     * Positions in the last row that cannot be chosen, in case something blocks
     * the last row ({@code space} between two numbers).
     */
    @JSONField(name = "last_row_pos_cannot_be_chosen")
    public String disabledLastRowPos;
    /**
     * Name list sorted by height ({@code space} between two people).
     */
    @JSONField(name = "person_sort_by_height")
    public String names;
    /**
     * A list of people who can be a leader of a column ({@code space} between two people).
     */
    @JSONField(name = "group_leader_list")
    public String groupLeaders;
    /**
     * A list of people pairs separated (a pair of names each line, and {@code space} between two names of a pair).
     */
    @JSONField(name = "separate_list")
    public String separatedPairs;
    /**
     * Whether there will be a lucky person specially chosen from the last rows.
     */
    @JSONField(name = "lucky_option")
    public Boolean lucky;
    /**
     * If seat table is exported writable.
     */
    @JSONField(name = "export_writable")
    public Boolean exportWritable;
    /**
     * The previous directory seat table is exported to.
     */
    @JSONField(name = "previous_export_dir")
    public String previousExportDir;
    /**
     * The previous directory config is loaded from.
     */
    @JSONField(name = "previous_import_dir")
    public String previousImportDir;
    /**
     * Determines whether the app is shown in the dark mode.
     */
    @JSONField(name = "dark_mode")
    public Boolean darkMode;

    /**
     * Constructs an instance.
     */
    public JSONAppConfig() {
    }

    /**
     * Returns an instance created from a JSON string.
     *
     * @param json JSON string parsed
     * @return element parsed from json
     */
    public static JSONAppConfig fromJson(final String json) {
        return JSON.parseObject(json, JSONAppConfig.class);
    }

    /**
     * Load an instance from a JSON path.
     *
     * @param path to load from
     * @return instance loaded from path
     * @throws IOException if an I/O error occurs
     */
    public static JSONAppConfig fromJson(final Path path) throws IOException {
        return fromJson(Files.readString(path));
    }

    @Override
    public int getRowCount() throws IllegalConfigException {
        checkRowCount();
        return rowCount;
    }

    @Override
    public int getColumnCount() throws IllegalConfigException {
        checkColumnCount();
        return columnCount;
    }

    @Override
    public int getRandomBetweenRows() throws IllegalConfigException {
        if (randomBetweenRows == null || randomBetweenRows <= 0) {
            return getRowCount();
        }

        return randomBetweenRows;
    }

    @Override
    public List<Integer> getDisabledLastRowPos() throws IllegalConfigException {
        if (disabledLastRowPos == null || disabledLastRowPos.isBlank()) {
            return new ArrayList<>();
        }
        checkDisabledLastRowPos();
        return buildList(
                Arrays.asList(disabledLastRowPos.split(" ")),
                Integer::parseUnsignedInt
        );
    }

    @Override
    public List<String> getNames() throws IllegalConfigException {
        if (names == null) {
            throw new IllegalConfigException("Name list cannot be null");
        }
        final List<String> l = modifiableListOf(names.split(" "));
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
        return l;
    }

    @Override
    public List<String> getGroupLeaders() throws IllegalConfigException {
        if (groupLeaders == null) {
            throw new IllegalConfigException("Group leader list cannot be null");
        }
        final List<String> l = modifiableListOf(groupLeaders.split(" "));
        if (l.contains(EMPTY_SEAT_PLACEHOLDER)) {
            throw new IllegalConfigException(
                    "Group leader list must not contain empty seat place holder \"%s\"".formatted(EMPTY_SEAT_PLACEHOLDER)
            );
        }
        return l;
    }

    @Override
    public List<SeparatedPair> getSeparatedPairs() throws IllegalConfigException {
        if (separatedPairs == null) {
            throw new IllegalConfigException("Separated list cannot be null");
        }

        final List<String> t = separatedPairs.lines().toList();
        final List<SeparatedPair> s = new ArrayList<>(t.size());

        t.forEach(m -> {
            if (m != null && !m.isBlank()) {
                s.add(new SeparatedPair(m));
            }
        });

        return s;
    }

    @Override
    public Boolean isLucky() {
        return Utils.elseIfNull(lucky, false);
    }

    private void checkRowCount() throws IllegalConfigException {
        if (rowCount == null || rowCount <= 0) {
            throw new IllegalConfigException("Row count cannot be equal to or less than 0");
        }
    }

    private void checkColumnCount() throws IllegalConfigException {
        if (columnCount == null || columnCount <= 0) {
            throw new IllegalConfigException("Column count cannot be null or equal to/less than 0");
        }
        if (columnCount > MAX_COLUMN_COUNT) {
            throw new IllegalConfigException("Column count cannot be larger than " + MAX_COLUMN_COUNT);
        }
    }

    private void checkDisabledLastRowPos() throws IllegalConfigException {
        if (disabledLastRowPos == null) {
            throw new IllegalConfigException("Disabled last row positions cannot be null");
        }
        if (!Strings.integerListPattern.matcher(disabledLastRowPos).matches()) {
            throw new IllegalConfigException("Invalid disabled last row positions: " + disabledLastRowPos);
        }
    }

    /**
     * Returns JSON string of this.
     *
     * @return parsed json string
     */
    public String toJson() {
        return JSON.toJSONString(this, Feature.PrettyFormat, Feature.FieldBased);
    }

    /**
     * Checks the format of this instance.
     *
     * @throws IllegalConfigException if this instance has an illegal format
     */
    public void check() {
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
            getNames();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getGroupLeaders();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        try {
            getSeparatedPairs();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        if (lucky == null) {
            causes.add(new IllegalConfigException("Lucky option cannot be null"));
        }
        if (exportWritable == null) {
            causes.add(new IllegalConfigException("Export writable cannot be null"));
        }
        if (!causes.isEmpty()) {
            throw new IllegalConfigException(causes);
        }
    }

    /**
     * Pulls {@code this} and {@code value} together.
     * Fields of {@code value} that are not null will override the field in {@code this}.
     *
     * @param value to set to {@code this}
     */
    public void set(final JSONAppConfig value) {
        if (value == null) {
            return;
        }

        try {
            for (final Field field : fields) {
                field.set(this, Utils.elseIfNull(field.get(value), field.get(this)));
            }
        } catch (final IllegalAccessException ignored) {
            // Impossible situation
        }
    }

    /**
     * Returns a clone of {@code this}.
     * Note that this method is native-based,
     * so it is fast but might not be safe.
     *
     * @return a clone of {@code this}
     * @see Object#clone()
     */
    @Override
    public JSONAppConfig clone() {
        try {
            return (JSONAppConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This is impossible, since we are Cloneable");
        }
    }

    /**
     * Checks format and returns {@code this}.
     *
     * @return this
     * @throws IllegalConfigException if this instance has an illegal format
     * @see #check()
     */
    public SeatConfig checkAndReturn() throws IllegalConfigException {
        check();
        return this;
    }
}
