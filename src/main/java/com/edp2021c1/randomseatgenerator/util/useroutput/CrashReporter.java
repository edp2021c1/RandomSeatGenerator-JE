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

import com.edp2021c1.randomseatgenerator.ui.stage.CrashReporterDialog;
import com.edp2021c1.randomseatgenerator.ui.stage.MessageDialog;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import lombok.Getter;

import static com.edp2021c1.randomseatgenerator.util.useroutput.Logger.LOG;

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
    private static final CrashReporter instance = (boolean) RuntimeUtils.getPropertyOrDefault("launching.gui", true)
            ? new GUICrashReporter()
            : new ConsoleCrashReporter();

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

    /**
     * Reports a notice.
     *
     * @param n notice to report
     * @param e {@code Throwable} to report
     */
    protected abstract void reportNotice(Notice n, Throwable e);

    @Override
    public final void uncaughtException(final Thread t, final Throwable e) {
        if (e == null) {
            return;
        }
        if (e instanceof ExceptionInInitializerError) {
            uncaughtException(t, e.getCause());
            return;
        }

        reportNotice(Notice.of(t, e), e);
        if (e instanceof ExecutableOnCaught) {
            ((ExecutableOnCaught) e).exec();
        }
    }

    private static class ConsoleCrashReporter extends CrashReporter {

        private ConsoleCrashReporter() {
        }

        @Override
        protected void reportNotice(Notice n, Throwable e) {
            try {
                LOG.logThrowable(e);
            } catch (final Throwable ex) {
                System.err.println(n.string());
                System.err.println(Strings.getStackTrace(ex));
            }
        }

    }

    private static class GUICrashReporter extends CrashReporter {

        private GUICrashReporter() {
        }

        @Override
        protected void reportNotice(final Notice n, final Throwable e) {
            try {
                LOG.logThrowable(e);
                if (e instanceof Notice) {
                    MessageDialog.showMessage(n);
                } else {
                    CrashReporterDialog.showCrashReporter(n);
                }
            } catch (final Throwable ex) {
                System.err.println(Strings.getStackTrace(ex));
            }
        }

    }

}
