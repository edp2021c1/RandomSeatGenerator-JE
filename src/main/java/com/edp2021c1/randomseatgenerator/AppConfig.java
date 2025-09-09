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

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.util.IOUtils;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;

@EqualsAndHashCode
public class AppConfig {

    public static AppConfig loadFromPath(Path path) throws IOException {
        if (!PathUtils.isRegularFile(path)) {
            return null;
        }
        return loadFromJson(IOUtils.readFile(path));
    }

    public static AppConfig loadFromJson(String json) {
        return RandomSeatGenerator.GSON.fromJson(json, AppConfig.class);
    }

    public boolean darkMode;

    public String language;

    public SeatConfig seatConfig;

    public void saveToPath(Path path) throws IOException {
        IOUtils.writeFile(path, RandomSeatGenerator.GSON.toJson(this));
    }

    public AppConfig copy() {
        AppConfig appConfig = new AppConfig();
        appConfig.darkMode = darkMode;
        appConfig.language = language;
        appConfig.seatConfig = seatConfig.copy();
        return appConfig;
    }

}
