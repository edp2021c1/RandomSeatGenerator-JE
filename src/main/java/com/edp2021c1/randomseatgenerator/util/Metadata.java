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

package com.edp2021c1.randomseatgenerator.util;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.System.getProperty;

public final class Metadata {

    public static final Map<String, String> META = Maps.newHashMapWithExpectedSize(1);

    public static final String LICENSE;

    public static final String ICON_URL = "assets/img/icon.png";

    public static final URI GIT_REPOSITORY_URI;

    public static final URI LICENSE_URI;

    public static final URI VERSION_PAGE_URI;

    public static final String LICENSE_NAME = "GPLv3";

    public static final Path DATA_DIR = Paths.get(".").toAbsolutePath();

    public static final String NAME = "Random Seat Generator";

    public static final String OS_NAME = getProperty("os.name");

    public static final String VERSION_ID;

    public static final String BUILD_DATE;

    public static final String VERSION;

    public static final String TITLE;

    static {
        try {
            LICENSE = IOUtils.readResource("license");
            META.putAll(RandomSeatGenerator.GSON.fromJson(IOUtils.readResource("app.json"), new TypeToken<Map<String, String>>() {
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VERSION_ID = META.get("version");
        BUILD_DATE = META.get("buildDate");
        VERSION = (VERSION_ID == null) ? "dev" : ("v" + VERSION_ID);
        TITLE = NAME + " - " + VERSION;

        try {
            GIT_REPOSITORY_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE");
            LICENSE_URI = new URI("https://www.gnu.org/licenses/gpl-3.0.txt");
            VERSION_PAGE_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE/releases/tags/" + (VERSION_ID == null ? "" : VERSION));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
