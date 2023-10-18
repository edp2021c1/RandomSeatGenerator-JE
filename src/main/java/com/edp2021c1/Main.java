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
     * @param args app startup arguments.
     */
    public static void main(String[] args) throws IOException {
        reloadConfig();
        Application.launch(App.class, args);
    }

    /**
     * @param file to load config from.
     * @return seat config loaded from path.
     * @throws IOException if didn't successfully load config from file.
     */
    public static SeatConfig loadConfigFromFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String str = new String(bytes, StandardCharsets.UTF_8);
        return new Gson().fromJson(str, SeatConfig.class);
    }

    /**
     * @return default seat config loaded from file.
     * @throws IOException if didn't successfully load config from file.
     */
    public static SeatConfig reloadConfig() throws IOException {
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

        return loadConfigFromFile(f);
    }

    /**
     * @param config {@code SeatConfig} to set as the default seat config and save to file.
     * @throws IOException if didn't successfully save the config to file.
     */
    public static void saveConfig(SeatConfig config) throws IOException {
        FileOutputStream out = new FileOutputStream("seat_config.json");
        out.write(new Gson().toJson(config).getBytes(StandardCharsets.UTF_8));
        out.close();
    }
}
