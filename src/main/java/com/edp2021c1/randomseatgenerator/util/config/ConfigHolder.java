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
import com.edp2021c1.randomseatgenerator.util.Utils;
import com.edp2021c1.randomseatgenerator.util.exception.ApplicationAlreadyRunningException;
import com.edp2021c1.randomseatgenerator.util.exception.FileAlreadyLockedException;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.edp2021c1.randomseatgenerator.util.IOUtils.*;

/**
 * Handles {@link JSONAppConfig}.
 *
 * @author Calboot
 * @see JSONAppConfig
 * @since 1.4.9
 */
public class ConfigHolder implements Closeable {

    /**
     * Default config handler.
     */
    private static final ConfigHolder global;
    private static final JSONAppConfig BUILT_IN;
    private static final Path GLOBAL_CONFIG_PATH = Utils.join(Metadata.DATA_DIR, "config", "randomseatgenerator.json");
    private static final Set<ConfigHolder> holders = new HashSet<>();

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
        BUILT_IN = JSONAppConfig.fromJson(str.toString());

        try {
            global = createHolder(GLOBAL_CONFIG_PATH);
        } catch (FileAlreadyLockedException e) {
            throw new ApplicationAlreadyRunningException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Path configPath;
    private final JSONAppConfig content;
    private final FileChannel channel;
    private FileTime configLastModifiedTime;
    private boolean closed;

    /**
     * Creates an instance with the given config path.
     *
     * @param configPath path of config
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    private ConfigHolder(final Path configPath) throws IOException {
        this.content = new JSONAppConfig();
        this.configLastModifiedTime = null;
        this.configPath = configPath;

        final Path configDir = configPath.getParent();
        replaceWithDirectory(configDir);
        if (notFullyPermitted(configDir)) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        boolean needsInit = false;
        if (!Files.exists(configPath) || !Files.isRegularFile(configPath)) {
            deleteIfExists(configPath);
            Files.createFile(configPath);
            needsInit = true;
        }
        if (notFullyPermitted(configPath)) {
            throw new IOException("Does not has enough permission to read/write config");
        }

        this.channel = FileChannel.open(configPath, StandardOpenOption.READ, StandardOpenOption.WRITE);
        if (this.channel.tryLock() == null) {
            throw new FileAlreadyLockedException(configPath);
        }
        if (readString(channel).isEmpty()) {
            needsInit = true;
        }
        if (needsInit) {
            set(BUILT_IN);
        }

        closed = false;
    }

    /**
     * Closes all opened holders.
     */
    public static void closeAll() {
        Logging.debug("Closing all config handlers");
        holders.forEach(ConfigHolder::close);
        holders.clear();
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
     * Creates an instance with the given config path and adds it to the holders set.
     *
     * @param configPath path of config
     * @return the holder created
     * @throws IOException if failed to init config path, or does not have enough permission of the path
     */
    public static ConfigHolder createHolder(final Path configPath) throws IOException {
        final ConfigHolder h = new ConfigHolder(configPath);
        holders.add(h);
        return h;
    }

    /**
     * Closes this.
     */
    public void close() {
        if (closed) return;
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closed = true;
        }
    }

    private void checkState() {
        if (closed) {
            throw new IllegalStateException("Closed ConfigHolder");
        }
    }

    /**
     * Sets the value of config
     *
     * @param config to set
     * @throws RuntimeException if an I/O error occurs
     */
    public void set(final JSONAppConfig config) {
        checkState();
        content.set(config);
        content.check();
        try {
            overwriteString(channel, content.toJson());
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
    public JSONAppConfig get() {
        try {
            flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.clone();
    }

    private void flush() throws IOException {
        checkState();
        if (Objects.equals(Files.getLastModifiedTime(configPath), configLastModifiedTime)) {
            return;
        }
        content.set(JSONAppConfig.fromJson(readString(channel)));
        try {
            content.check();
        } catch (final RuntimeException e) {
            Logging.warning("Invalid config loaded");
            Logging.warning(e.getLocalizedMessage());
        }
    }
}
