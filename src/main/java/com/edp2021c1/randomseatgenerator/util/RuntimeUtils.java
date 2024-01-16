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
import com.edp2021c1.randomseatgenerator.util.logging.Logging;

import java.util.HashMap;
import java.util.Set;

/**
 * Runtime utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public class RuntimeUtils {
    private static final HashMap<Long, Thread> threadIdMap = new HashMap<>();
    private static final Thread exitHook = new Thread("Exit Hook") {
        public void run() {
            exitTask();
        }
    };

    static {
        Runtime.getRuntime().addShutdownHook(exitHook);
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

        final Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (final Thread t : threads) {
            if (t.threadId() == id) {
                threadIdMap.put(id, t);
                return t;
            }
        }
        return null;
    }

    /**
     * Terminates the application.
     */
    private static void exitTask() {
        Logging.debug("Exiting");
        ConfigHolder.closeAll();
        Logging.close();
    }
}
