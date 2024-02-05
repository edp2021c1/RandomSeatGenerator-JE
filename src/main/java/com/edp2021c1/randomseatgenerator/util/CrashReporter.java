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
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Reports uncaught exceptions.
 *
 * @author Calboot
 * @since 1.2.8
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler {

    /**
     * Full crash reporter, with a window shown on call of {@link #uncaughtException(Thread, Throwable)}
     */
    public static final CrashReporter fullCrashReporter = new CrashReporter(true);
    /**
     * Crash reporter will log only.
     */
    public static final CrashReporter logOnlyCrashReporter = new CrashReporter(false);
    private final boolean withGUI;

    /**
     * Creates an instance.
     *
     * @param withGUI if the error message will be shown in a JavaFX stage.
     */
    private CrashReporter(final boolean withGUI) {
        this.withGUI = withGUI;
    }

    /**
     * Reports an exception with {@link #fullCrashReporter}
     *
     * @param e exception to report
     */
    public static void report(final Throwable e) {
        fullCrashReporter.uncaughtException(Thread.currentThread(), e);
    }

    /**
     * Handles crashes.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        try {
            final String str;
            if (e instanceof final IllegalConfigException ex) {
                if (ex.isSingle()) {
                    str = ex.toString();
                } else {
                    str = ex.getClass().getName() + "\n" + ex.getLocalizedMessage();
                }
            } else {
                str = Strings.getStackTrace(e);
            }

            Logging.error(str);

            if (withGUI) {
                try {
                    Application.launch(CrashReporterApp.class, str);
                } catch (final IllegalStateException exception) {
                    new CrashReporterWindow(str).showAndWait();
                }
            }
        } catch (final Throwable ex) {
            System.err.println(Strings.getStackTrace(ex));
        }
    }

    /**
     * JavaFX application used to launch {@code CrashReporterWindow}.
     */
    public static class CrashReporterApp extends Application {

        /**
         * Don't let anyone else instantiate this class.
         */
        private CrashReporterApp() {
        }

        @Override
        public void start(final Stage primaryStage) {
            new CrashReporterWindow(getParameters().getRaw().getFirst()).show();
        }
    }
}
