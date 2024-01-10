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

package com.edp2021c1.randomseatgenerator.util.logging;

import com.edp2021c1.randomseatgenerator.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.*;

/**
 * Logging related util.
 *
 * @author Calboot
 * @since 1.4.4
 */
public class Logging {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("RandomSeat");
    private static final Path logDir = Paths.get(Metadata.DATA_DIR, "logs");
    private static final List<Path> logPaths;
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("[{0,date,HH:mm:ss}] [{1}/{2}] {3}\n");
    private static final Formatter defaultFormatter;
    private static boolean initialized = false;

    static {
        final String str = "%tF-%%d.log".formatted(new Date());
        int t = 1;
        while (Files.exists(logDir.resolve(str.formatted(t)))) {
            t++;
        }
        logPaths = CollectionUtils.modifyFreeList(logDir.resolve("latest.log"), logDir.resolve(str.formatted(t)));

        defaultFormatter = new Formatter() {
            @Override
            public String format(final LogRecord record) {
                return record.getMessage();
            }
        };
    }

    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Logger not initialized");
        }
    }

    /**
     * Logs an USER message.
     *
     * @param msg logged message
     */
    public static void user(final String msg) {
        checkInitialized();
        logger.log(LoggingLevels.USER_INFO, msg);
    }

    /**
     * Logs an INFO message.
     *
     * @param msg logged message
     */
    public static void info(final String msg) {
        checkInitialized();
        logger.log(LoggingLevels.INFO, msg);
    }

    /**
     * Logs a WARNING message.
     *
     * @param msg logged message
     */
    public static void warning(final String msg) {
        checkInitialized();
        logger.log(LoggingLevels.WARNING, msg);
    }

    /**
     * Logs an ERROR message.
     *
     * @param msg logged message
     */
    public static void error(final String msg) {
        checkInitialized();
        logger.log(LoggingLevels.ERROR, msg);
    }

    /**
     * Logs a DEBUG message.
     *
     * @param msg logged message
     */
    public static void debug(final String msg) {
        checkInitialized();
        logger.log(LoggingLevels.DEBUG, msg);
    }

    /**
     * Starts logging.
     *
     * @param mode logging mode
     */
    public static void start(final LoggingMode mode) {
        if (initialized) {
            debug("Logging already started, there's no need to start it twice");
            return;
        }

        initialized = true;

        logger.setLevel(LoggingLevels.ALL);
        logger.setUseParentHandlers(false);
        logger.setFilter(record -> {
            record.setMessage(format(record));
            return true;
        });

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(defaultFormatter);
        consoleHandler.setLevel(mode == LoggingMode.CONSOLE ? LoggingLevels.USER_INFO : LoggingLevels.INFO);
        logger.addHandler(consoleHandler);

        try {
            if (!Files.isDirectory(logDir)) {
                IOUtils.deleteIfExists(logDir);
            }
            Files.createDirectories(logDir);
        } catch (final IOException e) {
            warning("Unable to create log dir, log may not be saved");
            warning(Strings.getStackTrace(e));
        }

        try {
            if (Files.notExists(logDir) || !Files.isDirectory(logDir)) {
                IOUtils.deleteIfExists(logDir);
                Files.createDirectories(logDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (IOUtils.lackOfPermission(logDir)) {
            warning("Does not have read/write permission of the log directory");
        }
        logPaths.forEach(path -> {
            try {
                final FileHandler fileHandler = new FileHandler(path.toString());
                fileHandler.setLevel(LoggingLevels.DEBUG);
                fileHandler.setFormatter(defaultFormatter);
                fileHandler.setEncoding("UTF-8");
                logger.addHandler(fileHandler);
            } catch (final IOException e) {
                warning("Failed to create log file at " + path);
                warning(Strings.getStackTrace(e));
            }
        });

        debug("Logging started");
        info("*** " + Metadata.TITLE + " ***");
        debug("Launching mode: " + mode);
        debug("OS: " + Metadata.SYSTEM_NAME + " " + Metadata.SYSTEM_VERSION);
        debug("Architecture: " + Metadata.SYSTEM_ARCH);
        debug("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        debug("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        debug("Java Home: " + System.getProperty("java.home"));
        debug("Memory: " + (Runtime.getRuntime().maxMemory() >>> 20) + "MB");
    }

    private static String format(LogRecord record) {
        final String message = record.getMessage();

        final StringBuffer buffer = new StringBuffer(1024);

        MESSAGE_FORMAT.format(new Object[]{
                new Date(record.getMillis()),
                Objects.requireNonNull(RuntimeUtils.getThreadById(record.getLongThreadID())).getName(), record.getLevel().getName(),
                message
        }, buffer, null);

        return buffer.toString();
    }

    /**
     * Ends logging
     */
    public static void close() {
        debug("Closing log");
        for (final Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
            h.close();
        }
        initialized = false;
    }

    /**
     * Logging modes.
     */
    public enum LoggingMode {
        /**
         * Console logging mode, sets console logging level
         * to {@link LoggingLevels#USER_INFO}
         */
        CONSOLE(false),
        /**
         * GUI logging mode, sets console logging level
         * to {@link LoggingLevels#INFO}
         */
        GUI(true);
        private final boolean upperCase;

        LoggingMode(boolean upperCase) {
            this.upperCase = upperCase;
        }

        @Override
        public String toString() {
            if (upperCase) {
                return name().toUpperCase();
            } else {
                final char[] str = name().toLowerCase().toCharArray();
                str[0] = Character.toUpperCase(str[0]);
                return new String(str);
            }
        }
    }

}
