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

package com.edp2021c1.randomseatgenerator.util.exception;

import com.edp2021c1.randomseatgenerator.util.i18n.TranslatableNotice;

import java.io.IOException;

public class TranslatableException extends RuntimeException implements TranslatableNotice {

    public static TranslatableException common(String key, Object... args) {
        return new TranslatableException(key, args);
    }

    public static TranslatableException io(IOException e, String key, Object... args) {
        return new TranslatableException(e, key, args);
    }

    public static TranslatableException seat(String key, Object... args) {
        return new TranslatableException(ExceptionType.SEAT, key, args);
    }

    private final String key;

    private final Object[] args;

    private final ExceptionType type;

    private TranslatableException(String key, Object... args) {
        this(ExceptionType.COMMON, key, args);
    }

    private TranslatableException(ExceptionType type, String key, Object... args) {
        super(key);
        this.key = type.trKey + "." + key;
        this.args = args;
        this.type = type;
    }

    private TranslatableException(Throwable cause, String key, Object... args) {
        super(key, cause);
        this.type = ExceptionType.of(cause);
        this.key = this.type.trKey + "." + key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return message();
    }

    @Override
    public String titleKey() {
        return this.type.trKey;
    }

    @Override
    public String messageKey() {
        return this.key;
    }

    @Override
    public Object[] messageArgs() {
        return this.args;
    }

}
