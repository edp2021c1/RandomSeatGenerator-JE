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

import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Useful methods related to {@link String}.
 *
 * @author Calboot
 * @since 1.4.5
 */
public final class Strings {

    /**
     * Pattern of integer.
     */
    public static final Predicate<String> integerPatternPredicate = Pattern.compile("-?\\d+").asMatchPredicate();

    /**
     * Pattern of unsigned integer.
     */
    public static final Predicate<String> unsignedIntegerPatternPredicate = Pattern.compile("\\d+").asMatchPredicate();

    /**
     * Pattern of list of integer, divided by spaces.
     */
    public static final Predicate<String> integerListPatternPredicate = Pattern.compile("[0-9 ]*").asMatchPredicate();

    /**
     * Simple date format
     */
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final List<Character> CHARACTERS_AND_DIGITS = List.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    /**
     * Don't let anyone else instantiate this class.
     */
    private Strings() {
    }

    /**
     * Returns the instant of now formatted into string.
     *
     * @return now in {@code String}
     */
    public static String nowStr() {
        return nowStr(defaultDateFormat);
    }

    /**
     * Returns a string representing the current time in the given format.
     *
     * @param format to format the date
     *
     * @return a string representing the current time
     */
    public static String nowStr(final DateFormat format) {
        return format.format(new Date());
    }

    /**
     * Returns a long hash code of a {@code String}.
     *
     * @param str owner of the hash code
     *
     * @return hash code of {@code str}
     */
    public static long longHashCode(final String str) {
        var h = 0;
        for (val v : str.getBytes()) {
            h = (v & 0xffff) + (h << 5) - h;
        }
        return h;
    }

    /**
     * Returns stack trace of a {@code Throwable}.
     *
     * @param e owner of stack trace
     *
     * @return stack trace of {@code e}
     */
    public static String getStackTrace(final Throwable e) {
        val writer      = new StringWriter(1024);
        val printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        printWriter.close();
        return writer.toString();
    }

    /**
     * Randomly generates a {@code String} that contains only
     * decimal digits and English letters.
     *
     * @param len length of the string
     *
     * @return a randomly generated string
     */
    public static String randomString(final int len) {
        val chars = new char[len];
        val rd    = new Random();
        for (var i = 0; i < len; i++) {
            chars[i] = DataUtils.pickRandomly(CHARACTERS_AND_DIGITS, rd);
        }
        return new String(chars);
    }

}
