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
    private static final Hashtable<String, Object> runtimeProperties = new Hashtable<>(2);

    private static final Hashtable<Long, Thread> threadIdHashtable = new Hashtable<>();

    private static final List<Runnable> exitHooks = new ArrayList<>(3);

    static {
        getRuntime().addShutdownHook(new Thread(RuntimeUtils::runExitHooks, "Exit Hooks"));

        addExitHook(() -> {
            if (LoggerWrapper.global().isOpen()) {
                LoggerWrapper.global().debug("Exiting");
                LoggerWrapper.global().close();
            }
        });

        loopThread(System::gc, 1000, "Auto GC Thread").start();
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private RuntimeUtils() {
    }

    /**
     * Runs the exit hooks one by one.
     */
    public static void runExitHooks() {
        synchronized (exitHooks) {
            exitHooks.forEach(Runnable::run);
            exitHooks.clear();
        }
    }

    /**
     * Sets the value of a specific property.
     *
     * @param key   of the property
     * @param value to set
     *
     * @return whether the property is empty
     */
    public static boolean setProperty(final String key, final Object value) {
        return runtimeProperties.put(key, value) == null;
    }

    /**
     * Returns the value of a specific property.
     *
     * @param key of the property
     *
     * @return value of the property
     */
    public static Object getProperty(final String key) {
        return runtimeProperties.get(key);
    }

    /**
     * Returns the value of a specific property, or the given value if is null
     *
     * @param key of the property
     * @param def default returned value
     *
     * @return the value of a specific property, or {@code def} if is null
     */
    public static Object getPropertyOrDefault(final String key, final Object def) {
        return runtimeProperties.getOrDefault(key, def);
    }

    /**
     * Returns a thread that executes a task repeatedly
     *
     * @param exe           task to execute repeatedly
     * @param waitingMillis time to wait between each run in millis
     * @param name          thread name
     *
     * @return a thread that executes the task repeatedly
     */
    public static Thread loopThread(final Runnable exe, final long waitingMillis, final String name) {
        return new LoopTaskThread(exe, waitingMillis, name);
    }

    /**
     * Adds a {@link Runnable} to call on application exit.
     *
     * @param taskToRun a {@code Runnable} to call on application exit
     */
    public static void addExitHook(final Runnable taskToRun) {
        exitHooks.add(taskToRun);
    }

    /**
     * Returns a thread that matches the given ID,
     * null if thread does not exist or is not live.
     *
     * @param id of the thread
     *
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
     * Returns a set of all live threads.
     *
     * @return all live threads
     */
    public static Set<Thread> getThreads() {
        return Thread.getAllStackTraces().keySet();
    }

    private static class LoopTaskThread extends Thread {

        private final Runnable loopTask;

        private final long waitingMillis;

        private final byte[] lock = new byte[0];

        private LoopTaskThread(final Runnable loopTask, final long waitingMillis, final String name) {
            this.loopTask = loopTask;
            this.waitingMillis = waitingMillis;
            if (name != null) {
                setName(name);
            }
        }

        @Override
        public void run() {
            while (isAlive()) {
                loopTask.run();
                synchronized (lock) {
                    try {
                        lock.wait(waitingMillis);
                    } catch (final InterruptedException ignored) {
                    }
                }
            }
        }

    }

}
