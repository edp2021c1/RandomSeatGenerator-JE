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

package com.edp2021c1.randomseatgenerator.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Contains several methods related to {@link AppConfig}.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class ConfigUtils {
    private static final Path CONFIG_PATH;
    private static final Logger LOGGER = Logger.getGlobal();
    private static FileTime configLastModifiedTime = null;
    private static AppConfig current;
    private static final AppConfig DEFAULT_CONFIG = loadDefaultConfig();

    static {
        final Path configDir;

        if (OperatingSystem.CURRENT == OperatingSystem.WINDOWS) {
            configDir = Paths.get(
                    Paths.get(System.getenv("APPDATA")).getParent().toString(),
                    "Local",
                    "RandomSeatGenerator");
        } else if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            configDir = Paths.get(
                    System.getProperty("user.home"),
                    "Library/Application Support",
                    "RandomSeatGenerator");
        } else {
            configDir = Paths.get(MetaData.WORKING_DIR, ".rdstgnrt");
        }

        if (Files.notExists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.isDirectory(configDir)) {
            try {
                Files.delete(configDir);
                Files.createDirectories(configDir);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        CONFIG_PATH = Paths.get(configDir.toString(), "randomseatgenerator.json");
        if (Files.notExists(CONFIG_PATH)) {
            try {
                Files.createFile(CONFIG_PATH);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.isRegularFile(CONFIG_PATH)) {
            try {
                Files.delete(CONFIG_PATH);
                Files.createFile(CONFIG_PATH);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
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
        return new Gson().fromJson(Files.readString(path), AppConfig.class);
    }

    /**
     * Translates an instance of {@link AppConfig} into {@code Json}.
     *
     * @param config to translate into Json.
     * @return a {@code Json} representation of the object.
     */
    public static String parseJson(final AppConfig config) {
        return new Gson().toJson(config);
    }

    private static AppConfig loadDefaultConfig() {
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
            if (current == null) {
                current = new Gson().fromJson(str, AppConfig.class);
            } else {
                current.set(new Gson().fromJson(str, AppConfig.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return current;
    }

    /**
     * Writes {@code SeatConfig} to {@code seat_config.json} under the current directory.
     *
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     */
    public static void saveConfig(final AppConfig config) {
        current.set(config);
        try {
            final FileWriter writer = new FileWriter(CONFIG_PATH.toFile());
            writer.write(parseJson(current));
            writer.close();
            Files.setLastModifiedTime(CONFIG_PATH, (configLastModifiedTime = FileTime.from(Instant.now())));
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
    public static AppConfig reloadConfig() {
        try {
            if (Files.getLastModifiedTime(CONFIG_PATH).equals(configLastModifiedTime)) {
                return current;
            }
            AppConfig config;
            if (Files.notExists(CONFIG_PATH)) {
                LOGGER.warning("seat_config.json not found, will use default value.");
                Files.createFile(CONFIG_PATH);
                saveConfig(DEFAULT_CONFIG);
            }
            config = ConfigUtils.fromJson(CONFIG_PATH);
            try {
                config.checkFormat();
            } catch (final RuntimeException e) {
                LOGGER.warning("Invalid seat_config.json, will use default value.");
                saveConfig(DEFAULT_CONFIG);
                config = DEFAULT_CONFIG;
            }
            return config;
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
