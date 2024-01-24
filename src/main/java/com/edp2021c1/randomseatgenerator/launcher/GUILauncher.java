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
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import com.edp2021c1.randomseatgenerator.util.ui.UIFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;

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

            if (!Files.isReadable(Metadata.DATA_DIR)) {
                throw new IOException("Does not have read/write permission of the data directory");
            }
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void start(final Stage primaryStage) {
        try {
            RawAppConfig config = ConfigHolder.globalHolder().get();
            UIFactory.setGlobalDarkMode(config.dark_mode);
            new MainWindow().show();
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
