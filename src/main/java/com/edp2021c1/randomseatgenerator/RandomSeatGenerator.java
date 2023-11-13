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

import com.edp2021c1.randomseatgenerator.launcher.ConsoleLauncher;
import com.edp2021c1.randomseatgenerator.launcher.GUILauncher;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.DataUtils;
import com.edp2021c1.randomseatgenerator.util.OperatingSystemUtils;
import javafx.application.Application;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Application intro, loads seat config.
 */
public class RandomSeatGenerator {
    private static final CrashReporter CRASH_REPORTER = new CrashReporter(true);

    /**
     * @param args used to start the application.
     */
    public static void main(String[] args) {
        Thread.currentThread().setUncaughtExceptionHandler(CRASH_REPORTER);

        List<String> arguments = Arrays.asList(args);
        // 如果有“--help”参数则打印帮助信息
        if (arguments.contains("--help")) {
            System.out.println(DataUtils.HELP_INFO);
            System.exit(0);
        }

        // 如果有“--license”参数则打印许可证
        if (arguments.contains("--license")) {
            System.out.println(DataUtils.LICENSE);
            System.exit(0);
        }

        // 如果是命令行模式则启动命令行程序
        if (arguments.contains("--nogui")) {
            ConsoleLauncher.launch(args);
            System.exit(0);
        }

        // 如果不是命令行模式则启动JavaFX程序
        if (OperatingSystemUtils.isOnMac()) {
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(RandomSeatGenerator.class.getResource(DataUtils.MAC_ICON_URL)));
        }
        ConfigUtils.reloadConfig();
        Application.launch(GUILauncher.class, args);
        System.exit(0);

    }
}
