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

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.edp2021c1.randomseatgenerator.util.Log.LOG;
import static java.lang.Runtime.getRuntime;

/**
 * Runtime utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public final class RuntimeUtils {

    private static final Hashtable<Object, Object> sysProp = System.getProperties();

    private static final Hashtable<Long, Thread> threadIdHashtable = new Hashtable<>();

    private static final List<Runnable> exitHooks = new ArrayList<>(3);

    private static final Timer timer = new Timer("Global Timer", true);

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    static {
        getRuntime().addShutdownHook(new Thread(RuntimeUtils::runExitHooks, "Exit Hooks"));

        addExitHook(() -> {
            if (LOG.isOpen()) {
                LOG.debug("Exiting");
                LOG.shutdown();
            }
        });

        addExitHook(timer::cancel);

        runLoopingTask(System::gc, 5000);
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
    public static boolean setProperty(final Object key, final Object value) {
        return sysProp.put(key, value) == null;
    }

    /**
     * Returns the value of a specific property.
     *
     * @param key of the property
     *
     * @return value of the property
     */
    public static Object getProperty(final Object key) {
        return sysProp.get(key);
    }

    /**
     * Returns the value of a specific property, or the given value if is null
     *
     * @param key of the property
     * @param def default returned value
     *
     * @return the value of a specific property, or {@code def} if is null
     */
    public static Object getPropertyOrDefault(final Object key, final Object def) {
        return sysProp.getOrDefault(key, def);
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
     * Runs a task in loop.
     *
     * @param taskToRun     task to run in loop
     * @param waitingMillis millis to wait between each run
     */
    public static void runLoopingTask(final Runnable taskToRun, final long waitingMillis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskToRun.run();
            }
        }, 0L, waitingMillis);
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
        Thread thread = threadIdHashtable.get(id);
        if (thread != null) {
            return thread;
        }
        Optional<Thread> res = getThreads().stream().filter(t -> t.threadId() == id).findAny();
        res.ifPresent(value -> threadIdHashtable.put(id, value));
        return res.orElse(null);
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
     * Runs a {@code Supplier} task with a timeout.
     *
     * @param task     to run
     * @param timeout  to wait before throwing a {@link TimeoutException}
     * @param timeUnit of {@code timeout}
     * @param <T>      type to return
     *
     * @return the result of {@code task}
     *
     * @throws TimeoutException      if the wait timed out
     * @throws ExecutionException    if the computation threw an exception
     * @throws InterruptedException  if the current thread is interrupted while waiting
     * @throws CancellationException if the computation is cancelled
     * @see ExecutorService#submit(Runnable)
     * @see Future#get(long, TimeUnit)
     */
    public static <T> T runWithTimeout(final Supplier<T> task, final long timeout, final TimeUnit timeUnit)
            throws TimeoutException, ExecutionException, InterruptedException, CancellationException {
        return executorService.submit(task::get).get(timeout, timeUnit);
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private RuntimeUtils() {
    }

}
