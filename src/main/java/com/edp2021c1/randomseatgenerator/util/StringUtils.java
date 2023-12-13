package com.edp2021c1.randomseatgenerator.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

/**
 * Useful methods related to {@link String}.
 *
 * @author Calboot
 * @since 1.4.5
 */
public class StringUtils {
    /**
     * Returns a long hash code of a {@code String}.
     *
     * @param str owner of the hash code
     * @return hash code of str
     */
    public static long longHash(final String str) {
        final byte[] val = str.getBytes();
        long h = 0;
        for (final byte v : val) {
            h = v - h + (h << 5);
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
