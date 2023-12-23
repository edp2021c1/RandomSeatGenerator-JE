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

import java.util.logging.Level;

/**
 * Logging levels.
 *
 * @author Calboot
 * @since 1.4.6
 */
class LoggingLevels extends Level {
    /**
     * Indicates messages for user to see.
     */
    public static final Level USER_INFO = new LoggingLevels("INFO", 950);

    /**
     * Indicates debug messages.
     */
    public static final Level DEBUG = new LoggingLevels("DEBUG", 200);
    /**
     * @see Level#SEVERE
     */
    public static final Level ERROR = SEVERE;

    protected LoggingLevels(String name, int value) {
        super(name, value);
    }

}
