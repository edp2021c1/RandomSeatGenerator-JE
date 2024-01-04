package com.edp2021c1.randomseatgenerator.util.config;

import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * Handles {@link RawAppConfig}.
 *
 * @author Calboot
 * @see RawAppConfig
 * @since 1.4.9
 */
public class ConfigHolder {

    /**
     * Default config handler.
     */
    public static final ConfigHolder CONFIG;

    private static final RawAppConfig DEFAULT_CONFIG;

    private static final Path DEFAULT_CONFIG_PATH = Paths.get(Metadata.DATA_DIR, "config", "randomseatgenerator.json");

    static {
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(ConfigHolder.class.getResourceAsStream("/assets/conf/default.json"))
                )
        );
        final StringBuilder str = new StringBuilder();
        for (final Object s : reader.lines().toArray()) {
            str.append(s);
        }
        DEFAULT_CONFIG = RawAppConfig.fromJson(str.toString());

        try {
            CONFIG = new ConfigHolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Path configPath;

    private final RawAppConfig current;

    private FileTime configLastModifiedTime;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public ConfigHolder(Path configPath) throws IOException {
        this.current = new RawAppConfig();
        this.configLastModifiedTime = null;
        this.configPath = configPath;
        init();
    }

    /**
     * Creates an instance with the default config path.
     *
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public ConfigHolder() throws IOException {
        this(DEFAULT_CONFIG_PATH);
    }

    /**
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private void init() throws IOException {
        final Path configDir = configPath.getParent();

        if (!Files.isDirectory(configDir)) {
            IOUtils.deleteIfExists(configDir);
        }
        Files.createDirectories(configDir);
        if (IOUtils.lackOfPermission(configDir)) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        if (Files.notExists(configPath)) {
            set(DEFAULT_CONFIG);
        }
        if (IOUtils.lackOfPermission(configPath)) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        if (!Files.isRegularFile(configPath)) {
            IOUtils.deleteIfExists(configPath);
            set(DEFAULT_CONFIG);
        }
    }

    /**
     * @param config to set
     * @throws IOException if an I/O error occurs
     */
    public void set(RawAppConfig config) throws IOException {
        current.set(config);
        Files.writeString(configPath, current.toJson());
        configLastModifiedTime = Files.getLastModifiedTime(configPath);
    }

    /**
     * @return the config
     * @throws IOException if an I/O error occurs
     */
    public RawAppConfig get() throws IOException {
        refresh();
        return current;
    }

    private void refresh() throws IOException {
        if (Objects.equals(Files.getLastModifiedTime(configPath), configLastModifiedTime)) {
            return;
        }
        if (Files.notExists(configPath) || !Files.isRegularFile(configPath)) {
            Logging.warning("Config file not found or directory found on the path, will use default value");
            IOUtils.deleteIfExists(configPath);
            Files.createFile(configPath);
            set(DEFAULT_CONFIG);
            return;
        }
        current.set(RawAppConfig.fromJson(configPath));
        try {
            current.checkFormat();
        } catch (final RuntimeException e) {
            Logging.warning("Invalid config");
        }
    }
}
