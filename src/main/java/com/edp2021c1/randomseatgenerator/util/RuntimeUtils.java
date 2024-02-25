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

import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;

import java.util.HashMap;
import java.util.Optional;

/**
 * Runtime utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public final class RuntimeUtils {
    private static final HashMap<Long, Thread> threadIdMap = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(RuntimeUtils::exit, "Exit Hook"));
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private RuntimeUtils() {
    }

    /**
     * Returns a thread that matches the given ID,
     * null if thread does not exist or is not live.
     *
     * @param id of the thread
     * @return thread identified by {@code id}
     */
    public static Thread getThreadById(final long id) {
        if (threadIdMap.containsKey(id)) {
            return threadIdMap.get(id);
        }
        final Optional<Thread> op = Thread.getAllStackTraces().keySet().stream().filter(t -> t.threadId() == id).findFirst();
        op.ifPresent(t -> threadIdMap.put(id, t));
        return op.orElse(null);
    }

    /**
     * Terminates the application.
     */
    private static void exit() {
        Logging.debug("Exiting");
        ConfigHolder.closeAll();
        Logging.close();
    }
}
