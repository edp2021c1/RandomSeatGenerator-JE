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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * IO utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public class IOUtils {
    private static final FileVisitor<Path> deleteAllUnder = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                throws IOException {
            if (e != null) {
                throw e;
            }
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Don't let anyone else instantiate this class.
     */
    private IOUtils() {
    }

    /**
     * Deletes a path and (if exists) everything under it.
     *
     * @param path to delete
     * @throws IOException if an I/O error occurs
     */
    public static void deleteIfExists(final Path path) throws IOException {
        if (Files.notExists(path)) {
            return;
        }
        if (!Files.isDirectory(path)) {
            Files.delete(path);
            return;
        }
        Files.walkFileTree(path, deleteAllUnder);
    }

    /**
     * Checks if this application has permission to read and write a specific path.
     *
     * @param path to check permission
     * @return if this app does not have read and write permission of the target path
     */
    public static boolean notFullyPermitted(final Path path) {
        return !(Files.isReadable(path) || Files.isWritable(path));
    }

    /**
     * Replaces the file on the given path (if exists) with an empty directory.
     *
     * @param path to replace
     * @throws IOException if an I/O error occurs
     */
    public static void replaceWithDirectory(final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            deleteIfExists(path);
            Files.createDirectories(path);
        }
    }

    /**
     * Replaces the file/directory on the given path (if exists) with a newly created file.
     *
     * @param path to replace
     * @throws IOException if an I/O error occurs
     */
    public static void replaceWithNewFile(final Path path) throws IOException {
        deleteIfExists(path);
        Files.createFile(path);
    }

}
