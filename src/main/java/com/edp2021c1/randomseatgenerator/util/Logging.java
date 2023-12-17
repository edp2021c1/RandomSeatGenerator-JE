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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
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
    private static final Logger LOG = Logger.getLogger("RandomSeat");
    private static final Path LOG_DIR = Paths.get(MetaData.DATA_DIR, "logs");
    private static final Path[] LOG_PATHS;
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("[{0,date,HH:mm:ss}] [{1}/{2}] {3}\n");
    private static final Formatter DEFAULT_FORMATTER;
    private static boolean started = false;

    static {
        String str = "%tF-%%d.log".formatted(new Date());
        int t = 1;
        while (Files.exists(LOG_DIR.resolve(str.formatted(t)))) {
            t++;
        }
        LOG_PATHS = new Path[]{LOG_DIR.resolve("latest.log"), LOG_DIR.resolve(str.formatted(t))};

        DEFAULT_FORMATTER = new Formatter() {
            @Override
            public String format(final LogRecord record) {
                return record.getMessage();
            }
        };
    }

    private static void checkStarted() {
        if (!started) {
            throw new IllegalStateException("Logging has not started yet");
        }
    }

    /**
     * Logs an INFO message.
     *
     * @param msg logged message
     */
    public static void info(String msg) {
        checkStarted();
        LOG.log(Level.INFO, msg);
    }

    /**
     * Logs a WARNING message.
     *
     * @param msg logged message
     */
    public static void warning(String msg) {
        checkStarted();
        LOG.log(Level.WARNING, msg);
    }

    /**
     * Logs an ERROR message.
     *
     * @param msg logged message
     */
    public static void error(String msg) {
        checkStarted();
        LOG.log(Level.ERROR, msg);
    }

    /**
     * Logs a DEBUG message.
     *
     * @param msg logged message
     */
    public static void debug(String msg) {
        checkStarted();
        LOG.log(Level.DEBUG, msg);
    }

    /**
     * Starts logging.
     */
    public static void start() {
        if (started) {
            throw new IllegalStateException("Logging already started");
        }

        started = true;

        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);
        LOG.setFilter(record -> {
            record.setMessage(format(record));
            return true;
        });

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(DEFAULT_FORMATTER);
        consoleHandler.setLevel(Level.INFO);
        LOG.addHandler(consoleHandler);

        try {
            if (!Files.isDirectory(LOG_DIR)) {
                IOUtils.delete(LOG_DIR);
            }
            Files.createDirectories(LOG_DIR);
        } catch (final IOException e) {
            warning("Unable to create log dir, log may not be saved");
            warning(StringUtils.getStackTrace(e));
        }

        for (final Path path : LOG_PATHS) {
            try {
                final FileHandler fileHandler = new FileHandler(path.toString());
                fileHandler.setLevel(Level.DEBUG);
                fileHandler.setFormatter(DEFAULT_FORMATTER);
                fileHandler.setEncoding("UTF-8");
                LOG.addHandler(fileHandler);
            } catch (final IOException e) {
                warning("Failed to create log file at " + path);
                warning(StringUtils.getStackTrace(e));
            }
        }

        debug("Logging started");
    }

    private static String format(LogRecord record) {
        final String message = record.getMessage();

        final StringBuffer buffer = new StringBuffer(1024);

        MESSAGE_FORMAT.format(new Object[]{
                new Date(record.getMillis()),
                ThreadUtils.getThreadById(record.getLongThreadID()).getName(), record.getLevel().getName(),
                message
        }, buffer, null);

        return buffer.toString();
    }

}
