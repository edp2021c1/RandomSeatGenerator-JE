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

package com.edp2021c1.randomseatgenerator;

import com.edp2021c1.randomseatgenerator.ui.UIUtils;
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
        try {
            Application.launch(GUILauncher.class);
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void init() {
        RuntimeUtils.initStatic(true);
        Logging.start();
        if (IOUtils.notFullyPermitted(Metadata.DATA_DIR)) {
            CrashReporter.report(new IOException("Does not have read/write permission of the data directory"));
        }
        UIUtils.setGlobalDarkMode(!Boolean.FALSE.equals(ConfigHolder.global().get().getBoolean("appearance.style.dark")));
    }

    @Override
    public void start(final Stage primaryStage) {
        try {
            DesktopUtils.initStatic();
            MainWindow.getMainWindow().show();
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }
}
