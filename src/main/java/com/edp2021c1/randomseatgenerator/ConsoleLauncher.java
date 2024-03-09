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
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfig;

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
        RuntimeUtils.runtimeConfig.put("launching.gui", false);
        Logging.start();

        if (!Files.isReadable(Metadata.DATA_DIR)) {
            throw new RuntimeException(new IOException("Does not have read permission of the data directory"));
        }

        int i;

        // 种子，默认为随机字符串
        final String seed;
        // 获取种子
        if ((i = args.lastIndexOf("--seed")) != -1 && i < args.size() - 1) {
            seed = args.get(i + 1);
            Logging.info("Seed set to " + seed);
        } else {
            seed = Strings.randomString(30);
        }

        // 导出路径
        final Path outputPath;
        // 获取导出路径
        if ((i = args.lastIndexOf("--output-path")) != -1 && i < args.size() - 1) {
            outputPath = Path.of(args.get(i + 1)).toAbsolutePath();
            if (!outputPath.endsWith(".xlsx")) {
                Logging.error("Invalid output path: " + outputPath);
                System.exit(1);
            }
            Logging.info("Output path set to " + outputPath);
            if (Files.exists(outputPath)) {
                Logging.warning("Something's already on the output path, will try to overwrite");
                try {
                    IOUtils.deleteIfExists(outputPath);
                } catch (final IOException e) {
                    Logging.warning("Failed to clear the output path");
                }
            }
        } else {
            outputPath = SeatTable.DEFAULT_EXPORTING_DIR.resolve("%tF.xlsx".formatted(new Date()));
        }

        // 处理座位表生成配置
        final JSONAppConfig config;
        // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        final Path configPath;
        // 获取配置文件路径
        if ((i = args.lastIndexOf("--config-path")) != -1 && i < args.size() - 1) {
            configPath = Path.of(args.get(i + 1)).toAbsolutePath();
            Logging.info("Config path set to " + configPath);
            try {
                final ConfigHolder holder = ConfigHolder.createHolder(configPath);
                config = holder.get().checkAndReturn();
                holder.close();
            } catch (final IOException e) {
                throw new RuntimeException("Failed to load config from specific file", e);
            }
        } else {
            configPath = ConfigHolder.global().getConfigPath();
            config = ConfigHolder.global().get().checkAndReturn();
        }
        Logging.debug("Config path: " + configPath);

        // 生成座位表
        final SeatTable seatTable = SeatTable.generate(config, seed);

        Logging.info("\n" + seatTable);

        // 导出
        seatTable.exportToExcelDocument(outputPath, Boolean.TRUE.equals(config.getBoolean("export.writable")));
        Logging.info("Seat table successfully exported to " + outputPath);

        // 防止某表格抽风
        System.exit(0);
    }
}
