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

package com.edp2021c1.randomseatgenerator.util.i18n;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.exception.ExceptionHandler;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

public final class I18N {

    private static final TypeToken<Map<String, String>> MAP_TYPE = new TypeToken<>() {
    };

    private static final Map<String, String> translations = Maps.newHashMap();

    private static final Map<String, String> fallback = Maps.newHashMap();

    public static final String LANG_PATH = "assets/lang/%s.json";

    public static final String ROOT_KEY = "randomseatgenerator.";

    public static final String CONSTANT_KEY = ROOT_KEY + "constants.";

    private static String code = "zh_cn";

    public static void init(@NotNull String code) {
        I18N.code = code.toLowerCase();
        LOGGER.debug("Language: {}", I18N.code);

        String json;

        try {
            json = IOUtils.readResource(LANG_PATH.formatted(I18N.code));
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
            return;
        }
        translations.putAll(RandomSeatGenerator.GSON.fromJson(json, MAP_TYPE));

        try {
            json = IOUtils.readResource(LANG_PATH.formatted("en_us"));
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
            return;
        }
        fallback.putAll(RandomSeatGenerator.GSON.fromJson(json, MAP_TYPE));

    }

    public static String tr(@NotNull String key, Object... args) {
        return translations.getOrDefault(key, key).formatted(args);
    }

    public static String constant(String name) {
        return tr(CONSTANT_KEY + name);
    }

}
