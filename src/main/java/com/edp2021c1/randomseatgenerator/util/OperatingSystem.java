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
 * Enum of the operating system.
 *
 * @author Calboot
 * @since 1.2.9
 */
public enum OperatingSystem {
    /**
     * Microsoft Windows.
     */
    WINDOWS,
    /**
     * Linux and Unix like OS, including Solaris.
     */
    LINUX,
    /**
     * Mac OS X.
     */
    MAC,
    /**
     * Unknown operating system.
     */
    UNKNOWN;

    /**
     * Current operating system.
     */
    public static final OperatingSystem CURRENT = getOsByName(System.getProperty("os.name"));

    private static OperatingSystem getOsByName(String osName) {
        if (osName == null) {
            return UNKNOWN;
        }

        osName = osName.toLowerCase();
        if (osName.startsWith("windows")) {
            return WINDOWS;
        } else if (osName.startsWith("mac")) {
            return MAC;
        } else if (osName.contains("solaris") || osName.contains("linux") || osName.contains("unix") || osName.contains("sunos")) {
            return LINUX;
        } else {
            return UNKNOWN;
        }
    }

}
