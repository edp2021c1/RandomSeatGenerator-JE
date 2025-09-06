package com.edp2021c1.randomseatgenerator.v2;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatConfig;
import com.edp2021c1.randomseatgenerator.v2.util.IOUtils;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;

@EqualsAndHashCode
public class AppConfig {

    public static AppConfig loadFromPath(Path path) throws IOException {
        if (!PathUtils.isRegularFile(path)) {
            return null;
        }
        return loadFromJson(IOUtils.readFile(path));
    }

    public static AppConfig loadFromJson(String json) {
        return RandomSeatGenerator.GSON.fromJson(json, AppConfig.class);
    }

    public boolean darkMode;

    public String language;

    public SeatConfig seatConfig;

    public void saveToPath(Path path) throws IOException {
        IOUtils.writeFile(path, RandomSeatGenerator.GSON.toJson(this));
    }

    public AppConfig copy() {
        AppConfig appConfig = new AppConfig();
        appConfig.darkMode = darkMode;
        appConfig.language = language;
        appConfig.seatConfig = seatConfig.copy();
        return appConfig;
    }

}
