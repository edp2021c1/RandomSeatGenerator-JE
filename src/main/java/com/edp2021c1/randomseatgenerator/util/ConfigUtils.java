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

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.google.gson.Gson;

import java.io.*;
import java.util.Objects;

/**
 * Includes several methods related to config.
 */
public class ConfigUtils {

    private static final SeatConfig DEFAULT_CONFIG;

    static {
        DEFAULT_CONFIG = loadDefaultConfig();
    }

    private static SeatConfig loadDefaultConfig() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(RandomSeatGenerator.class.getResourceAsStream("/assets/conf/default.json"))));
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
            FileWriter writer = new FileWriter("seat_config.json");
            writer.write(config.toJson());
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
        File f = new File("seat_config.json");
        try {
            SeatConfig config;
            if (f.createNewFile()) {
                System.err.println("WARNING: seat_config.json not found, will use default value.");
                saveConfig(DEFAULT_CONFIG);
            }
            config = SeatConfig.fromJsonFile(f);
            try {
                config.checkFormat();
            } catch (RuntimeException e) {
                System.err.println("WARNING: Invalid seat_config.json, will reset to default.");
                saveConfig(DEFAULT_CONFIG);
                config = SeatConfig.fromJsonFile(f);
            }
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
