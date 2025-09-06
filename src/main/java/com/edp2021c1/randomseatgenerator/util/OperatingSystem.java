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

import java.util.stream.Stream;

import static com.edp2021c1.randomseatgenerator.v2.util.Metadata.OS_NAME;

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
    WINDOWS(),
    /**
     * Mac OS X.
     */
    MAC(),
    /**
     * Linux and Unix like OS, including Solaris.
     */
    LINUX(),
    /**
     * Unknown operating system.
     */
    UNKNOWN();

    /**
     * Current operating system.
     */
    @Getter
    private static final OperatingSystem current;

    static {
        if (OS_NAME != null) {
            String osName = OS_NAME.toLowerCase();
            if (osName.startsWith("windows")) {
                current = WINDOWS;
            } else if (osName.startsWith("mac")) {
                current = MAC;
            } else if (Stream.of("solaris", "linux", "unix", "sunos").anyMatch(osName::contains)) {
                current = LINUX;
            } else {
                current = UNKNOWN;
            }
        } else {
            current = UNKNOWN;
        }
    }

}
