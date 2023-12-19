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

import com.edp2021c1.randomseatgenerator.ui.stage.MainWindow;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * JavaFX application intro.
 *
 * @author Calboot
 * @since 1.2.0
 */
public class GUILauncher extends Application {

    /**
     * Launches the JavaFX application.
     */
    public static void launch() {
        Logging.start(Logging.LoggingMode.GUI);
        Application.launch(GUILauncher.class);
    }

    @Override
    public void start(final Stage primaryStage) {
        try {
            Logging.info("*** " + MetaData.TITLE + " ***");
            Logging.debug("OS: " + MetaData.SYSTEM_NAME + " " + MetaData.SYSTEM_VERSION);
            Logging.debug("Architecture: " + MetaData.SYSTEM_ARCH);
            Logging.debug("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
            Logging.debug("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
            Logging.debug("Java Home: " + System.getProperty("java.home"));
            Logging.debug("Memory: " + (Runtime.getRuntime().maxMemory() >>> 20) + "MB");
            Logging.debug("Working dir: " + MetaData.WORKING_DIR);
            Logging.debug("Config path: " + ConfigUtils.getConfigPath());

            if (IOUtils.lackOfPermission(Paths.get(MetaData.DATA_DIR))) {
                throw new RuntimeException("Does not have read/write permission of the data directory");
            }

            new MainWindow().show();
        } catch (final Throwable e) {
            CrashReporter.CRASH_REPORTER_FULL.uncaughtException(Thread.currentThread(), e);
        }
    }
}
