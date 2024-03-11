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

import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfig;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import static java.lang.Runtime.getRuntime;

/**
 * Runtime utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public final class RuntimeUtils {

    /**
     * Runtime config.
     */
    public static final JSONAppConfig runtimeConfig = new JSONAppConfig();
    private static final Hashtable<Long, Thread> threadIdHashtable = new Hashtable<>();
    private static final List<Runnable> runOnExit = new ArrayList<>(3);
    private static boolean staticInitialized;

    /**
     * Don't let anyone else instantiate this class.
     */
    private RuntimeUtils() {
    }

    /**
     * Called on application start up.
     *
     * @param withGUI whether the application is launched with a GUI
     */
    public static void initStatic(final boolean withGUI) {
        if (!staticInitialized) {
            getRuntime().addShutdownHook(new Thread(() -> {
                synchronized (runOnExit) {
                    for (int i = 0, j = runOnExit.size(); i < j; i++) {
                        Thread.ofVirtual().name("Exit Hook No." + i).start(runOnExit.get(i));
                    }
                }
            }, "Exit Hooks"));

            addRunOnExit(() -> {
                if (Logging.isStarted()) {
                    Logging.debug("Exiting");
                    Logging.close();
                }
            });

            runtimeConfig.put("launching.gui", withGUI);
            staticInitialized = true;
        }
    }

    /**
     * Returns a set of all live threads.
     *
     * @return all live threads
     */
    public static Set<Thread> getThreads() {
        return Thread.getAllStackTraces().keySet();
    }

    /**
     * Returns a thread that matches the given ID,
     * null if thread does not exist or is not live.
     *
     * @param id of the thread
     * @return thread identified by {@code id}
     */
    public static Thread getThreadById(final long id) {
        if (threadIdHashtable.containsKey(id)) {
            return threadIdHashtable.get(id);
        }
        getThreads().forEach(t -> {
            if (!threadIdHashtable.containsValue(t)) {
                threadIdHashtable.put(t.threadId(), t);
            }
        });
        return threadIdHashtable.getOrDefault(id, null);
    }

    /**
     * Adds a {@link Runnable} to call on application exit.
     *
     * @param taskToRun a {@code Runnable} to call on application exit
     */
    public static void addRunOnExit(final Runnable taskToRun) {
        runOnExit.add(taskToRun);
    }

}
