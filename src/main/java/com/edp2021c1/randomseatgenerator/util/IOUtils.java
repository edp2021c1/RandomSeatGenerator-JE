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

import com.google.common.io.Resources;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtils {

    public static String readResource(String path) throws IOException, IllegalArgumentException {
        return Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8);
    }

    public static String readFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeFile(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public static void replaceWithFile(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return;
        }
        if (!Files.exists(path)) {
            PathUtils.createParentDirectories(path);
            Files.createFile(path);
        }
        PathUtils.deleteDirectory(path);
        Files.createFile(path);
    }

    public static void delete(Path path) throws IOException {
        PathUtils.delete(path);
    }

    @Contract(pure = true)
    public static File getClosestDirectory(File file) {
        while (!file.isDirectory()) {
            file = file.getParentFile();
        }
        return file;
    }

}
