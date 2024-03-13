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
import lombok.val;

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
                        new Thread(runOnExit.get(i), "Exit Hook No." + i).start();
                    }
                }
            }, "Exit Hooks"));

            addRunOnExit(() -> {
                if (Logging.isStarted()) {
                    Logging.debug("Exiting");
                    Logging.end();
                }
            });

            loopThread(System::gc, 1000, "GC Thread").start();

            runtimeConfig.put("launching.gui", withGUI);
            staticInitialized = true;
        }
    }

    public static Thread loopThread(final Runnable taskToRun, final long waitingMillis, final String name) {
        return new LoopTaskThread(taskToRun, waitingMillis, name);
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
        val thread = threadIdHashtable.get(id);
        if (thread != null) {
            return thread;
        }
        getThreads().forEach(t -> threadIdHashtable.putIfAbsent(t.threadId(), t));
        return threadIdHashtable.get(id);
    }

    /**
     * Adds a {@link Runnable} to call on application exit.
     *
     * @param taskToRun a {@code Runnable} to call on application exit
     */
    public static void addRunOnExit(final Runnable taskToRun) {
        runOnExit.add(taskToRun);
    }

    private static class LoopTaskThread extends Thread {
        private final Runnable loopTask;
        private final long waitingMillis;

        private LoopTaskThread(final Runnable loopTask, final long waitingMillis, final String name) {
            this.loopTask = loopTask;
            this.waitingMillis = waitingMillis;
            if (name != null) {
                setName(name);
            }
        }

        @Override
        @SuppressWarnings("all")
        public void run() {
            while (true) {
                loopTask.run();
                synchronized (this) {
                    try {
                        wait(waitingMillis);
                    } catch (final InterruptedException ignored) {
                    }
                }
            }
        }
    }

}
