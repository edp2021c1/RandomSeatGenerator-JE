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
import com.edp2021c1.randomseatgenerator.util.Logging;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * Don't let anyone else instantiate this class.
     */
    private ConsoleLauncher() {
    }

    /**
     * Launches the application.
     *
     * @param args arguments used to launch the application.
     */
    public static void launch(final List<String> args) {
        Logging.start(Logging.LoggingMode.CONSOLE);

        if (!Files.isReadable(Metadata.DATA_DIR)) {
            throw new RuntimeException(new IOException("Does not have read permission of the data directory"));
        }

        // 命令行参数相关
        // 种子，默认为随机字符串
        String seed = Strings.randomString(30);
        // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        Path configPath = ConfigHolder.globalHolder().getConfigPath();
        // 导出路径，默认为用户根目录当前路径
        Path outputPath = SeatTable.DEFAULT_EXPORTING_DIR.resolve("%tF.xlsx".formatted(new Date()));

        int i;

        // 获取配置文件路径
        if ((i = args.lastIndexOf("--config-path")) != -1 && i < args.size() - 1) {
            configPath = Path.of(args.get(i + 1)).toAbsolutePath();
            Logging.info("Config path set to " + configPath);
        }

        // 获取种子
        if ((i = args.lastIndexOf("--seed")) != -1 && i < args.size() - 1) {
            seed = args.get(i + 1);
            Logging.info("Seed set to " + seed);
        }

        // 获取导出路径
        if ((i = args.lastIndexOf("--output-path")) != -1 && i < args.size() - 1) {
            outputPath = Path.of(args.get(i + 1)).toAbsolutePath();
            if (!outputPath.endsWith(".xlsx") || (Files.exists(outputPath))) {
                Logging.error("Invalid output path: " + outputPath);
            }
            Logging.info("Output path set to " + outputPath);
        }

        // 处理座位表生成配置
        RawAppConfig config;
        try {
            config = ConfigHolder.createHolder(configPath).get();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config from specific file", e);
        }

        config.checkFormat();
        Logging.debug("Config path: " + configPath);

        // 生成座位表
        final SeatTable seatTable = SeatTableFactory.generate(config.getContent(), seed);

        Logging.info("\n" + seatTable);

        // 导出
        seatTable.exportToExcelDocument(outputPath, config.exportWritable);
        Logging.info("Seat table successfully exported to " + outputPath);

        System.exit(0);
    }
}
