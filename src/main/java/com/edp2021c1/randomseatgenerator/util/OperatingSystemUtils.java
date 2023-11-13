/*
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

/**
 * Contains methods related to the operating system.
 */
public class OperatingSystemUtils {
    /**
     * Check if the application runs on macOS.
     *
     * @return if runs on macOS
     */
    public static boolean isOnMac() {
        String name = System.getProperty("os.name");
        return name != null && name.contains("Mac");
    }
}
