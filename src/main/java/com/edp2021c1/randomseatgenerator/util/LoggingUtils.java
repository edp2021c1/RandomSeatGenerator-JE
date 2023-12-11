package com.edp2021c1.randomseatgenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;

/**
 * Logging related util.
 */
public class LoggingUtils {
    /**
     * Logger.
     */
    public static final Logger LOG = Logger.getLogger("RandomSeat");
    private static final Path LOG_DIR = Paths.get(MetaData.DATA_DIR, "logs");
    private static final Path LATEST_LOG_PATH = LOG_DIR.resolve("latest.log");
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("[{0,date,HH:mm:ss}] [{1}.{2}/{3}] {4}\n");
    private static boolean started = false;

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

        try {
            if (Files.isRegularFile(LOG_DIR)) {
                Files.delete(LOG_DIR);
            }
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create randomseat.log", e);
        }

        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage();
            }
        };
        for (final Handler h : getHandlers(formatter)) {
            LOG.addHandler(h);
        }

        LOG.info("Logging started");
        LOG.info("Log file: " + LATEST_LOG_PATH);
    }

    private static String format(LogRecord record) {
        String message = record.getMessage();

        final StringBuffer buffer = new StringBuffer(1024);

        MESSAGE_FORMAT.format(new Object[]{
                new Date(record.getMillis()),
                record.getSourceClassName(), record.getSourceMethodName(), record.getLevel().getName(),
                message
        }, buffer, null);

        return buffer.toString();
    }

    private static ArrayList<Handler> getHandlers(Formatter formatter) {
        ArrayList<Handler> handlers = new ArrayList<>(3);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.FINER);
        handlers.add(consoleHandler);

        try {
            FileHandler latestLogHandler = new FileHandler(LATEST_LOG_PATH.toString());
            latestLogHandler.setLevel(Level.FINEST);
            latestLogHandler.setFormatter(formatter);
            latestLogHandler.setEncoding("UTF-8");
            handlers.add(latestLogHandler);

            String string = String.format("%tF", new Date());
            int t = 1;
            String tmp = "-" + t;
            while (Files.exists(LOG_DIR.resolve(string + tmp + ".log"))) {
                t++;
                tmp = "-" + t;
            }

            FileHandler currentLogHandler = new FileHandler(LOG_DIR.resolve(string + tmp + ".log").toString());
            currentLogHandler.setLevel(Level.FINEST);
            currentLogHandler.setFormatter(formatter);
            currentLogHandler.setEncoding("UTF-8");
            handlers.add(currentLogHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return handlers;
    }
}
