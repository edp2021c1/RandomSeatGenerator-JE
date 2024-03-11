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
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.FileAlreadyLockedException;
import lombok.Getter;
import lombok.val;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static com.edp2021c1.randomseatgenerator.util.IOUtils.*;

/**
 * Handles {@link JSONAppConfig}.
 *
 * @author Calboot
 * @see JSONAppConfig
 * @since 1.4.9
 */
public class JSONAppConfigHolder implements AutoCloseable {

    /**
     * Default config handler.
     */
    private static final JSONAppConfigHolder global;
    private static final Path GLOBAL_CONFIG_PATH = Path.of(Metadata.DATA_DIR.toString(), "config", "randomseatgenerator.json");

    static {
        try {
            replaceWithDirectory(GLOBAL_CONFIG_PATH.getParent());

            if (!Files.isRegularFile(GLOBAL_CONFIG_PATH)) {
                deleteIfExists(GLOBAL_CONFIG_PATH);
                Files.createFile(GLOBAL_CONFIG_PATH);
            }
            global = createHolder(GLOBAL_CONFIG_PATH, true);
            if (readString(global.channel).isBlank()) {
                val builtInConfigStream = JSONAppConfigHolder.class.getResourceAsStream("/assets/conf/default.json");
                if (builtInConfigStream != null) {
                    global.putJson(new String(
                            builtInConfigStream.readAllBytes()
                    ));
                    builtInConfigStream.close();
                }
            }

        } catch (final FileAlreadyLockedException e) {
            throw new ApplicationAlreadyRunningException();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Path configPath;
    private final JSONAppConfig content;
    private FileChannel channel;
    private boolean closed;
    private boolean loaded;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private JSONAppConfigHolder(final Path configPath) throws IOException {
        this.content = new JSONAppConfig();
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
    public static JSONAppConfigHolder global() {
        return global;
    }

    /**
     * Creates an instance with the given config path and adds it to the holders set.
     *
     * @param configPath  path of config
     * @param closeOnExit whether the created holder will be automatically closed on application exit
     * @return the holder created
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public static JSONAppConfigHolder createHolder(final Path configPath, final boolean closeOnExit) throws IOException {
        val res = new JSONAppConfigHolder(configPath);
        if (closeOnExit) {
            RuntimeUtils.addRunOnExit(res::close);
        }
        return res;
    }

    private synchronized void loadConfig0() throws IOException {
        if (!Files.isRegularFile(configPath)) {
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
            content.putJsonAndReturn(readString(channel)).check();
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
            Logging.debug("Config holder at " + configPath + " closed");
            closed = true;
        }
    }

    private synchronized void checkState() {
        if (closed) {
            throw new IllegalStateException("Closed JSONAppConfigHolder");
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
        putAll(new JSONAppConfig(1).putAndReturn(key, value));
    }

    /**
     * Puts a map.
     *
     * @param map to put
     * @throws RuntimeException if an I/O error occurs
     */
    public synchronized void putAll(final Map<? extends String, ?> map) {
        checkState();
        try {
            overwriteString(channel, content.putAllAndReturn(map).checkAndReturn().toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses and puts the JSONObject from the string.
     *
     * @param jsonString that contains the map to parse and put
     * @throws RuntimeException if an I/O error occurs
     * @see JSONAppConfig#putJsonAndReturn(String)
     */
    public synchronized void putJson(final String jsonString) {
        checkState();
        try {
            overwriteString(channel, content.putJsonAndReturn(jsonString).checkAndReturn().toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the clone of the config.
     *
     * @return the clone of the config
     */
    public synchronized JSONAppConfig get() {
        checkState();
        return content.cloneThis();
    }

}
