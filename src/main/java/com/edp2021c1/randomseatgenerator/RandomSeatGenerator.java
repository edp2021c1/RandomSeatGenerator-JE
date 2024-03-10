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

import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.Logging;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import lombok.val;

import java.util.Arrays;
import java.util.List;

/**
 * Application intro.
 */
public class RandomSeatGenerator {

    /**
     * Don't let anyone else instantiate this class.
     */
    private RandomSeatGenerator() {
    }

    /**
     * App entrance.
     *
     * @param args used to start the application.
     */
    public static void main(final String... args) {
        val arguments = Arrays.asList(args);

        // 如果有“--help”参数则打印帮助信息然后退出
        if (arguments.contains("--help")) {
            System.out.println(Metadata.HELP_INFO);
            System.exit(0);
        }

        // 如果有“--license”参数则打印许可证然后退出
        if (arguments.contains("--license")) {
            System.out.println();
            System.out.println(Metadata.LICENSE_INFO);
            System.out.println();
            System.exit(0);
        }

        Thread.currentThread().setUncaughtExceptionHandler(CrashReporter.instance);
        Thread.currentThread().setName("Main Thread");

        launch(!arguments.contains("--nogui"), arguments);
    }

    private static void launch(final boolean useGUI, final List<String> args) {
        RuntimeUtils.initStatic(useGUI);
        Logging.start(useGUI);
        if (useGUI) {
            GUILauncher.launch();
        } else {
            ConsoleLauncher.launch(args);
        }
    }
}
