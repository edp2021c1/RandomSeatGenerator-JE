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

import com.edp2021c1.randomseatgenerator.ui.stage.CrashReporterDialog;
import com.edp2021c1.randomseatgenerator.ui.stage.MessageDialog;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.Getter;
import lombok.val;

/**
 * Reports uncaught exceptions.
 *
 * @author Calboot
 * @since 1.2.8
 */
public abstract class CrashReporter implements Thread.UncaughtExceptionHandler {

    /**
     * The now-in-use instance.
     */
    @Getter
    private static final CrashReporter instance = (boolean) RuntimeUtils.getPropertyOrDefault("launching.gui", false)
            ? new ConsoleCrashReporter()
            : new GUICrashReporter();

    /**
     * Creates an instance.
     */
    private CrashReporter() {
    }

    /**
     * Reports an exception with {@link #instance}
     *
     * @param e exception to report
     */
    public static void report(final Throwable e) {
        getInstance().uncaughtException(Thread.currentThread(), e);
    }

    private static class ConsoleCrashReporter extends CrashReporter {

        private ConsoleCrashReporter() {
        }

        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            if (e == null) {
                return;
            }
            if (e instanceof ExceptionInInitializerError) {
                uncaughtException(t, e.getCause());
                return;
            }

            try {
                if (e instanceof final IllegalConfigException ex) {
                    Logging.error(
                            (ex.isSingle() ? "IllegalConfigException: " : "IllegalConfigException:") + ex.getLocalizedMessage()
                    );
                    return;
                }

                if (e instanceof ApplicationAlreadyRunningException) {
                    Logging.error("Another instance of the application is already running");
                    System.exit(1);
                    return;
                }

                Logging.error((t == null ? "" : "Throwable thrown from thread \"%s\":\n".formatted(t.getName())) + Strings.getStackTrace(e));
            } catch (final Throwable ex) {
                System.err.println(Strings.getStackTrace(ex));
            }
        }
    }

    private static class GUICrashReporter extends CrashReporter {

        private GUICrashReporter() {
        }

        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            if (e == null) {
                return;
            }
            if (e instanceof ExceptionInInitializerError) {
                uncaughtException(t, e.getCause());
                return;
            }

            try {
                if (e instanceof final IllegalConfigException ex) {
                    Logging.error(
                            (ex.isSingle() ? "IllegalConfigException: " : "IllegalConfigException:") + ex.getLocalizedMessage()
                    );
                    MessageDialog.showMessage(
                            (ex.isSingle() ? "配置格式错误\n" : "配置格式错误") + ex.getLocalizedMessage()
                    );
                    return;
                }

                if (e instanceof ApplicationAlreadyRunningException) {
                    Logging.error("Another instance of the application is already running");
                    MessageDialog.showMessage("已有另一个实例在运行");
                    System.exit(1);
                    return;
                }

                val str = (t == null ? "" : "Throwable thrown from thread \"%s\":\n".formatted(t.getName())) + Strings.getStackTrace(e);
                Logging.error(str);
                CrashReporterDialog.showCrashReporter(e.getClass().getName(), str);
            } catch (final Throwable ex) {
                System.err.println(Strings.getStackTrace(ex));
            }
        }
    }
}
