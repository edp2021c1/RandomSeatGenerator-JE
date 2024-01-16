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

import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.core.SeatTableFactory;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * Launches the application in console mode.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class ConsoleLauncher {

    /**
     * Launches the application.
     *
     * @param args arguments used to launch the application.
     */
    public static void launch(final String... args) {
        Logging.start(Logging.LoggingMode.CONSOLE);

        if (IOUtils.lackOfPermission(Paths.get(Metadata.DATA_DIR))) {
            throw new RuntimeException(new IOException("Does not have read/write permission of the data directory"));
        }

        final List<String> arguments = CollectionUtils.mutableListOf(args);

        // 命令行参数相关
        // 种子，默认为随机字符串
        String seed = Strings.randomString(30);
        // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        Path configPath = ConfigHolder.globalHolder().getConfigPath();
        // 导出路径，默认为用户根目录当前路径
        Path outputPath = Paths.get(Metadata.USER_HOME, "SeatTables", "%tF.xlsx".formatted(new Date()));

        int i;

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            configPath = Paths.get(arguments.get(i + 1)).toAbsolutePath();
            Logging.user("Config path set to " + configPath);
        }

        // 获取种子
        if ((i = arguments.lastIndexOf("--seed")) != -1 && i < arguments.size() - 1) {
            seed = arguments.get(i + 1);
            Logging.user("Seed set to " + seed);
        }

        // 获取导出路径
        if ((i = arguments.lastIndexOf("--output-path")) != -1 && i < arguments.size() - 1) {
            outputPath = Paths.get(arguments.get(i + 1)).toAbsolutePath();
            if (!outputPath.endsWith(".xlsx") || (Files.exists(outputPath))) {
                Logging.error("Invalid output path: " + outputPath);
            }
            Logging.user("Output path set to " + outputPath);
        }

        // 处理座位表生成配置
        RawAppConfig config;
        try {
            config = new ConfigHolder(configPath).get();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config from specific file", e);
        }

        config.checkFormat();
        Logging.debug("Config path: " + configPath);

        // 生成座位表
        final SeatTable seatTable = SeatTableFactory.generate(config.getContent(), seed);

        Logging.user("\n" + seatTable);

        // 导出
        try {
            SeatTables.exportToExcelDocument(seatTable, outputPath.toFile(), config.export_writable);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to export seat table to " + outputPath, e);
        }
        Logging.user("Seat table successfully exported to " + outputPath);

        System.exit(0);
    }
}
