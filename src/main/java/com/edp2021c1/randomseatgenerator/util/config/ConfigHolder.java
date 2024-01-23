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

package com.edp2021c1.randomseatgenerator.util.config;

import com.edp2021c1.randomseatgenerator.util.CollectionUtils;
import com.edp2021c1.randomseatgenerator.util.IOUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.Utils;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
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
    private static final ConfigHolder global;
    private static final RawAppConfig BUILT_IN;
    private static final Path DEFAULT_CONFIG_PATH = Utils.join(Metadata.DATA_DIR, "config", "randomseatgenerator.json");
    private static final List<ConfigHolder> holders = new ArrayList<>();

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
        BUILT_IN = RawAppConfig.fromJson(str.toString());

        try {
            global = new ConfigHolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Path configPath;
    private final RawAppConfig content;
    private final FileChannel fileChannel;
    private FileTime configLastModifiedTime;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public ConfigHolder(Path configPath) throws IOException {
        this.content = new RawAppConfig();
        this.configLastModifiedTime = null;
        this.configPath = configPath;
        holders.add(this);

        final Path configDir = configPath.getParent();
        if (!Files.isDirectory(configDir)) {
            Utils.delete(configDir);
        }
        Files.createDirectories(configDir);
        if (IOUtils.notFullyPermitted(configDir)) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        if (Files.notExists(configPath)) {
            set(BUILT_IN);
        }
        if (IOUtils.notFullyPermitted(configPath)) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        if (!Files.isRegularFile(configPath)) {
            Utils.delete(configPath);
            set(BUILT_IN);
        }

        Path lockerPath = Path.of(configPath + ".lck");
        Utils.delete(lockerPath);
        Files.createFile(lockerPath);
        this.fileChannel = FileChannel.open(lockerPath, StandardOpenOption.DELETE_ON_CLOSE);
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
     * Closes all opened holders.
     */
    public static void closeAll() {
        Logging.debug("Closing all config handlers");
        CollectionUtils.modifiableListOf(holders).forEach(ConfigHolder::close);
    }

    /**
     * Returns the global config holder.
     *
     * @return the global config holder
     */
    public static ConfigHolder globalHolder() {
        return global;
    }

    /**
     * Closes this.
     */
    public void close() {
        try {
            holders.remove(this);
            fileChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the value of config
     *
     * @param config to set
     * @throws RuntimeException if an I/O error occurs
     */
    public void set(RawAppConfig config) {
        content.set(config);
        try {
            Files.writeString(configPath, content.toJson());
            configLastModifiedTime = Files.getLastModifiedTime(configPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the config.
     *
     * @return the config
     * @throws RuntimeException if an I/O error occurs
     */
    public RawAppConfig get() {
        try {
            flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.clone();
    }

    private void flush() throws IOException {
        if (Files.notExists(configPath) || !Files.isRegularFile(configPath)) {
            Logging.warning("Config file not found or directory found on the path, will use default value");
            Utils.delete(configPath);
            Files.createFile(configPath);
            set(BUILT_IN);
            return;
        }
        if (Objects.equals(Files.getLastModifiedTime(configPath), configLastModifiedTime)) {
            return;
        }
        content.set(RawAppConfig.fromJson(configPath));
        try {
            content.checkFormat();
        } catch (final RuntimeException e) {
            Logging.warning("Invalid config loaded");
            Logging.warning(e.getLocalizedMessage());
        }
    }
}
