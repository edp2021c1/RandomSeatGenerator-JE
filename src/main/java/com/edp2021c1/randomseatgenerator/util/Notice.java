/*
 * RandomSeatGenerator
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

import java.util.Objects;

/**
 * Implemented by classes that can be shown in a simple dialog.
 *
 * @author Calboot
 * @since 1.6.0
 */
@FunctionalInterface
public interface Notice {

    /**
     * Returns a notice whose {@link #message()} method returns the given value.
     *
     * @param message of the notice
     *
     * @return a notice whose {@code message()} method returns the given value
     */
    static Notice of(final String message) {
        return () -> message;
    }

    /**
     * Returns a notice that represents the given {@link Throwable}.
     *
     * @param t thread from which the {@code Throwable} is thrown from
     * @param e the {@code Throwable} to show in the notice
     *
     * @return a notice that represents the given {@code Throwable}
     */
    static Notice of(final Thread t, final Throwable e) {
        if (Objects.requireNonNull(e) instanceof final Notice n) {
            return n;
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

    /**
     * Returns the string form of the notice.
     *
     * @return the string form of the notice
     */
    default String string() {
        return title() + ": " + message();
    }

    /**
     * Returns the title of the notice, default to {@code "Notice"}.
     *
     * @return the title of the notice
     */
    default String title() {
        return "Notice";
    }

    /**
     * Returns the message of the notice.
     * The message should be shown in the body part of the dialog.
     *
     * @return the message of the notice
     */
    String message();

}
