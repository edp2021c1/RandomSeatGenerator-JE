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

import lombok.Getter;

import static com.edp2021c1.randomseatgenerator.util.Metadata.OS_NAME;

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
    WINDOWS("windows"),
    /**
     * Mac OS X.
     */
    MAC("mac"),
    /**
     * Linux and Unix like OS, including Solaris.
     */
    LINUX("linux"),
    /**
     * Unknown operating system.
     */
    UNKNOWN("unknown");

    /**
     * Current operating system.
     */
    @Getter
    private static final OperatingSystem current;

    static {
        if (OS_NAME != null) {
            final String osName = OS_NAME.toLowerCase();
            if (osName.startsWith("windows")) {
                current = WINDOWS;
            } else if (osName.startsWith("mac")) {
                current = MAC;
            } else if (osName.contains("solaris")
                    || osName.contains("linux")
                    || osName.contains("unix")
                    || osName.contains("sunos")) {
                current = LINUX;
            } else {
                current = UNKNOWN;
            }
        } else {
            current = UNKNOWN;
        }
    }

    @Getter
    private final boolean windows;
    @Getter
    private final boolean mac;
    @Getter
    private final boolean linux;
    @Getter
    private final boolean unknown;

    OperatingSystem(String osName) {
        switch (osName) {
            case "windows" -> {
                windows = true;
                mac = linux = unknown = false;
            }
            case "mac" -> {
                mac = true;
                windows = linux = unknown = false;
            }
            case "linux" -> {
                linux = true;
                windows = mac = unknown = false;
            }
            default -> {
                unknown = true;
                windows = mac = linux = false;
            }
        }
    }

}
