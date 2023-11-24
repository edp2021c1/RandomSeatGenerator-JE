/*
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

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import com.edp2021c1.randomseatgenerator.util.SeatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Launches the application in console mode.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class ConsoleLauncher {
    private static final Logger LOGGER = Logger.getGlobal();

    /**
     * Launches the application.
     *
     * @param args arguments used to launch the application.
     */
    public static void launch(String[] args) {
        List<String> arguments = Arrays.asList(args);

        // 命令行参数相关
        int i;
        long seed = new Random().nextLong();  // 种子，默认为随机数
        Path configPath = ConfigUtils.getConfigPath(); // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        Path outputPath = Paths.get(System.getProperty("user.home"), String.format("%tF.xlsx", new Date())); // 导出路径，默认为用户根目录当前路径

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            configPath = Paths.get(arguments.get(i + 1)).toAbsolutePath();
        }

        // 获取种子
        if ((i = arguments.lastIndexOf("--seed")) != -1 && i < arguments.size() - 1) {
            try {
                seed = Long.parseLong(arguments.get(i + 1));
            } catch (NumberFormatException e) {
                LOGGER.warning(String.format("Invalid seed: \"%s\", will ignore.", arguments.get(i + 1)));
            }
        }

        // 获取导出路径
        if ((i = arguments.lastIndexOf("--output-path")) != -1 && i < arguments.size() - 1) {
            Path tmp = Paths.get(arguments.get(i + 1));
            if (Files.isDirectory(tmp)) {
                outputPath = Paths.get(tmp.toString(), outputPath.toString()).toAbsolutePath();
            } else {
                outputPath = tmp.toAbsolutePath();
                if (!outputPath.endsWith(".xlsx")) {
                    LOGGER.warning(String.format("Invalid output file name: %s, will add \".xlsx\" to the end of it.", outputPath.getFileName()));
                    outputPath = Paths.get(outputPath + ".xlsx");
                }
            }
        }

        // 处理座位表生成配置
        SeatConfig config;
        try {
            config = ConfigUtils.fromJson(configPath);
        } catch (IOException e) {
            LOGGER.warning("Failed to load config from specific file, will use default config.");
            configPath = ConfigUtils.getConfigPath();
            config = ConfigUtils.reloadConfig();
        }
        try {
            config.checkFormat();
        } catch (RuntimeException e) {
            LOGGER.warning("Invalid seat config, will use default value.");
            config = ConfigUtils.reloadConfig();
        }
        LOGGER.info(String.format("Config path: %s", configPath));

        // 生成座位表
        SeatTable seatTable;
        seatTable = new SeatGenerator().generate(config, seed);

        LOGGER.info("\n" + seatTable);

        // 导出
        File outputFile = outputPath.toFile();
        LOGGER.info(String.format("Output path: %s", outputFile.getAbsolutePath()));
        try {
            SeatUtils.exportToExcelDocument(seatTable, outputFile);
        } catch (IOException e) {
            LOGGER.severe(String.format("Failed to export seat table to %s.", outputFile.getAbsolutePath()));
        }
        LOGGER.info(String.format("Seat table successfully exported to %s.", outputFile.getAbsolutePath()));
    }
}
