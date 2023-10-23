package com.edp2021c1;

import com.edp2021c1.core.Seat;
import com.edp2021c1.core.SeatConfig;
import com.edp2021c1.core.SeatGenerator;
import com.edp2021c1.ui.App;
import javafx.application.Application;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * Application intro, loads seat config.
 */
public class Main {
    /**
     * @param args used to start the application.
     */
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        // 如果有“--help”参数则打印帮助信息
        if (arguments.contains("--help")) {
            out.println(
                    """
                            OPTIONS:
                                --help                  Print this message and then quit.
                                --license               Print the license of this application and then quit.
                                --nogui                 Start the application in console mode.
                                --config-path <path>    Path of a specific Json config file (default to seat_config.json under the current directory).
                                --seed <value>          Seed used to generate seat table (default to a random number), must be in the format of a number.
                                --output-path <path>    Path to export seat table to (default to yyyy-mm-dd.xlsx under the current directory).
                            """
            );
            exit(0);
        }

        // 如果有“--license”参数则打印许可证
        if (arguments.contains("--license")) {
            out.println(
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
            exit(0);
        }

        // 如果不是命令行模式则启动JavaFX程序
        if (!arguments.contains("--nogui")) {
            reloadConfig();
            Application.launch(App.class, args);
            exit(0);
        }

        // 命令行参数相关
        int i;
        long seed = new Random().nextLong();  // 种子，默认为随机数
        String outputPath = String.format("%tF.xlsx", new Date()); // 导出路径，默认为当前路径
        SeatConfig conf = reloadConfig(); // 座位表生成配置，默认为当前目录下的seat_config.json中的配置

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            try {
                conf = SeatConfig.fromJsonFile(new File(arguments.get(i + 1)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // 获取种子
        if ((i = arguments.lastIndexOf("--seed")) != -1 && i < arguments.size() - 1) {
            seed = Long.parseLong(arguments.get(i + 1));
        }

        //获取导出路径
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

        File f = new File(outputPath);
        Seat seat;
        seat = new SeatGenerator().generate(conf, seed);
        seat.exportToExcelDocument(f);
        out.println("Seat table successfully exported to " + f.getAbsolutePath() + ".");
        exit(0);
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
            if (f.createNewFile()) {
                // 获取jar内文件信息的特殊方法
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/conf/seat_config.json"))));
                StringBuilder buffer = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                str = buffer.toString();
                FileWriter writer = new FileWriter(f);
                writer.write(str);
                writer.close();
                return SeatConfig.fromJsonFile(f);
            }

            return SeatConfig.fromJsonFile(f);
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
        try {
            FileWriter writer = new FileWriter("seat_config.json");
            writer.write(config.toJson());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
