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

package com.edp2021c1.randomseatgenerator.util.exception;

/**
 * Thrown when the given class type cannot be cast into another class type.
 *
 * @author Calboot
 * @since 1.5.2
 */
public class InvalidClassTypeException extends ClassCastException {
    /**
     * Constructs an instance.
     *
     * @param clazz1 class type to cast to
     * @param clazz2 the given class type
     */
    public InvalidClassTypeException(final Class<?> clazz1, final Class<?> clazz2) {
        super("Unmatched class type: " + clazz1 + " and " + clazz2);
    }
}
