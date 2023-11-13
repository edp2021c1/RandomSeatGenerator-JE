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

package com.edp2021c1.randomseatgenerator.launcher;

import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX application intro.
 */
public class GUILauncher extends Application {
    private static final CrashReporter CRASH_REPORTER = new CrashReporter(true);

    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler(CRASH_REPORTER);

        Stage stage;
        try {
            stage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/MainWindow.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}
