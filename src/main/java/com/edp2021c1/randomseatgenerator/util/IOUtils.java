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

package com.edp2021c1.randomseatgenerator.util;

import lombok.val;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

/**
 * IO utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public final class IOUtils {

    /**
     * Don't let anyone else instantiate this class.
     */
    private IOUtils() {
    }

    /**
     * Reads all the content from the channel to a string.
     * <p>Will not modify the position of the channel.
     *
     * @param channel file channel
     * @return string read from the channel
     * @throws IOException if an I/O error occurs
     */
    public synchronized static String readString(final FileChannel channel) throws IOException {
        val buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer, 0);
        return new String(buffer.array());
    }

    /**
     * Overwrites the given channel with the string.
     *
     * @param channel file channel
     * @param str     string to write
     * @throws IOException if an I/O error occurs
     */
    public synchronized static void overwriteString(final FileChannel channel, final String str) throws IOException {
        val bytes = Objects.requireNonNull(str, "Cannot write null string").getBytes();
        if (channel.truncate(0).write(ByteBuffer.wrap(bytes)) != bytes.length) {
            throw new IOException();
        }
    }

}
