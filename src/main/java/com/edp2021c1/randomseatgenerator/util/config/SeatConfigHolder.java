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

import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.FileAlreadyLockedException;
import lombok.Getter;
import lombok.val;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * Handles {@link SeatConfigWrapper}.
 *
 * @author Calboot
 * @see SeatConfigWrapper
 * @since 1.4.9
 */
public class SeatConfigHolder {

    /**
     * Default config handler.
     */
    private static final SeatConfigHolder global;

    private static final PathWrapper globalPath = PathWrapper.wrap(Metadata.DATA_DIR.toString(), "config", "randomseatgenerator.json");

    static {
        try {
            globalPath.getParent().replaceWithDirectory();

            global = createHolder(globalPath.replaceIfNonRegularFile(), true);
            if (global.configPath.readString().isBlank()) {
                val builtInConfigStream = SeatConfigHolder.class.getResourceAsStream("/assets/conf/default.json");
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
    private final PathWrapper configPath;

    private final SeatConfigWrapper content;

    private FileChannel channel;

    private boolean closed;

    private boolean loaded;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     *
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private SeatConfigHolder(final Path configPath) throws IOException {
        this.content = new SeatConfigWrapper();
        this.configPath = PathWrapper.wrap(configPath);

        if (this.configPath.getParent().replaceWithDirectory().notFullyPermitted()) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        loadConfig();
    }

    private synchronized void loadConfig() throws IOException {
        loadConfig0();
        loaded = true;
    }

    private synchronized void loadConfig0() throws IOException {
        if (configPath.replaceIfNonRegularFile().notFullyPermitted()) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        channel = configPath.openFileChannel(StandardOpenOption.READ, StandardOpenOption.WRITE);
        if (channel.tryLock() == null) {
            throw new FileAlreadyLockedException(configPath);
        }
        if (configPath.readString().isEmpty()) {
            return;
        }
        try {
            content.putJsonAndReturn(configPath.readString()).check();
        } catch (final RuntimeException e) {
            Logging.warning("Invalid config loaded");
            Logging.warning(Strings.getStackTrace(e));
        }
    }

    /**
     * Returns the global config holder.
     *
     * @return the global config holder
     */
    public static SeatConfigHolder global() {
        return global;
    }

    /**
     * Creates an instance with the given config path and adds it to the holders set.
     *
     * @param configPath  path of config
     * @param closeOnExit whether the created holder will be automatically closed on application exit
     *
     * @return the holder created
     *
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public static SeatConfigHolder createHolder(final Path configPath, final boolean closeOnExit) throws IOException {
        val res = new SeatConfigHolder(configPath);
        if (closeOnExit) {
            RuntimeUtils.addRunOnExit(res::close);
        }
        return res;
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

    /**
     * Puts a map.
     *
     * @param map to put
     *
     * @throws RuntimeException if an I/O error occurs
     */
    public synchronized void putAll(final Map<? extends String, ?> map) {
        checkState();
        try {
            configPath.writeString(content.putAllAndReturn(map).checkAndReturn().toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void checkState() {
        if (closed) {
            throw new IllegalStateException("Closed SeatConfigHolder");
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
     * Parses and puts the JSONObject from the string.
     *
     * @param jsonString that contains the map to parse and put
     *
     * @throws RuntimeException if an I/O error occurs
     * @see SeatConfigWrapper#putJsonAndReturn(String)
     */
    public synchronized void putJson(final String jsonString) {
        checkState();
        try {
            configPath.writeString(content.putJsonAndReturn(jsonString).checkAndReturn().toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the clone of the config.
     *
     * @return the clone of the config
     */
    public synchronized SeatConfigWrapper get() {
        checkState();
        return content.cloneThis();
    }

}
