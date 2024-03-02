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

import com.edp2021c1.randomseatgenerator.ui.UIFactory;
import com.edp2021c1.randomseatgenerator.ui.stage.MainWindow;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX application intro.
 *
 * @author Calboot
 * @since 1.2.0
 */
public class GUILauncher extends Application {

    /**
     * Default constructor.
     */
    public GUILauncher() {
        super();
    }

    /**
     * Launches the JavaFX application.
     */
    public static void launch() {
        Application.launch(GUILauncher.class);
    }

    @Override
    public void init() {
        try {
            Logging.start(Logging.LoggingMode.GUI);
            if (IOUtils.notFullyPermitted(Metadata.DATA_DIR)) {
                CrashReporter.report(new IOException("Does not have read/write permission of the data directory"));
            }
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void start(final Stage primaryStage) {
        try {
            UIFactory.setGlobalDarkMode(Utils.elseIfNull(ConfigHolder.globalHolder().get().getBoolean("appearance.style.dark"), true));
            UIFactory.setMainWindow(MainWindow.getMainWindow(this));
            UIFactory.getMainWindow().show();
        } catch (final Throwable e) {
            if (e instanceof ExceptionInInitializerError) {
                CrashReporter.report(e.getCause());
            } else {
                CrashReporter.report(e);
            }
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
