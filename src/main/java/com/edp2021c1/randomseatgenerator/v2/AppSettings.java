package com.edp2021c1.randomseatgenerator.v2;

import com.edp2021c1.randomseatgenerator.v2.util.IOUtils;
import com.edp2021c1.randomseatgenerator.v2.util.Metadata;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

public final class AppSettings {

    private static final Path configPath = Metadata.DATA_DIR.resolve("config.json");

    private static final AppConfig BUILTIN;

    public static boolean withGUI = false;

    public static AppConfig config = null;

    public static boolean initializingDone = false;

    static {
        try {
            BUILTIN = AppConfig.loadFromJson(IOUtils.readResource("assets/conf/builtin.json"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void loadConfig() throws IOException {
        LOGGER.debug("Loading config from {}", configPath);
        if (Files.isDirectory(configPath)) {
            LOGGER.warn("Directory on config path, will remove");
            IOUtils.replaceWithFile(configPath);
        }
        AppConfig c = AppConfig.loadFromPath(configPath);
        if (c == null) {
            LOGGER.warn("No valid config in {}, will use builtin config", configPath);
            c = BUILTIN.copy();
            c.saveToPath(configPath);
        }
        config = c;
    }

    public static void saveConfig() throws IOException {
        if (config != null) {
            config.saveToPath(configPath);
        } else {
            loadConfig();
        }
    }

}
