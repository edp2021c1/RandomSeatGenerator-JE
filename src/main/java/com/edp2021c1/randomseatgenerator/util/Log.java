package com.edp2021c1.randomseatgenerator.util;

import lombok.val;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

import static com.edp2021c1.randomseatgenerator.util.Metadata.*;

/**
 * Logger.
 *
 * @author Calboot
 * @since 1.4.4
 */
public final class Log {

    private static final Formatter DEFAULT_FORMATTER = new Formatter() {
        @Override
        public String format(final LogRecord record) {
            return record.getMessage();
        }
    };

    private static final MessageFormat messageFormat = new MessageFormat("[{0,date,HH:mm:ss}] [{1}/{2}] {3}\n");

    /**
     * The global instance.
     */
    public static final Log LOG = new Log(DATA_DIR.resolve("logs"));

    private static LogRecord generateLogRecord(final Level level, final String message) {
        LogRecord res = new LogRecord(level, message);
        res.setInstant(Instant.now());
        res.setLongThreadID(Thread.currentThread().threadId());
        return res;
    }

    private static boolean checkAndFormat(final LogRecord record) {
        String msg = record.getMessage();
        if (msg == null || msg.isEmpty()) {
            return false;
        }
        Thread thread = RuntimeUtils.getThreadById(record.getLongThreadID());
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

    private static List<LogRecord> generateLogRecord(final Throwable throwable) {
        return List.of(
                generateLogRecord(LoggingLevels.ERROR, throwable.getClass().getName() + ": " + throwable.getLocalizedMessage()),
                generateLogRecord(LoggingLevels.DEBUG, Strings.getStackTrace(throwable))
        );
    }

    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    private final Logger logger;

    private final Thread loggerThread;

    private boolean shutdown;

    /**
     * Constructs an instance.
     *
     * @param logDir dir to store the log file
     */
    public Log(final PathWrapper logDir) {
        logger = Logger.getLogger("RandomSeatGenerator");

        logger.setLevel(LoggingLevels.ALL);
        logger.setUseParentHandlers(false);
        logger.setFilter(Log::checkAndFormat);

        CH.register(logger, Boolean.TRUE.equals(RuntimeUtils.getProperty("launching.debug")));

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

        loggerThread = new Thread(() -> {
            LinkedList<LogRecord> logs = new LinkedList<LogRecord>();
            while (!shutdown) {
                if (queue.drainTo(logs) > 0) {
                    for (LogRecord log : logs) {
                        logger.log(log);
                    }
                    logs.clear();
                }
            }
            queue.drainTo(logs);
            for (LogRecord log : logs) {
                logger.log(log);
            }
            logs.clear();
        }, "RandomSeatGenerator Logger Thread");
    }

    /**
     * Starts the logger.
     */
    public void start() {
        loggerThread.start();

        debug("Logging started");
        debug("Debug: " + (Boolean.TRUE.equals(RuntimeUtils.getProperty("launching.debug")) ? "on" : "off"));
        info("*** %s ***".formatted(TITLE));
        debug("OS: %s %s".formatted(OS_NAME, OS_VERSION));
        debug("Architecture: " + OS_ARCH);
        debug("Java Version: " + JAVA_VERSION);
        debug("JVM Version: " + JVM_VERSION);
        debug("Java Home: " + JAVA_HOME);
        debug("Data Directory: " + DATA_DIR);
        debug("Home Directory: " + Path.of("").toAbsolutePath());
        debug("VM Memory: %dMB".formatted(Runtime.getRuntime().maxMemory() >>> 20));
        debug("Launching Mode: " + ((boolean) RuntimeUtils.getPropertyOrDefault("launching.gui", false) ? "GUI" : "Console"));
    }

    /**
     * Stops the logging thread, then closes and removes the handlers.
     * The instance will be useless after this method is called.
     */
    public void shutdown() {
        shutdown = true;
        try {
            loggerThread.join(1000);
        } catch (final InterruptedException ignored) {
        }
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
            handler.close();
        }
    }

    /**
     * Logs the message in {@link LoggingLevels#WARNING}.
     *
     * @param message to log
     */
    public void warning(final String message) {
        log(LoggingLevels.WARNING, message);
    }

    /**
     * Logs the message in the given level.
     *
     * @param level   to log in
     * @param message to log
     */
    public void log(final Level level, final String message) {
        log(generateLogRecord(level, message));
    }

    /**
     * Logs the record.
     *
     * @param record to log
     */
    public void log(final LogRecord record) {
        queue.add(record);
    }

    /**
     * Returns whether the logger is open to log messages.
     *
     * @return whether the logger is open to log messages
     */
    public boolean isOpen() {
        return loggerThread.isAlive();
    }

    /**
     * Logs a {@link Throwable}.
     * The {@code Throwable}'s message will be logged in {@link LoggingLevels#ERROR},
     * and its stacktrace will be logged in {@link LoggingLevels#DEBUG}.
     *
     * @param throwable to log
     */
    public void logThrowable(final Throwable throwable) {
        queue.addAll(generateLogRecord(throwable));
    }

    /**
     * Logs the message in {@link LoggingLevels#INFO}.
     *
     * @param message to log
     */
    public void info(final String message) {
        log(LoggingLevels.INFO, message);
    }

    /**
     * Logs the message in {@link LoggingLevels#DEBUG}.
     *
     * @param message to log
     */
    public void debug(final String message) {
        log(LoggingLevels.DEBUG, message);
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

    private static final class CH extends ConsoleHandler {

        public static void register(final Logger logger, final boolean debugOn) {
            logger.addHandler(new CH(logger, debugOn));
        }

        private final Logger logger;

        private CH(final Logger logger, final boolean debugOn) {
            super();

            this.logger = logger;
            setFormatter(DEFAULT_FORMATTER);
            setLevel(debugOn ? LoggingLevels.DEBUG : LoggingLevels.INFO);
        }

        @Override
        public void close() throws SecurityException {
            LogRecord record = new LogRecord(LoggingLevels.DEBUG, "Closing console log handler");
            checkAndFormat(record);
            for (Handler h : logger.getHandlers()) {
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

        protected PathWrapper getPath() {
            return path;
        }

        @Override
        public void close() throws SecurityException {
            LogRecord record = new LogRecord(LoggingLevels.DEBUG, "Closing log file \"%s\"".formatted(path));
            checkAndFormat(record);
            publish(record);
            for (Handler h : logger.getHandlers()) {
                h.publish(record);
            }
            super.close();
        }

    }

    private static final class LH extends FH {

        public static void register(final Logger logger, final PathWrapper logDir) throws IOException {
            logger.addHandler(new LH(logger, logDir));
        }

        private LH(final Logger logger, final PathWrapper logDir) throws IOException, SecurityException {
            super(logger, logDir.resolve("latest.log"));
        }

    }

    private static final class DH extends FH {

        private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

        public static void register(final Logger logger, final PathWrapper logDir) throws IOException {
            logger.addHandler(new DH(logger, logDir));
        }

        private DH(final Logger logger, final PathWrapper logDir) throws IOException, SecurityException {
            super(logger, logDir.resolve(Strings.nowStr(dateFormat) + ".log"));
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

}
