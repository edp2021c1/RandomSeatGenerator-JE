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
import lombok.val;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

import static com.edp2021c1.randomseatgenerator.util.Metadata.*;

/**
 * Logging related util.
 *
 * @author Calboot
 * @since 1.4.4
 */
public final class Logging {

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("RandomSeat");
    private static final PathWrapper logDir = DATA_DIR.resolve("logs");
    private static final List<PathWrapper> logPaths;
    private static final MessageFormat messageFormat = new MessageFormat("[{0,date,yyyy-MM-dd HH:mm:ss.SSS}] [{1}/{2}] {3}\n");
    private static final Formatter DEFAULT_FORMATTER;
    @Getter
    private static State state = State.UNINITIALIZED;

    static {
        val str = "%tF-%%d.log".formatted(new Date());
        var t = 1;
        while (logDir.resolve(str.formatted(t)).exists()) {
            t++;
        }
        logPaths = List.of(logDir.resolve("latest.log"), logDir.resolve(str.formatted(t)));

        DEFAULT_FORMATTER = new Formatter() {
            @Override
            public String format(final LogRecord record) {
                return record.getMessage();
            }
        };
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private Logging() {
    }

    /**
     * Returns whether the log manager is started.
     *
     * @return whether the log manager is started
     */
    public static boolean isStarted() {
        return state == State.STARTED;
    }

    private static void checkState() {
        if (!isStarted()) {
            throw new IllegalStateException("Logger closed or uninitialized");
        }
    }

    /**
     * Logs an INFO message.
     *
     * @param msg logged message
     */
    public static void info(final String msg) {
        checkState();
        logger.log(LoggingLevels.INFO, msg);
    }

    /**
     * Logs a WARNING message.
     *
     * @param msg logged message
     */
    public static void warning(final String msg) {
        checkState();
        logger.log(LoggingLevels.WARNING, msg);
    }

    /**
     * Logs an ERROR message.
     *
     * @param msg logged message
     */
    public static void error(final String msg) {
        checkState();
        logger.log(LoggingLevels.ERROR, msg);
    }

    /**
     * Logs a DEBUG message.
     *
     * @param msg logged message
     */
    public static void debug(final String msg) {
        checkState();
        logger.log(LoggingLevels.DEBUG, msg);
    }

    /**
     * Starts logging.
     */
    public static void start() {
        switch (state) {
            case STARTED -> {
                debug("Logging already started, there's no need to start it twice");
                return;
            }
            case ENDED -> throw new IllegalStateException("Logging manager already closed");
        }

        state = State.STARTED;

        val withGUI = (boolean) RuntimeUtils.runtimeConfig.getOrDefault("launching.gui", false);

        logger.setLevel(LoggingLevels.ALL);
        logger.setUseParentHandlers(false);
        logger.setFilter(record -> {
            format(record);
            return true;
        });

        final ConsoleHandler consoleHandler = new ConsoleHandler() {
            @Override
            public void close() {
                val record = format(new LogRecord(LoggingLevels.DEBUG, "Closing console log handler"));
                for (val h : logger.getHandlers()) {
                    h.publish(record);
                }
                publish(record);
                super.close();
            }
        };
        consoleHandler.setFormatter(DEFAULT_FORMATTER);
        consoleHandler.setLevel(withGUI ? LoggingLevels.DEBUG : LoggingLevels.INFO);
        logger.addHandler(consoleHandler);

        try {
            logDir.replaceWithDirectory();
        } catch (final IOException e) {
            warning("Unable to create log dir, log may not be saved");
            warning(Strings.getStackTrace(e));
        }
        if (logDir.notFullyPermitted()) {
            warning("Does not have read/write permission of the log directory");
        }
        logPaths.forEach(path -> {
            try {
                val fileHandler = new FileHandler(path.toString()) {
                    @Override
                    public void close() throws SecurityException {
                        val record = format(new LogRecord(LoggingLevels.DEBUG, "Closing log file " + path));
                        for (val h : logger.getHandlers()) {
                            h.publish(record);
                        }
                        publish(record);
                        super.close();
                    }
                };
                fileHandler.setLevel(LoggingLevels.DEBUG);
                fileHandler.setFormatter(DEFAULT_FORMATTER);
                fileHandler.setEncoding("UTF-8");
                logger.addHandler(fileHandler);
            } catch (final Throwable e) {
                warning("Failed to create log file at " + path);
                warning(Strings.getStackTrace(e));
            }
        });

        debug("Logging started");
        info("*** %s ***".formatted(TITLE));
        debug("Launching mode: " + (withGUI ? "GUI" : "Console"));
        debug("OS: %s %s".formatted(OS_NAME, OS_VERSION));
        debug("Architecture: " + OS_ARCH);
        debug("Java Version: " + JAVA_VERSION);
        debug("JVM Version: " + JVM_VERSION);
        debug("Java Home: " + JAVA_HOME);
        debug("VM Memory: %dMB".formatted(Runtime.getRuntime().maxMemory() >>> 20));
        info("Data directory: " + DATA_DIR);
    }

    private static LogRecord format(final LogRecord record) {
        val thread = RuntimeUtils.getThreadById(record.getLongThreadID());

        record.setMessage(messageFormat.format(
                new Object[]{
                        new Date(record.getMillis()),
                        thread == null ? "Unrecognized Thread" : thread.getName(),
                        record.getLevel().getName(),
                        record.getMessage()
                },
                new StringBuffer(1024),
                null
        ).toString());

        return record;
    }

    /**
     * Ends logging.
     */
    public static void end() {
        if (state != State.ENDED) {
            for (val h : logger.getHandlers()) {
                logger.removeHandler(h);
                h.close();
            }
            state = State.ENDED;
        }
    }

    private enum State {
        UNINITIALIZED,
        STARTED,
        ENDED
    }

    /**
     * Logging levels.
     */
    private static class LoggingLevels extends Level {

        /**
         * Indicates debug messages.
         */
        public static final Level DEBUG = new LoggingLevels("DEBUG", 200);

        /**
         * Same as {@link Level#SEVERE} but have different names.
         *
         * @see Level#SEVERE
         */
        public static final Level ERROR = new LoggingLevels("ERROR", 1000);

        private LoggingLevels(final String name, final int value) {
            super(name, value, null);
        }

    }

}
