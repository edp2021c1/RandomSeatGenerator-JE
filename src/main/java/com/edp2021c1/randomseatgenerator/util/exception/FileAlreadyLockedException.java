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

import java.io.IOException;
import java.nio.file.Path;

/**
 * Thrown if a file is already locked by another process when it is tried to be locked.
 */
public class FileAlreadyLockedException extends IOException {
    /**
     * Default constructor.
     *
     * @param filePath path of the file locked
     */
    public FileAlreadyLockedException(final Path filePath) {
        super(filePath.toAbsolutePath().toString());
    }
}
