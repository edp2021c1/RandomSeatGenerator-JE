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

package com.edp2021c1.randomseatgenerator.core;

/**
 * Thrown if {@code SeatConfig} has an illegal format.
 *
 * @author Calboot
 * @since 1.2.6
 */
public class IllegalConfigException extends RuntimeException {

    /**
     * Constructs an {@code IllegalConfigException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public IllegalConfigException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in the detail of this exception
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). (A {@code null} value
     *                is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public IllegalConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
