/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.ui.stage.PrimaryWindowManager;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.exception.ExceptionHandler;
import com.edp2021c1.randomseatgenerator.util.i18n.I18N;
import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

public final class AppLaunch extends Application {

    private List<String> unnamedPara;

    private Map<String, String> namedPara;

    private boolean withGUI;

    public AppLaunch() {
        super();
    }

    @Override
    public void init() {
        Thread.currentThread().setUncaughtExceptionHandler(ExceptionHandler.INSTANCE);

        LOGGER.info("***   RandomSeatGenerator {}  ***", Metadata.VERSION);
        LOGGER.debug("Build date: {}", Metadata.BUILD_TIME);
        LOGGER.debug("OS name: {}", Metadata.OS_NAME);
        LOGGER.debug("Launching dir: {}", Metadata.DATA_DIR);

        Parameters para = getParameters();
        unnamedPara = para.getUnnamed();
        namedPara = para.getNamed();

        withGUI = !unnamedPara.contains("--nogui");
        AppSettings.withGUI = withGUI;
        AppSettings.mac = Metadata.OS_NAME.toLowerCase().startsWith("mac");
        try {
            AppSettings.loadConfig();
        } catch (IOException e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
        I18N.init(AppSettings.config.language);
        AwtUtils.setAppIcon(Toolkit.getDefaultToolkit().getImage(Resources.getResource(Metadata.ICON_URL)));
        AppSettings.initializingDone = true;
    }

    @Override
    public void start(final Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler(ExceptionHandler.INSTANCE);

        try {
            if (withGUI) {
                PrimaryWindowManager.init(primaryStage);
                primaryStage.show();
            } else {

                // 种子，默认为随机字符串
                String seed = namedPara.getOrDefault("seed", Strings.randomString(30));
                LOGGER.info("Seed: {}", seed);

                // 处理座位表生成配置
                SeatConfig config = AppSettings.config.seatConfig;

                // 生成座位表
                SeatTable seatTable = new SeatGenerator(config).generate(seed);

                LOGGER.info("{}{}", System.lineSeparator(), seatTable.toString());

                // 导出
                Path outputPath = Metadata.DATA_DIR.resolve("%tF.xlsx".formatted(new Date()));
                if (Files.exists(outputPath)) {
                    LOGGER.warn("Something's already on the output path, will delete");
                    IOUtils.delete(outputPath);
                }

                LOGGER.debug("Exporting seat table to \"{}\"", outputPath);
                SeatUtils.exportToXlsx(seatTable, outputPath);
                LOGGER.info("Seat table exported to \"{}\"", outputPath);

                if (unnamedPara.contains("--open-result")) {
                    LOGGER.debug("Opening output file...");
                    if (!DesktopUtils.openFileIfSupported(outputPath.toFile())) {
                        LOGGER.debug("Operation skipped because unsupported");
                    } else {
                        LOGGER.debug("Opened output file");
                    }
                }
                System.exit(0);
            }
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

}
