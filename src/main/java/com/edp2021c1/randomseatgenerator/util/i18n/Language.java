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

import com.edp2021c1.randomseatgenerator.AppSettings;
import org.jetbrains.annotations.Contract;

public enum Language {

    CHINESE_SIMPLIFIED("zh_cn", "简体中文"),
    ENGLISH_US("en_us", "English (US)");

    @Contract(pure = true)
    public static Language getByCode(String code) {
        for (Language language : Language.values()) {
            if (language.code.equals(code)) {
                return language;
            }
        }
        return null;
    }

    @Contract
    public static Language getCurrent() {
        return getByCode(AppSettings.config.language);
    }

    public final String code;

    public final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
