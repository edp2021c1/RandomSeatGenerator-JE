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
import com.edp2021c1.randomseatgenerator.ui.stage.MainWindow;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.AppPropertiesHolder;
import com.edp2021c1.randomseatgenerator.util.config.CachedMapSeatConfig;
import com.edp2021c1.randomseatgenerator.util.config.SeatConfigHolder;
import com.edp2021c1.randomseatgenerator.util.useroutput.CrashReporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.val;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.edp2021c1.randomseatgenerator.util.Log.LOG;
import static com.edp2021c1.randomseatgenerator.util.Metadata.KEY_EXPORT_WRITABLE;

/**
 * Application intro.
 *
 * @author Calboot
 * @since 1.0.0
 */
public final class RandomSeatGenerator extends Application {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create();

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

    private List<String> unnamedPara;

    private Map<String, String> namedPara;

    private boolean withGUI;

    /**
     * Default constructor.
     */
    public RandomSeatGenerator() {
        super();
    }

    @Override
    public void init() {
        Parameters para = getParameters();
        unnamedPara = para.getUnnamed();
        namedPara = para.getNamed();

        withGUI = !unnamedPara.contains("--nogui");
        RuntimeUtils.setProperty("launching.gui", withGUI);

        RuntimeUtils.setProperty("launching.debug", unnamedPara.contains("--debug"));

        LOG.start();
        if (Metadata.DATA_DIR.notFullyPermitted()) {
            LOG.warning("Does not have read/write permission of the data directory");
        }

        try {
            SeatTable.DEFAULT_EXPORTING_DIR.replaceWithDirectory();
        } catch (final IOException ignored) {
        }
    }

    @Override
    public void start(final Stage primaryStage) {

        for (String s : unnamedPara) {
            switch (s) {
                case "--help" -> {
                    System.out.println(Metadata.HELP_INFO);
                    System.exit(0);
                    return;
                }
                case "--license" -> {
                    System.out.println();
                    System.out.println(Metadata.LICENSE_INFO);
                    System.out.println();
                    System.exit(0);
                    return;
                }
                case "--version" -> {
                    System.out.println(Metadata.TITLE);
                    System.exit(0);
                    return;
                }
            }
        }

        try {
            if (!withGUI) {

                // 种子，默认为随机字符串
                String seed = namedPara.getOrDefault("seed", Strings.randomString(30));

                // 导出路径
                PathWrapper outputPath = PathWrapper.wrap(SeatTable.DEFAULT_EXPORTING_DIR.resolve("%tF.xlsx".formatted(new Date())));
                // 获取导出路径
                if (namedPara.containsKey("output-path")) {
                    outputPath = PathWrapper.wrap(namedPara.get("output-path"));
                    LOG.info("Output path set to " + outputPath);
                    if (outputPath.exists()) {
                        LOG.warning("Something's already on the output path, will move to trash");
                        outputPath.moveToTrash();
                    }
                }

                // 处理座位表生成配置
                CachedMapSeatConfig config = SeatConfigHolder.global().getClone().checkAndReturn();
                // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
                PathWrapper configPath = SeatConfigHolder.global().getConfigPath();
                // 获取配置文件路径
                if (namedPara.containsKey("config-path")) {
                    configPath = PathWrapper.wrap(namedPara.get("config-path"));
                    LOG.info("Config path set to " + configPath);
                    try {
                        SeatConfigHolder holder = SeatConfigHolder.createHolder(configPath, false);
                        config = holder.getClone().checkAndReturn();
                        holder.close();
                    } catch (final IOException e) {
                        throw new RuntimeException("Failed to load config from specific file", e);
                    }
                }
                LOG.debug("Config path: " + configPath);

                // 生成座位表
                SeatTable seatTable = SeatTable.generate(config, seed);

                LOG.info(System.lineSeparator() + seatTable);

                // 导出
                LOG.debug("Exporting seat table to \"%s\"".formatted(outputPath));
                seatTable.exportToChart(outputPath, AppPropertiesHolder.global().getBoolean(KEY_EXPORT_WRITABLE));
                LOG.info("Seat table successfully exported to \"%s\"".formatted(outputPath));

                if (unnamedPara.contains("--open-result")) {
                    LOG.debug("Opening output file...");
                    if (!DesktopUtils.openFileIfSupported(outputPath.toFile())) {
                        LOG.debug("Operation skipped because unsupported");
                    } else {
                        LOG.debug("Successfully opened output file");
                    }
                }

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
        System.exit(0);
    }

}
