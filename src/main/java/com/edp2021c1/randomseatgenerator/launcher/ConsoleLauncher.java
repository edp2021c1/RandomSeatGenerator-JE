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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.edp2021c1.randomseatgenerator.util.Logging.LOG;

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
    public static void launch(final String[] args) {
        final List<String> arguments = Arrays.asList(args);

        // 命令行参数相关
        // 种子，默认为随机数
        String seed = StringUtils.randomString(30);
        // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        Path configPath = ConfigUtils.getConfigPath();
        // 导出路径，默认为用户根目录当前路径
        Path outputPath = Paths.get(MetaData.USER_HOME, "%tF.xlsx".formatted(new Date()));

        int i;

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            configPath = Paths.get(arguments.get(i + 1)).toAbsolutePath();
        }

        // 获取种子
        if ((i = arguments.lastIndexOf("--seed")) != -1 && i < arguments.size() - 1) {
            seed = arguments.get(i + 1);
        }

        // 获取导出路径
        if ((i = arguments.lastIndexOf("--output-path")) != -1 && i < arguments.size() - 1) {
            final Path tmp = Paths.get(arguments.get(i + 1));
            if (Files.isDirectory(tmp)) {
                outputPath = tmp.resolve(outputPath).toAbsolutePath();
            } else {
                outputPath = tmp.toAbsolutePath();
                if (!outputPath.endsWith(".xlsx")) {
                    LOG.warning("Invalid output file name: %s, will add \".xlsx\" to the end of it".formatted(outputPath.getFileName()));
                    outputPath = Paths.get(outputPath + ".xlsx");
                }
            }
        }

        // 处理座位表生成配置
        AppConfig config;
        try {
            config = ConfigUtils.fromJson(configPath);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load config from specific file", e);
        }

        config.checkFormat();
        LOG.info("Config path: " + configPath);

        // 生成座位表
        final SeatTable seatTable = SeatTableFactory.generate(config, seed);

        LOG.info("\n" + seatTable);

        // 导出
        LOG.info("Output path: " + outputPath);
        try {
            SeatTableUtils.exportToExcelDocument(seatTable, outputPath.toFile(), config.export_writable);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to export seat table to " + outputPath, e);
        }
        LOG.info("Seat table successfully exported to " + outputPath);
    }
}
