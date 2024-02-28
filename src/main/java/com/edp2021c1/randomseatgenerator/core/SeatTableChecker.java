package com.edp2021c1.randomseatgenerator.core;

import java.util.List;

/**
 * Seat table checker interface.
 */
@FunctionalInterface
public interface SeatTableChecker {

    /**
     * Returns if the seat table is valid.
     *
     * @param seatTable to check
     * @param config    used to check
     * @return if the seat table is valid
     * @throws IllegalConfigException if config is invalid
     */
    boolean check(final List<String> seatTable, final SeatConfig config) throws IllegalConfigException;
}
