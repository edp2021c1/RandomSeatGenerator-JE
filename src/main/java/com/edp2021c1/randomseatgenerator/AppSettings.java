/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

package com.edp2021c1.randomseatgenerator;

import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

public final class AppSettings {

    private static final Path configPath = Metadata.DATA_DIR.resolve("config.json");

    private static final AppConfig BUILTIN;

    public static boolean withGUI = false;

    public static boolean mac = false;

    public static AppConfig config = null;

    public static boolean initializingDone = false;

    static {
        try {
            BUILTIN = AppConfig.loadFromJson(IOUtils.readResource("assets/conf/builtin.json"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void loadConfig() throws IOException {
        LOGGER.debug("Loading config from {}", configPath);
        if (Files.isDirectory(configPath)) {
            LOGGER.warn("Directory on config path, will remove");
            IOUtils.replaceWithFile(configPath);
        }
        AppConfig c = AppConfig.loadFromPath(configPath);
        if (c == null) {
            LOGGER.warn("No valid config in {}, will use builtin config", configPath);
            c = BUILTIN.copy();
            c.saveToPath(configPath);
        }
        config = c;
    }

    public static void saveConfig() throws IOException {
        if (config != null) {
            config.saveToPath(configPath);
        } else {
            loadConfig();
        }
    }

}
