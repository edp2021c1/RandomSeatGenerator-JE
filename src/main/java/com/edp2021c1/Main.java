package com.edp2021c1;

import com.edp2021c1.core.Seat;
import com.edp2021c1.core.SeatConfig;
import com.edp2021c1.core.SeatGenerator;
import com.edp2021c1.ui.App;
import com.google.gson.Gson;
import javafx.application.Application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * Application intro, loads seat config.
 */
public class Main {
    /**
     * @param args used to start the application.
     */
    public static void main(String[] args) {
        if(!Arrays.asList(args).contains("--nogui")) {
            reloadConfig();
            Application.launch(App.class, args);
            return;
        }
        System.out.print("Input seed: ");
        long seed=new Scanner(System.in).nextLong();
        Seat seat=new SeatGenerator(reloadConfig()).generate(seed);
        File f=new File("seat_table.xlsx");
        seat.exportToExcelDocument(f);
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
