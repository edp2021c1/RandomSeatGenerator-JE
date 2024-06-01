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

import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.ui.UIUtils;
import com.edp2021c1.randomseatgenerator.ui.stage.MainWindow;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.SeatConfigHolder;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.val;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Application intro.
 *
 * @author Calboot
 * @since 1.0.0
 */
public class RandomSeatGenerator extends Application {

    private static final LoggerWrapper LOGGER = LoggerWrapper.global();

    private List<String> unnamedPara;

    private Map<String, String> namedPara;

    private boolean withGUI;

    /**
     * Default constructor.
     */
    public RandomSeatGenerator() {
        super();
    }

    /**
     * App entrance.
     *
     * @param args used to start the application.
     */
    public static void main(final String... args) {
        Thread.currentThread().setName("Main Thread");

        try {
            Application.launch(RandomSeatGenerator.class, args);
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void init() {
        val para = getParameters();
        unnamedPara = para.getUnnamed();
        namedPara = para.getNamed();

        withGUI = !unnamedPara.contains("--nogui");
        RuntimeUtils.setProperty("launching.gui", withGUI);

        LoggerWrapper.start();
        if (Metadata.DATA_DIR.notFullyPermitted()) {
            LOGGER.warning("Does not have read/write permission of the data directory");
        }
    }

    @Override
    public void start(final Stage primaryStage) {

        // 如果有“--help”参数则打印帮助信息然后退出
        if (unnamedPara.contains("--help")) {
            System.out.println(Metadata.HELP_INFO);
            System.exit(0);
            return;
        }

        // 如果有“--license”参数则打印许可证然后退出
        if (unnamedPara.contains("--license")) {
            System.out.println();
            System.out.println(Metadata.LICENSE_INFO);
            System.out.println();
            System.exit(0);
            return;
        }

        try {
            if (!withGUI) {

                // 种子，默认为随机字符串
                var seed = Strings.randomString(30);
                // 获取种子
                if (namedPara.containsKey("--seed")) {
                    seed = namedPara.get("--seed");
                }

                // 导出路径
                var outputPath = PathWrapper.wrap(SeatTable.DEFAULT_EXPORTING_DIR.resolve("%tF.xlsx".formatted(new Date())));
                // 获取导出路径
                if (namedPara.containsKey("--output-path")) {
                    outputPath = PathWrapper.wrap(namedPara.get("--output-path"));
                    if (!outputPath.endsWith(".xlsx")) {
                        LOGGER.error("Invalid output path: " + outputPath);
                        System.exit(1);
                    }
                    LOGGER.info("Output path set to " + outputPath);
                    if (outputPath.exists()) {
                        LOGGER.warning("Something's already on the output path, will move to trash");
                        outputPath.moveToTrash();
                    }
                }

                // 处理座位表生成配置
                var config = SeatConfigHolder.global().getClone().checkAndReturn();
                // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
                var configPath = SeatConfigHolder.global().getConfigPath();
                // 获取配置文件路径
                if (namedPara.containsKey("--config-path")) {
                    configPath = PathWrapper.wrap(namedPara.get("--config-path"));
                    LOGGER.info("Config path set to " + configPath);
                    try {
                        val holder = SeatConfigHolder.createHolder(configPath, false);
                        config = holder.getClone().checkAndReturn();
                        holder.close();
                    } catch (final IOException e) {
                        throw new RuntimeException("Failed to load config from specific file", e);
                    }
                }
                LOGGER.debug("Config path: " + configPath);

                // 生成座位表
                val seatTable = SeatTable.generate(config, seed);

                LOGGER.info(System.lineSeparator() + seatTable);

                // 导出
                seatTable.exportToChart(outputPath, UIUtils.exportWritableProperty().get());

                // 防止某表格抽风
                System.exit(0);
                return;
            }
            MainWindow.getMainWindow().show();
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    @Override
    public void stop() {
        RuntimeUtils.runExitHooks();
        System.exit(0);
    }

}
