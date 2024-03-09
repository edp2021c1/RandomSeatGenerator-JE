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

import com.edp2021c1.randomseatgenerator.util.Logging;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.FileAlreadyLockedException;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.util.IOUtils.*;

/**
 * Handles {@link AppConfig}.
 *
 * @author Calboot
 * @see AppConfig
 * @since 1.4.9
 */
public class ConfigHolder implements AutoCloseable {

    /**
     * Default config handler.
     */
    private static final ConfigHolder global;
    private static final AppConfig BUILT_IN;
    private static final Path GLOBAL_CONFIG_PATH = Path.of(Metadata.DATA_DIR.toString(), "config", "randomseatgenerator.json");

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
        BUILT_IN = AppConfig.fromJsonString(str.toString());

        try {
            replaceWithDirectory(GLOBAL_CONFIG_PATH.getParent());

            boolean needsInit = false;

            if (!Files.isRegularFile(GLOBAL_CONFIG_PATH)) {
                deleteIfExists(GLOBAL_CONFIG_PATH);
                Files.createFile(GLOBAL_CONFIG_PATH);
                needsInit = true;
            } else if (Files.readString(GLOBAL_CONFIG_PATH).isBlank()) {
                needsInit = true;
            }
            global = createHolder(GLOBAL_CONFIG_PATH);
            if (needsInit) {
                global.putAll(BUILT_IN);
            }
        } catch (final FileAlreadyLockedException e) {
            throw new ApplicationAlreadyRunningException();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Path configPath;
    private final AppConfig content;
    private FileChannel channel;
    private boolean closed;
    private boolean loaded;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private ConfigHolder(final Path configPath) throws IOException {
        content = new AppConfig();
        this.configPath = configPath;

        if (notFullyPermitted(replaceWithDirectory(configPath.getParent()))) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        loadConfig();
    }

    /**
     * Returns the global config holder.
     *
     * @return the global config holder
     */
    public static ConfigHolder global() {
        return global;
    }

    /**
     * Creates an instance with the given config path and adds it to the holders set.
     *
     * @param configPath path of config
     * @return the holder created
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public static ConfigHolder createHolder(final Path configPath) throws IOException {
        return new ConfigHolder(configPath);
    }

    private synchronized void loadConfig0() throws IOException {
        if (!Files.exists(configPath) || !Files.isRegularFile(configPath)) {
            deleteIfExists(configPath);
            Files.createFile(configPath);
        }
        if (notFullyPermitted(configPath)) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        channel = FileChannel.open(configPath, StandardOpenOption.READ, StandardOpenOption.WRITE);
        if (channel.tryLock() == null) {
            throw new FileAlreadyLockedException(configPath);
        }
        if (readString(channel).isEmpty()) {
            return;
        }
        try {
            content.putAllAndReturn(AppConfig.fromJsonString(readString(channel))).check();
        } catch (final RuntimeException e) {
            Logging.warning("Invalid config loaded");
            Logging.warning(Strings.getStackTrace(e));
        }
    }

    private synchronized void loadConfig() throws IOException {
        loadConfig0();
        loaded = true;
    }

    /**
     * Closes this.
     */
    public synchronized void close() {
        if (closed) {
            return;
        }
        try {
            channel.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            closed = true;
        }
    }

    private synchronized void checkState() {
        if (closed) {
            throw new IllegalStateException("Closed ConfigHolder");
        }
        if (!loaded) {
            try {
                loadConfig();
            } catch (final IOException e) {
                Logging.warning("Failed to load config from " + configPath);
                Logging.warning(Strings.getStackTrace(e));
            }
        }
    }

    /**
     * Associates the specified value with the specified key in the config.
     *
     * @param key   with which the specified value is to be associated
     * @param value to be associated with the specified key
     * @throws RuntimeException if an I/O error occurs
     */
    public synchronized void put(final String key, final Object value) {
        putAll(new Config(1).putAndReturn(key, value));
    }

    /**
     * Sets the value of config
     *
     * @param map to set
     * @throws RuntimeException if an I/O error occurs
     */
    public synchronized void putAll(final Map<? extends String, ?> map) {
        checkState();
        try {
            overwriteString(channel, content.putAllAndReturn(map).checkAndReturn().toJsonString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the clone of the config.
     *
     * @return the clone of the config
     */
    public synchronized AppConfig get() {
        checkState();
        return content.clone();
    }

}
