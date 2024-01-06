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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Useful methods related to {@link String}.
 *
 * @author Calboot
 * @since 1.4.5
 */
public class StringUtils {
    /**
     * Simple date format
     */
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * @return now in {@code String}
     */
    public static String nowStr() {
        return defaultDateFormat.format(new Date());
    }

    /**
     * Returns a long hash code of a {@code String}.
     *
     * @param str owner of the hash code
     * @return hash code of {@code str}
     */
    public static long longHash(final String str) {
        final byte[] val = str.getBytes();
        long h = 0;
        for (final byte v : val) {
            h = (v & 0xffff) - h + (h << 5);
        }
        return h;
    }

    /**
     * Returns stack trace of a {@code Throwable}.
     *
     * @param e owner of stack trace
     * @return stack trace of {@code e}
     */
    public static String getStackTrace(final Throwable e) {
        final StringWriter writer = new StringWriter(512);
        try (final PrintWriter printWriter = new PrintWriter(writer)) {
            e.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    /**
     * Randomly generates a {@code String} that contains only
     * decimal digits and English letters.
     *
     * @param len length of the string
     * @return a randomly generated string
     */
    public static String randomString(final int len) {
        final char[] chars = new char[len];
        final Random random = new Random();
        int t;
        for (int i = 0; i < len; i++) {
            t = random.nextInt(62) + 48;
            if (t > 57) {
                t += 7;
            }
            if (t > 90) {
                t += 6;
            }
            chars[i] = (char) t;
        }
        return new String(chars);
    }
}
