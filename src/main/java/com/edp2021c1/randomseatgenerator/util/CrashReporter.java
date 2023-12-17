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

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.ui.stage.CrashReporterWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Reports runtime exceptions.
 *
 * @author Calboot
 * @since 1.2.8
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler {
    /**
     * Default crash reporter.
     */
    public static final CrashReporter CRASH_REPORTER_FULL = new CrashReporter(true, true);

    /**
     * Shows error message in a {@code Swing} window rather than an {@code JavaFX} window.
     */
    public static final CrashReporter CRASH_REPORTER_LOG_ONLY = new CrashReporter(true, false);

    private final boolean useLog;
    private final boolean useFX;

    /**
     * Creates an instance.
     *
     * @param useLog if the error message will be logged.
     * @param useFX  if the error message will be shown in a JavaFX stage.
     */
    public CrashReporter(final boolean useLog, final boolean useFX) {
        this.useLog = useLog;
        this.useFX = useFX;
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        final String str = "Exception in thread \"%s\":\n".formatted(t.getName()) +
                (e instanceof IllegalConfigException ? e.getMessage() : StringUtils.getStackTrace(e));

        if (useLog) {
            Logging.error(str);
        }

        if (useFX) {
            try {
                Application.launch(CrashReporterApp.class, str);
            } catch (final IllegalStateException exception) {
                new CrashReporterWindow(str).showAndWait();
            }
        }
    }

    /**
     * JavaFX application used to launch {@code CrashReporterWindow}.
     */
    public static class CrashReporterApp extends Application {

        @Override
        public void start(final Stage primaryStage) {
            new CrashReporterWindow(getParameters().getRaw().get(0)).show();
        }
    }
}
