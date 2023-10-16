package com.edp2021c1.core;

import lombok.Getter;

import java.util.List;

/**
 * Used to pack some useful data related to a seat table.
 *
 * @author Calboot
 * @since 1.2.0
 */
@Getter
public class Seat {
    /**
     * The seat table stored as a {@code  List}.
     */
    private final List<String> seat;
    /**
     * The config used to generate the seat table.
     */
    private final SeatConfig config;
    /**
     * The seed used to generate the seat table.
     */
    private final long seed;
    /**
     * The lucky person specially chosen.
     *
     * @since 1.2.1
     */
    private final String luckyPerson;

    /**
     * @param seat        {@link #seat}
     * @param config      {@link #config}
     * @param seed        {@link #seed}
     * @param luckyPerson {@link #luckyPerson}
     */
    public Seat(List<String> seat, SeatConfig config, long seed, String luckyPerson) {
        this.seat = seat;
        this.config = config;
        this.seed = seed;
        if (luckyPerson == null) luckyPerson = "";
        this.luckyPerson = luckyPerson;
    }
}
