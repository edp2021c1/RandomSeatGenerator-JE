package com.edp2021c1;

import com.edp2021c1.core.Seat;
import com.edp2021c1.core.SeatConfig;
import com.edp2021c1.core.SeatGenerator;
import com.edp2021c1.ui.App;
import com.google.gson.Gson;
import javafx.application.Application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Application intro, loads seat config.
 */
public class Main {
    /**
     * @param args used to start the application.
     */
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        // 如果不是命令行模式则启动JavaFX程序
        if (!arguments.contains("--nogui")) {
            reloadConfig();
            Application.launch(App.class, args);
            return;
        }

        // 命令行参数相关
        int i;
        long seed = new Random().nextLong();  // 种子，默认为随机数
        Date date = new Date();
        String outputPath = String.format("%tF.xlsx", date); // 导出路径，默认为当前路径
        SeatConfig conf=reloadConfig(); // 座位表生成配置，默认为当前目录下的seat_config.json中的配置

        // 获取配置文件路径
        if ((i = arguments.lastIndexOf("--config-path")) != -1 && i < arguments.size() - 1) {
            conf=SeatConfig.fromJsonFile(new File(arguments.get(i + 1)));
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
        Seat seat = new SeatGenerator(conf).generate(seed);
        seat.exportToExcelDocument(f);
        System.out.println("Seat table successfully exported to " + f.getAbsolutePath() + ".");
    }

    /**
     * @return default seat config loaded from file.
     */
    public static SeatConfig reloadConfig() {
        try {
            File f = new File("seat_config.json");
            if (f.createNewFile()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/conf/seat_config.json"))));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                FileOutputStream outputStream = new FileOutputStream(f);
                outputStream.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }

            return SeatConfig.fromJsonFile(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     */
    public static void saveConfig(SeatConfig config) {
        try {
            FileOutputStream out = new FileOutputStream("seat_config.json");
            out.write(new Gson().toJson(config).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
