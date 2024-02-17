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

import java.io.IOException;
import java.nio.file.Path;

/**
 * Shorter usages of complicated methods and usages.
 *
 * @author Calboot
 * @since 1.5.0
 */
public class Utils {

    /**
     * Don't let anyone else instantiate this class.
     */
    private Utils() {
    }

    /**
     * Returns if the current os is macOS.
     * <p>AKA shorter usage of {@code  OperatingSystem.getCurrent().isMac()}
     *
     * @return if the current os is macOS.
     */
    public static boolean isMac() {
        return OperatingSystem.getCurrent().isMac();
    }

    /**
     * Deletes a path and (if exists) everything under it.
     * <p>AKA shorter usage of {@link IOUtils#deleteIfExists(Path)}
     *
     * @param path to delete
     * @throws IOException if an I/O error occurs
     * @see IOUtils#deleteIfExists(Path)
     */
    public static void delete(final Path path) throws IOException {
        IOUtils.deleteIfExists(path);
    }

    /**
     * Joins the parent path and the children.
     *
     * @param parent   the path or initial part of the path
     * @param children additional strings to be joined to form the path string
     * @return the resulting path
     */
    public static Path join(final Path parent, final String... children) {
        return Path.of(parent.toString(), children);
    }

    /**
     * Returns {@code elseObj} if {@code obj} is null, and {@code obj} if not.
     *
     * @param obj     object
     * @param elseObj the other object
     * @param <T>     type of the object
     * @return {@code elseObj} if {@code obj} is null, and {@code obj} if not
     */
    public static <T> T elseIfNull(final T obj, final T elseObj) {
        return obj == null ? elseObj : obj;
    }

}
