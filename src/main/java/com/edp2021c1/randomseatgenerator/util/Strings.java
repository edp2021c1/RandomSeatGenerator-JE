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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Strings {

    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");

    private static final List<Character> CHARACTERS_AND_DIGITS = List.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    public static final Predicate<String> integerPatternPredicate = Pattern.compile("-?\\d+").asMatchPredicate();

    public static final Predicate<String> unsignedIntegerPatternPredicate = Pattern.compile("\\d+").asMatchPredicate();

    public static String nowStr() {
        return nowStr(defaultDateFormat);
    }

    public static String nowStrShort() {
        return nowStr(dateFormatShort);
    }

    public static String nowStr(final DateFormat format) {
        return format.format(new Date());
    }

    public static String getStackTrace(final Throwable e) {
        StringWriter writer      = new StringWriter(1024);
        PrintWriter  printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        printWriter.close();
        return writer.toString();
    }

    public static String randomString(final int len) {
        char[] chars = new char[len];
        Random rd    = new Random();
        for (int i = 0; i < len; i++) {
            chars[i] = CollectionUtils.randomlyPickOne(CHARACTERS_AND_DIGITS, rd);
        }
        return new String(chars);
    }

}
