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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public enum ExceptionType {

    IO("io", IOException.class),
    SEAT("seat", RuntimeException.class),
    COMMON("", Throwable.class);

    private static final String BASE = "randomseatgenerator.exception";

    public static ExceptionType of(@NotNull Throwable cause) {
        if (IO.isOf(cause)) {
            return IO;
        } else {
            return COMMON;
        }
    }

    public final String trKey;

    public final Class<? extends Throwable> clazz;

    ExceptionType(@NotNull String trKey, @NotNull Class<? extends Throwable> clazz) {
        this.trKey = trKey.isBlank() ? BASE : BASE + "." + trKey;
        this.clazz = clazz;
    }

    public boolean isOf(@NotNull Throwable cause) {
        return clazz.isAssignableFrom(cause.getClass());
    }

}
