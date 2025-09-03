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

import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.PathWrapper;
import lombok.val;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Holder of the application's common properties.
 *
 * @author Calboot
 * @see Properties
 * @since 1.6.0
 */
public class AppPropertiesHolder {

    private static final PathWrapper globalPath = PathWrapper.wrap(Metadata.DATA_DIR.toString(), "config", "app.properties");

    private static final AppPropertiesHolder global;

    static {
        try {
            global = new AppPropertiesHolder(globalPath);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the global properties holder.
     *
     * @return the global holder
     */
    public static AppPropertiesHolder global() {
        return global;
    }

    private final Properties appProperties;

    private final PathWrapper path;

    /**
     * Constructs an instance.
     *
     * @param propertiesPath path of the file storing the properties
     *
     * @throws IOException if an I/O error occurs
     */
    public AppPropertiesHolder(final Path propertiesPath) throws IOException {
        path = PathWrapper.wrap(propertiesPath);
        appProperties = new Properties();
        init();
    }

    private void init() throws IOException {
        if (path.replaceIfNonRegularFile().notFullyPermitted()) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        appProperties.load(new StringReader(path.readString()));
    }

    private void store() {
        StringWriter str = new StringWriter();
        try {
            appProperties.store(str, null);
            path.writeString(str.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets and stores a property.
     *
     * @param key   to be placed into this property list
     * @param value the value corresponding to {@code key}
     *
     * @see Properties#setProperty(String, String)
     */
    public void setProperty(final String key, final Object value) {
        if (value != null) {
            appProperties.setProperty(key, value.toString());
            store();
        } else if (appProperties.containsKey(key)) {
            appProperties.remove(key);
            store();
        }
    }

    /**
     * Returns the boolean value in this property list with the specified key value.
     *
     * @param key of the property
     *
     * @return the value in this property list with the specified key value
     *
     * @see #getProperty(String)
     * @see Boolean#parseBoolean(String)
     */
    public boolean getBoolean(final String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * Returns the string value in this property list with the specified key value.
     *
     * @param key of the property
     *
     * @return the value in this property list with the specified key value
     *
     * @see Properties#getProperty(String)
     */
    public String getProperty(final String key) {
        return appProperties.getProperty(key);
    }

    /**
     * Returns the double value in this property list with the specified key value.
     *
     * @param key of the property
     *
     * @return the value in this property list with the specified key value
     *
     * @see #getProperty(String)
     * @see Double#parseDouble(String)
     */
    public double getDouble(final String key) {
        return Double.parseDouble(getProperty(key));
    }

}
