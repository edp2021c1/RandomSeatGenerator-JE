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

package com.edp2021c1.randomseatgenerator.util.useroutput;

import com.edp2021c1.randomseatgenerator.util.PathWrapper;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import lombok.val;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import static com.edp2021c1.randomseatgenerator.util.Metadata.*;

/**
 * Logging related util.
 *
 * @author Calboot
 * @since 1.4.4
 */
public class LoggerWrapper {

    /**
     * Name of the global logger.
     */
    public static final String GLOBAL_LOGGER_NAME = "RandomSeatGenerator";

    /**
     * Logger.
     */
    private static final LoggerWrapper global;

    private static final PathWrapper globalLogDir = DATA_DIR.resolve("logs");

    private static final MessageFormat messageFormat = new MessageFormat("[{0,date,HH:mm:ss}] [{1}/{2}] {3}\n");

    private static final Formatter DEFAULT_FORMATTER = new Formatter() {
        @Override
        public String format(final LogRecord record) {
            return record.getMessage();
        }
    };

    private static boolean started;

    static {
        global = new LoggerWrapper();
    }

    private final Logger logger;

    private boolean closed;

    private LoggerWrapper() {
        this(GLOBAL_LOGGER_NAME, globalLogDir);
    }

    private LoggerWrapper(final String loggerName, PathWrapper logDir) {
        this.logger = Logger.getLogger(loggerName);

        logger.setLevel(LoggingLevels.ALL);
        logger.setUseParentHandlers(false);
        logger.setFilter(LoggerWrapper::checkAndFormat);

        CH.register(logger);

        try {
            logDir.replaceWithDirectory();
        } catch (final IOException e) {
            warning("Unable to create log dir, log may not be saved");
            warning(Strings.getStackTrace(e));
        }
        if (logDir.notFullyPermitted()) {
            warning("Does not have read/write permission of the log directory");
        }
        try {
            LH.register(logger, logDir);
        } catch (final IOException e) {
            warning("Unable to create log file, log may not be saved");
        }
        try {
            DH.register(logger, logDir);
        } catch (final IOException e) {
            warning("Unable to create log file, log may not be saved");
        }

    }

    private static boolean checkAndFormat(final LogRecord record) {
        val msg = record.getMessage();
        if (msg == null || msg.isEmpty()) {
            return false;
        }
        val thread = RuntimeUtils.getThreadById(record.getLongThreadID());
        record.setMessage(messageFormat.format(
                new Object[]{
                        new Date(record.getMillis()),
                        thread == null ? "Unrecognized Thread" : thread.getName(),
                        record.getLevel().getName(),
                        (msg.lines().count() > 1) ? (System.lineSeparator() + msg) : msg
                },
                new StringBuffer(1024),
                null
        ).toString());

        return true;
    }

    /**
     * Logs a WARNING message.
     *
     * @param msg logged message
     */
    public void warning(final String msg) {
        checkState();
        logger.log(LoggingLevels.WARNING, msg);
    }

    private void checkState() {
        if (closed) {
            throw new IllegalStateException("Logger already closed");
        }
    }

    /**
     * Returns the global logger.
     *
     * @return the global logger
     */
    public static LoggerWrapper global() {
        return global;
    }

    /**
     * Starts logging.
     */
    public static void start() {
        if (started) {
            return;
        }

        started = true;

        global.debug("Logging started");
        global.info("*** %s ***".formatted(TITLE));
        global.debug("Launching mode: " + ((boolean) RuntimeUtils.getPropertyOrDefault("launching.gui", false) ? "GUI" : "Console"));
        global.debug("OS: %s %s".formatted(OS_NAME, OS_VERSION));
        global.debug("Architecture: " + OS_ARCH);
        global.debug("Java Version: " + JAVA_VERSION);
        global.debug("JVM Version: " + JVM_VERSION);
        global.debug("Java Home: " + JAVA_HOME);
        global.debug("VM Memory: %dMB".formatted(Runtime.getRuntime().maxMemory() >>> 20));
        global.info("Data directory: " + DATA_DIR);
    }

    /**
     * Logs a DEBUG message.
     *
     * @param msg logged message
     */
    public void debug(final String msg) {
        checkState();
        logger.log(LoggingLevels.DEBUG, msg);
    }

    /**
     * Logs an INFO message.
     *
     * @param msg logged message
     */
    public void info(final String msg) {
        checkState();
        logger.log(LoggingLevels.INFO, msg);
    }

    /**
     * Logs an ERROR message.
     *
     * @param msg logged message
     */
    public void error(final String msg) {
        checkState();
        logger.log(LoggingLevels.ERROR, msg);
    }

    /**
     * Closes the current logger, returns immediately if the logger is already closed.
     */
    public void close() {
        if (!closed) {
            for (val h : logger.getHandlers()) {
                logger.removeHandler(h);
                h.close();
            }
            closed = true;
        }
    }

    /**
     * Returns whether the log manager is started.
     *
     * @return whether the log manager is started
     */
    public boolean isOpen() {
        return !closed;
    }

    private static final class CH extends ConsoleHandler {

        private final Logger logger;

        private CH(final Logger logger) {
            super();

            this.logger = logger;
            setFormatter(DEFAULT_FORMATTER);
            setLevel(LoggingLevels.DEBUG);
        }

        public static void register(final Logger logger) {
            logger.addHandler(new CH(logger));
        }

        @Override
        public void close() throws SecurityException {
            val record = new LogRecord(LoggingLevels.DEBUG, "Closing console log handler");
            checkAndFormat(record);
            for (val h : logger.getHandlers()) {
                h.publish(record);
            }
            publish(record);
            super.close();
        }

    }

    private static sealed class FH extends FileHandler permits LH, DH {

        private final Logger logger;

        private final PathWrapper path;

        protected FH(final Logger logger, final PathWrapper path) throws IOException {
            super(path.toString());

            this.logger = logger;
            this.path = path;
            setLevel(LoggingLevels.DEBUG);
            setFormatter(DEFAULT_FORMATTER);
            setEncoding("UTF-8");
        }

        @Override
        public void close() throws SecurityException {
            val record = new LogRecord(LoggingLevels.DEBUG, "Closing log file \"%s\"".formatted(path));
            checkAndFormat(record);
            publish(record);
            for (val h : logger.getHandlers()) {
                h.publish(record);
            }
            super.close();
        }

        protected PathWrapper getPath() {
            return path;
        }

    }

    private static final class LH extends FH {

        private LH(final Logger logger, final PathWrapper logDir) throws IOException, SecurityException {
            super(logger, logDir.resolve("latest.log"));
        }

        public static void register(final Logger logger, final PathWrapper logDir) throws IOException {
            logger.addHandler(new LH(logger, logDir));
        }

    }

    private static final class DH extends FH {

        private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

        private DH(final Logger logger, final PathWrapper logDir) throws IOException, SecurityException {
            super(logger, logDir.resolve(Strings.nowStr(dateFormat) + ".log"));
        }

        public static void register(final Logger logger, final PathWrapper logDir) throws IOException {
            logger.addHandler(new DH(logger, logDir));
        }

        @Override
        public void close() throws SecurityException {
            super.close();
            try {
                getPath().compressToGZip();
                getPath().delete();
            } catch (final IOException ignored) {
            }
        }

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
