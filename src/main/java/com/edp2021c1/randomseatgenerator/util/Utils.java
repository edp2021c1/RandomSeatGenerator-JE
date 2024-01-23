package com.edp2021c1.randomseatgenerator.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Shorter usages of complicated methods and usages.
 *
 * @author Calboot
 * @since 1.5.0
 */
public class Utils {

    /**
     * Don't let anyone else instantiate this class.
     */
    private Utils() {
    }

    public static boolean isMac() {
        return OperatingSystem.getCurrent().isMac();
    }

    public static void delete(final Path path) throws IOException {
        IOUtils.deleteIfExists(path);
    }

    public static Path join(final Path parent, final String... children) {
        return Path.of(parent.toString(), children);
    }

}
