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

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Reports runtime exceptions.
 *
 * @author Calboot
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler {
    /**
     * Default crash reporter.
     */
    public static final CrashReporter DEFAULT_CRASH_REPORTER;
    /**
     * Smaller crash reporter that does not show stack traces info.
     */
    public static final CrashReporter SMALLER_CRASH_REPORTER;

    static {
        DEFAULT_CRASH_REPORTER = new CrashReporter(true);
        SMALLER_CRASH_REPORTER = new CrashReporter();
    }

    private final boolean showStackTrace;

    /**
     * Creates an instance
     */
    public CrashReporter() {
        this(false);
    }

    /**
     * Creates an instance
     *
     * @param showStackTrace if stack traces of the exception shall be shown.
     */
    public CrashReporter(boolean showStackTrace) {
        this.showStackTrace = showStackTrace;
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
    public void uncaughtException(Thread t, Throwable e) {
        CrashReporterApp.stringBuilder = getStringBuilder(t, e);

        if (OperatingSystemUtils.isOnMac()) {
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(RandomSeatGenerator.class.getResource(MetaData.ERROR_ICON_URL)));
        }
        try {
            Application.launch(CrashReporterApp.class);
        } catch (IllegalStateException ignored) {
        }

    }

    private StringBuilder getStringBuilder(Thread t, Throwable e) {
        String message = e.getMessage();
        StringBuilder str = new StringBuilder();
        str.append(String.format("Exception in thread \"%s\":", t.getName()));

        if (message != null) {
            str.append(" ");
            str.append(message);
        }

        if (showStackTrace) {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement s : stackTraceElements) {
                str.append(String.format("\n        at: %s", s.toString()));
            }
        }
        return str;
    }

    private static class CrashReporterWindow extends Stage {
        public CrashReporterWindow(Object message) {
            this(message.toString());
        }

        private CrashReporterWindow(String message) {
            Label label = new Label(message);
            label.setWrapText(true);

            Button button = new Button("确定");
            button.setOnAction(event -> close());
            button.setDefaultButton(true);

            ButtonBar buttonBar = new ButtonBar();
            buttonBar.getButtons().add(button);

            ScrollPane scrollPane = new ScrollPane(label);

            VBox vBox = new VBox(scrollPane, buttonBar);
            vBox.setPadding(new Insets(10, 10, 10, 10));
            vBox.setSpacing(10);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            Scene scene = new Scene(vBox);
            scene.getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);

            setScene(scene);
            setResizable(false);
            setTitle("出错啦");
            setWidth(960);
            setMaxHeight(1080);
            getIcons().add(new Image(MetaData.ERROR_ICON_URL));
        }
    }

    public static class CrashReporterApp extends Application {
        public static StringBuilder stringBuilder;

        @Override
        public void start(Stage primaryStage) {
            new CrashReporterWindow(stringBuilder).show();
        }
    }
}
