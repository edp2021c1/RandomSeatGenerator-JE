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

import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;

import java.util.Hashtable;
import java.util.Map;

/**
 * This class stores the config in a hash table, which maps keys to values.
 * Only non-null strings can be keys, and only non-null objects can be values.
 *
 * @author Calboot
 * @see java.util.Hashtable
 * @since 1.5.1
 */
public class Config extends Hashtable<String, Object> {

    /**
     * Constructs an empty instance.
     *
     * @see Hashtable#Hashtable(Map)  Hashtable
     */
    public Config() {
        super();
    }

    /**
     * Constructs an instance.
     *
     * @param map whose mappings are to be placed in this map
     * @see Hashtable#Hashtable(Map)  Hashtable
     */
    public Config(final Map<String, ?> map) {
        super(map);
    }

    /**
     * Returns the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code String} value is to be returned
     * @return the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code String}
     */
    public String getString(final String key) {
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final String s) {
            return s;
        }
        throw new IllegalConfigException("Invalid class type");
    }

    /**
     * Returns the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Integer} value is to be returned
     * @return the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Integer getInteger(final String key) {
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Number n) {
            return n.intValue();
        }
        throw new IllegalConfigException("Invalid class type");
    }

    /**
     * Returns the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Boolean} value is to be returned
     * @return the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Boolean}
     */
    public Boolean getBoolean(final String key) {
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Boolean b) {
            return b;
        }
        throw new IllegalConfigException("Invalid class type");
    }

    /**
     * Returns the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Double} value is to be returned
     * @return the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Double getDouble(final String key) {
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Number n) {
            return n.doubleValue();
        }
        throw new IllegalConfigException("Invalid class type");
    }

}
