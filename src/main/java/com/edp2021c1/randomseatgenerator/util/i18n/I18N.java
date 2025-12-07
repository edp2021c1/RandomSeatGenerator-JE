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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

public final class I18N {

    @Deprecated
    public static final String LANG_PATH = "assets/lang/%s.json";

    @Deprecated
    public static final String ROOT_KEY = "randomseatgenerator.";

    @Deprecated
    public static final String CONSTANT_KEY = ROOT_KEY + "constants.";

    private static ResourceBundle resourceBundle = null;

    private static Language language = Language.ENGLISH_US;

    public static void init(@NotNull String code) {
        LOGGER.debug("Language: {}", code);
        language = Language.getByCode(code);
    }

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle != null) {
            return resourceBundle;
        }
        resourceBundle = ResourceBundle.getBundle("assets.lang.I18N", language.getLocale(), DefaultResourceBundleControl.INSTANCE);
        return resourceBundle;
    }

    public static String i18n(@PropertyKey(resourceBundle = "assets.lang.I18N") String key, Object... formatArgs) {
        try {
            return String.format(getResourceBundle().getString(key), formatArgs);
        } catch (MissingResourceException e) {
            LOGGER.error("Cannot find key {} in resource bundle", key, e);
        } catch (IllegalFormatException e) {
            LOGGER.error("Illegal format string, key={}, args={}", key, Arrays.toString(formatArgs), e);
        }

        return key + Arrays.toString(formatArgs);
    }

    public static String i18n(@PropertyKey(resourceBundle = "assets.lang.I18N") String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            LOGGER.error("Cannot find key {} in resource bundle", key, e);
            return key;
        }
    }

}
