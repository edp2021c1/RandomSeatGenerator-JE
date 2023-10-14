package com.edp2021c1;

import com.edp2021c1.core.SeatConfig;
import com.edp2021c1.ui.App;
import com.google.gson.Gson;
import javafx.application.Application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Application intro, loads seat config.
 */
public class Main {
    /**
     * Stores the seat config.
     */
    public static SeatConfig seatConfig = new SeatConfig();

    /**
     * @param args app startup arguments.
     * @throws IOException if fails to create or load seat config file.
     */
    public static void main(String[] args) throws IOException {
        // Load default seat config
        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/conf/seat_config.json"))));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }

        // Create seat config file if it doesn't exist
        File f = new File("seat_config.json");
        if (f.createNewFile()) {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }

        // Load user seat config
        FileInputStream inputStream = new FileInputStream(f);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String str = new String(bytes, StandardCharsets.UTF_8);
        seatConfig = new Gson().fromJson(str, SeatConfig.class);

        Application.launch(App.class, args);
    }
}
