/*
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

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Contains several methods related to {@link SeatConfig}.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class ConfigUtils {
    private static final SeatConfig DEFAULT_CONFIG = loadDefaultConfig();
    private static final Path CONFIG_PATH;

    static {
        Path configDir = MetaData.USER_HOME;

        if (OperatingSystem.CURRENT == OperatingSystem.WINDOWS) {
            configDir = Paths.get(System.getenv("APPDATA"), "RandomSeatGenerator");
        } else if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            configDir = Paths.get(System.getProperty("user.home"), "Library/Application Support", "RandomSeatGenerator");
        }

        configDir = configDir.toAbsolutePath();
        if (!Files.isDirectory(configDir)) {
            try {
                Files.delete(configDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (Files.notExists(configDir)) {
            try {
                Files.createDirectory(configDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        CONFIG_PATH = Paths.get(configDir.toString(), "seat_config.json");
    }

    /**
     * Load an instance from a JSON file.
     *
     * @param file to load from.
     * @return {@code SeatConfig} loaded from file.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     */
    public static SeatConfig fromJsonFile(File file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), SeatConfig.class);
    }

    /**
     * Translates an instance of {@link SeatConfig} into {@code Json}.
     *
     * @param config to translate into Json.
     * @return a {@code Json} representation of the object.
     */
    public static String parseJson(SeatConfig config) {
        return new Gson().toJson(config);
    }

    private static SeatConfig loadDefaultConfig() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ConfigUtils.class.getResourceAsStream("/assets/conf/default.json"))));
        StringBuilder buffer = new StringBuilder();
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        str = buffer.toString();
        return new Gson().fromJson(str, SeatConfig.class);
    }

    /**
     * Writes {@code SeatConfig} to {@code seat_config.json} under the current directory.
     *
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     */
    public static void saveConfig(SeatConfig config) {
        config.checkFormat();
        try {
            FileWriter writer = new FileWriter(CONFIG_PATH.toFile());
            writer.write(parseJson(config));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reload config from {@code seat_config.json} under the current directory.
     * If the file does nod exist, it will be created and containing the built-in config.
     *
     * @return default seat config loaded from file.
     */
    public static SeatConfig reloadConfig() {
        File f = CONFIG_PATH.toFile();
        try {
            SeatConfig config;
            if (f.createNewFile()) {
                System.err.println("WARNING: seat_config.json not found, will use default value.");
                saveConfig(DEFAULT_CONFIG);
            }
            config = ConfigUtils.fromJsonFile(f);
            try {
                config.checkFormat();
            } catch (RuntimeException e) {
                throw new IllegalConfigException("Invalid seat_config.json.", e);
            }
            return config;
        } catch (IOException e) {
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
