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

import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * Contains several methods related to {@link AppConfig}.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class ConfigUtils {
    private static final Path CONFIG_DIR;
    private static final Path CONFIG_PATH;
    private static final Gson GSON = new Gson();
    private final static AppConfig current = new AppConfig();
    private static final AppConfig DEFAULT_CONFIG;
    private static FileTime configLastModifiedTime = null;
    private static boolean initialized = false;

    static {
        CONFIG_DIR = Paths.get(Metadata.DATA_DIR, "config");
        CONFIG_PATH = CONFIG_DIR.resolve("randomseatgenerator.json");

        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(ConfigUtils.class.getResourceAsStream("/assets/default.json"))
                    )
            );
            final StringBuilder buffer = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            str = buffer.toString();
            DEFAULT_CONFIG = GSON.fromJson(str, AppConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the config directory and file.
     */
    public static void initConfig() {
        Logging.debug("Initializing config");

        initialized = true;

        try {
            if (!Files.isDirectory(CONFIG_DIR)) {
                IOUtils.delete(CONFIG_DIR);
            }
            Files.createDirectories(CONFIG_DIR);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (IOUtils.lackOfPermission(CONFIG_DIR)) {
            throw new RuntimeException("Does not has enough permission to read/write config");
        }

        if (Files.notExists(CONFIG_PATH)) {
            saveConfig(DEFAULT_CONFIG);
        }
        if (IOUtils.lackOfPermission(CONFIG_PATH)) {
            throw new RuntimeException("Does not has enough permission to read/write config");
        }
        if (!Files.isRegularFile(CONFIG_PATH)) {
            try {
                IOUtils.delete(CONFIG_PATH);
                saveConfig(DEFAULT_CONFIG);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        Logging.debug("Config path: " + CONFIG_PATH);
    }

    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Config not initialized");
        }
    }

    /**
     * Load an instance from a JSON path.
     *
     * @param path to load from.
     * @return {@code SeatConfig} loaded from path.
     * @throws IOException if for some reason the path cannot be opened for reading.
     */
    public static AppConfig fromJson(final Path path) throws IOException {
        return GSON.fromJson(Files.readString(path), AppConfig.class);
    }

    /**
     * Writes {@code SeatConfig} to {@code seat_config.json} under the current directory.
     *
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     */
    public static void saveConfig(final AppConfig config) {
        checkInitialized();
        current.set(config);
        try {
            final FileWriter writer = new FileWriter(CONFIG_PATH.toFile());
            writer.write(GSON.toJson(ConfigUtils.current));
            writer.close();
            configLastModifiedTime = Files.getLastModifiedTime(CONFIG_PATH);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reload config from {@code seat_config.json} under the current directory.
     * If the file does nod exist, it will be created and containing the built-in config.
     *
     * @return default seat config loaded from file.
     */
    public static AppConfig getConfig() {
        refreshConfig();
        return current;
    }

    private static void refreshConfig() {
        checkInitialized();
        try {
            if (Objects.equals(Files.getLastModifiedTime(CONFIG_PATH), configLastModifiedTime)) {
                return;
            }
            if (Files.notExists(CONFIG_PATH)) {
                Logging.warning("Config file not found, will use default value");
                Files.createFile(CONFIG_PATH);
                saveConfig(DEFAULT_CONFIG);
            }
            current.set(ConfigUtils.fromJson(CONFIG_PATH));
            try {
                current.checkFormat();
            } catch (final RuntimeException e) {
                Logging.warning("Invalid config");
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns path of the config file.
     *
     * @return path of the config file.
     */
    public static Path getConfigPath() {
        return CONFIG_PATH;
    }

}
