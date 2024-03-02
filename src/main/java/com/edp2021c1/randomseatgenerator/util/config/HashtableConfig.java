package com.edp2021c1.randomseatgenerator.util.config;

import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;

import java.util.Hashtable;
import java.util.Map;

public class HashtableConfig extends Hashtable<String, Object> {

    public HashtableConfig(final Map<String, ?> map) {
        putAll(map);
    }

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
