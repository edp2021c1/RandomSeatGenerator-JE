package com.edp2021c1.randomseatgenerator;

import com.edp2021c1.randomseatgenerator.core.Seat;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.fx.App;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;

/**
 * Application intro, loads seat config.
 */
public class Main {

    private static final SeatConfig DEFAULT_CONFIG = loadDefaultConfig();

    /**
     * @param args used to start the application.
     */
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        // 如果有“--help”参数则打印帮助信息
        if (arguments.contains("--help")) {
            System.out.println(
                    """
                            OPTIONS:
                                --help                  Print this message and then quit.
                                --license               Print the license of this application and then quit.
                                --nogui                 Start the application in console mode.
                                --config-path <path>    Path of a specific Json config file. (optional, default to seat_config.json under the current directory)
                                --seed <value>          Seed used to generate seat table, must be in the format of a number. (optional, default to a random number)
                                --output-path <path>    File or directory to export seat table to. If the path is a directory, seat table will be exported to yyyy-mm-dd.xlsx under it. (optional, default to yyyy-mm-dd.xlsx under the current directory)
                            """
            );
            System.exit(0);
        }

        // 如果有“--license”参数则打印许可证
        if (arguments.contains("--license")) {
            System.out.println(
                    """
                                                        
                            Copyright (c) 2023  EDP2021C1
                            This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
                            This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
                            See the GNU General Public License for more details.
                            You should have received a copy of the GNU General Public License along with this program.
                            If not, see <https://www.gnu.org/licenses/>.

                            Libraries that have been used in RandomSeatGenerator are listed below (Sorted by date added):
                            JavaFX v20.0.1 (GPLv2)
                            Gson v2.10.1 (Apache 2.0)
                            EasyExcel v3.3.2 (Apache 2.0)

                            Contributors:
                            Calboot <calboot39@outlook.com>
                                                        
                            """
            );
            System.exit(0);
        }

        // 如果不是命令行模式则启动JavaFX程序
        if (!arguments.contains("--nogui")) {
            try {
                Class.forName("javafx.application.Application");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "请安装 JavaFX 以启动 RandomSeatGenerator");
            }

            if (System.getProperty("os.name").contains("Mac")) {
                Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/assets/img/mac_icon.png")));
            }

            reloadConfig();
            javafx.application.Application.launch(App.class, args);
            System.exit(0);
        }

        // 命令行参数相关
        int i;
        long seed = new Random().nextLong();  // 种子，默认为随机数
        String configPath = "seat_config.json"; // 座位表生成配置文件路径，默认为当前目录下的seat_config.json
        String outputPath = String.format("%tF.xlsx", new Date()); // 导出路径，默认为当前路径

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            configPath = arguments.get(i + 1);
        }

        // 获取种子
        if ((i = arguments.lastIndexOf("--seed")) != -1 && i < arguments.size() - 1) {
            try {
                seed = Long.parseLong(arguments.get(i + 1));
            } catch (NumberFormatException e) {
                System.err.printf("WARNING: Invalid seed: \"%s\", will ignore.%n", arguments.get(i + 1));
            }
        }

        // 获取导出路径
        if ((i = arguments.lastIndexOf("--output-path")) != -1 && i < arguments.size() - 1) {
            File tmp = new File(arguments.get(i + 1));
            if (tmp.isDirectory()) {
                outputPath = new File(tmp, outputPath).getAbsolutePath();
            } else {
                outputPath = tmp.getAbsolutePath();
                if (!outputPath.endsWith(".xlsx")) {
                    outputPath += ".xlsx";
                }
            }
        }

        // 处理座位表生成配置
        File configFile = new File(configPath);
        SeatConfig config;
        try {
            config = SeatConfig.fromJsonFile(configFile);
        } catch (FileNotFoundException e) {
            System.err.println("WARNING: Failed to load config from specific file, will use default config.");
            configFile = new File("seat_config.json");
            config = reloadConfig();
        }
        try {
            config.checkFormat();
        } catch (RuntimeException e) {
            System.err.println("WARNING: Invalid seat config, will use default value.");
            config = reloadConfig();
        }
        System.out.printf("Config path: %s%n", configFile.getAbsolutePath());

        // 生成座位表
        Seat seat;
        seat = new SeatGenerator().generate(config, seed);

        // 导出
        File outputFile = new File(outputPath);
        System.out.printf("Output path: %s%n", outputFile.getAbsolutePath());
        try {
            seat.exportToExcelDocument(outputFile);
        } catch (IOException e) {
            System.err.printf("ERROR: Failed to export seat table to %s.%n", outputFile.getAbsolutePath());
        }
        System.out.printf("Seat table successfully exported to %s.%n", outputFile.getAbsolutePath());

        System.exit(0);
    }

    /**
     * Reload config from {@code seat_config.json} under the current directory.
     * If the file does nod exist, it will be created and containing the built-in config.
     *
     * @return default seat config loaded from file.
     */
    public static SeatConfig reloadConfig() {
        File f = new File("seat_config.json");
        try {
            SeatConfig config;
            if (f.createNewFile()) {
                System.err.println("WARNING: seat_config.json not found, will use default value.");
                saveConfig(DEFAULT_CONFIG);
            }
            config = SeatConfig.fromJsonFile(f);
            try {
                config.checkFormat();
            } catch (RuntimeException e) {
                System.err.println("WARNING: Invalid seat_config.json, will reset to default.");
                saveConfig(DEFAULT_CONFIG);
                config = SeatConfig.fromJsonFile(f);
            }
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes {@code SeatConfig} to {@code seat_config.json} under the current directory.
     *
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     */
    public static void saveConfig(SeatConfig config) {
        config.checkFormat();
        try {
            FileWriter writer = new FileWriter("seat_config.json");
            writer.write(config.toJson());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SeatConfig loadDefaultConfig() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/conf/seat_config.json"))));
        StringBuilder buffer = new StringBuilder();
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        str = buffer.toString();
        return new Gson().fromJson(str, SeatConfig.class);
    }
}
