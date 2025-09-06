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

import com.edp2021c1.randomseatgenerator.ui.stage.PrimaryWindowManager;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatConfig;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatGenerator;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatTable;
import com.edp2021c1.randomseatgenerator.v2.util.I18N;
import com.edp2021c1.randomseatgenerator.v2.util.Metadata;
import com.edp2021c1.randomseatgenerator.v2.util.SeatUtils;
import com.edp2021c1.randomseatgenerator.v2.util.exception.ExceptionHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Application intro.
 *
 * @author Calboot
 * @since 1.0.0
 */
public final class RandomSeatGenerator extends Application {

    public static final Logger LOGGER = LoggerFactory.getLogger("RandomSeatGenerator");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create();

    /**
     * App entrance.
     *
     * @param args used to start the application.
     */
    public static void main(final String... args) {
        Thread.currentThread().setName("main");

        try {
            Application.launch(RandomSeatGenerator.class, args);
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
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
        AppSettings.withGUI = withGUI;
        try {
            AppSettings.loadConfig();
        } catch (IOException e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
        I18N.init(AppSettings.config.language);
        AppSettings.initializingDone = true;
    }

    @Override
    public void start(final Stage primaryStage) {

        try {
            if (!withGUI) {

                // 种子，默认为随机字符串
                String seed = namedPara.getOrDefault("seed", Strings.randomString(30));

                // 导出路径
                Path outputPath = Metadata.DATA_DIR.resolve("%tF.xlsx".formatted(new Date()));
                // 获取导出路径
                if (Files.exists(outputPath)) {
                    LOGGER.warn("Something's already on the output path, will move to trash");
                    DesktopUtils.moveToTrashIfSupported(outputPath.toFile());
                }

                // 处理座位表生成配置
                SeatConfig config = AppSettings.config.seatConfig;

                // 生成座位表
                SeatTable seatTable = new SeatGenerator(config).generate(seed);

                LOGGER.info("{}{}", System.lineSeparator(), seatTable.toString());

                // 导出
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

                // 防止某表格抽风
                System.exit(0);
                return;
            }
            PrimaryWindowManager.init(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

}
