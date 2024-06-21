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

import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.PathWrapper;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.FileAlreadyLockedException;
import com.edp2021c1.randomseatgenerator.util.useroutput.LoggerWrapper;
import lombok.Getter;
import lombok.val;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static com.alibaba.fastjson2.JSON.parseObject;

/**
 * Handles {@link CachedMapSeatConfig}.
 *
 * @author Calboot
 * @see CachedMapSeatConfig
 * @since 1.4.9
 */
public class SeatConfigHolder {

    private static final LoggerWrapper LOGGER = LoggerWrapper.global();

    /**
     * Default config handler.
     */
    private static final SeatConfigHolder global;

    private static final PathWrapper globalPath = PathWrapper.wrap(Metadata.DATA_DIR.toString(), "config", "randomseatgenerator.json");

    static {
        try {
            globalPath.getParent().replaceWithDirectory();

            global = createHolder(globalPath, true);
            if (global.configPath.readString().isBlank()) {
                val builtInConfigStream = SeatConfigHolder.class.getResourceAsStream("/assets/conf/default.json");
                if (builtInConfigStream != null) {
                    global.putAll(parseObject(new String(builtInConfigStream.readAllBytes())));
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

    private final CachedMapSeatConfig content;

    private final FileChannel channel;

    private boolean closed;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     *
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private SeatConfigHolder(final Path configPath) throws IOException {
        this.content = new CachedMapSeatConfig();
        this.configPath = PathWrapper.wrap(configPath);
        initConfigPath();

        this.channel = this.configPath.openFileChannel(StandardOpenOption.READ, StandardOpenOption.WRITE);
        initChannel();
    }

    private void initConfigPath() throws IOException {
        if (configPath.getParent().replaceWithDirectory().notFullyPermitted()) {
            throw new IOException("Does not has enough permission to read/write config");
        }
        if (configPath.replaceIfNonRegularFile().notFullyPermitted()) {
            throw new IOException("Does not has enough permission to read/write config");
        }
    }

    private void initChannel() throws IOException {
        if (channel.tryLock() == null) {
            throw new FileAlreadyLockedException(configPath);
        }
        val obj = parseObject(configPath.readString());
        putAll(
                content.putAllAndReturn(
                        obj == null ? Map.of() : obj
                ).checkAndReturn()
        ); // To load and truncate the content
    }

    /**
     * Puts a map.
     *
     * @param map to put
     *
     * @throws IOException           if an I/O error occurs
     * @throws IllegalStateException if is closed
     */
    public synchronized void putAll(final Map<? extends String, ?> map) throws IOException {
        checkState();
        configPath.writeString(content.putAllAndReturn(map).checkAndReturn().toJsonString());
    }

    private void checkState() {
        if (closed) {
            throw new IllegalStateException("Closed SeatConfigHolder");
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
            RuntimeUtils.addExitHook(res::close);
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
            if (LOGGER.isOpen()) {
                LOGGER.debug("Config holder at " + configPath + " closed");
            }
            closed = true;
        }
    }

    /**
     * Returns the clone of the config.
     *
     * @return the clone of the config
     *
     * @throws IllegalStateException if is closed
     */
    public CachedMapSeatConfig getClone() {
        checkState();
        return content.cloneThis();
    }

}
