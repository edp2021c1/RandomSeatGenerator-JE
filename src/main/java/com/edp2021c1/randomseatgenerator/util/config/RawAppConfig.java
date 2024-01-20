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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the raw config of the application.
 *
 * @author Calboot
 * @since 1.4.8
 */
public class RawAppConfig extends RawSeatConfig implements Cloneable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    /**
     * If seat table is exported writable.
     */
    public Boolean export_writable;
    /**
     * The previous directory seat table is exported to.
     */
    public String last_export_dir;
    /**
     * The previous directory config is loaded from.
     */
    public String last_import_dir;
    /**
     * Determines whether the app is shown in the dark mode.
     */
    public Boolean dark_mode;

    /**
     * Constructs an instance.
     */
    public RawAppConfig() {
    }

    /**
     * Returns an instance created from a JSON string.
     *
     * @param json JSON string parsed
     * @return element parsed from json
     */
    public static RawAppConfig fromJson(final String json) {
        return GSON.fromJson(json, RawAppConfig.class);
    }

    /**
     * Load an instance from a JSON path.
     *
     * @param path to load from.
     * @return {@code RawSeatConfig} loaded from path.
     * @throws IOException if for some reason the path cannot be opened for reading.
     */
    public static RawAppConfig fromJson(final Path path) throws IOException {
        return fromJson(Files.readString(path));
    }

    /**
     * Returns JSON string of this.
     *
     * @return parsed json string
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    @Override
    public void checkFormat() {
        final List<IllegalConfigException> causes = new ArrayList<>();
        try {
            super.checkFormat();
        } catch (IllegalConfigException e) {
            causes.add(e);
        }
        if (export_writable == null) {
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
    public void set(final RawAppConfig value) {
        if (value == null) {
            return;
        }
        if (value.row_count != null) {
            row_count = value.row_count;
        }
        if (value.column_count != null) {
            column_count = value.column_count;
        }
        if (value.random_between_rows != null) {
            random_between_rows = value.random_between_rows;
        }
        if (value.last_row_pos_cannot_be_chosen != null) {
            last_row_pos_cannot_be_chosen = value.last_row_pos_cannot_be_chosen;
        }
        if (value.person_sort_by_height != null) {
            person_sort_by_height = value.person_sort_by_height;
        }
        if (value.group_leader_list != null) {
            group_leader_list = value.group_leader_list;
        }
        if (value.separate_list != null) {
            separate_list = value.separate_list;
        }
        if (value.lucky_option != null) {
            lucky_option = value.lucky_option;
        }
        if (value.export_writable != null) {
            export_writable = value.export_writable;
        }
        if (value.last_export_dir != null) {
            last_export_dir = value.last_export_dir;
        }
        if (value.last_import_dir != null) {
            last_import_dir = value.last_import_dir;
        }
        if (value.dark_mode != null) {
            dark_mode = value.dark_mode;
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
    public RawAppConfig clone() {
        try {
            return (RawAppConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This is impossible, since we are Cloneable");
        }
    }
}
