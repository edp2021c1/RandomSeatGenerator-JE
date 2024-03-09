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

import com.alibaba.fastjson2.JSONObject;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.util.exception.InvalidClassTypeException;
import lombok.val;

import java.util.Map;

import static com.alibaba.fastjson2.JSONWriter.Feature.MapSortField;
import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;

/**
 * This class stores the config in a hash map, which maps keys to values.
 * A {@code null} cannot be used as a key.
 *
 * @author Calboot
 * @see java.util.HashMap
 * @since 1.5.1
 */
public class JSONConfig extends JSONObject {

    /**
     * Constructs an empty instance.
     *
     * @see JSONObject#JSONObject()  JSONObject
     */
    public JSONConfig() {
        super();
    }

    /**
     * Constructs an instance.
     *
     * @param initialCapacity the initial capacity of the config
     * @see JSONObject#JSONObject(int)  JSONObject
     */
    public JSONConfig(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Returns the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code String} value is to be returned
     * @return the {@code String} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code String}
     */
    public String getString(final String key) {
        val o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final String s) {
            return s;
        }
        throw new InvalidClassTypeException(String.class, o.getClass());
    }

    /**
     * Returns the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Double} value is to be returned
     * @return the {@code Double} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Double getDouble(final String key) {
        val o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Number n) {
            return n.doubleValue();
        }
        throw new InvalidClassTypeException(Double.class, o.getClass());
    }

    /**
     * Returns the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Integer} value is to be returned
     * @return the {@code Integer} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Number}
     */
    public Integer getInteger(final String key) {
        val o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Number n) {
            return n.intValue();
        }
        throw new InvalidClassTypeException(Integer.class, o.getClass());
    }

    /**
     * Returns the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key whose associated {@code Boolean} value is to be returned
     * @return the {@code Boolean} value to which the specified key is mapped, or null if this map contains no mapping for the key
     * @throws IllegalConfigException if the key exists, and the value of the key is not a {@code Boolean}
     */
    public Boolean getBoolean(final String key) {
        val o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Boolean b) {
            return b;
        }
        throw new InvalidClassTypeException(Boolean.class, o.getClass());
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
    public JSONConfig putAndReturn(final String key, final Object value) {
        put(key, value);
        return this;
    }

    /**
     * Copies all the mappings from the specified map to this instance, and returns {@code this}.
     *
     * @param map mappings to be stored in this map
     * @return {@code this}
     */
    public JSONConfig putAllAndReturn(final Map<? extends String, ?> map) {
        putAll(map);
        return this;
    }

    /**
     * Parses and puts the {@link JSONObject} from the string.
     *
     * @param jsonString to parse and put
     * @return {@code this}
     */
    public JSONConfig putJsonAndReturn(final String jsonString) {
        return putAllAndReturn(parseObject(jsonString));
    }

}
