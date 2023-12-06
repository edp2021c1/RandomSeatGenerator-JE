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

import javax.swing.*;
import java.awt.*;

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
    public static final CrashReporter DEFAULT_CRASH_REPORTER = new CrashReporter(false);

    /**
     * Shows error message in a {@code Swing} window rather than an {@code JavaFX} window.
     */
    public static final CrashReporter SWING_CRASH_REPORTER = new CrashReporter(true);

    private final boolean useSwing;

    /**
     * Creates an instance.
     *
     * @param useSwing          if the reporter will be shown in a {@code Swing} window.
     */
    public CrashReporter(final boolean useSwing) {
        this.useSwing = useSwing;
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
        final String str = getDetailMessage(t, e);

        if (OperatingSystem.CURRENT == OperatingSystem.MAC && Taskbar.getTaskbar().getIconImage() == null) {
            Taskbar.getTaskbar().setIconImage(
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource(MetaData.ERROR_ICON_URL))
            );
        }

        if (useSwing) {
            JOptionPane.showMessageDialog(null, str, "出错啦", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Application.launch(CrashReporterApp.class, str);
        } catch (final IllegalStateException exception) {
            new CrashReporterWindow(str).showAndWait();
        }

    }

    private String getDetailMessage(final Thread t, final Throwable e) {
        final String message = e.getMessage();
        final StringBuilder str = new StringBuilder();

        str.append(String.format("Exception in thread \"%s\":", t.getName()));

        if (message != null) {
            str.append(" ");
            str.append(message);
        }

        if (!(e instanceof IllegalConfigException)) {
            final StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (final StackTraceElement s : stackTraceElements) {
                str.append(String.format("\n        at: %s", s.toString()));
            }
        }
        return str.toString();
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
