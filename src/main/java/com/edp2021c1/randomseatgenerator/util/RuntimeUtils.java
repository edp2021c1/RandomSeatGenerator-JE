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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;

/**
 * Runtime utils.
 *
 * @author Calboot
 * @since 1.4.6
 */
public final class RuntimeUtils {

    private static final List<Runnable> exitHooks = Lists.newLinkedList();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(RuntimeUtils::runExitHooks, "Exit Hooks"));
        addExitHook(() -> LOGGER.debug("Exiting"));
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
     * Adds a {@link Runnable} to call on application exit.
     *
     * @param taskToRun a {@code Runnable} to call on application exit
     */
    public static void addExitHook(final Runnable taskToRun) {
        exitHooks.add(taskToRun);
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
    @Deprecated
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
