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

import java.util.Objects;

@FunctionalInterface
public interface Notice {

    static Notice of(final Thread t, final Throwable e) {
        if (Objects.requireNonNull(e) instanceof Notice) {
            return (Notice) e;
        }

        return new Notice() {

            private final String message = Strings.getStackTrace(e);

            private final String title = e.getClass().getSimpleName() + (t == null ? "" : " thrown from " + t);

            @Override
            public String title() {
                return title;
            }

            @Override
            public String message() {
                return message;
            }
        };
    }

    default String title() {
        return "Notice";
    }

    String message();

}
